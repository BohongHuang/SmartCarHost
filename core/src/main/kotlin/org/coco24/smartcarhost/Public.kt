package org.coco24.smartcarhost

import com.badlogic.gdx.assets.AssetManager

val CommandIdle = (0x00).toUByte()
val CommandBleControl = (0x01).toUByte()
val CommandAvoidance = (0x02).toUByte()
val CommandTracking = (0x03).toUByte()

inline fun <reified T> AssetManager.loadNow(path: String): T {
    load(path, T::class.java)
    return finishLoadingAsset(path)
}