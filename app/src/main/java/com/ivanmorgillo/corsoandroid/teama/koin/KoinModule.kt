package com.ivanmorgillo.corsoandroid.teama.koin

import com.ateam.delicious.domain.CategoryRepository
import com.ateam.delicious.domain.CategoryRepositoryImpl
import com.ateam.delicious.domain.FavouriteRepository
import com.ateam.delicious.domain.FavouriteRepositoryImpl
import com.ateam.delicious.domain.RecipeDetailsRepository
import com.ateam.delicious.domain.RecipeDetailsRepositoryImpl
import com.ateam.delicious.domain.RecipeRepositoryImpl
import com.ateam.delicious.domain.RecipesRepository
import com.ateam.delicious.domain.SettingsRepository
import com.ateam.delicious.domain.SettingsRepositoryImpl
import com.ivanmorgillo.corsoandroid.teama.MainViewModel
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.TrackingImpl
import com.ivanmorgillo.corsoandroid.teama.category.CategoryViewModel
import com.ivanmorgillo.corsoandroid.teama.detail.DetailViewModel
import com.ivanmorgillo.corsoandroid.teama.favourite.FavouriteViewModel
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeViewModel
import com.ivanmorgillo.corsoandroid.teama.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<RecipesRepository> {
        RecipeRepositoryImpl(api = get())
    }
    single<RecipeDetailsRepository> {
        RecipeDetailsRepositoryImpl(api = get())
    }
    single<CategoryRepository> {
        CategoryRepositoryImpl(api = get())
    }
    single<FavouriteRepository> {
        FavouriteRepositoryImpl(fireStoreDatabase = get())
    }



    single<SettingsRepository> {
        SettingsRepositoryImpl(context = androidContext())
    }
    single<Tracking> {
        TrackingImpl()
    }


    viewModel { MainViewModel(repository = get(), tracking = get()) }
    viewModel { CategoryViewModel(repository = get(), tracking = get()) }
    viewModel { RecipeViewModel(repository = get(), tracking = get()) }
    viewModel { DetailViewModel(repository = get(), tracking = get(), favouritesRepository = get()) }
    viewModel { FavouriteViewModel(repository = get(), tracking = get(), settingsRepository = get()) }
    viewModel { SettingsViewModel(repository = get(), tracking = get()) }
}
