package mz.co.moovi.mpesalibui.payment

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import mz.co.moovi.mpesalibui.R
import kotlinx.android.synthetic.main.view_payment_card.view.amount_to_pay as amount
import kotlinx.android.synthetic.main.view_payment_card.view.pay_button as payButton
import kotlinx.android.synthetic.main.view_payment_card.view.phone_number as phoneNumber
import kotlinx.android.synthetic.main.view_payment_card.view.service_provider_code as serviceProviderCode
import kotlinx.android.synthetic.main.view_payment_card.view.service_provider_logo as serviceProviderLogo
import kotlinx.android.synthetic.main.view_payment_card.view.service_provider_name as serviceProviderName

class PaymentViewCard @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : CardView(context, attrs) {

    private lateinit var amount: TextView
    var handler: ((PaymentViewAction) -> Unit)? = null

    init {
        inflate(context, R.layout.view_payment_card, this)
        setupUi()
    }

    private fun setupUi() {
        amount = findViewById(R.id.amount_to_pay)
        payButton.setOnClickListener {
            handler?.invoke(PaymentViewAction.MakePayment)
        }
        phoneNumber.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }

            override fun afterTextChanged(editable: Editable?) {
                val viewAction = PaymentViewAction.AddPhoneNumber(editable.toString())
                handler?.invoke(viewAction)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                return
            }
        })
    }

    fun render(viewState: PaymentCardViewState?) {
        val shouldBeVisible = viewState != null
        if (shouldBeVisible) {
            amount.text = viewState?.amount
            serviceProviderName.text = viewState?.serviceProviderName
            serviceProviderCode.text = viewState?.serviceProviderCode
            Glide.with(context).load(viewState?.serviceProviderLogo).apply(RequestOptions.circleCropTransform().fitCenter()).into(serviceProviderLogo)
        }
        visibility = if (shouldBeVisible) View.VISIBLE else GONE
    }
}