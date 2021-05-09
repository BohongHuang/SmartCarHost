package org.coco24.smartcarhost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.kotcrab.vis.ui.widget.VisLabel
import ktx.actors.stage
import ktx.app.KtxScreen
import ktx.scene2d.actor
import ktx.scene2d.actors

class MainScreen(val bt: BluetoothInterafce) : KtxScreen {
    inline fun <reified T> AssetManager.load(filename: String): Unit {
        this.load(filename, T::class.java)
    }

    fun Float.toIntHalfDown(): Int {
        val intValue = this.toInt()
        return if (this - intValue.toFloat() > 0.5f) intValue + 1 else intValue
    }

    val stageHeight = 720f
    val stageWidth = stageHeight * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())
    val stage = stage(viewport = FitViewport(stageWidth, stageHeight))
    val assetManager = AssetManager(InternalFileHandleResolver())
    val touchpadLeft: Touchpad
    val touchpadRight: Touchpad
    val statusLabel: VisLabel
    val btDevice = object : BluetoothDevice {
        override val name: String?
            get() = null
        override val mac: String
            get() = "24:0A:C4:9B:8B:A6"
    }

    init {
        assetManager.load<Texture>("res/graphics/joystick.png")
        assetManager.finishLoading()

        val knobDrawable =
            TextureRegionDrawable(assetManager.get<Texture>("res/graphics/joystick.png"))
        knobDrawable.minHeight = 128f
        knobDrawable.minWidth = 128f
        val touchpadBackDrawable =
            TextureRegionDrawable(assetManager.get<Texture>("res/graphics/joystick.png"))
        touchpadBackDrawable.minWidth = 384f
        touchpadBackDrawable.minHeight = 384f
        touchpadLeft = Touchpad(16f, Touchpad.TouchpadStyle(touchpadBackDrawable, knobDrawable))
        touchpadRight = Touchpad(16f, Touchpad.TouchpadStyle(touchpadBackDrawable, knobDrawable))
        statusLabel = VisLabel("Disconnected")
        stage.actors {
            actor(statusLabel) {
                setPosition(0f, stageHeight, Align.topLeft)
            }
            actor(touchpadLeft) {
                setPosition(stageWidth / 10f, stageHeight / 3f, Align.left)
            }
            actor(touchpadRight) {
                setPosition(stageWidth / 10f * 9f, stageHeight / 3f, Align.right)
            }
        }
        bt.connect(btDevice) {}
    }

    override fun show() {
        super.show()
        Gdx.input.inputProcessor = stage
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        stage.viewport.update(width, height)
    }

    fun encodeControlData(direction: Float, speed: Float): UByteArray {
        val high = ((0x0f).toFloat() * (direction + 2f) / 4f).toIntHalfDown()
        val low = ((0x0f).toFloat() * (speed + 1f) / 2f).toIntHalfDown()
        return ubyteArrayOf(((high shl 4) or low).toUByte())
    }

    val txPeriod = 1f / 30f //每秒发送30次数据
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
            bt.sendData(btDevice, encodeControlData(direction, speed))
        }
    }

    val tryConnectionPeriod = 3f //断开连接的状态下，每3秒尝试连接一次
    var tryConnectionPeriodCounter = 0f
    fun tryConnection(delta: Float) {
        if (bt.isConnected(btDevice)) {
            tryConnectionPeriodCounter = 0f
            return
        }
        tryConnectionPeriodCounter += delta
        if (tryConnectionPeriodCounter >= tryConnectionPeriod) {
            tryConnectionPeriodCounter = 0f
            bt.connect(btDevice) {}
        }
    }

    override fun render(delta: Float) {
        super.render(delta)
        tryConnection(delta)
        statusLabel.setText(if (bt.isConnected(btDevice)) "Connected" else "Disconnected")
        sendData(delta)
        stage.act(delta)
        stage.draw()
    }

}