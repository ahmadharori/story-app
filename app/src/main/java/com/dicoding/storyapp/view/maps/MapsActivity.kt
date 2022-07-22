package com.dicoding.storyapp.view.maps

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.Result

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.storyapp.databinding.ActivityMapsBinding
import com.dicoding.storyapp.model.UserModel
import com.dicoding.storyapp.model.UserPreference
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.welcome.WelcomeActivity
import com.google.android.gms.maps.model.MapStyleOptions

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var user : UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        supportActionBar?.title = getString(R.string.maps)

        mapsViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this@MapsActivity)
        )[MapsViewModel::class.java]

        mapsViewModel.getUser().observe(this) {user ->
            if (user.isLogin) {
                this.user = user
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mapsViewModel.getStoriesWithLocation(user.token).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Success -> {
                        val storiesList = result.data
                        storiesList.forEach { story ->
                            val location = story.lat?.let { lat -> story.lon?.let { lon ->
                                LatLng(lat,
                                    lon
                                )
                            } }
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(location!!)
                                    .title(story.name)
                            )
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                        }
                    }
                }
            }
        }
        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Toast.makeText(this@MapsActivity, "Style parsing failed", Toast.LENGTH_SHORT)
            }
        } catch (exception: Resources.NotFoundException) {
            Toast.makeText(this@MapsActivity, "Can't find style. Error: $exception", Toast.LENGTH_SHORT)
        }
    }
}