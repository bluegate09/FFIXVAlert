package com.example.ffixvalert.constant

import com.example.ffixvalert.constant.serverlist.EuropeanDataCentersServerList
import com.example.ffixvalert.constant.serverlist.JapaneseDataCentersServerList
import com.example.ffixvalert.constant.serverlist.NorthAmericanDataCentersServerList
import com.example.ffixvalert.model.DataCenterLabel

object DataCentersTabLabel {

    const val EU = "EuropeanDataCentersServer"
    const val JP = "JapaneseDataCentersServer"
    const val NA = "NorthAmericanDataCentersServer"

    private val map = emptyMap<String,List<String>>().toMutableMap()

    fun displayEUDataCenter(): DataCenterLabel{
        map.clear()
        EuropeanDataCentersServerList.values().forEach { map += mapOf(it.name to it.servers) }
        return DataCenterLabel(map)
    }

    fun displayNADataCenter(): DataCenterLabel{
        map.clear()
        NorthAmericanDataCentersServerList.values().forEach { map += mapOf(it.name to it.servers) }
        return DataCenterLabel(map)
    }

    fun displayJPDataCenter(): DataCenterLabel{
        map.clear()
        JapaneseDataCentersServerList.values().forEach { map += mapOf(it.name to it.servers) }
        return DataCenterLabel(map)
    }



}