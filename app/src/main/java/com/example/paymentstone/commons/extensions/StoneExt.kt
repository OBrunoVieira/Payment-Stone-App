package com.example.paymentstone.commons.extensions

import com.example.paymentstone.R
import com.example.paymentstone.StoneApplication
import stone.application.enums.InstalmentTransactionEnum

fun InstalmentTransactionEnum.toReadableString(): String = let {
    val context = StoneApplication.instance
    when {
        count == 1 -> context.getString(R.string.transaction_one_installment)
        insterest -> context.getString(R.string.transaction_interest_installment, count)
        else -> context.getString(R.string.transaction_no_interest_installment, count)
    }
}
