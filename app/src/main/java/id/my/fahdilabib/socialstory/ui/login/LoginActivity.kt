package id.my.fahdilabib.socialstory.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import id.my.fahdilabib.socialstory.R
import id.my.fahdilabib.socialstory.data.local.UserPreference
import id.my.fahdilabib.socialstory.data.remote.ApiConfig
import id.my.fahdilabib.socialstory.data.remote.Result
import id.my.fahdilabib.socialstory.databinding.ActivityLoginBinding
import id.my.fahdilabib.socialstory.ui.home.HomeActivity
import id.my.fahdilabib.socialstory.utils.MyEditText
import id.my.fahdilabib.socialstory.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ApiConfig().getApiService(),
                UserPreference.getInstance(dataStore)
            )
        )[LoginViewModel::class.java]
    }

    private fun setupAction() {
        viewModel.login.observe(this) {
            when (it) {
                is Result.Loading -> {
                    binding.loginButton.showProgress {
                        buttonTextRes = R.string.loading
                        progressColor = Color.WHITE
                    }
                }
                is Result.Error -> {
                    binding.loginButton.hideProgress(R.string.login)
                    Snackbar.make(binding.root, "Error: ${it.error}", Snackbar.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    binding.loginButton.hideProgress(R.string.login)
                    startActivity(Intent(this, HomeActivity::class.java))
                    finishAffinity()
                }
            }
        }

        binding.apply {
            val editLayouts = arrayOf(
                arrayOf(emailEditText, emailEditTextLayout),
                arrayOf(passwordEditText, passwordEditTextLayout),
            )

            editLayouts.iterator().forEach {
                val editText = it[0] as MyEditText
                val editLayout = it[1] as TextInputLayout

                editText.initOnErrorInterface(object : MyEditText.OnErrorInterface {
                    override fun onError(message: String?) {
                        editLayout.error = message

                        if (editLayout.childCount == 2) {
                            when (message) {
                                null -> editLayout.getChildAt(1).visibility = View.GONE
                                else -> editLayout.getChildAt(1).visibility = View.VISIBLE
                            }
                        }
                    }
                })
            }

            loginButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                viewModel.doLogin(email, password)
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, message, emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, login)
            startDelay = 500
        }.start()
    }
}