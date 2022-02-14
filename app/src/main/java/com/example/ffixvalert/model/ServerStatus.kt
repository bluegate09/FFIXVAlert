package com.example.ffixvalert.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerStatus(
    val serverName: String = "Unknown",
    val serverType: String = "Unknown",
    val canCreateCharacter: Boolean = false,
    val isServerOnline: Boolean = false
) : Parcelable
