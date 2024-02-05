package com.smartstorage.application

class ItemHandler {

    companion object {

        @Volatile
        private var instance: ItemHandler? = null

        private var maxRow = 2
        private var maxColumn = 3
        private lateinit var outputCell : Pair<Int,Int>
        private lateinit var emptyCell : IntArray
        private lateinit var isAvailable : Array<Array<Boolean>>
        private lateinit var items: ArrayList<Item>


        fun createInstance(): ItemHandler? {
            synchronized(this) {
                if (instance == null) {
                    instance = ItemHandler()

                    isAvailable = Array(maxRow+1) {
                                    Array(maxColumn+1) {
                                       true
                                    }
                                }

                    outputCell =  Pair(2,2)
                    emptyCell = intArrayOf(2,0)
                    items = ArrayList()

                    isAvailable[outputCell.first][outputCell.second]=false
                    isAvailable[emptyCell[0]][emptyCell[1]]=false
                }
            }
            return instance
        }

        fun storeItem(name : String, description : String) : String {
            var check = 1+ outputCell.first + outputCell.second
            USBHandler.getInstance()?.sendMessage(byteArrayOf(1,outputCell.first.toByte(),outputCell.second.toByte(),check.toByte()))
            items.add(Item(emptyCell,name,description))
            isAvailable[emptyCell[0]][emptyCell[1]] = false
            updateEmptyCell()
            check = 1+emptyCell[0]+emptyCell[1]
            USBHandler.getInstance()?.sendMessage(byteArrayOf(1,emptyCell[0].toByte(),emptyCell[1].toByte(),check.toByte()))
            check = 2+outputCell.first+outputCell.second
            USBHandler.getInstance()?.sendMessage(byteArrayOf(2,outputCell.first.toByte(),outputCell.second.toByte(),check.toByte()))
            return "Successfully stored $name"
        }

        fun retrieveItem(position : Int) : String {
            var check = 1+outputCell.first+outputCell.second
            USBHandler.getInstance()?.sendMessage(byteArrayOf(1,outputCell.first.toByte(),outputCell.second.toByte(),check.toByte()))
            check = 2+emptyCell[0]+ emptyCell[1]
            USBHandler.getInstance()?.sendMessage(byteArrayOf(2,emptyCell[0].toByte(),emptyCell[1].toByte(),check.toByte()))
            val retrievedItem = items.removeAt(position)
            isAvailable[retrievedItem.row][retrievedItem.column] = true
            emptyCell[0]=retrievedItem.row
            emptyCell[1]=retrievedItem.column
            check = 1+retrievedItem.row+retrievedItem.column
            USBHandler.getInstance()?.sendMessage(byteArrayOf(1,retrievedItem.row.toByte(),retrievedItem.column.toByte(),check.toByte()))
            check = 2+outputCell.first+outputCell.second
            USBHandler.getInstance()?.sendMessage(byteArrayOf(2,outputCell.first.toByte(),outputCell.second.toByte(),check.toByte()))
            return "${retrievedItem.row},${retrievedItem.column} removed"
        }

        fun getItem(position: Int) : Item {
            return items[position]
        }

        fun getItemList() : ArrayList<Item> {
            return items
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
}