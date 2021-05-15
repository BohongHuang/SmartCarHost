package org.coco24.smartcarhost

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.kotcrab.vis.ui.VisUI
import ktx.app.KtxGame
import ktx.app.KtxScreen

/** [com.badlogic.gdx.ApplicationListener] implementation shared by all platforms.  */
class MainGame(val bt: BluetoothInterafce) : KtxGame<KtxScreen>() {
    lateinit var assetManager: AssetManager
    override fun create() {
        assetManager = AssetManager(InternalFileHandleResolver())
        VisUI.load(VisUI.SkinScale.X2)
        addScreen(MainScreen(this))
        setScreen<MainScreen>()
        super.create()
    }

    fun setScreen(screen: KtxScreen) {
        currentScreen = screen
        currentScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
        currentScreen.show()
    }
}
