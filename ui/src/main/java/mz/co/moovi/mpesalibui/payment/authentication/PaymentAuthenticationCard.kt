package mz.co.moovi.mpesalibui.payment.authentication

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.view_payment_authentication_card.view.timer_countdown as countDownTextView
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.payment.PaymentViewAction
import kotlinx.android.synthetic.main.view_payment_authentication_card.view.error_message as authMessageView

class PaymentAuthenticationCard @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : CardView(context, attributeSet) {

    companion object {
        private const val TIME_OUT = 90
        private const val TIME_OUT_IN_MILLIS = TIME_OUT * 1000L
        private const val TIME_OUT_COUNT_DOWN_INTERVAL_IN_MILLIS = 1000L
    }

    private var startTime = TIME_OUT

    var handler: ((PaymentViewAction) -> Unit)? = null

    private val countDownTimer: CountDownTimer = object : CountDownTimer(TIME_OUT_IN_MILLIS, TIME_OUT_COUNT_DOWN_INTERVAL_IN_MILLIS) {
        override fun onTick(millis: Long) {
            startTime -= 1
            countDownTextView.text = startTime.toString()
        }

        override fun onFinish() {
            handler?.invoke(PaymentViewAction.ShowError(R.string.payment_response_ins_9))
        }
    }

    init {
        inflate(context, R.layout.view_payment_authentication_card, this)
    }

    fun render(viewState: PaymentAuthenticationCardViewState?) {
        val shouldBeVisible = viewState != null
        if (shouldBeVisible) {
            val message = resources.getString(R.string.payment_authentication_card_description, viewState?.phoneNumber)
            authMessageView.setText(message)
            countDownTextView.text = startTime.toString()
            countDownTimer.start()
        }
        visibility = if (shouldBeVisible) View.VISIBLE else View.GONE
    }

}