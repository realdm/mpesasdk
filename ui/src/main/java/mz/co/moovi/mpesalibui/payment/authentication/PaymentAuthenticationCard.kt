package mz.co.moovi.mpesalibui.payment.authentication

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.payment.PaymentViewAction
import kotlinx.android.synthetic.main.view_payment_authentication_card.view.error_message as authMessageView

class PaymentAuthenticationCard @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : CardView(context, attributeSet) {
    var handler: ((PaymentViewAction) -> Unit)? = null

    init {
        inflate(context, R.layout.view_payment_authentication_card, this)
    }

    fun render(viewState: PaymentAuthenticationCardViewState?) {
        val shouldBeVisible = viewState != null
        if (shouldBeVisible) {
            val message = resources.getString(R.string.payment_authentication_card_description, viewState?.phoneNumber)
            authMessageView.text = message
        }
        visibility = if (shouldBeVisible) View.VISIBLE else View.GONE
    }
}