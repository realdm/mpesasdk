package mz.co.moovi.mpesalibui.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_payment.toolbar
import mz.co.moovi.mpesalibui.MpesaSdk
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_CODE
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_LOGO_URL
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_NAME
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_TRANSACTION_AMOUNT
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_TRANSACTION_REFERENCE
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.extensions.provideViewModel
import mz.co.moovi.mpesalibui.payment.devtools.MockAuthPaymentViewModel
import mz.co.moovi.mpesalibui.ui.Action
import mz.co.moovi.mpesalibui.ui.ViewState
import mz.co.moovi.mpesalibui.utils.Injector
import kotlinx.android.synthetic.main.fragment_payment.payment_authentication_card as authCard
import kotlinx.android.synthetic.main.fragment_payment.payment_error_card as errorCard
import kotlinx.android.synthetic.main.fragment_payment.start_payment_card as paymentCard
import kotlinx.android.synthetic.main.view_payment_card.view.pay_button as payButton

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PaymentFragment : Fragment() {

    private val viewModel by lazy {
        provideViewModel(scope = this) {
            val bundle = arguments!!
            val args = PaymentArgs(
                    amount = bundle.getString(ARG_TRANSACTION_AMOUNT),
                    serviceProviderName = bundle.getString(ARG_SERVICE_PROVIDER_NAME),
                    serviceProviderCode = bundle.getString(ARG_SERVICE_PROVIDER_CODE),
                    transactionReference = bundle.getString(ARG_TRANSACTION_REFERENCE),
                    serviceProviderLogoUrl = bundle.getString(ARG_SERVICE_PROVIDER_LOGO_URL))

            when (MpesaSdk.hasMockAuthEnabled) {
                true -> MockAuthPaymentViewModel(args)
                else -> PaymentViewModel(Injector.mPesaService(), args)
            }
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
            is PaymentAction.Cancel -> {
                activity?.run {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
            is PaymentAction.EnablePaymentButton -> {
                paymentCard.payButton.isEnabled = true
            }
            is PaymentAction.DisablePaymentButton -> {
                paymentCard.payButton.isEnabled = false
            }
            is PaymentAction.ShowMockPinDialog -> {
                showMockDialog(action.providerName, amount = action.amount, transactionRef = action.reference)
            }
            is PaymentAction.SendResult -> {
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
        authCard.handler = { viewModel.handleViewAction(it) }
        errorCard.handler = { viewModel.handleViewAction(it) }
        paymentCard.handler = { viewModel.handleViewAction(it) }
    }

    private fun setupToolbar() {
        toolbar.let { it ->
            it.title = resources.getString(R.string.payment_activity_toolbar_title)
            it.setNavigationIcon(R.drawable.ic_close_24dp)
            it.setNavigationOnClickListener {
                viewModel.handleViewAction(PaymentViewAction.Cancel)
            }
        }
    }

    /**
     * Shows the Dialog that simulates the USSD Message sent by Vodacom
     * Requesting for an Authentication PIN.
     */
    private fun showMockDialog(providerName: String, amount: String, transactionRef: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext()).apply {
            val message = resources.getString(R.string.mock_pina_auth_dialog_mpes_message, amount, providerName, transactionRef)
            setMessage(message)

            val input = EditText(requireContext()).apply {
                inputType = InputType.TYPE_NUMBER_VARIATION_PASSWORD
            }

            val container = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                val margin = resources.getDimensionPixelSize(R.dimen.space_24dp)
                val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(margin, margin, margin, margin)
                input.layoutParams = lp
                addView(input)
            }

            setView(container)

            setPositiveButton(resources.getString(R.string.mock_pin_auth_dialog_send_button)) { dialog, _ ->
                val pin = try {
                    input.text.toString().toInt()
                } catch (e: Exception) {
                    -1
                }
                dialog.cancel()
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.handleViewAction(PaymentViewAction.ProcessMockPin(pin))
                }, 2000)

            }
            setNegativeButton(resources.getString(R.string.mock_pin_auth_dialog_cancel_button)) { dialog, _ ->
                dialog.cancel()
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.handleViewAction(PaymentViewAction.ProcessMockPin(-1))
                }, 2000)
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            dialogBuilder.show()
        }, 1500)

    }
}