package com.smartstorage.application

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi


class NewItemMenu : Activity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.popup_window)

        val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
        val insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        val width = windowMetrics.bounds.width() - insets.left - insets.right
        val height = windowMetrics.bounds.height() - insets.left - insets.right

        val cancel : ImageView = findViewById(R.id.cancel_item)
        val add : ImageView = findViewById(R.id.add_item)

        val inputName : EditText = findViewById(R.id.inputName)
        val inputDescription : EditText = findViewById(R.id.inputDescription)

        window.setLayout((width*0.75).toInt(), (height*0.75).toInt())

        add.setOnClickListener{
            val name : String = inputName.text.toString()
            val description : String = inputDescription.text.toString()

            val message = ItemHandler.storeItem(name,description)

            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }

        cancel.setOnClickListener {
            finish()
        }
    }

}