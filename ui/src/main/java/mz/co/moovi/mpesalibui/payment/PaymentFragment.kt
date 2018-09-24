package mz.co.moovi.mpesalibui.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_payment.*
import mz.co.moovi.mpesalibui.MpesaSdk
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_CODE
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_LOGO_URL
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_NAME
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_THIRD_PARTY_REFERENCE
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_TRANSACTION_AMOUNT
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_TRANSACTION_REFERENCE
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.extensions.provideViewModel
import mz.co.moovi.mpesalibui.ui.Action
import mz.co.moovi.mpesalibui.ui.ViewState
import kotlinx.android.synthetic.main.fragment_payment.payment_authentication_card as authCard
import kotlinx.android.synthetic.main.fragment_payment.payment_error_card as errorCard
import kotlinx.android.synthetic.main.fragment_payment.start_payment_card as paymentCard
import kotlinx.android.synthetic.main.view_payment_card.view.pay_button as payButton

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PaymentFragment : Fragment() {

    private val viewModel by lazy {
        provideViewModel<PaymentViewModel>().apply {
            val args = arguments!!
            val viewAction = PaymentViewAction.Init(amount = args.getString(ARG_TRANSACTION_AMOUNT),
                    serviceProviderCode = args.getString(ARG_SERVICE_PROVIDER_CODE),
                    thirdPartyReference = args.getString(ARG_THIRD_PARTY_REFERENCE),
                    transactionReference = args.getString(ARG_TRANSACTION_REFERENCE),
                    serviceProviderName = args.getString(ARG_SERVICE_PROVIDER_NAME),
                    serviceProviderLogoUrl = args.getString(ARG_SERVICE_PROVIDER_LOGO_URL))
            handleViewAction(viewAction)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.action.observe(this, Observer { handleAction(it!!) })
        viewModel.viewState.observe(this, Observer { handleViewState(it!!) })
    }

    private fun handleAction(action: Action) {
        when (action) {
            is PaymentViewModelAction.Cancel -> {
                activity?.run {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
            is PaymentViewModelAction.EnablePaymentButton -> {
                paymentCard.payButton.isEnabled = true
            }
            is PaymentViewModelAction.DisablePaymentButton -> {
                paymentCard.payButton.isEnabled = false
            }
            is PaymentViewModelAction.SendResult -> {
                val paymentStatus = action.paymentStatus
                when (paymentStatus) {
                    is PaymentStatus.Success -> {
                        val intent = Intent().apply {
                            val bundle = Bundle().apply {
                                putString(MpesaSdk.ARG_RESULT_TRANSACTION_ID, paymentStatus.transactionId)
                                putString(MpesaSdk.ARG_RESULT_CONVERSATION_ID, paymentStatus.conversationId)
                            }
                            putExtras(bundle)
                        }
                        activity?.run {
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }

            }
        }
    }

    private fun handleViewState(viewState: ViewState) {
        val state = viewState as PaymentViewState
        errorCard.render(state.errorCard)
        paymentCard.render(state.paymentCard)
        authCard.render(state.authenticationCard)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        setupToolbar()
        paymentCard.handler = { viewModel.handleViewAction(it) }
        authCard.handler = { viewModel.handleViewAction(it) }
        errorCard.handler = { viewModel.handleViewAction(it) }
    }

    private fun setupToolbar() {
        toolbar.setTitle(R.string.payment_activity_toolbar_title)
        toolbar.setNavigationIcon(R.drawable.ic_close_24dp)
        toolbar.setNavigationOnClickListener {
            viewModel.handleViewAction(PaymentViewAction.Cancel)
        }

    }
}