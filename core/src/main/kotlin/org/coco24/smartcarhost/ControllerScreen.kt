package org.coco24.smartcarhost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.actors.stage
import ktx.app.KtxScreen
import ktx.scene2d.actor
import ktx.scene2d.actors
import ktx.scene2d.vis.visImage
import ktx.scene2d.vis.visTextButton

class ControllerScreen(val btDevice: BluetoothDevice, game: MainGame) : KtxScreen {
    inline fun <reified T> AssetManager.load(filename: String): Unit {
        this.load(filename, T::class.java)
    }

    fun Float.toIntHalfDown(): Int {
        val intValue = this.toInt()
        return if (this - intValue.toFloat() > 0.5f) intValue + 1 else intValue
    }

    val stageHeight = 720f
    val stageWidth = stageHeight * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())
    val uiStage = stage(viewport = FitViewport(stageWidth, stageHeight))

    val touchpadLeft: Touchpad
    val touchpadRight: Touchpad

    val assetManager = game.assetManager
    val bt = game.bt

    init {
        val knobDrawable =
            TextureRegionDrawable(assetManager.loadNow<Texture>("res/graphics/joystick.png"))
        knobDrawable.minWidth = 128f
        knobDrawable.minHeight = 128f
        val touchpadBackDrawable =
            TextureRegionDrawable(assetManager.loadNow<Texture>("res/graphics/joystick.png"))
        touchpadBackDrawable.minWidth = 384f
        touchpadBackDrawable.minHeight = 384f
        val returnDrawable =
            TextureRegionDrawable(assetManager.loadNow<Texture>("res/graphics/return.png"))
        returnDrawable.minWidth = 100f
        returnDrawable.minHeight = 100f
        touchpadLeft = Touchpad(16f, Touchpad.TouchpadStyle(touchpadBackDrawable, knobDrawable))
        touchpadRight = Touchpad(16f, Touchpad.TouchpadStyle(touchpadBackDrawable, knobDrawable))

        uiStage.actors {
            visTextButton(text = "Return") {
                clearChildren()
                visImage(returnDrawable)
                row()
                actor(label)
                pack()
                setPosition(0f, stageHeight, Align.topLeft)

                onClick {
                    Thread.sleep((txPeriod * 1000f).toLong())
                    game.setScreen<MainScreen>()
                }
            }
            actor(touchpadLeft) {
                setPosition(stageWidth / 10f, stageHeight / 3f, Align.left)
            }
            actor(touchpadRight) {
                setPosition(stageWidth / 10f * 9f, stageHeight / 3f, Align.right)
            }
        }
    }

    override fun show() {
        super.show()
        Gdx.input.inputProcessor = uiStage
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        uiStage.viewport.update(width, height)
    }

    fun encodeControlData(direction: Float, speed: Float): UByte {
        val high = ((0x0f).toFloat() * (direction + 2f) / 4f).toIntHalfDown()
        val low = ((0x0f).toFloat() * (speed + 1f) / 2f).toIntHalfDown()
        return ((high shl 4) or low).toUByte()
    }

    val txPeriod = 1f / 10f //每秒发送10次数据
    var txPeriodCounter = 0f
    fun sendData(delta: Float) {
        txPeriodCounter += delta
        if (txPeriodCounter >= txPeriod) {
            txPeriodCounter = 0f
            val direction =
                if (touchpadLeft.knobPercentY >= -0.5f) touchpadLeft.knobPercentX
                else if (touchpadLeft.knobPercentX > 0f) 2 - (touchpadLeft.knobPercentY + 1f)
                else -2f + (touchpadLeft.knobPercentY + 1f)
            val speed = touchpadRight.knobPercentY
            bt.sendData(
                btDevice,
                ubyteArrayOf((0x01).toUByte(), encodeControlData(direction, speed))
            )
        }
    }


    override fun render(delta: Float) {
        super.render(delta)

        sendData(delta)
        uiStage.act(delta)
        uiStage.draw()
    }

}