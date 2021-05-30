package com.trashed.trasheducation.ui

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.trashed.trasheducation.R

class WebActivity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        val webView = findViewById<WebView>(R.id.webview)
        webView.loadUrl("")
    }
}