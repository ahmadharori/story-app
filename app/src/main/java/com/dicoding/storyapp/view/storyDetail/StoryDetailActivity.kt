package com.dicoding.storyapp.view.storyDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.database.Story
import com.dicoding.storyapp.databinding.ActivityStoryDetailBinding
import com.dicoding.storyapp.model.UserPreference
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.maps.MapsActivity
import com.dicoding.storyapp.view.welcome.WelcomeActivity


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding
    private lateinit var storyDetailViewModel: StoryDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            R.id.logout -> {
                storyDetailViewModel.logout()
                true
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> true
        }
    }

    private fun setupViewModel() {
        storyDetailViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[StoryDetailViewModel::class.java]

        storyDetailViewModel.getUser().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun setupData() {
        val story = intent.getParcelableExtra<Story>("Story") as Story
        Glide.with(applicationContext)
            .load(story.photoUrl)
            .into(binding.photo)
        binding.apply {
            name.text = story.name
            description.text = story.description
        }
    }
}