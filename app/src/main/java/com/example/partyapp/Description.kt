package com.example.partyapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Description : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)
        val a: TextView = findViewById(R.id.name)
        a.text = intent.getStringExtra("title")
        val b: TextView = findViewById(R.id.description)
        b.text= intent.getStringExtra("context")
        val c: ImageView = findViewById(R.id.poster)
        c.setImageResource(intent.getIntExtra("image", R.drawable.foto1))
    }
}