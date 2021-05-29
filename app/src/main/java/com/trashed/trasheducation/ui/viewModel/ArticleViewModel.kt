package com.trashed.trasheducation.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.trashed.trasheducation.data.EducationRepository
import com.trashed.trasheducation.data.source.remote.response.ArticleResponse

class ArticleViewModel(private val educationRepository: EducationRepository): ViewModel() {

    fun getArticle(label: String): LiveData<ArticleResponse> = educationRepository.getArticle(label)

}