package id.my.fahdilabib.socialstory.ui.maps

import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import id.my.fahdilabib.socialstory.data.local.UserModel
import id.my.fahdilabib.socialstory.data.local.UserPreference
import id.my.fahdilabib.socialstory.data.remote.ApiService
import id.my.fahdilabib.socialstory.data.remote.Result
import id.my.fahdilabib.socialstory.data.remote.responses.BaseResponse
import id.my.fahdilabib.socialstory.data.remote.responses.StoriesResponse
import retrofit2.HttpException

class MapsViewModel(
    private val apiService: ApiService,
    private val preference: UserPreference
): ViewModel() {
    var user : LiveData<UserModel> = preference.getUser().asLiveData()

    val stories : LiveData<Result<StoriesResponse>> = Transformations.switchMap(user) {
        liveData {
            emit(Result.Loading)

            try {
                val response = apiService.stories("Bearer $it.token", size = 50, location = 1)
                emit(Result.Success(response))
            } catch (e: Exception) {
                if (e is HttpException) {
                    try {
                        val body = e.response()!!.errorBody()
                        val gson = Gson()
                        val adapter: TypeAdapter<BaseResponse> = gson.getAdapter(BaseResponse::class.java)
                        val errorParser: BaseResponse = adapter.fromJson(body?.string())

                        emit(e.message?.let { Result.Error(errorParser.message) })
                    } catch (e: Exception) {
                        emit(e.message?.let { Result.Error(e.message.toString()) })
                    }
                } else {
                    emit(e.message?.let { Result.Error(it) })
                }
            }
        }
    }
}