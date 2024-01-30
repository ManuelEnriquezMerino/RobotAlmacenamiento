package com.smartstorage.application

import android.util.Log
import com.hoho.android.usbserial.driver.UsbSerialPort

class Item (emptyCell: IntArray, val name: String,val  description: String){

    val row = emptyCell[0]
    val column = emptyCell[1]

    init {
        val check = 2+emptyCell[0]+emptyCell[1]
        USBHandler.getInstance()?.sendMessage(byteArrayOf(2,emptyCell[0].toByte(),emptyCell[1].toByte(),check.toByte()))
    }

    fun retrieve(outputCell : Pair<Int,Int>) : String {
        var check = 1+row+column
        USBHandler.getInstance()?.sendMessage(byteArrayOf(1,row.toByte(),column.toByte(),check.toByte()))
        check = 2+outputCell.first+outputCell.second
        USBHandler.getInstance()?.sendMessage(byteArrayOf(2,outputCell.first.toByte(),outputCell.second.toByte(),check.toByte()))
        return "$row,$column removed"
    }
}