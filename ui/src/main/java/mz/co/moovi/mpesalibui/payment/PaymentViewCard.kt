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
import mz.co.moovi.mpesalibui.databinding.ViewPaymentCardBinding

class PaymentViewCard @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        CardView(context, attrs) {

    private lateinit var amount: TextView
    var handler: ((PaymentViewAction) -> Unit)? = null

    private val binding: ViewPaymentCardBinding

    init {
        val view = inflate(context, R.layout.view_payment_card, this)
        binding = ViewPaymentCardBinding.bind(view)
        setupUi()
    }

    private fun setupUi() {
        amount = findViewById(R.id.amount_to_pay)
        binding.payButton.setOnClickListener {
            handler?.invoke(PaymentViewAction.MakePayment)
        }
        binding.phoneNumber.addTextChangedListener(object : TextWatcher {
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
            binding.serviceProviderName.text = viewState?.serviceProviderName
            binding.serviceProviderCode.text = viewState?.serviceProviderCode
            Glide.with(context).load(viewState?.serviceProviderLogo)
                    .apply(RequestOptions.circleCropTransform().fitCenter())
                    .into(binding.serviceProviderLogo)
        }
        visibility = if (shouldBeVisible) View.VISIBLE else GONE
    }
}