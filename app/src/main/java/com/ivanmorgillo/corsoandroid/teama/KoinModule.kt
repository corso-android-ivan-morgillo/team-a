package com.ivanmorgillo.corsoandroid.teama

import com.ivanmorgillo.corsoandroid.teama.detail.DetailViewModel
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetailsRepository
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetailsRepositoryImpl
import com.ivanmorgillo.corsoandroid.teama.network.RecipeAPI
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        RecipeAPI()
    }
    single<RecipesRepository> {
        RecipeRepositoryImpl(api = get())
    }
    single<RecipeDetailsRepository> {
        RecipeDetailsRepositoryImpl(api = get())
    }
    single<Tracking> {
        TrackingImpl()
    }
    viewModel { MainViewModel(repository = get(), tracking = get()) }
    viewModel { DetailViewModel(repository = get()) }
}
