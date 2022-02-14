package com.example.ffixvalert

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.ffixvalert.databinding.ActivityMainBinding
import com.example.ffixvalert.service.FetchDataService
import com.example.ffixvalert.utility.CheckConnection

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: FFixvAlertViewModel by viewModels {
        MainViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val remainingTime = FetchDataService.remainingTime


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        setActionBar()

        viewModel.checkIsServiceRunning()
        if (CheckConnection.isNetworkAvailable(this)) {
            viewModel.setConnectionStatus(true)
            viewModel.getServerStatus()
        } else {
            viewModel.setConnectionStatus(false)
        }

        viewModel.currentDataCenterLabel.observe(this, {
            binding.topAppBar.title = it
        })

        remainingTime.observe(this, {
            viewModel.setCountDownTimer(it)
        })

    }

    private fun setActionBar() {
        setSupportActionBar(binding.topAppBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

}