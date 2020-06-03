package mz.co.moovi.mpesalibui.payment.c2b

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.extensions.createAfterTextChangeWatcher
import mz.co.moovi.mpesalibui.extensions.setTextWithoutListener
import kotlinx.android.synthetic.main.view_c2b_payment.view.amount_to_pay as amountToPay
import kotlinx.android.synthetic.main.view_c2b_payment.view.pay_button as payButton
import kotlinx.android.synthetic.main.view_c2b_payment.view.phone_number as phoneNumberField
import kotlinx.android.synthetic.main.view_c2b_payment.view.service_provider_code as serviceProviderCode
import kotlinx.android.synthetic.main.view_c2b_payment.view.service_provider_logo as serviceProviderLogo
import kotlinx.android.synthetic.main.view_c2b_payment.view.service_provider_name as serviceProviderName

class C2BPaymentView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    FrameLayout(context, attrs) {

    private lateinit var amount: TextView

    init {
        inflate(context, R.layout.view_c2b_payment, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    private var payButtonHandler: (() -> Unit)? = null
    private var editNumberHandler: ((String) -> Unit)? = null

    private val phoneNumberTextWatcher = createAfterTextChangeWatcher {
        editNumberHandler?.invoke(it.toString())
    }

    fun render(
        amount: String,
        phoneNumber: String,
        providerName: String,
        providerCode: String,
        providerLogo: String,
        payButtonIsEnabled: Boolean,
        onPayButtonClicked: () -> Unit,
        onEditPhoneNumber: (String) -> Unit
    ) {
        amountToPay.text = amount
        serviceProviderName.text = providerName
        serviceProviderCode.text = providerCode
        Glide.with(context).load(providerLogo)
            .apply(RequestOptions.circleCropTransform().fitCenter()).into(serviceProviderLogo)

        payButtonHandler = onPayButtonClicked
        payButton.isEnabled = payButtonIsEnabled
        payButton.setOnClickListener { payButtonHandler?.invoke() }

        editNumberHandler = onEditPhoneNumber
        phoneNumberField.addTextChangedListener(phoneNumberTextWatcher)
        phoneNumberField.setTextWithoutListener(phoneNumber, phoneNumberTextWatcher)
    }
}