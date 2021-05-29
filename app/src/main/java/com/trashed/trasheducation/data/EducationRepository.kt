package com.trashed.trasheducation.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.trashed.trasheducation.data.source.remote.RemoteDataSource
import com.trashed.trasheducation.data.source.remote.response.ArticleResponse

class EducationRepository private constructor(private val remoteDataSource: RemoteDataSource): EducationDataSource{

    companion object{
        @Volatile
        private var instance: EducationRepository? = null

        fun getInstance(remoteDataSource: RemoteDataSource): EducationRepository =
            instance?: synchronized(this){
                instance?:EducationRepository(remoteDataSource).apply { instance = this }
            }
    }

    override fun getArticle(label: String): LiveData<ArticleResponse> {
        val articleResult = MutableLiveData<ArticleResponse>()

        remoteDataSource.getAllArticle(label, object : RemoteDataSource.LoadArticleCallBack{
            override fun onArticleReceived(articleResponse: ArticleResponse) {
                articleResult.postValue(articleResponse)
            }
        })
        return articleResult
    }

}