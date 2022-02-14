package com.example.ffixvalert.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.ffixvalert.FFixvAlertViewModel
import com.example.ffixvalert.MainActivity
import com.example.ffixvalert.R
import com.example.ffixvalert.constant.DataCentersTabLabel
import com.example.ffixvalert.constant.PrefKeys
import com.example.ffixvalert.databinding.FragmentTabLayoutBinding
import com.example.ffixvalert.model.DataCenterLabel
import com.example.ffixvalert.service.FetchDataService
import com.example.ffixvalert.ui.adapter.ViewPagerAdapter
import com.example.ffixvalert.ui.dialog.NumberPickerDialogFragment
import com.example.ffixvalert.utility.CheckConnection
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "TabLayoutFragment"

class TabLayoutFragment : Fragment() {

    private lateinit var binding: FragmentTabLayoutBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var prefs: SharedPreferences

    private val viewModel: FFixvAlertViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        prefs = requireContext().getSharedPreferences(PrefKeys.PACKAGE_NAME, Context.MODE_PRIVATE)
        val timerIsRunning = FetchDataService.timerIsRunning

        binding = FragmentTabLayoutBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.menuButton.setOnClickListener {
            showMenu(it)
        }
        binding.imageButton.setOnClickListener{
            showNumberPicker()
        }

        if(timerIsRunning.value == null){
            viewModel.setTimerIcon(false)
        }

        timerIsRunning.observe(viewLifecycleOwner,{
            viewModel.setTimerIcon(it)
        })

        viewModel.isConnectToInternet.observe(viewLifecycleOwner,{
            if(!it){
                showSnackBar()
            }
        })

        viewPager = binding.viewPager
        tabLayout = binding.mainTabLayout
        setTabLayout(DataCentersTabLabel.displayEUDataCenter())

        createChannel(
            getString(R.string.ffxiv_notification_channel_id),
            getString(R.string.ffxiv_notification_channel_name)
        )

        return binding.root
    }

    // Notification
    private fun createChannel(channelId: String, channelName: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply{
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "ff14 server congestion alert"

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    // ViewPager2
    private fun setTabLayout(location: DataCenterLabel) {
        val adapter =
            ViewPagerAdapter(location, requireActivity().supportFragmentManager, lifecycle)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val keyList = location.dataCenter.keys.toList()
            for (i in 0..location.dataCenter.keys.size) {
                when (position) {
                    i -> tab.text = keyList[i]
                }
            }
        }.attach()
    }

    // Popup menu
    private fun showMenu(v: View) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.option_eu -> {
                    setTabLayout(DataCentersTabLabel.displayEUDataCenter())
                    viewModel.updateDataCenterLabel(DataCentersTabLabel.EU)
                }
                R.id.option_jp -> {
                    setTabLayout(DataCentersTabLabel.displayJPDataCenter())
                    viewModel.updateDataCenterLabel(DataCentersTabLabel.JP)
                }
                R.id.option_na -> {
                    setTabLayout(DataCentersTabLabel.displayNADataCenter())
                    viewModel.updateDataCenterLabel(DataCentersTabLabel.NA)
                }
            }
            true
        }
        // Show the popup menu.
        popup.show()
    }

    private fun showNumberPicker(){
        val newFragment = NumberPickerDialogFragment()
        newFragment.show((activity as MainActivity).supportFragmentManager,"Time Picker")
    }

    private fun showSnackBar(){
        Snackbar.make(binding.root,"Can't connect to internet",Snackbar.LENGTH_INDEFINITE).setAction("Retry"){
            if(CheckConnection.isNetworkAvailable(requireContext())){
                viewModel.setConnectionStatus(true)
                viewModel.getServerStatus()
            }else{
                viewModel.setConnectionStatus(false)
            }
        }.show()
    }

}