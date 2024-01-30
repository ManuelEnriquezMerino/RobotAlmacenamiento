package com.smartstorage.application

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.SystemClock
import androidx.core.content.ContextCompat.getSystemService
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber

class USBHandler private constructor(){

    companion object {

        @Volatile
        private var instance: USBHandler? = null
        private var port : UsbSerialPort? = null
        private var contexto: Context? = null

        fun createInstance(context: Context): USBHandler? {
            synchronized(this) {
                if (instance == null) {
                    instance = USBHandler()
                    contexto = context
                    val manager = context.applicationContext.getSystemService(Context.USB_SERVICE) as UsbManager
                    val driver = UsbSerialProber.getDefaultProber().findAllDrivers(manager)[0]

                    manager.requestPermission(driver.device, PendingIntent.getBroadcast(context, 0, Intent("com.smartstorage.application" + ".GRANT_USB"), PendingIntent.FLAG_MUTABLE))

                    val connection = manager.openDevice(driver.device)
                    port = driver.ports[0]
                    port?.open(connection)
                    port?.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
                    port?.dtr = true
                }
            }
            return instance
        }

        fun getInstance(): USBHandler? {
            return instance
        }
    }

    fun getPort(): UsbSerialPort? {
        return port
    }

    fun sendMessage(message: ByteArray): Boolean {
        val read = ByteArray(8192)

        val manager = contexto?.applicationContext?.getSystemService(Context.USB_SERVICE) as UsbManager
        val driver = UsbSerialProber.getDefaultProber().findAllDrivers(manager)[0]

        manager.requestPermission(driver.device, PendingIntent.getBroadcast(contexto, 0, Intent("com.smartstorage.application" + ".GRANT_USB"), PendingIntent.FLAG_MUTABLE))

        val connection = manager.openDevice(driver.device)
        port = driver.ports[0]
        port?.open(connection)
        port?.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        port?.dtr = true

        if(port != null){
            while(read[0].toInt()-48 != 1 ){
                read[0]=0

                try {
                    port!!.read(read, 100)
                    if(read[0].toInt()-48 == 3 ){
                        port!!.write(byteArrayOf(11),500)
                    } else {
                        port!!.write(message, 10)
                    }
                } catch (e : Exception) {println(e.message)}

            }

            while(read[0].toInt()-48 != 2 ){
                try {port!!.read(read, 100)  } catch (e : Exception) {println(e.message)}
                try {port!!.write(byteArrayOf(10),50)} catch (e : Exception) {println(e.message)}
            }

            while(read[0].toInt()-48 != 3 ){
                try {port!!.read(read, 100)  } catch (e : Exception) {println(e.message)}
                if(read[0].toInt()-48 == 1 )
                    try {port!!.write(byteArrayOf(10),10)} catch (e : Exception) {println(e.message)}
                else
                    try {port!!.write(byteArrayOf(11),10)} catch (e : Exception) {println(e.message)}

            }
            return true
        }
        return false
    }
}