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

class ListViewAdapter(context : Context, items : ArrayList<Item>) : ArrayAdapter<Item>(context, R.layout.list_row, items) {

    private var items : ArrayList<Item>? = items
    private var context : Context? = context

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
            val message = items?.removeAt(position)?.retrieve()
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            notifyDataSetChanged()
        }

        return view
    }
}