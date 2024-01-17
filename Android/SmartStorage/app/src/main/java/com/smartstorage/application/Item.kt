package com.smartstorage.application

import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort

class Item (row: Int, column : Int, name: String, description: String){

    private val outputRow = 2
    private val outputColumn = 3

    var row = row
    var column = column
    var name = name
    var description = description

    fun retrieve() : String {
        System.out.println("Entre 1")
        val check = 1+row+column

        USBHandler.getInstance()?.sendMessage(byteArrayOf(1,row.toByte(),column.toByte(),check.toByte()))
        return "$row,$column removed"
    }
}