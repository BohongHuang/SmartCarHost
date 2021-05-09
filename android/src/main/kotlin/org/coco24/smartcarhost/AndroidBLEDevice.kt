package org.coco24.smartcarhost

import com.clj.fastble.data.BleDevice

class AndroidBLEDevice(val device: BleDevice) : BluetoothDevice {
    override val name: String?
        get() = device.name
    override val mac: String
        get() = device.mac
}