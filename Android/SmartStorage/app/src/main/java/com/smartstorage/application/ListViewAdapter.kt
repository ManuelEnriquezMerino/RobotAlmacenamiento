package com.smartstorage.application

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson


class ListViewAdapter(private val context : Context) : ArrayAdapter<Item>(context, R.layout.list_row, ItemHandler.getItemList()) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        //val item = items?.get(position)
        if(view == null){
            val layoutInflater : LayoutInflater = context?.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.list_row,null)
        }
        val number : TextView = view!!.findViewById(R.id.number)
        number.text = (position+1).toString()

        val name : TextView = view.findViewById(R.id.name)
        name.text = ItemHandler.getItem(position).name

        //val info : ImageView = view.findViewById(R.id.info)
        val retrieve : ImageView = view.findViewById(R.id.retrieve)

        //info.setOnClickListener {}



        retrieve.setOnClickListener {
            ItemHandler.retrieveItem(position)
            notifyDataSetChanged()
            /*val convertedDataItems: String = Gson().toJson(items)
            val convertedDataEmptyCell: String = Gson().toJson(emptyCell)
            val convertedDataIsAvailable: String = Gson().toJson(isAvailable)
            val editor = context.getSharedPreferences(context.resources.getString(R.string.file),MODE_PRIVATE).edit()
            editor.putString("items",convertedDataItems)
            editor.putString("emptyCell",convertedDataEmptyCell)
            editor.putString("isAvailable",convertedDataIsAvailable)
            editor.apply()*/
        }

        return view
    }
}