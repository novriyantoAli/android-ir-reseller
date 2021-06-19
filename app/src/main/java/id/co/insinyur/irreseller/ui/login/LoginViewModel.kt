package id.co.insinyur.irreseller.ui.login

import android.util.Patterns
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.base.BaseViewModel
import id.co.insinyur.irreseller.model.LoginRequest
import id.co.insinyur.irreseller.repository.LoginRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.net.ConnectException

class LoginViewModel(private val repository: LoginRepository) : BaseViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _loading = MutableLiveData<Int>()
    val loading: LiveData<Int> = _loading

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    suspend fun login(path: String, request: LoginRequest) {
        repository.postLogin(path, request)
            .onStart { _loading.value = View.VISIBLE
            }.catch { e ->
                e.printStackTrace()
                _message.value = e.message
            }.onCompletion { _loading.value = View.INVISIBLE
            }.collect { _message.value = "Selamat Datang ${it.username}" }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 3
    }
}