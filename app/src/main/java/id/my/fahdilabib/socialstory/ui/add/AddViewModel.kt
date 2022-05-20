package id.my.fahdilabib.socialstory.ui.add

import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import id.my.fahdilabib.socialstory.data.local.UserModel
import id.my.fahdilabib.socialstory.data.local.UserPreference
import id.my.fahdilabib.socialstory.data.remote.ApiService
import id.my.fahdilabib.socialstory.data.remote.Result
import id.my.fahdilabib.socialstory.data.remote.responses.BaseResponse
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AddViewModel(
    private val apiService: ApiService,
    preference: UserPreference
): ViewModel() {
    private val _loc = MutableLiveData<LatLng>()
    var user : LiveData<UserModel> = preference.getUser().asLiveData()

    private val _upload = MutableLiveData<Result<BaseResponse>>()
    val upload: LiveData<Result<BaseResponse>> = _upload

    fun setLoc(loc: LatLng) {
        _loc.postValue(loc)
    }

    fun doUpload(imageMultipart: MultipartBody.Part, description: String) {
        viewModelScope.launch {
            _upload.postValue(Result.Loading)

            try {
                val response = apiService.uploadImage(
                    "Bearer ${user.value?.token}",
                    imageMultipart,
                    description.toRequestBody("text/plain".toMediaType()),
                    _loc.value?.latitude.toString().toRequestBody("text/plain".toMediaType()),
                    _loc.value?.longitude.toString().toRequestBody("text/plain".toMediaType()),
                )

                _upload.postValue(Result.Success(response))
            } catch (e: Exception) {
                if (e is HttpException) {
                    try {
                        val body = e.response()!!.errorBody()
                        val gson = Gson()
                        val adapter: TypeAdapter<BaseResponse> = gson.getAdapter(BaseResponse::class.java)
                        val errorParser: BaseResponse = adapter.fromJson(body?.string())

                        _upload.postValue(e.message?.let { Result.Error(errorParser.message) })
                    } catch (e: Exception) {
                        _upload.postValue(e.message?.let { Result.Error(e.message.toString()) })
                    }
                } else {
                    _upload.postValue(e.message?.let { Result.Error(it) })
                }
            }
        }
    }
}