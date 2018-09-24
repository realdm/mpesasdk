package mz.co.moovi.mpesalibui.payment.error

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.payment.PaymentViewAction
import kotlinx.android.synthetic.main.view_payment_error_card.view.error_message as messageView
import kotlinx.android.synthetic.main.view_payment_error_card.view.retry_button as retryButton

class PaymentErrorCardView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : CardView(context, attributeSet) {

    var handler: ((PaymentViewAction) -> Unit)? = null

    init {
        View.inflate(context, R.layout.view_payment_error_card, this)
        setupUi()
    }

    fun setupUi() {
        retryButton.setOnClickListener {
            handler?.invoke(PaymentViewAction.Retry)
        }
    }

    fun render(viewState: PaymentErrorCardViewState?) {
        val shouldBeVisible = viewState != null

        viewState?.run {
            val message = resources.getString(this.messageResId)
            messageView.text = message
        }
        visibility = if (shouldBeVisible) View.VISIBLE else View.GONE
    }
}