package com.example.paymentstone.ui.activities

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.paymentstone.R
import com.example.paymentstone.commons.bindView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import stone.application.enums.Action
import stone.application.enums.InstalmentTransactionEnum
import stone.application.enums.TransactionStatusEnum
import stone.application.enums.TypeOfTransactionEnum
import stone.application.interfaces.StoneActionCallback
import stone.database.transaction.TransactionObject
import stone.providers.TransactionProvider
import stone.utils.Stone
import java.text.NumberFormat

class TransactionActivity : AppCompatActivity() {

    private val textViewZero by bindView<TextView>(R.id.transaction_value_text_view_zero)
    private val textViewOne by bindView<TextView>(R.id.transaction_value_text_view_one)
    private val textViewTwo by bindView<TextView>(R.id.transaction_value_text_view_two)
    private val textViewThree by bindView<TextView>(R.id.transaction_value_text_view_three)
    private val textViewFour by bindView<TextView>(R.id.transaction_value_text_view_four)
    private val textViewFive by bindView<TextView>(R.id.transaction_value_text_view_five)
    private val textViewSix by bindView<TextView>(R.id.transaction_value_text_view_six)
    private val textViewSeven by bindView<TextView>(R.id.transaction_value_text_view_seven)
    private val textViewEight by bindView<TextView>(R.id.transaction_value_text_view_eight)
    private val textViewNine by bindView<TextView>(R.id.transaction_value_text_view_nine)
    private val textViewValue by bindView<TextView>(R.id.transaction_text_view)
    private val imageViewDelete by bindView<ImageView>(R.id.transaction_value_image_view_delete)
    private val viewSwitcher by bindView<ViewSwitcher>(R.id.transaction_view_switcher)
    private val button by bindView<Button>(R.id.transaction_method_button)
    private val fab by bindView<FloatingActionButton>(R.id.transaction_value_text_view_done)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        setupNumberPresentation("")

        textViewZero.setOnClickListener(keyNumberListener())
        textViewOne.setOnClickListener(keyNumberListener())
        textViewTwo.setOnClickListener(keyNumberListener())
        textViewThree.setOnClickListener(keyNumberListener())
        textViewFour.setOnClickListener(keyNumberListener())
        textViewFive.setOnClickListener(keyNumberListener())
        textViewSix.setOnClickListener(keyNumberListener())
        textViewSeven.setOnClickListener(keyNumberListener())
        textViewEight.setOnClickListener(keyNumberListener())
        textViewNine.setOnClickListener(keyNumberListener())

        imageViewDelete.setOnClickListener {
            deleteChar()
        }

        textViewValue.setOnClickListener {
            if (viewSwitcher.currentView.id == R.id.transaction_method_content_main) {
                viewSwitcher.showPrevious()
            }
        }

        fab.setOnClickListener {
            if (viewSwitcher.currentView.id == R.id.transaction_value_content_main) {
                viewSwitcher.showNext()
            }
        }

        button.setOnClickListener {
            val transactionObject = TransactionObject().apply {
                amount = cleanString(textViewValue.text)
                initiatorTransactionKey = "SEU_IDENTIFICADOR_UNICO"

                subMerchantCity = "city"
                subMerchantPostalAddress = "00000000"
                subMerchantRegisteredIdentifier = "00000000"
                subMerchantTaxIdentificationNumber = "33368443000199"

                instalmentTransaction = InstalmentTransactionEnum.ONE_INSTALMENT
                typeOfTransaction = TypeOfTransactionEnum.CREDIT
            }

            TransactionProvider(this,
                    transactionObject,
                    Stone.getUserModel(0),
                    Stone.getPinpadFromListAt(0)).apply {
                useDefaultUI(true)
                connectionCallback = object : StoneActionCallback {
                    override fun onSuccess() {
                        if (transactionStatus == TransactionStatusEnum.APPROVED) {
                            Toast.makeText(applicationContext, "Transação enviada com sucesso e salva no banco. Para acessar, use o TransactionDAO.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, "Erro na transação: \"$messageFromAuthorize\"", Toast.LENGTH_LONG).show()
                        }
                        finish()
                    }

                    override fun onStatusChanged(p0: Action?) {
                    }

                    override fun onError() {
                        Toast.makeText(context, "Erro na transação", Toast.LENGTH_SHORT).show()
                    }

                }
            }.execute()
        }
    }

    override fun onBackPressed() {
        if (viewSwitcher.currentView.id == R.id.transaction_method_content_main) {
            viewSwitcher.showPrevious()
            return
        }
        super.onBackPressed()
    }

    private fun keyNumberListener() = View.OnClickListener {
        val textView = it as TextView
        setupNumberPresentation(textView.text)
    }

    private fun setupNumberPresentation(text: CharSequence) {
        var cleanedString = cleanString(textViewValue.text)
        cleanedString += text

        val parsedValue = if (cleanedString.isEmpty()) 0.0 else cleanedString.toDouble()
        val formattedText = NumberFormat.getCurrencyInstance().format(parsedValue / 100)
        textViewValue.text = formattedText
    }

    private fun deleteChar() {
        val textValue = textViewValue.text
        if (textValue.isNotEmpty()) {
            val cleanedString = cleanString(textValue.substring(0, textValue.length - 1))
            val parsedValue = if (cleanedString.isEmpty()) 0.0 else cleanedString.toDouble()
            val formattedText = NumberFormat.getCurrencyInstance().format(parsedValue / 100)
            textViewValue.text = formattedText
        }
    }

    private fun cleanString(text: CharSequence) = text.replace(Regex("[$,.]"), "")
}