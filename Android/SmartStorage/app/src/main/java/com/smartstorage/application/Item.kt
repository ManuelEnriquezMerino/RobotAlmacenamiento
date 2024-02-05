package com.smartstorage.application


class Item (emptyCell: IntArray, val name: String,val  description: String){

    val row = emptyCell[0]
    val column = emptyCell[1]

    init {
        val check = 2+emptyCell[0]+emptyCell[1]
        USBHandler.getInstance()?.sendMessage(byteArrayOf(2,emptyCell[0].toByte(),emptyCell[1].toByte(),check.toByte()))
    }


}