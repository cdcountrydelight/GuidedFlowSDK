package com.cd.extracttagapp.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    
    // Gson instance
    single<Gson> {
        GsonBuilder()
            .create()
    }
    
    // Retrofit instance
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://api.example.com/") // TODO: Replace with actual base URL
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }
}