package com.example.hotelnest.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CityData(
    @SerialName(value = "desc")
    val desc: String = "",
    @SerialName(value = "name")
    val name: String = "",
    @SerialName(value = "image")
    val image: String = ""
)
