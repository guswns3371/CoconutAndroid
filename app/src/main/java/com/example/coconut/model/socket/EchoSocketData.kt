package com.example.coconut.model.socket

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class EchoSocketData(
    var echo: String,
    var userIndex: String
)