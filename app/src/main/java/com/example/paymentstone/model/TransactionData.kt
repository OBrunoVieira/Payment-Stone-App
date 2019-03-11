package com.example.paymentstone.model

class TransactionData(val amount: String = "",
                      val installmentDescription: String = "",
                      val date: String = "",
                      val cardNumber: String = "",
                      val status: String = "") {
    companion object {
        const val CLIENT_RECEIPT = 1200
        const val ESTABLISHMENT_RECEIPT = 1211
        const val CAPTURE = 1222
        const val CANCEL = 1233
    }
}