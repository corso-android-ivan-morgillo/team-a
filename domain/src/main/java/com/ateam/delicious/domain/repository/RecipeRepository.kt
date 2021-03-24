package com.ateam.delicious.domain.repository

interface RecipesRepository : RecipeByIngredients, RecipeByCategory, RecipeByArea {}

class RecipeRepositoryImpl(
    private val ingredientsRepository: RecipeByIngredientImpl,
    private val categoriesRepository: RecipeByCategoryImpl,
    private val areaRepository: RecipeByAreaImpl
) : RecipesRepository,
    RecipeByIngredients by ingredientsRepository,
    RecipeByCategory by categoriesRepository,
    RecipeByArea by areaRepository {}
