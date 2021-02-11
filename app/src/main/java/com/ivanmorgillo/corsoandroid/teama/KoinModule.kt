package com.ivanmorgillo.corsoandroid.teama

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

    single<Tracking> {
        TrackingImpl()
    }
    viewModel { MainViewModel(repository = get(), tracking = get()) }
}
