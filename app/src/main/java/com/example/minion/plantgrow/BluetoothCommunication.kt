package com.example.minion.plantgrow

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.util.Log
import java.io.BufferedReader
import java.io.OutputStream
import java.util.*

class BluetoothCommunication private constructor() {
    private var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var bluetoothDevice: BluetoothDevice
    private lateinit var bluetoothSocket: BluetoothSocket
    private lateinit var output: OutputStream
    private lateinit var input: BufferedReader

    companion object {
        private var bluetoothCommunication: BluetoothCommunication? = null
        fun getInstance(): BluetoothCommunication {
            if (bluetoothCommunication == null) {
                bluetoothCommunication = BluetoothCommunication()
            }
            return bluetoothCommunication!!
        }
    }

    fun connect() {
        var runnable = Runnable {
            while (!bluetoothAdapter.isEnabled) Thread.sleep(1)
            var bondedDevices = bluetoothAdapter.bondedDevices
            for (bondedDevice in bondedDevices) {
                if (bondedDevice.name == "JDY-31-SPP") {
                    bluetoothDevice = bondedDevice
                    Log.i("App", bluetoothDevice.name)
                }
            }
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(bluetoothDevice.address)
            bluetoothSocket =
                bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
            bluetoothAdapter.cancelDiscovery()
            bluetoothSocket.connect()
            output = bluetoothSocket.outputStream
            input = bluetoothSocket.inputStream.bufferedReader()
            Thread.sleep(200)
            sendCommand("")
            Thread.sleep(50)
            var readed = input.readLine()

            Log.i("App", readed)


        }
        Handler().postDelayed(runnable, 1000)
    }

    fun sendCommand(string: String) {
        var message = "#knock!$string"
        for (char in message) {
            output.write(char.toInt())
        }

    }

    fun intToStyleString(value: Int): String {
        if (value < 10) return "00$value"
        else if (value < 100) return "0$value"
        else return "$value"
    }

    fun sendManualCommand(string: String) {
        var runnable = Runnable {
            var message = "#knock!sm$string"
            for (i in 1..2) {
                for (char in message) {
                    output.write(char.toInt())
                }
                Thread.sleep(100)
            }
        }
        Handler().post(runnable)
    }
    fun sendGetMoistureCommand(index:Int):Int{
        var message = "#knock!gm${index}"
        for (char in message) {
            output.write(char.toInt())
        }
        Thread.sleep(100)
        if (input.ready()){
            var received = input.readLine()
            return received.toInt()
        }
        return 0
    }



}

