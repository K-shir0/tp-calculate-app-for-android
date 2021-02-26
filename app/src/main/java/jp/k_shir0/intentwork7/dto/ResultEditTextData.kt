package jp.k_shir0.intentwork7.dto

import android.widget.EditText
import jp.k_shir0.intentwork7.R

data class ResultEditTextData(
    val tpEditText: EditText,
    val perfectEditText: EditText,
    val goodEditText: EditText,
    val badEditText: EditText,
    val missEditText: EditText,
    val blackPerfectEditText: EditText
)
