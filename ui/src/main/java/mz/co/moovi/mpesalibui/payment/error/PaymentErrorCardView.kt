package mz.co.moovi.mpesalibui.payment.error

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.databinding.ViewPaymentErrorCardBinding
import mz.co.moovi.mpesalibui.payment.PaymentViewAction

class PaymentErrorCardView @JvmOverloads constructor(context: Context,
                                                     attributeSet: AttributeSet? = null) :
        CardView(context, attributeSet) {

    var handler: ((PaymentViewAction) -> Unit)? = null
    private val binding: ViewPaymentErrorCardBinding

    init {
        val view = View.inflate(context, R.layout.view_payment_error_card, this)
        binding = ViewPaymentErrorCardBinding.bind(view)
        setupUi()
    }

    private fun setupUi() {
        binding.retryButton.setOnClickListener {
            handler?.invoke(PaymentViewAction.Retry)
        }
    }

    fun render(viewState: PaymentErrorCardViewState?) {
        val shouldBeVisible = viewState != null

        viewState?.run {
            val message = resources.getString(this.messageResId)
            binding.errorMessage.text = message
        }
        visibility = if (shouldBeVisible) View.VISIBLE else View.GONE
    }
}