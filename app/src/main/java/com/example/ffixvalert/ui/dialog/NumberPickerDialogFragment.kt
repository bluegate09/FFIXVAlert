package com.example.ffixvalert.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.ffixvalert.FFixvAlertViewModel
import com.example.ffixvalert.constant.PrefKeys
import com.example.ffixvalert.databinding.CustomNumberPickerLayoutBinding
import com.example.ffixvalert.extension.isServiceRunning
import com.example.ffixvalert.service.FetchDataService

private const val TAG = "NPDialogFragment"

class NumberPickerDialogFragment : DialogFragment() {

    private lateinit var binding: CustomNumberPickerLayoutBinding
    private val viewModel: FFixvAlertViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)
        val layoutInflater = LayoutInflater.from(context)
        val prefs =
            requireContext().getSharedPreferences(PrefKeys.PACKAGE_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet(PrefKeys.SERVER_SET, null)

        binding = CustomNumberPickerLayoutBinding.inflate(layoutInflater)

        val pickerSecond = binding.numberPickerSecond

        pickerSecond.apply {
            minValue = 15
            maxValue = 60
        }

        builder.setPositiveButton(
            "OK"
        ) { _, _ ->
            val triggerTimer = pickerSecond.value
            viewModel.checkIsServiceRunning()

            if (set!!.size == 0) {
                Toast.makeText(context, "No Server is selected", Toast.LENGTH_LONG).show()
            } else {
                viewModel.saveRefreshTime(triggerTimer)
                viewModel.setTimerIcon(true)
                startService()
            }
        }

        builder.setNegativeButton(
            "Cancel"
        ) { _, _ ->
            context?.stopService(Intent(requireContext(), FetchDataService::class.java))
        }

        builder.setView(binding.root)
        return builder.create()
    }

    private fun startService() {
        context?.stopService(Intent(requireContext(), FetchDataService::class.java))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(Intent(requireContext(), FetchDataService::class.java))
        } else {
            context?.startService(Intent(requireContext(), FetchDataService::class.java))
        }
    }

}