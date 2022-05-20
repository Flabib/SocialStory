package id.my.fahdilabib.socialstory.ui.register

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
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import id.my.fahdilabib.socialstory.R
import id.my.fahdilabib.socialstory.data.local.UserPreference
import id.my.fahdilabib.socialstory.data.remote.ApiConfig
import id.my.fahdilabib.socialstory.data.remote.Result
import id.my.fahdilabib.socialstory.databinding.ActivityRegisterBinding
import id.my.fahdilabib.socialstory.ui.home.HomeActivity
import id.my.fahdilabib.socialstory.utils.MyEditText
import id.my.fahdilabib.socialstory.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        bindProgressButton(binding.registerButton)

        binding.registerButton.attachTextChangeAnimator()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                ApiConfig().getApiService(),
                UserPreference.getInstance(dataStore)
            )
        )[RegisterViewModel::class.java]
    }

    private fun setupAction() {
        viewModel.register.observe(this) {
            when (it) {
                is Result.Loading -> {
                    binding.registerButton.showProgress {
                        buttonTextRes = R.string.loading
                        progressColor = Color.WHITE
                    }
                }
                is Result.Error -> {
                    binding.registerButton.hideProgress(R.string.register)
                    Snackbar.make(binding.root, "Error: ${it.error}", Snackbar.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    binding.registerButton.hideProgress(R.string.register)
                    Snackbar.make(binding.root, "Success: ${it.data.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.login.observe(this) {
            if (it is Result.Success) {
                startActivity(Intent(this, HomeActivity::class.java))
                finishAffinity()
            }
        }

        binding.apply {
            val editLayouts = arrayOf(
                arrayOf(nameEditText, nameEditTextLayout),
                arrayOf(emailEditText, emailEditTextLayout),
                arrayOf(passwordEditText, passwordEditTextLayout),
            )

            editLayouts.iterator().forEach {
                val editText = it[0] as MyEditText
                val editLayout = it[1] as TextInputLayout

                editText.initOnErrorInterface(object: MyEditText.OnErrorInterface {
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

            registerButton.setOnClickListener {
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                viewModel.doRegister(name, email, password)
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
        val nameTextView = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                register
            )
            startDelay = 500
        }.start()
    }
}