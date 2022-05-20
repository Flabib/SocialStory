package id.my.fahdilabib.socialstory.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.my.fahdilabib.socialstory.data.local.UserPreference
import id.my.fahdilabib.socialstory.data.remote.ApiService
import id.my.fahdilabib.socialstory.ui.add.AddViewModel
import id.my.fahdilabib.socialstory.ui.home.HomeViewModel
import id.my.fahdilabib.socialstory.ui.login.LoginViewModel
import id.my.fahdilabib.socialstory.ui.maps.MapsViewModel
import id.my.fahdilabib.socialstory.ui.register.RegisterViewModel

class ViewModelFactory(private val apiService: ApiService, private val preference: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(apiService, preference) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(apiService, preference) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(apiService, preference) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(apiService, preference) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(apiService, preference) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}