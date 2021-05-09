package org.coco24.smartcarhost

import android.app.Application
import android.bluetooth.BluetoothGatt
import java.lang.Exception
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleGattCallback
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.callback.BleWriteCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.exception.BleException

class AndroidBluetoothInterfaceImpl(val application: Application) : BluetoothInterafce {
    private val ble: BleManager = BleManager.getInstance()
    private val connected = HashMap<String, BleDevice>()
    init {
        BleManager.getInstance().init(application)
        if(!ble.isSupportBle) throw Exception("当前设备不支持BLE！")
        if(!ble.isBlueEnable) ble.enableBluetooth()

    }
    override fun sendData(device: BluetoothDevice, data: UByteArray) {
        val bleDevice = connected.get(device.mac)
        ble.write(bleDevice, "6E400001-B5A3-F393-E0A9-E50E24DCCA9E", "6E400002-B5A3-F393-E0A9-E50E24DCCA9E", data.toByteArray(), object : BleWriteCallback() {
            override fun onWriteSuccess(current: Int, total: Int, justWrite: ByteArray?) {}
            override fun onWriteFailure(exception: BleException?) {}
        })
    }

    override fun onReceiveData(callback: (BluetoothDevice, UByteArray) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun connect(device: BluetoothDevice, callback: () -> Unit) {
        ble.connect(device.mac, object : BleGattCallback() {
            override fun onStartConnect() = Unit
            override fun onConnectFail(bleDevice: BleDevice?, exception: BleException?) = Unit

            override fun onConnectSuccess(
                bleDevice: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                bleDevice?.also { connected.put(bleDevice.mac, bleDevice) }
                callback()
            }

            override fun onDisConnected(
                isActiveDisConnected: Boolean,
                bleDevice: BleDevice?,
                gatt: BluetoothGatt?,
                status: Int
            ) {
                bleDevice?.also { connected.remove(bleDevice.mac) }
            }
        })
    }

    override fun isConnected(device: BluetoothDevice): Boolean = connected.contains(device.mac)
    override fun disconnect(device: BluetoothDevice) {
        ble.disconnect(connected.get(device.mac))
        connected.remove(device.mac)
    }

    override fun scan(callback: (List<BluetoothDevice>) -> Unit) {
        ble.scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {}
            override fun onScanning(bleDevice: BleDevice?) {}
            override fun onScanFinished(scanResultList: MutableList<BleDevice>) {
                callback(scanResultList.map { AndroidBLEDevice(it) })
            }
        })
    }


}