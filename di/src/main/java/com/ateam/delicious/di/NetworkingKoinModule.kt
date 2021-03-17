package com.ateam.delicious.di

import com.ateam.delicious.domain.NetworkAPI
import com.ateam.delicious.networking.NetworkApiImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val networkingKoinModule = module {

    single<NetworkAPI> {
        NetworkApiImpl(androidApplication().cacheDir)
    }


}
