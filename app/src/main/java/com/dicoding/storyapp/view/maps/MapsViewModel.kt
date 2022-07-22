package com.dicoding.storyapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.model.UserModel
import com.dicoding.storyapp.model.UserPreference

class MapsViewModel(private val pref: UserPreference,
                    private val storyRepository: StoryRepository) : ViewModel() {

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getStoriesWithLocation(token: String) = storyRepository.getStoriesWithLocation(token)
}