package id.my.fahdilabib.socialstory.ui.maps

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import id.my.fahdilabib.socialstory.R
import id.my.fahdilabib.socialstory.data.local.UserPreference
import id.my.fahdilabib.socialstory.data.remote.ApiConfig
import id.my.fahdilabib.socialstory.data.remote.Result
import id.my.fahdilabib.socialstory.databinding.ActivityMapsBinding
import id.my.fahdilabib.socialstory.ui.welcome.WelcomeActivity
import id.my.fahdilabib.socialstory.utils.TAG
import id.my.fahdilabib.socialstory.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        )

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ApiConfig().getApiService(),
                UserPreference.getInstance(dataStore)
            )
        )[MapsViewModel::class.java]
    }

    private fun setupAction() {
        viewModel.user.observe(this) {
            if (it.token.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finishAffinity()
            }
        }

        viewModel.stories.observe(this) {
            when (it) {
                is Result.Loading -> {
                    Snackbar.make(binding.root, "Loading: Please wait.", Snackbar.LENGTH_SHORT).show()
                }
                is Result.Error -> {
                    Snackbar.make(binding.root, "Error: ${it.error}", Snackbar.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    mMap.clear()

                    it.data.listStory.forEach {
                        val latLng = LatLng(it.lat, it.lon)
                        val markerOptions = MarkerOptions()
                        markerOptions.position(latLng)
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        markerOptions.title(it.name)

                        mMap.addMarker(markerOptions)
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val cameraUpdateFactory = CameraUpdateFactory
            .newLatLngZoom(LatLng(-4.4082464, 119.7275602), 1.0F)

        mMap = googleMap
        mMap.moveCamera(cameraUpdateFactory)

        mMap.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }
            true
        }

        setMapStyle()
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }
}