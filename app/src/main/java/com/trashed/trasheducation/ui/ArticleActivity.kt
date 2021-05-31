package com.trashed.trasheducation.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
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

        val backButton = activityArticleBinding.BackButton2
        val text = intent.getStringExtra("label")

        val factory = ViewModelFactory.getInstance()
        val viewModel = ViewModelProvider(this, factory)[ArticleViewModel::class.java]
        if (text != null){
            viewModel.getArticle(text).observe(this, {
                putDataView(it)
            })
        }

        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun putDataView(articleResponse: ArticleResponse){
        activityArticleBinding.TrashArticle.text = articleResponse.explanation
        activityArticleBinding.ImpactArticle.text = articleResponse.impact
        activityArticleBinding.OvercomeArticle.text = articleResponse.overcome

        Glide.with(this)
            .load(articleResponse.photo)
            .apply(RequestOptions.placeholderOf(R.drawable.ic_loading)
                .error(R.drawable.ic_error))
            .into(activityArticleBinding.PreviewImage)

        val youtubePlayerView: YouTubePlayerView = activityArticleBinding.youtubeVideoPlayer
        lifecycle.addObserver(youtubePlayerView)
        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                val videoId =  articleResponse.video
                youTubePlayer.loadVideo(videoId, 0F)
            }
        })

        activityArticleBinding.link1.setOnClickListener {
            val link: String = articleResponse.link1
            startActivity(
                Intent(this@ArticleActivity, WebActivity::class.java)
                    .putExtra("link" ,link)
            )
        }

        activityArticleBinding.link2.setOnClickListener {
            val link: String = articleResponse.link2
            startActivity(
                Intent(this@ArticleActivity, WebActivity::class.java)
                    .putExtra("link", link)
            )
        }

    }
}