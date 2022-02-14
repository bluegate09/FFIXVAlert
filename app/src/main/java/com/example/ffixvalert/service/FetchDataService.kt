package com.example.ffixvalert.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.example.ffixvalert.MainActivity
import com.example.ffixvalert.R
import com.example.ffixvalert.constant.PrefKeys
import com.example.ffixvalert.extension.sendNotification
import com.example.ffixvalert.extension.toMessageBody
import com.example.ffixvalert.model.AllServerStatus
import com.example.ffixvalert.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.reflect.full.memberProperties

private const val TAG = "FetchDataService"

class FetchDataService: Service() {

    companion object{
        // countdown timer display
        val remainingTime = MutableLiveData<Int>()
        val timerIsRunning = MutableLiveData<Boolean>()
        // The identifier for this notification
        private const val IDENTIFIER_ID = 9999
    }

    private lateinit var timer: Timer
    private lateinit var wakelock: PowerManager.WakeLock
    private lateinit var wakelockCpu: PowerManager.WakeLock
    private val repository = Repository()
    private lateinit var countDownTimer: CountDownTimer


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences(PrefKeys.PACKAGE_NAME, Context.MODE_PRIVATE)
        val time = prefs.getInt(PrefKeys.REFRESH_TIMER,0)*1000.toLong()
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val refreshTime = prefs.getInt(PrefKeys.REFRESH_TIMER,0)

        countDownTimer = object: CountDownTimer(refreshTime*1000.toLong(),1000){
            override fun onTick(millisUntilFinished: Long) {
                val onTickTime = (millisUntilFinished/1000).toDouble()
                // fun ceil
                // Rounds the given value x to an integer towards positive infinity
                remainingTime.value = ceil(onTickTime).toInt()
            }

            override fun onFinish() {
                ContextCompat.getMainExecutor(applicationContext).execute {
                    remainingTime.value = refreshTime
                }
                Log.i(TAG,"timer finished: $refreshTime")
            }

        }

        wakelock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,"app: ffixvAlert")
        wakelockCpu = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"app: ffixvAlert")
        wakelockCpu.acquire()

        val contentIntent = Intent(applicationContext, MainActivity::class.java)
        val contentPendingIntent: PendingIntent = createPendingIntent(contentIntent)
        val notification = createNotification(contentPendingIntent)
        
        startForeground(IDENTIFIER_ID,notification)
        timerIsRunning.value = true
        timer = Timer()
        timer.scheduleAtFixedRate(FetchDataTask(),0,time)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        timerIsRunning.value = false
        countDownTimer.cancel()
        countDownTimer.onFinish()
        timer.cancel()
        if(wakelock.isHeld){
            wakelock.release()
        }
        if(wakelockCpu.isHeld){
            wakelockCpu.release()
        }
        Log.i(TAG,"$TAG destroy")
    }
    
    inner class FetchDataTask: TimerTask() {

        private val prefs = getSharedPreferences(PrefKeys.PACKAGE_NAME, Context.MODE_PRIVATE)
        override fun run() {
            val prefServerSet = prefs.getStringSet(PrefKeys.SERVER_SET, null)
            val nonCongestionServerList: MutableList<String> = emptyList<String>().toMutableList()

            countDownTimer.start()
            fetchData()?.let { allServerStatus ->
                // use reflection to get server name
                for (prop in AllServerStatus::class.memberProperties) {

                    val list = prop.get(allServerStatus) as ArrayList<*>
                    val serverName = prop.name.replaceFirstChar { it.uppercase() }
                    val canCreateCharacter = list[1] as Boolean

                    // check servers can create new character or not
                    if (prefServerSet!!.contains(serverName) && canCreateCharacter) {
                        nonCongestionServerList.add(serverName)
                    }
                }

                if (nonCongestionServerList.size > 0) {
                    wakelock.acquire(10*1000L /*10 minutes*/)
                    sendNotification(nonCongestionServerList.toMessageBody())
                    countDownTimer.cancel()
                    countDownTimer.onFinish()
                    timer.cancel()
                    timer.purge()
                    stopService(Intent(applicationContext, FetchDataService::class.java))
                }
            }
            Log.i(TAG,"fetchData() time: ${getDateTime()}")
//            ContextCompat.getMainExecutor(applicationContext).execute {
//                // test
//                Toast.makeText(applicationContext,"Data fetched, ${getDateTime()}",Toast.LENGTH_LONG).show()
//            }
        }

        private fun getDateTime(): String{
            val sdf = SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]")
            return sdf.format(Date())
        }

    }

    private fun fetchData(): AllServerStatus? {

        var allServerStatus: AllServerStatus? = null
        val job = GlobalScope.launch(Dispatchers.IO) {
            if (repository.getServerStatus().isSuccessful &&
                repository.getServerStatus().code() != 204
            ) {
                Log.i(TAG, "Response Code: ${repository.getServerStatus().code()}")
                allServerStatus = repository.getServerStatus().body()
            } else {
                Log.i(TAG, "Response Code: ${repository.getServerStatus().code()}")
            }
        }
        // wait until data is ready
        runBlocking {
            job.join()
        }

        return allServerStatus
    }

    private fun createPendingIntent(contentIntent: Intent): PendingIntent {
        return PendingIntent.getActivity(
            applicationContext,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createNotification(contentPendingIntent: PendingIntent): Notification {
        return NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.ffxiv_notification_channel_id)
        ).setContentTitle("FF14 Alert")
            .setContentText("fetching server status...")
            .setContentIntent(contentPendingIntent)
            .build()
    }

    private fun sendNotification(nonCongestionServers: String) {
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(
            "$nonCongestionServers can login now",
            applicationContext
        ).also {
            Log.i(TAG, "Notification Send")
        }
    }

}