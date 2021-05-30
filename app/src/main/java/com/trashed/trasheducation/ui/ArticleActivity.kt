package com.trashed.trasheducation.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.trashed.trasheducation.R
import com.trashed.trasheducation.data.source.remote.response.ArticleResponse
import com.trashed.trasheducation.databinding.ActivityArticleBinding
import com.trashed.trasheducation.ui.viewModel.ArticleViewModel
import com.trashed.trasheducation.ui.viewModel.factory.ViewModelFactory


class ArticleActivity :AppCompatActivity(){

    private lateinit var activityArticleBinding: ActivityArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityArticleBinding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(activityArticleBinding.root)

        var backButton = findViewById(R.id.BackButton2) as Button
        val text = intent.getStringExtra("label")
        val img: Bitmap? = intent.getParcelableExtra("img")
        if (img != null){
            activityArticleBinding.PreviewImage.setImageBitmap(img)
        }


        val factory = ViewModelFactory.getInstance()
        val viewModel = ViewModelProvider(this, factory)[ArticleViewModel::class.java]
        if (text != null){
            viewModel.getArticle(text).observe(this, {
                putDataView(it)
            })
        }

        backButton.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun putDataView(articleResponse: ArticleResponse){
        activityArticleBinding.ImpactArticle.text = articleResponse.impact
        activityArticleBinding.OvercomeArticle.text = articleResponse.overcome
        activityArticleBinding.LinkArticle.text = articleResponse.link1
    }
}