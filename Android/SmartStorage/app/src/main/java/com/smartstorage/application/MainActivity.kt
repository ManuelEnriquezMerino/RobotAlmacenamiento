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

    private var maxRow = 2
    private var maxColumn = 3

    private var outputCell = Pair(2,2) //Row,Column

    private var emptyCell = intArrayOf(2,0)


    private var isAvailable = Array(maxRow+1) {
        Array(maxColumn+1) {
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        isAvailable[outputCell.first][outputCell.second]=false
        isAvailable[emptyCell[0]][emptyCell[1]]=false

        //USBHandler.createInstance(applicationContext)

        listView = findViewById(R.id.listview)
        val input : EditText = findViewById(R.id.input)
        val add : ImageView = findViewById(R.id.add)
        adapter = ListViewAdapter(applicationContext,items,outputCell,emptyCell,isAvailable)
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
                if(emptyCell!=null){
                    var check = 1+outputCell.first+outputCell.second
                    USBHandler.getInstance()?.sendMessage(byteArrayOf(1,outputCell.first.toByte(),outputCell.second.toByte(),check.toByte()))
                    addItem(Item(emptyCell,text,text))
                    isAvailable[emptyCell[0]][emptyCell[1]] = false
                    makeToast("Item added")
                    updateEmptyCell()
                    println("${emptyCell[0]} ${emptyCell[1]}")
                    if(emptyCell!=null){
                        check = 1+emptyCell[0]+emptyCell[1]
                        USBHandler.getInstance()?.sendMessage(byteArrayOf(1,emptyCell[0].toByte(),emptyCell[1].toByte(),check.toByte()))
                        check = 2+outputCell.first+outputCell.second
                        USBHandler.getInstance()?.sendMessage(byteArrayOf(2,outputCell.first.toByte(),outputCell.second.toByte(),check.toByte()))
                    }
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

    private fun updateEmptyCell() {
        var position : Pair<Int,Int>? = null
        var currentRow = maxRow
        while (position==null && currentRow>=0) {
            val position1 = getPositionToStoreLeft(currentRow, outputCell.second - 1)
            val position2 = getPositionToStoreRight(currentRow, outputCell.second)
            if (position1 != null && position2 != null)
                if (outputCell.second - position1.second <= position2.second - outputCell.second)
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
        if (position != null) {
            emptyCell[0]=position.first
            emptyCell[1]=position.second
        }
    }

    private fun getPositionToStoreLeft(x: Int, y: Int) : Pair<Int,Int>?{
        if(isAvailable[x][y])
            return Pair(x,y)
        else
            if(y==0)
                return null
            else
                return getPositionToStoreLeft(x,y-1)
    }

    private fun getPositionToStoreRight(x: Int, y: Int) : Pair<Int,Int>?{
        if(isAvailable[x][y])
            return Pair(x,y)
        else
            if(y==(maxColumn))
                return null
            else
                return getPositionToStoreLeft(x,y+1)
    }

}