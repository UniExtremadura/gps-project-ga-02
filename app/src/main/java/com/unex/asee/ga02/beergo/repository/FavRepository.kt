package com.unex.asee.ga02.beergo.repository

import com.unex.asee.ga02.beergo.database.UserDao
import com.unex.asee.ga02.beergo.model.Beer
import com.unex.asee.ga02.beergo.model.UserFavouriteBeerCrossRef

class FavRepository (private val userDao: UserDao) {
    suspend fun addFav(userId: Long, beerId: Long) {
        userDao.insertAndRelateUserFavouriteBeer(userId, beerId)
    }
    suspend fun loadFavs(userId: Long): List<Beer> {
        return userDao.getFavouritesBeersByUserId(userId)
    }
    suspend fun deleteFav(userId: Long, beerId: Long) {
        val uFb = UserFavouriteBeerCrossRef(userId, beerId)
        userDao.deleteUserFavouriteBeer(uFb)
    }
    suspend fun isFavorite(userId: Long, beerId: Long): Boolean {
        val beers = userDao.getFavouritesBeersByUserId(userId)
        var isFav = false
        beers.forEach {
            if (it.beerId == beerId) {
                isFav = true
            }
        }
        return isFav
    }
}