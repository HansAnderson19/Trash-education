package com.trashed.trasheducation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        check(true)
        val text = intent.getStringExtra("label")
        activityArticleBinding.TrashLabel.text = text
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
                val videoId = articleResponse.video
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
        check(false)
    }

    private fun check(state: Boolean){
        if (state){
            activityArticleBinding.pbMain.visibility = View.VISIBLE
            activityArticleBinding.PreviewImage.visibility = View.INVISIBLE
            activityArticleBinding.TrashArticle.visibility = View.INVISIBLE
            activityArticleBinding.TrashLabel.visibility = View.INVISIBLE
            activityArticleBinding.ImpactArticle.visibility = View.INVISIBLE
            activityArticleBinding.ImpactLabel.visibility = View.INVISIBLE
            activityArticleBinding.OvercomeArticle.visibility = View.INVISIBLE
            activityArticleBinding.OvercomeLabel.visibility = View.INVISIBLE
            activityArticleBinding.LinkLabel.visibility = View.INVISIBLE
            activityArticleBinding.link1.visibility = View.INVISIBLE
            activityArticleBinding.link2.visibility = View.INVISIBLE
            activityArticleBinding.VideoLabel.visibility = View.INVISIBLE
            activityArticleBinding.youtubeVideoPlayer.visibility = View.INVISIBLE
        }else{
            activityArticleBinding.pbMain.visibility = View.INVISIBLE
            activityArticleBinding.layoutId.visibility = View.VISIBLE
            activityArticleBinding.PreviewImage.visibility = View.VISIBLE
            activityArticleBinding.TrashArticle.visibility = View.VISIBLE
            activityArticleBinding.TrashLabel.visibility = View.VISIBLE
            activityArticleBinding.ImpactArticle.visibility = View.VISIBLE
            activityArticleBinding.ImpactLabel.visibility = View.VISIBLE
            activityArticleBinding.OvercomeArticle.visibility = View.VISIBLE
            activityArticleBinding.OvercomeLabel.visibility = View.VISIBLE
            activityArticleBinding.LinkLabel.visibility = View.VISIBLE
            activityArticleBinding.link1.visibility = View.VISIBLE
            activityArticleBinding.link2.visibility = View.VISIBLE
            activityArticleBinding.VideoLabel.visibility = View.VISIBLE
            activityArticleBinding.youtubeVideoPlayer.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}