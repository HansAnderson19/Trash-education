package com.trashed.trasheducation.ui.viewModel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trashed.trasheducation.data.EducationRepository
import com.trashed.trasheducation.di.Injection
import com.trashed.trasheducation.ui.viewModel.ArticleViewModel

class ViewModelFactory private constructor(private val mEducationRepository: EducationRepository): ViewModelProvider.NewInstanceFactory(){

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(): ViewModelFactory =
            instance ?: synchronized(this){
                instance ?: ViewModelFactory(Injection.provideRepository()).apply{
                    instance = this
                }
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(ArticleViewModel::class.java) -> {
                ArticleViewModel(mEducationRepository) as T
            }
            else -> throw Throwable("Unknown ViewModel class : "+modelClass.name)
        }
    }
}