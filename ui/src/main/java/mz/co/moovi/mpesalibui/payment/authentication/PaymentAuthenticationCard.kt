package mz.co.moovi.mpesalibui.payment.authentication

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.databinding.ViewPaymentAuthenticationCardBinding
import mz.co.moovi.mpesalibui.payment.PaymentViewAction

class PaymentAuthenticationCard @JvmOverloads constructor(context: Context,
                                                          attributeSet: AttributeSet? = null) :
        CardView(context, attributeSet) {
    var handler: ((PaymentViewAction) -> Unit)? = null
    private val binding: ViewPaymentAuthenticationCardBinding

    init {
        val view = inflate(context, R.layout.view_payment_authentication_card, this)
        binding = ViewPaymentAuthenticationCardBinding.bind(view)
    }

    fun render(viewState: PaymentAuthenticationCardViewState?) {
        val shouldBeVisible = viewState != null
        if (shouldBeVisible) {
            val message = resources.getString(R.string.payment_authentication_card_description, viewState?.phoneNumber)
            binding.errorMessage.text = message
        }
        visibility = if (shouldBeVisible) View.VISIBLE else View.GONE
    }
}