package jp.k_shir0.intentwork7.dto

import android.text.Editable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class ResultData(
    @SerialName("TP")
    val tp: Float,
    @SerialName("PERFECT")
    val perfect: Int,
    @SerialName("GOOD")
    val good: Int,
    @SerialName("BAD")
    val bad: Int,
    @SerialName("MISS")
    val miss: Int
)
