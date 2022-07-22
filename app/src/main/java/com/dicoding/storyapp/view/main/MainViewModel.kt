package com.dicoding.storyapp.view.main

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import com.dicoding.storyapp.api.response.StoryItem
import com.dicoding.storyapp.database.Story
import com.dicoding.storyapp.data.StoryRepository
import com.dicoding.storyapp.model.UserModel
import com.dicoding.storyapp.model.UserPreference
import kotlinx.coroutines.launch

class MainViewModel(private val pref: UserPreference, private val storyRepository: StoryRepository) : ViewModel() {


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var stories: LiveData<PagingData<Story>>? = null

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun getStories(token: String) {
        this.stories = storyRepository.getStories(token)
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}