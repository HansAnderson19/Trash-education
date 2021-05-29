package com.trashed.trasheducation.di

import com.trashed.trasheducation.data.EducationRepository
import com.trashed.trasheducation.data.source.remote.RemoteDataSource

object Injection {

    fun provideRepository(): EducationRepository{
        val remoteDataSource = RemoteDataSource()

        return EducationRepository.getInstance(remoteDataSource)
    }

}