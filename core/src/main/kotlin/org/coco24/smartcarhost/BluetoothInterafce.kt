package org.coco24.smartcarhost

interface BluetoothInterafce {
    fun sendData(device: BluetoothDevice, data: UByteArray, callback: (() -> Unit)? = null): Unit
    fun onReceiveData(callback: (BluetoothDevice, UByteArray) -> Unit) : Unit
    fun connect(device: BluetoothDevice, callback: () -> Unit): Unit
    fun disconnect(device: BluetoothDevice): Unit
    fun isConnected(device: BluetoothDevice): Boolean
    fun scan(callback: (List<BluetoothDevice>) -> Unit): Unit
}