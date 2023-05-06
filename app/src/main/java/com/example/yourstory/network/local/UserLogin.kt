package com.example.yourstory.network.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserLogin(
    var name: String,
    var userId: String,
    var token: String
) : Parcelable