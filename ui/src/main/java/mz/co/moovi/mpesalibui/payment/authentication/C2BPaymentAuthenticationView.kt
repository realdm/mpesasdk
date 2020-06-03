package mz.co.moovi.mpesalibui.payment.authentication

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import mz.co.moovi.mpesalibui.R
import kotlinx.android.synthetic.main.view_c2b_authentication.view.description as authMessageView

class C2BPaymentAuthenticationView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {
    init {
        inflate(context, R.layout.view_c2b_authentication, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    fun render(phoneNumber: String) {
        val message =
            resources.getString(R.string.payment_authentication_card_description, phoneNumber)
        authMessageView.text = message
    }
}