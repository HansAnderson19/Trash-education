package com.trashed.trasheducation.data

import androidx.lifecycle.LiveData
import com.trashed.trasheducation.data.source.remote.response.ArticleResponse

interface EducationDataSource {

    fun getArticle(label: String) : LiveData<ArticleResponse>

}