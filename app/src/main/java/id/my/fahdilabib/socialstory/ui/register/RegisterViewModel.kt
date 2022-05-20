package id.my.fahdilabib.socialstory.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import id.my.fahdilabib.socialstory.data.local.UserPreference
import id.my.fahdilabib.socialstory.data.remote.ApiService
import id.my.fahdilabib.socialstory.data.remote.Result
import id.my.fahdilabib.socialstory.data.remote.request.RegisterRequest
import id.my.fahdilabib.socialstory.data.remote.responses.BaseResponse
import id.my.fahdilabib.socialstory.ui.login.LoginViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException


class RegisterViewModel(
    private val apiService: ApiService,
    private val preference: UserPreference
): LoginViewModel(apiService, preference) {
    private val _register = MutableLiveData<Result<BaseResponse>>()
    val register: LiveData<Result<BaseResponse>> = _register

    fun doRegister(name: String, email: String, password: String) {
        viewModelScope.launch {
            _register.postValue(Result.Loading)

            try {
                val response = apiService.register(RegisterRequest(name, email, password))
                _register.postValue(Result.Success(response))
                doLogin(email, password)
            } catch (e: Exception) {
                if (e is HttpException) {
                    try {
                        val body = e.response()!!.errorBody()
                        val gson = Gson()
                        val adapter: TypeAdapter<BaseResponse> = gson.getAdapter(BaseResponse::class.java)
                        val errorParser: BaseResponse = adapter.fromJson(body?.string())

                        _register.postValue(e.message?.let { Result.Error(errorParser.message) })
                    } catch (e: Exception) {
                        _register.postValue(e.message?.let { Result.Error(e.message.toString()) })
                    }
                } else {
                    _register.postValue(e.message?.let { Result.Error(it) })
                }
            }
        }
    }
}