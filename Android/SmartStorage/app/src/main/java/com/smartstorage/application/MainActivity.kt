package com.smartstorage.application

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {

    private var items: ArrayList<Item> = ArrayList()
    private var t : Toast? = null
    private var listView : ListView? = null
    private var adapter : ListViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        USBHandler.createInstance(applicationContext)

        items.add(Item(0,0,"0,0","a"))
        items.add(Item(1,1,"1,1","b"))

        listView = findViewById(R.id.listview)
        val input : EditText = findViewById(R.id.input)
        val add : ImageView = findViewById(R.id.add)
        adapter = ListViewAdapter(applicationContext,items)
        listView?.adapter = adapter

        listView?.setOnItemClickListener{ _, _, position, _ ->
            makeToast(items[position].name)
        }

        listView?.setOnItemLongClickListener{ _, _, _, _ ->
            return@setOnItemLongClickListener(true)
        }

        add.setOnClickListener {
            val text : String = input.text.toString()
            if (text.isEmpty())
                makeToast("Enter an item")
            else {
                addItem(Item(1,1,text,text))
                input.setText("")
                makeToast("Item added")
            }
        }

    }

    private fun addItem(item : Item) {
        items.add(item)
        listView?.adapter = adapter
    }

    private fun makeToast (s : String){
        t?.cancel()
        t = Toast.makeText(applicationContext, s, Toast.LENGTH_LONG)
        t?.show()
    }

}