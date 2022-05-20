package id.my.fahdilabib.socialstory.ui.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import coil.load
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import id.my.fahdilabib.socialstory.R
import id.my.fahdilabib.socialstory.adapter.StoryRecyclerAdapter
import id.my.fahdilabib.socialstory.data.local.UserModel
import id.my.fahdilabib.socialstory.data.local.UserPreference
import id.my.fahdilabib.socialstory.data.remote.ApiConfig
import id.my.fahdilabib.socialstory.data.remote.responses.StoryResponse
import id.my.fahdilabib.socialstory.databinding.ActivityHomeBinding
import id.my.fahdilabib.socialstory.databinding.BsdItemDetailBinding
import id.my.fahdilabib.socialstory.ui.add.AddActivity
import id.my.fahdilabib.socialstory.ui.maps.MapsActivity
import id.my.fahdilabib.socialstory.ui.welcome.WelcomeActivity
import id.my.fahdilabib.socialstory.utils.ViewModelFactory
import id.my.fahdilabib.socialstory.utils.showGroupViews
import id.my.fahdilabib.socialstory.utils.uctHumanize
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private var adapter = StoryRecyclerAdapter()
    private var doubleBackToExitPressedOnce = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Snackbar.make(binding.root, "Please click BACK again to exit", Snackbar.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        )[HomeViewModel::class.java]
    }

    private fun setupAction() {
        viewModel.user.observe(this) {
            if (it.token.isNotEmpty()) {
                setupView(it)
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finishAffinity()
            }
        }

        binding.apply {
            addButton.setOnClickListener {
                resultLauncher.launch(Intent(this@HomeActivity, AddActivity::class.java))
            }
            mapsButton.setOnClickListener {
                startActivity(Intent(this@HomeActivity, MapsActivity::class.java))
            }
            logoutButton.setOnClickListener {
                viewModel.logout()
            }
        }

        adapter.setOnItemClickCallback(object: StoryRecyclerAdapter.OnItemClickCallback {
            override fun onItemClicked(it: View, story: StoryResponse) {
                val bottomSheetDialog = BottomSheetDialog(binding.root.context)
                val view = BsdItemDetailBinding.inflate(layoutInflater)

                view.apply {
                    itemImage.load(story.photoUrl) {
                        crossfade(true)
                        placeholder(R.drawable.image_loading)
                    }

                    itemDesc.text = story.description
                    itemName.text = story.name
                    itemDate.text = uctHumanize(story.createdAt)
                }

                bottomSheetDialog.setContentView(view.root)
                bottomSheetDialog.show()
            }
        })
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            adapter.refresh()

            lifecycleScope.launch {
                adapter.loadStateFlow
                    .collect { loadStates ->
                        if (!(loadStates.refresh is LoadState.Loading))
                            binding.rvStories.smoothScrollToPosition(0)
                    }
            }
        }
    }

    private fun setupView(user: UserModel) {
        binding.apply {
            showGroupViews()

            textView.text = user.name
            rvStories.adapter = adapter
            rvStories.layoutManager = StaggeredGridLayoutManager(
                3, StaggeredGridLayoutManager.VERTICAL
            )
        }

        viewModel.obsStories.observe(this) { stories ->
            adapter.submitData(lifecycle, stories)
        }
    }
}