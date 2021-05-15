package org.coco24.smartcarhost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.kotcrab.vis.ui.widget.VisLabel
import ktx.actors.onClick
import ktx.actors.stage
import ktx.app.KtxScreen
import ktx.scene2d.actor
import ktx.scene2d.actors
import ktx.scene2d.vis.*

/*
* 0x00 -> 停止
* 0x01 -> 蓝牙BLE串口控制
* 0x02 -> 避障
* */

class MainScreen(game: MainGame) : KtxScreen {

    val stageHeight = 720f
    val stageWidth = stageHeight * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())
    val uiStage = stage(viewport = FitViewport(stageWidth, stageHeight))

    //    val functionTable = visTable {}
    val btDevice = object : BluetoothDevice {
        override val name: String?
            get() = null
        override val mac: String
            get() = "24:0A:C4:9B:8B:A6"
    }
    val bt = game.bt
    val assetManager = game.assetManager
    val statusLabel: VisLabel = VisLabel("Disconnected")

    init {
        uiStage.actors {
            actor(statusLabel) {
                setPosition(0f, stageHeight, Align.topLeft)
            }
            visTable {
                setFillParent(true)

                visTextButton(text = "Remote Control") {
                    clearChildren()
                    visImage(texture = assetManager.loadNow("res/graphics/rc.png"))
                    row()
                    actor(label)

                    onClick {
                        sendCommand(CommandIdle)
                        sendCommand(CommandBleControl) {
                            game.setScreen(ControllerScreen(btDevice, game))
                        }
                    }
                }.cell(pad = 10f)
                visTextButton(text = "Avoidance") {
                    clearChildren()
                    visImage(texture = assetManager.loadNow("res/graphics/avoidance.png"))
                    row()
                    actor(label)

                    onClick {
                        sendCommand(CommandAvoidance)
                    }
                }.cell(pad = 10f)
            }
            visTextButton(text = "Stop") {
                clearChildren()
                visImage(texture = assetManager.loadNow("res/graphics/stop.png"))
                row()
                actor(label)
                pack()
                setPosition(stageWidth, 0f, Align.bottomRight)

                onClick {
                    sendCommand(CommandIdle)
                }
            }
        }
    }

    override fun show() {
        super.show()
        Gdx.input.inputProcessor = uiStage
        sendCommand(CommandIdle)
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        uiStage.viewport.update(width, height)
    }

    fun sendCommand(cmd: UByte, callback: (() -> Unit)? = null) {
        if (bt.isConnected(btDevice)) {
            bt.sendData(btDevice, ubyteArrayOf(cmd))
            callback?.invoke()
        }
    }

    val tryConnectionPeriod = 3f //断开连接的状态下，每3秒尝试连接一次
    var tryConnectionPeriodCounter = tryConnectionPeriod
    fun tryConnection(delta: Float) {
        if (bt.isConnected(btDevice)) {
            tryConnectionPeriodCounter = 0f
            return
        }
        tryConnectionPeriodCounter += delta
        if (tryConnectionPeriodCounter >= tryConnectionPeriod) {
            tryConnectionPeriodCounter = 0f
            bt.connect(btDevice) {
                sendCommand(CommandIdle)
            }
        }
    }

    override fun render(delta: Float) {
        tryConnection(delta)
        statusLabel.setText(if (bt.isConnected(btDevice)) "Connected" else "Disconnected")
        super.render(delta)
        uiStage.act()
        uiStage.draw()
    }
}