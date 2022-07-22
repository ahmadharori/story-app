package com.dicoding.storyapp.view.main

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.adapter.LoadingStateAdapter
import com.dicoding.storyapp.databinding.ActivityMainBinding
import com.dicoding.storyapp.model.UserModel
import com.dicoding.storyapp.model.UserPreference
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.adapter.StoryListAdapter
import com.dicoding.storyapp.view.maps.MapsActivity
import com.dicoding.storyapp.view.addStory.AddStoryActivity
import com.dicoding.storyapp.view.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var user: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.story_list_title)

        setupViewModel()
        setupAction()
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
                mainViewModel.logout()
                true
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> true
        }
    }

    private fun setupAction() {
        binding.addStoryButton.setOnClickListener { view ->
            if (view.id == R.id.addStoryButton) {
                startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
            }
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this@MainActivity)
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                this.user = user
                binding.welcomeUser.text = getString(R.string.welcome_user, user.name)
                setupRecyclerView()
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }
    }

    private fun setupRecyclerView() {
        mainViewModel.getStories(user.token)

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager

        val adapter = StoryListAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.stories?.observe(this) { listStory ->
            adapter.submitData(lifecycle, listStory)
        }
    }

    private fun showLoading(isLoading : Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}