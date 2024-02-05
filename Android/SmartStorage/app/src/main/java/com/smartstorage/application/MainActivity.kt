package com.smartstorage.application

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private var t : Toast? = null
    private var listView : ListView? = null
    private var adapter : ListViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        USBHandler.createInstance(applicationContext)
        ItemHandler.createInstance()

        listView = findViewById(R.id.listview)
        val input : EditText = findViewById(R.id.input)
        val add : ImageView = findViewById(R.id.add)
        adapter = ListViewAdapter(applicationContext)
        listView?.adapter = adapter

        listView?.setOnItemClickListener{ _, _, position, _ ->
            makeToast(ItemHandler.getItem(position).description)
        }

        listView?.setOnItemLongClickListener{ _, _, _, _ ->
            return@setOnItemLongClickListener(true)
        }

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            adapter!!.notifyDataSetChanged()
        }


        add.setOnClickListener {
            val activity = startForResult.launch(Intent(this,NewItemMenu::class.java))

        }



    }

    private fun makeToast (s : String){
        t?.cancel()
        t = Toast.makeText(applicationContext, s, Toast.LENGTH_LONG)
        t?.show()
    }

}