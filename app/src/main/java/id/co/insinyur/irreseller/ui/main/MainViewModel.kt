package id.co.insinyur.irreseller.ui.main

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import id.co.insinyur.irreseller.base.BaseViewModel
import id.co.insinyur.irreseller.repository.LoginRepository
import id.co.insinyur.irreseller.db.entity.JWT
import id.co.insinyur.irreseller.model.Balance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.net.ConnectException

class MainViewModel(private val repository: LoginRepository): BaseViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _loading = MutableLiveData<Int>()
    val loading: LiveData<Int> = _loading

    private val _balance = MutableLiveData<Balance>()
    val balance: LiveData<Balance> = _balance

    val stateLogin: LiveData<JWT?> = liveData {
        repository.checkLogin().onStart {
                // show progress bar
        }.onCompletion {
            if (it != null) { // show message
            }
        // hide progress bar
        }.collect { emit(it) }
    }

    suspend fun getBalance(path: String) {
        repository.getBalance(path)
            .onStart {  _loading.value = View.VISIBLE }
            .catch { e -> _message.value = e.localizedMessage }
            .onCompletion { _loading.value = View.INVISIBLE }
            .collect { _balance.value = it }
    }

    fun logout() = CoroutineScope(Dispatchers.Main).launch{ repository.logout() }
}