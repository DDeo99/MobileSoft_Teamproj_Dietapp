package com.example.mobileteamproj

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var inputButton: Button

    lateinit var showButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()

        inputButton.setOnClickListener {
            startActivity(Intent(this, InputActivity::class.java))

        }
        
        showButton.setOnClickListener {
            startActivity(Intent(this,ShowActivity::class.java))
        }
    }


    private fun initView() {
        inputButton = findViewById(R.id.input_button)
        showButton=findViewById(R.id.show_button)
    }
}