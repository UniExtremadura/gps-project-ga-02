package com.unex.asee.ga02.beergo.view.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.unex.asee.ga02.beergo.BeerGoApplication
import com.unex.asee.ga02.beergo.model.Beer
import com.unex.asee.ga02.beergo.model.User
import com.unex.asee.ga02.beergo.repository.FavRepository
import kotlinx.coroutines.launch

class ShowBeerViewModel(
    private val favRepository: FavRepository
) : ViewModel() {

    val favBeers = favRepository.favBeers
    private val _isFavourite = MutableLiveData<Boolean>(null)
    val isFavourite: LiveData<Boolean>
        get() = _isFavourite
    var user: User? = null
        set(value) {
            field = value
            favRepository.setUserid(value!!.userId)
        }
    var beer: Beer? = null
        set(value) {
            field = value
            checkIfFavourite()
        }


    private fun checkIfFavourite() {
        viewModelScope.launch {
            Log.d("ObservationFavorite", "checkIfFavourite: ${favBeers.value}")
            _isFavourite.value = favBeers.value?.contains(beer) == true
        }
    }

    fun addFav() {
        viewModelScope.launch {
            favRepository.addFav(user!!.userId, beer!!.beerId)
        }
    }

    fun deleteFav() {
        viewModelScope.launch {
            favRepository.deleteFav(user!!.userId, beer!!.beerId)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T { // Get the Application object from extras

                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ShowBeerViewModel(
                    (application as BeerGoApplication).appContainer.favRepository,
                ) as T
            }
        }
    }
}