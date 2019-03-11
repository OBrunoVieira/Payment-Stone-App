package com.example.paymentstone.ui.activities

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.Group
import com.example.paymentstone.R
import com.example.paymentstone.commons.extensions.bindView
import com.example.paymentstone.commons.extensions.toReadableString
import com.example.paymentstone.commons.extensions.transformToCurrency
import com.google.android.material.checkbox.MaterialCheckBox
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
    private val viewSwitcher by bindView<ViewSwitcher>(R.id.transaction_view_switcher)
    private val textViewValue by bindView<TextView>(R.id.transaction_text_view)
    private val imageViewDelete by bindView<ImageView>(R.id.transaction_value_image_view_delete)
    private val cardViewDebit by bindView<CardView>(R.id.transaction_method_card_view_debit)
    private val cardViewCredit by bindView<CardView>(R.id.transaction_method_card_view_credit)
    private val radioDebit by bindView<RadioButton>(R.id.transaction_method_radio_button_debit)
    private val radioCredit by bindView<RadioButton>(R.id.transaction_method_radio_button_credit)
    private val button by bindView<Button>(R.id.transaction_method_button)
    private val fab by bindView<FloatingActionButton>(R.id.transaction_value_text_view_done)
    private val spinner by bindView<Spinner>(R.id.transaction_method_spinner)
    private val checkBoxCapture by bindView<MaterialCheckBox>(R.id.transaction_method_check_box)
    private val groupInstallment by bindView<Group>(R.id.transaction_method_group_installment)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)
        spinner.adapter = recoverArrayAdapter()
        setupNumberPresentation()

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

        cardViewCredit.setOnClickListener { managePaymentMethodClick(it.id) }
        cardViewDebit.setOnClickListener { managePaymentMethodClick(it.id) }

        imageViewDelete.setOnClickListener { deleteChar() }
        button.setOnClickListener { executeTransaction() }

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

    }

    override fun onBackPressed() {
        if (viewSwitcher.currentView.id == R.id.transaction_method_content_main) {
            viewSwitcher.showPrevious()
            return
        }

        super.onBackPressed()
    }

    private fun executeTransaction() {
        TransactionProvider(
            this,
            recoverTransactionObject(),
            Stone.getUserModel(0),
            Stone.getPinpadFromListAt(0)
        ).apply {
            useDefaultUI(true)
            connectionCallback = object : StoneActionCallback {
                override fun onSuccess() {
                    if (transactionStatus == TransactionStatusEnum.APPROVED) {
                        Toast.makeText(context, R.string.transaction_success, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.transaction_error_detailed, messageFromAuthorize),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onStatusChanged(p0: Action?) {
                }

                override fun onError() {
                    Toast.makeText(context, R.string.transaction_error, Toast.LENGTH_SHORT).show()
                }
            }
        }.execute()
    }

    private fun keyNumberListener() = View.OnClickListener {
        val textView = it as TextView
        setupNumberPresentation(textView.text)
    }

    private fun setupNumberPresentation(text: CharSequence = "") {
        var cleanedString = cleanString(textViewValue.text)
        cleanedString += text

        val parsedValue = if (cleanedString.isEmpty()) 0.0 else cleanedString.toDouble()
        textViewValue.text = parsedValue.transformToCurrency()
    }

    private fun deleteChar() {
        val textValue = textViewValue.text
        if (textValue.isNotEmpty()) {
            val cleanedString = cleanString(textValue.substring(0, textValue.length - 1))
            val parsedValue = if (cleanedString.isEmpty()) 0.0 else cleanedString.toDouble()
            textViewValue.text = parsedValue.transformToCurrency()
        }
    }

    private fun managePaymentMethodClick(@IdRes id: Int) {
        val isDebitMethod = id == cardViewDebit.id
        button.isEnabled = true
        radioDebit.isChecked = isDebitMethod
        radioCredit.isChecked = !isDebitMethod
        groupInstallment.visibility = if (isDebitMethod) GONE else VISIBLE
    }

    private fun cleanString(text: CharSequence) = text.replace(Regex("[$,.]"), "")

    private fun recoverArrayAdapter() =
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            InstalmentTransactionEnum.values().map { it.toReadableString() }
        )


    private fun recoverTransactionObject() =
        TransactionObject().apply {
            amount = cleanString(textViewValue.text)
            isCapture = checkBoxCapture.isChecked

            subMerchantCity = "city"
            subMerchantPostalAddress = "00000000"
            subMerchantRegisteredIdentifier = "00000000"
            subMerchantTaxIdentificationNumber = "33368443000199"
            subMerchantCategoryCode = "123"
            subMerchantAddress = "address"

            instalmentTransaction = InstalmentTransactionEnum.getAt(spinner.selectedItemPosition)
            typeOfTransaction = if (radioCredit.isChecked) TypeOfTransactionEnum.CREDIT else TypeOfTransactionEnum.DEBIT
        }
}