package com.example.ffixvalert

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.lifecycle.*
import com.example.ffixvalert.constant.PrefKeys
import com.example.ffixvalert.extension.isServiceRunning
import com.example.ffixvalert.model.AllServerStatus
import com.example.ffixvalert.model.ServerStatus
import com.example.ffixvalert.repository.Repository
import com.example.ffixvalert.service.FetchDataService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "FFixvAlertViewModel"

class FFixvAlertViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository = Repository()

    var firstTimeInit: Boolean = true
    private var serverSet: MutableSet<String> = emptySet<String>().toMutableSet()

    private val _response = MutableLiveData<AllServerStatus>()
    val response: LiveData<AllServerStatus> = _response

    private val _currentDataCenterLabel = MutableLiveData<String>()
    val currentDataCenterLabel: LiveData<String> = _currentDataCenterLabel

    private val _countDownTimer = MutableLiveData<Int>()
    val countDownTimer: LiveData<Int> = _countDownTimer

    private val _timerIcon = MutableLiveData<Int>()
    val timerIcon: LiveData<Int> = _timerIcon

    private val _isConnectToInternet = MutableLiveData<Boolean>()
    val isConnectToInternet: LiveData<Boolean> = _isConnectToInternet

    private val _isServiceRunning = MutableLiveData<Boolean>()
    val isServiceRunning: LiveData<Boolean> = _isServiceRunning

    private val prefs =
        app.getSharedPreferences(PrefKeys.PACKAGE_NAME, Context.MODE_PRIVATE)

    init {
        serverSet = prefs.getStringSet(PrefKeys.SERVER_SET, null) as MutableSet<String>
    }

    fun getServerStatus() {
        viewModelScope.launch {
            if (repository.getServerStatus().isSuccessful &&
                repository.getServerStatus().code() != 204
            ) {
                Log.d(TAG, "${repository.getServerStatus().code()}")
                _response.value = repository.getServerStatus().body()
            } else {
                Log.e(TAG, "Response Code: ${repository.getServerStatus().code()}")
            }
        }
    }

    fun setCountDownTimer(value: Int){
        _countDownTimer.value = value
    }

    fun saveRefreshTime(time: Int) {
        viewModelScope.launch {
            saveTimeToPref(time)
        }
    }

    private suspend fun saveTimeToPref(triggerTime: Int) {
        withContext(Dispatchers.IO) {
            prefs.edit().putInt(PrefKeys.REFRESH_TIMER, triggerTime).apply()
        }
    }

    fun checkBoxOnChecked(_b: CompoundButton, checked: Boolean, server: ServerStatus) {

        if (checked) {
            serverSet.add(server.serverName)
            Log.i(TAG, "${server.serverName} added into prefs")
        } else {
            serverSet.remove(server.serverName)
            Log.i(TAG, "${server.serverName} removed from prefs")
        }
        setPrefsServerSet()
    }

    private fun setPrefsServerSet() {
        prefs.edit().putStringSet(PrefKeys.SERVER_SET, serverSet).apply()
    }

    fun updateDataCenterLabel(label: String) {
        _currentDataCenterLabel.value = label
    }

    fun setTimerIcon(isRunning: Boolean) {
        _timerIcon.value = if (isRunning) {
            R.drawable.ic_baseline_timer_24
        } else {
            R.drawable.ic_baseline_timer_off_24
        }
    }

    fun setConnectionStatus(isConnect: Boolean) {
        _isConnectToInternet.postValue(isConnect)
    }

    fun checkIsServiceRunning(){
        _isServiceRunning.value = app.applicationContext.isServiceRunning(FetchDataService::class.java)
    }

}