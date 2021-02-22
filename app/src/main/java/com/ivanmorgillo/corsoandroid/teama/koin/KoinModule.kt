package com.ivanmorgillo.corsoandroid.teama

import com.ivanmorgillo.corsoandroid.teama.category.CategoryRepository
import com.ivanmorgillo.corsoandroid.teama.category.CategoryRepositoryImpl
import com.ivanmorgillo.corsoandroid.teama.category.CategoryViewModel
import com.ivanmorgillo.corsoandroid.teama.detail.DetailViewModel
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetailsRepository
import com.ivanmorgillo.corsoandroid.teama.detail.RecipeDetailsRepositoryImpl
import com.ivanmorgillo.corsoandroid.teama.network.NetworkAPI
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        NetworkAPI()
    }
    single<RecipesRepository> {
        RecipeRepositoryImpl(api = get())
    }
    single<RecipeDetailsRepository> {
        RecipeDetailsRepositoryImpl(api = get())
    }
    single<CategoryRepository> {
        CategoryRepositoryImpl(api = get())
    }
    single<Tracking> {
        TrackingImpl()
    }
    viewModel { CategoryViewModel(repository = get(), tracking = get()) }
    viewModel { MainViewModel(repository = get(), tracking = get()) }
    viewModel { DetailViewModel(repository = get()) }
}
