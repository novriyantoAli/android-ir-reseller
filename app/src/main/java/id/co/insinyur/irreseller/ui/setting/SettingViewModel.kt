package id.co.insinyur.irreseller.ui.setting

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import id.co.insinyur.irreseller.R
import id.co.insinyur.irreseller.db.entity.App
import id.co.insinyur.irreseller.repository.ApplicationRepository
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

class SettingViewModel(private val repository: ApplicationRepository, private val app: Application): ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    val stateApp: LiveData<App?> = liveData {

        repository.load().onStart { _message.value = app.resources.getString(R.string.saving_data)
        }.onCompletion {
            if (it != null) {
                _success.value = false
                _message.value = it.localizedMessage
                return@onCompletion
            }
            _message.value = app.resources.getString(R.string.save_complete)
            _success.value = true
        }.collect { emit(it) }
    }

    suspend fun save(host: String, port: String) { repository.save(host, port) }
}