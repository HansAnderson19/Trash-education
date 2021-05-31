package com.trashed.trasheducation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trashed.trasheducation.databinding.ActivityWebBinding

class WebActivity: AppCompatActivity(){

    private lateinit var activityWebBinding: ActivityWebBinding

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        activityWebBinding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(activityWebBinding.root)

        val url = intent.getStringExtra("link")

        val webView = activityWebBinding.webview
        if (url != null){
            webView.loadUrl(url)
        }
    }
}