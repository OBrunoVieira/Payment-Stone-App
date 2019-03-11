package com.example.paymentstone.commons.extensions

import java.text.NumberFormat

fun Double.transformToCurrency(): String = NumberFormat.getCurrencyInstance().format(this / 100)
