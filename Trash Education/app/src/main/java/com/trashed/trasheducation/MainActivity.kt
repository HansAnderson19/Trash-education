package com.trashed.trasheducation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.ml.custom.FirebaseCustomRemoteModel
import com.trashed.trasheducation.ui.ArticleActivity
import com.trashed.trasheducation.ui.MenuActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var backButton = findViewById(R.id.BackButton) as Button
        var articleButton = findViewById(R.id.ArticleButton) as Button

        backButton.setOnClickListener(){
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        articleButton.setOnClickListener(){
            val intent = Intent(this, ArticleActivity::class.java)
            startActivity(intent)
        }
    }

}