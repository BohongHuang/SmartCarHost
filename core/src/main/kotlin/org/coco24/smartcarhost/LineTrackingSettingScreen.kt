package org.coco24.smartcarhost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.kotcrab.vis.ui.widget.VisTextField
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.actors.stage
import ktx.app.KtxScreen
import ktx.scene2d.actor
import ktx.scene2d.actors
import ktx.scene2d.vis.*

class LineTrackingSettingScreen(btDevice: BluetoothDevice, game: MainGame) : KtxScreen {
    val stageHeight = 720f
    val stageWidth = stageHeight * (Gdx.graphics.width.toFloat() / Gdx.graphics.height.toFloat())
    val uiStage = stage(viewport = FitViewport(stageWidth, stageHeight))

    val assetManager = game.assetManager
    val bt = game.bt
    var kp = 1.5f
    var ti = 1.5f
    var td = 0.01f
    var t = 0.02f
    var speed = 0.4f

    init {
        val returnDrawable =
            TextureRegionDrawable(assetManager.loadNow<Texture>("res/graphics/return.png"))
        returnDrawable.minWidth = 100f
        returnDrawable.minHeight = 100f
        val applyDrawable =
            TextureRegionDrawable(assetManager.loadNow<Texture>("res/graphics/ok.png"))
        applyDrawable.minWidth = 100f
        applyDrawable.minHeight = 100f

        val pauseDrawable =
            TextureRegionDrawable(assetManager.loadNow<Texture>("res/graphics/pause.png"))
        pauseDrawable.minWidth = 100f
        pauseDrawable.minHeight = 100f

        uiStage.actors {
            visTable {
                visTable {
                    visTextButton(text = "Return") {
                        clearChildren()
                        visImage(returnDrawable)
                        row()
                        actor(label)
                        pack()

                        onClick {
                            game.setScreen<MainScreen>()
                        }
                    }.cell(expandX = true, align = Align.left)
                    visTextButton(text = "Pause") {
                        clearChildren()
                        visImage(pauseDrawable)
                        row()
                        actor(label)
                        pack()

                        onClick {
                            val args = "Pause".encodeToByteArray().toUByteArray()

                            val data = UByteArray(args.size + 2)
                            data[0] = CommandTracking
                            data[1] = args.size.toUByte()

                            for(i in args.indices) data[i + 2] = args[i]

                            bt.sendData(btDevice, data)
                        }
                    }.cell(expandX = true, align = Align.center)
                    visTextButton(text = "Apply") {
                        clearChildren()
                        visImage(applyDrawable)
                        row()
                        actor(label)
                        pack()

                        onClick {
                            val args = """
                                ${kp}
                                ${ti}
                                ${td}
                                ${t}
                                ${speed}
                            """.trimIndent().encodeToByteArray().toUByteArray()

                            val data = UByteArray(args.size + 2)
                            data[0] = CommandTracking
                            data[1] = args.size.toUByte()
                            for(i in args.indices) data[i + 2] = args[i]

                            bt.sendData(btDevice, data)
                        }
                    }.cell(expandX = true, align = Align.right)
                }.cell(growX = true, expandY = true, align = Align.top)
                row()
                visTable {
                    visLabel("Kp: ")
                    val fieldKp  = visTextField {
                        onChange {
                            kp = text.toFloatOrNull()?:kp
                        }
                        text = kp.toString()
                    }.cell(align = Align.left, expandX = true, fillX = true)
                    row()
                    val sliderKp = visSlider(max = 10f, step = 0.01f) {
                        onChange {
                            fieldKp.text = value.toString()
                            kp = value
                        }
                        value = kp
                    }.cell(colspan = 2, width = stageWidth * 0.9f, pad = stageHeight / 80f)
                    row()

                    visLabel("Ti: ")
                    val fieldTi  = visTextField {
                        onChange {
                            ti = text.toFloatOrNull()?:ti
                        }
                        text = ti.toString()
                    }.cell(align = Align.left, expandX = true, fillX = true)
                    row()
                    val sliderTi = visSlider(max = 10f, step = 0.01f) {
                        onChange {
                            fieldTi.text = value.toString()
                            ti = value
                        }
                        value = ti
                    }.cell(colspan = 2, width = stageWidth * 0.9f, pad = stageHeight / 80f)
                    row()

                    visLabel("Td: ")
                    val fieldTd  = visTextField {
                        onChange {
                            td = text.toFloatOrNull()?:td
                        }
                        text = td.toString()
                    }.cell(align = Align.left, expandX = true, fillX = true)
                    row()
                    val sliderTd = visSlider(max = 0.25f, step = 0.001f) {
                        onChange {
                            fieldTd.text = value.toString()
                            td = value
                        }
                        value = td
                    }.cell(colspan = 2, width = stageWidth * 0.9f, pad = stageHeight / 80f)
                    row()


                    visLabel("T: ")
                    val fieldT  = visTextField {
                        onChange {
                            t = text.toFloatOrNull()?:t
                        }
                        text = t.toString()
                    }.cell(align = Align.left, expandX = true, fillX = true)
                    row()
                    val sliderT = visSlider(max = 1f, step = 0.01f) {
                        onChange {
                            fieldT.text = value.toString()
                            t = value
                        }
                        value = t
                    }.cell(colspan = 2, width = stageWidth * 0.9f, pad = stageHeight / 80f)
                    row()

                    visLabel("Speed: ")
                    val fieldSpeed  = visTextField {
                        onChange {
                            speed = text.toFloatOrNull()?:t
                        }
                        text = speed.toString()
                    }.cell(align = Align.left, expandX = true, fillX = true)
                    row()
                    val sliderSpeed = visSlider(max = 1f, step = 0.01f) {
                        onChange {
                            fieldSpeed.text = value.toString()
                            speed = value
                        }
                        value = speed
                    }.cell(colspan = 2, width = stageWidth * 0.9f, pad = stageHeight / 80f)
                    row()

                }.cell(grow = true)
                setFillParent(true)
            }

        }
    }

    override fun show() {
        super.show()
        Gdx.input.inputProcessor = uiStage
    }
    override fun render(delta: Float) {
        super.render(delta)
//        println(kp)
        uiStage.act(delta)
        uiStage.draw()
    }
}