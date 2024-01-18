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

    private var maxRow = 3
    private var maxColumn = 4

    private var outputRow = 2
    private var outputColumn = 2

    private var isEmpty = Array(maxRow) {
        Array(maxColumn) {
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isEmpty[outputRow][outputColumn]=false

        //USBHandler.createInstance(applicationContext)

        listView = findViewById(R.id.listview)
        val input : EditText = findViewById(R.id.input)
        val add : ImageView = findViewById(R.id.add)
        adapter = ListViewAdapter(applicationContext,items)
        listView?.adapter = adapter

        listView?.setOnItemClickListener{ _, _, position, _ ->
            makeToast("${items[position].row}, ${items[position].column}")
        }

        listView?.setOnItemLongClickListener{ _, _, _, _ ->
            return@setOnItemLongClickListener(true)
        }

        add.setOnClickListener {
            val text : String = input.text.toString()
            if (text.isEmpty())
                makeToast("Enter an item")
            else {
                val position = getPositionToStore()
                if (position != null) {
                    isEmpty[position.first][position.second] = false
                    addItem(Item(position.first,position.second,text,text))
                    input.setText("")
                    makeToast("Item added")
                } else {
                    makeToast("No Empty Slots")
                }

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

    private fun getPositionToStore() : Pair<Int,Int>?{
        var position : Pair<Int,Int>? = null
        var currentRow = maxRow-1
        while (position==null && currentRow>=0) {
            val position1 = getPositionToStoreLeft(currentRow, outputColumn - 1)
            val position2 = getPositionToStoreRight(currentRow, outputColumn)
            if (position1 != null && position2 != null)
                if (outputColumn - position1.second <= position2.second - outputColumn)
                    position = position1
                else
                    position = position2
            else
                if (position1 != null)
                    position = position1
                else
                    if (position2 != null)
                        position = position2
                    else
                        currentRow--
        }
        return position
    }

    private fun getPositionToStoreLeft(x: Int, y: Int) : Pair<Int,Int>?{
        if(isEmpty[x][y])
            return Pair(x,y)
        else
            if(y==0)
                return null
            else
                return getPositionToStoreLeft(x,y-1)
    }

    private fun getPositionToStoreRight(x: Int, y: Int) : Pair<Int,Int>?{
        if(isEmpty[x][y])
            return Pair(x,y)
        else
            if(y==(maxColumn-1))
                return null
            else
                return getPositionToStoreLeft(x,y+1)
    }

}