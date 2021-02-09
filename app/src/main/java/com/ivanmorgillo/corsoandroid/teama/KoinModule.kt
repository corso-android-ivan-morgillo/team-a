package com.ivanmorgillo.corsoandroid.teama

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<RecipesRepository> {
        RecipeRepositoryImpl()
    }
    viewModel { MainViewModel(repository = get()) }
}
