package com.bohush.surveyapp.di

import com.bohush.surveyapp.data.ApiService
import com.bohush.surveyapp.data.SurveyRepository
import com.bohush.surveyapp.data.SurveyRepositoryImpl
import com.bohush.surveyapp.data.source.remote.RemoteDataSource
import com.bohush.surveyapp.data.source.remote.RemoteDataSourceImpl
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
object DataModule {
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://xm-assignment.web.app")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    fun provideRemoteDataSource(apiService: ApiService): RemoteDataSource {
        return RemoteDataSourceImpl(apiService)
    }

    @Provides
    fun provideSurveyRepository(remoteDataSource: RemoteDataSourceImpl): SurveyRepository {
        return SurveyRepositoryImpl(remoteDataSource)
    }
}
