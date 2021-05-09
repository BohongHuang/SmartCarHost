package org.coco24.smartcarhost

import com.kotcrab.vis.ui.VisUI
import ktx.app.KtxGame
import ktx.app.KtxScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class MainGame(val bt: BluetoothInterafce) : KtxGame<KtxScreen>() {
    override fun create() {
        VisUI.load(VisUI.SkinScale.X2)
        currentScreen = MainScreen(bt)
        super.create()
    }
}