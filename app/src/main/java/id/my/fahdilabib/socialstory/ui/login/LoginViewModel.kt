package id.my.fahdilabib.socialstory.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import id.my.fahdilabib.socialstory.data.local.UserModel
import id.my.fahdilabib.socialstory.data.local.UserPreference
import id.my.fahdilabib.socialstory.data.remote.ApiService
import id.my.fahdilabib.socialstory.data.remote.Result
import id.my.fahdilabib.socialstory.data.remote.request.LoginRequest
import id.my.fahdilabib.socialstory.data.remote.responses.BaseResponse
import id.my.fahdilabib.socialstory.data.remote.responses.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

open class LoginViewModel (
    private val apiService: ApiService,
    private val preference: UserPreference
): ViewModel() {
    private val _login = MutableLiveData<Result<LoginResponse>>()
    val login: LiveData<Result<LoginResponse>> = _login

    fun doLogin(email: String, password: String) {
        viewModelScope.launch {
            _login.postValue(Result.Loading)

            try {
                val response = apiService.login(LoginRequest(email, password))
                _login.postValue(Result.Success(response))

                response.loginResult.let {
                    preference.saveUser(
                        UserModel(
                        it.userId, it.name, it.token
                    )
                    )
                }
            } catch (e: Exception) {
                if (e is HttpException) {
                    try {
                        val body = e.response()!!.errorBody()
                        val gson = Gson()
                        val adapter: TypeAdapter<BaseResponse> = gson.getAdapter(BaseResponse::class.java)
                        val errorParser: BaseResponse = adapter.fromJson(body?.string())

                        _login.postValue(e.message?.let { Result.Error(errorParser.message) })
                    } catch (e: Exception) {
                        _login.postValue(e.message?.let { Result.Error(e.message.toString()) })
                    }
                } else {
                    _login.postValue(e.message?.let { Result.Error(it) })
                }
            }
        }
    }
}