package com.trashed.trasheducation.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trashed.trasheducation.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var activityMenuBinding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMenuBinding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(activityMenuBinding.root)

        val btnStart = activityMenuBinding.buttonStart
        btnStart.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}