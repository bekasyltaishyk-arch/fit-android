package com.example.fit.utils

object Validation {
    fun email(value: String) = Regex("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$").matches(value.trim())
    fun password(value: String) = value.length >= 6
    fun matches(a: String, b: String) = a == b
    fun weight(value: String): Double? = value.replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() && it in 20.0..400.0 }
}
