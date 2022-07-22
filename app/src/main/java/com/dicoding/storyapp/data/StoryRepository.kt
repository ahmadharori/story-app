package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import com.dicoding.storyapp.api.ApiService
import com.dicoding.storyapp.api.response.StoriesResponse
import com.dicoding.storyapp.api.response.StoryItem
import com.dicoding.storyapp.database.Story
import com.dicoding.storyapp.database.StoryDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService) {

    private val result = MediatorLiveData<Result<List<StoryItem>>>()

    private val _storiesWithLocationList = MutableLiveData<List<StoryItem>>()
    private val storiesWithLocationList : LiveData<List<StoryItem>> = _storiesWithLocationList

    fun getStories(token: String): LiveData<PagingData<Story>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    fun getStoriesWithLocation(token: String) : LiveData<Result<List<StoryItem>>> {
        result.value = Result.Loading
        val client = apiService.storiesWithLocation("Bearer $token", 1, 50, 1)
        client.enqueue(object : Callback<StoriesResponse> {
            override fun onResponse(
                call: Call<StoriesResponse>,
                response: Response<StoriesResponse>
            ) {
                if (response.isSuccessful) {
                    _storiesWithLocationList.value = (response.body()?.listStory as List<StoryItem>)
                    result.addSource(storiesWithLocationList) { listStory ->
                        result.value = Result.Success(listStory)
                    }
                }
            }

            override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }

        })
        return result
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            apiService: ApiService
        ) : StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, apiService)
            }.also { instance = it }
    }
}