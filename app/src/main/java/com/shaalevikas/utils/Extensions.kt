package com.shaalevikas.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Double.asCurrency(): String = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(this)

fun Long.asReadableDate(): String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))
