package com.example.ffixvalert.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ffixvalert.FFixvAlertViewModel
import com.example.ffixvalert.constant.DataCentersTabLabel
import com.example.ffixvalert.constant.serverlist.EuropeanDataCentersServerList
import com.example.ffixvalert.constant.serverlist.JapaneseDataCentersServerList
import com.example.ffixvalert.constant.serverlist.NorthAmericanDataCentersServerList
import com.example.ffixvalert.databinding.FragmentDataCenterBinding
import com.example.ffixvalert.model.AllServerStatus
import com.example.ffixvalert.model.ServerStatus
import com.example.ffixvalert.ui.adapter.ServerCheckChangeListener
import com.example.ffixvalert.ui.adapter.ServerStatusAdapter
import kotlin.reflect.full.memberProperties

class DataCenterFragment(private val position: Int) : Fragment() {

    private lateinit var binding: FragmentDataCenterBinding
    private val viewModel: FFixvAlertViewModel by activityViewModels()

    private lateinit var serverStatus: MutableMap<String, List<ServerStatus>>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDataCenterBinding.inflate(inflater)

        val adapter = ServerStatusAdapter(
            ServerCheckChangeListener { b,check,serverStatus ->
                viewModel.checkBoxOnChecked(b,check,serverStatus)
            })
        binding.recyclerView.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.lifecycleOwner = viewLifecycleOwner

        if (viewModel.firstTimeInit) {
            viewModel.response.observe(viewLifecycleOwner, {
                serverStatus = getDataCentersData(it)
                viewModel.updateDataCenterLabel(DataCentersTabLabel.EU)
                val key = EuropeanDataCentersServerList.values()[position].name
                adapter.submitList(serverStatus[key])
            })
            viewModel.firstTimeInit = false
        }

        viewModel.currentDataCenterLabel.observe(viewLifecycleOwner, { dataCenter ->
            val key: String
            when (dataCenter) {
                DataCentersTabLabel.EU -> {
                    key = EuropeanDataCentersServerList.values()[position].name
                }
                DataCentersTabLabel.JP -> {
                    key = JapaneseDataCentersServerList.values()[position].name
                }
                DataCentersTabLabel.NA -> {
                    key = NorthAmericanDataCentersServerList.values()[position].name
                }
                else -> throw IllegalArgumentException("$dataCenter is not exist")
            }.also {
                serverStatus = getDataCentersData(viewModel.response.value!!)
                adapter.submitList(serverStatus[key])
            }
        })

        return binding.root
    }

    private fun getDataCentersData(data: AllServerStatus): MutableMap<String, List<ServerStatus>> {

        var serverName: String
        var list: List<Any>
        var serverType: String
        var canCreateCharacter: Boolean
        var isServerOnline: Boolean

        val dataList = emptyList<ServerStatus>().toMutableList()

        // use reflection to get server name
        for (prop in AllServerStatus::class.memberProperties) {

            list = prop.get(data) as ArrayList<*>

            serverName = prop.name.replaceFirstChar { it.uppercase() }
            serverType = list[0] as String
            canCreateCharacter = list[1] as Boolean
            isServerOnline = list[2] as Boolean

            dataList += ServerStatus(serverName, serverType, canCreateCharacter, isServerOnline)
        }

        val serverChaos = dataList.filter { serverList ->
            EuropeanDataCentersServerList.Chaos.servers.any { it == serverList.serverName }
        }

        val serverLight = dataList.filter { serverList ->
            EuropeanDataCentersServerList.Light.servers.any { it == serverList.serverName }
        }

        val serverMana = dataList.filter { serverList ->
            JapaneseDataCentersServerList.Mana.servers.any { it == serverList.serverName }
        }

        val serverGaia = dataList.filter { serverList ->
            JapaneseDataCentersServerList.Gaia.servers.any { it == serverList.serverName }
        }

        val serverElemental = dataList.filter { serverList ->
            JapaneseDataCentersServerList.Elemental.servers.any { it == serverList.serverName }
        }

        val serverPrimal = dataList.filter { serverList ->
            NorthAmericanDataCentersServerList.Primal.servers.any { it == serverList.serverName }
        }

        val serverCrystal = dataList.filter { serverList ->
            NorthAmericanDataCentersServerList.Crystal.servers.any { it == serverList.serverName }
        }

        val serverAether = dataList.filter { serverList ->
            NorthAmericanDataCentersServerList.Aether.servers.any { it == serverList.serverName }
        }

        val serverStatusData = emptyMap<String, List<ServerStatus>>().toMutableMap()


        serverStatusData += mapOf(EuropeanDataCentersServerList.Chaos.name to serverChaos)
        serverStatusData += mapOf(EuropeanDataCentersServerList.Light.name to serverLight)

        serverStatusData += mapOf(JapaneseDataCentersServerList.Elemental.name to serverElemental)
        serverStatusData += mapOf(JapaneseDataCentersServerList.Gaia.name to serverGaia)
        serverStatusData += mapOf(JapaneseDataCentersServerList.Mana.name to serverMana)

        serverStatusData += mapOf(NorthAmericanDataCentersServerList.Aether.name to serverAether)
        serverStatusData += mapOf(NorthAmericanDataCentersServerList.Crystal.name to serverCrystal)
        serverStatusData += mapOf(NorthAmericanDataCentersServerList.Primal.name to serverPrimal)

        return serverStatusData

    }

}