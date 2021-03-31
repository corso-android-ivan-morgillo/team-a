package com.ivanmorgillo.corsoandroid.teama.koin

import com.ateam.delicious.domain.repository.AreaRepository
import com.ateam.delicious.domain.repository.AreaRepositoryImpl
import com.ateam.delicious.domain.repository.AuthenticationManager
import com.ateam.delicious.domain.repository.AuthenticationManagerImpl
import com.ateam.delicious.domain.repository.CategoryRepository
import com.ateam.delicious.domain.repository.CategoryRepositoryImpl
import com.ateam.delicious.domain.repository.FavouriteRepository
import com.ateam.delicious.domain.repository.FavouriteRepositoryImpl
import com.ateam.delicious.domain.repository.RecipeByAreaImpl
import com.ateam.delicious.domain.repository.RecipeByCategoryImpl
import com.ateam.delicious.domain.repository.RecipeByIngredientImpl
import com.ateam.delicious.domain.repository.RecipeDetailsRepository
import com.ateam.delicious.domain.repository.RecipeDetailsRepositoryImpl
import com.ateam.delicious.domain.repository.RecipeRepositoryImpl
import com.ateam.delicious.domain.repository.RecipesRepository
import com.ateam.delicious.domain.repository.SettingsRepository
import com.ateam.delicious.domain.repository.SettingsRepositoryImpl
import com.ivanmorgillo.corsoandroid.teama.MainViewModel
import com.ivanmorgillo.corsoandroid.teama.Tracking
import com.ivanmorgillo.corsoandroid.teama.TrackingImpl
import com.ivanmorgillo.corsoandroid.teama.area.AreaViewModel
import com.ivanmorgillo.corsoandroid.teama.category.CategoryViewModel
import com.ivanmorgillo.corsoandroid.teama.detail.DetailViewModel
import com.ivanmorgillo.corsoandroid.teama.favourite.FavouriteViewModel
import com.ivanmorgillo.corsoandroid.teama.home.HomeViewModel
import com.ivanmorgillo.corsoandroid.teama.home.IngredientViewModel
import com.ivanmorgillo.corsoandroid.teama.recipe.RecipeViewModel
import com.ivanmorgillo.corsoandroid.teama.recipe.area.RecipeAreaViewModel
import com.ivanmorgillo.corsoandroid.teama.settings.SettingsViewModel
import com.ivanmorgillo.corsoandroid.teama.shoppinglist.ShoppingListRepository
import com.ivanmorgillo.corsoandroid.teama.shoppinglist.ShoppingListRepositoryImpl
import com.ivanmorgillo.corsoandroid.teama.shoppinglist.ShoppingListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<RecipesRepository> {
        RecipeRepositoryImpl(
            ingredientsRepository = RecipeByIngredientImpl(api = get()),
            categoriesRepository = RecipeByCategoryImpl(api = get()),
            areaRepository = RecipeByAreaImpl(api = get())
        )
    }

    single<RecipeDetailsRepository> {
        RecipeDetailsRepositoryImpl(api = get())
    }
    single<CategoryRepository> {
        CategoryRepositoryImpl(api = get())
    }
    single<AreaRepository> {
        AreaRepositoryImpl(api = get())
    }
    single<FavouriteRepository> {
        FavouriteRepositoryImpl(fireStoreDatabase = get(), authManager = get())
    }
    single<AuthenticationManager> {
        AuthenticationManagerImpl()
    }
    single<SettingsRepository> {
        SettingsRepositoryImpl(context = androidContext())
    }
    single<ShoppingListRepository> {
        ShoppingListRepositoryImpl(fireStoreDatabase = get(), authManager = get())
    }
    single<Tracking> {
        TrackingImpl()
    }
    viewModel { MainViewModel(repository = get(), tracking = get(), authManager = get()) }
    viewModel { CategoryViewModel(repository = get(), tracking = get()) }
    viewModel { RecipeViewModel(repository = get(), tracking = get()) }
    viewModel {
        DetailViewModel(
            repository = get(),
            tracking = get(),
            favouritesRepository = get(),
            authManager = get()
        )
    }
    viewModel { FavouriteViewModel(repository = get(), tracking = get(), settingsRepository = get()) }
    viewModel { SettingsViewModel(repository = get(), tracking = get()) }
    viewModel { HomeViewModel(tracking = get()) }
    viewModel { AreaViewModel(tracking = get(), repository = get()) }
    viewModel { IngredientViewModel(api = get()) }
    viewModel { RecipeAreaViewModel(repository = get(), tracking = get()) }
    viewModel { ShoppingListViewModel(repository = get()) }
}
