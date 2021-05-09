package org.coco24.smartcarhost.lwjgl3

import org.coco24.smartcarhost.BluetoothDevice
import org.coco24.smartcarhost.BluetoothInterafce


class DesktopBluetoothInterfaceImpl : BluetoothInterafce {
    override fun sendData(device: BluetoothDevice, data: UByteArray) {

    }

    override fun onReceiveData(callback: (BluetoothDevice, UByteArray) -> Unit) {

    }
    override fun scan(callback: (List<BluetoothDevice>) -> Unit) {

    }

    override fun connect(device: BluetoothDevice, callback: () -> Unit) {

    }

    override fun disconnect(device: BluetoothDevice) {

    }

    override fun isConnected(device: BluetoothDevice): Boolean {
        return false
    }

}