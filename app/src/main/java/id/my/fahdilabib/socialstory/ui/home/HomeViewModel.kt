package id.my.fahdilabib.socialstory.ui.home

import androidx.lifecycle.*
import androidx.paging.*
import id.my.fahdilabib.socialstory.adapter.StoryPagingSource
import id.my.fahdilabib.socialstory.data.local.UserModel
import id.my.fahdilabib.socialstory.data.local.UserPreference
import id.my.fahdilabib.socialstory.data.remote.ApiService
import id.my.fahdilabib.socialstory.data.remote.responses.StoryResponse
import kotlinx.coroutines.launch

class HomeViewModel(
    private val apiService: ApiService,
    private val preference: UserPreference
): ViewModel() {
    var user : LiveData<UserModel> = preference.getUser().asLiveData()

    val obsStories : LiveData<PagingData<StoryResponse>> = Transformations.switchMap(user) {
        return@switchMap Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, "Bearer ${it.token}")
            }
        ).liveData.cachedIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            preference.destroy()
        }
    }
}