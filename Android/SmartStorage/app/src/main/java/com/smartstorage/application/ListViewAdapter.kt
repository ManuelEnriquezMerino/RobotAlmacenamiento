package com.smartstorage.application

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.hoho.android.usbserial.driver.UsbSerialPort

class ListViewAdapter(private val context : Context, private val items : ArrayList<Item>, private val outputCell : Pair<Int,Int>, private val emptyCell : IntArray, private val isAvailable : Array<Array<Boolean>>) : ArrayAdapter<Item>(context, R.layout.list_row, items) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        //val item = items?.get(position)
        if(view == null){
            val layoutInflater : LayoutInflater = context?.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.list_row,null)
        }
        val number : TextView = view!!.findViewById(R.id.number)
        number.text = "${position + 1}."

        val name : TextView = view.findViewById(R.id.name)
        name.text = "${items?.get(position)?.name}"

        val info : ImageView = view.findViewById(R.id.info)
        val retrieve : ImageView = view.findViewById(R.id.retrieve)

        info.setOnClickListener {}



        retrieve.setOnClickListener {
            var check = 1+outputCell.first+outputCell.second
            USBHandler.getInstance()?.sendMessage(byteArrayOf(1,outputCell.first.toByte(),outputCell.second.toByte(),check.toByte()))
            println("${emptyCell[0]} ${emptyCell[1]}")
            check = 2+emptyCell[0]+ emptyCell[1]
            USBHandler.getInstance()?.sendMessage(byteArrayOf(2,emptyCell[0].toByte(),emptyCell[1].toByte(),check.toByte()))
            isAvailable[items[position].row][items[position].column] = true
            emptyCell[0]=items[position].row
            emptyCell[1]=items[position].column
            val message = items.removeAt(position).retrieve(outputCell)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            notifyDataSetChanged()
        }

        return view
    }
}