package mz.co.moovi.mpesalibui.payment.c2b

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_c2b_payment.*
import mz.co.moovi.mpesalibui.MpesaSdk
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_CODE
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_LOGO_URL
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_NAME
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_TRANSACTION_AMOUNT
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_TRANSACTION_REFERENCE
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.extensions.provideViewModel
import mz.co.moovi.mpesalibui.payment.PaymentResult
import mz.co.moovi.mpesalibui.ui.Event
import mz.co.moovi.mpesalibui.utils.Injector
import kotlinx.android.synthetic.main.fragment_c2b_payment.authentication_view as authView
import kotlinx.android.synthetic.main.fragment_c2b_payment.error_view as errorView
import kotlinx.android.synthetic.main.fragment_c2b_payment.payment_view as paymentCard

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class C2BPaymentFragment : Fragment() {

    private val viewModel by lazy {
        provideViewModel(this) {
            C2BPaymentViewModel(
                mpesaService = Injector.mpesaService,
                arguments = C2BPaymentViewModel.Arguments(
                    amount = arguments!!.getString(ARG_TRANSACTION_AMOUNT),
                    serviceProviderCode = arguments!!.getString(ARG_SERVICE_PROVIDER_CODE),
                    serviceProviderName = arguments!!.getString(ARG_SERVICE_PROVIDER_NAME),
                    transactionReference = arguments!!.getString(ARG_TRANSACTION_REFERENCE),
                    serviceProviderLogo = arguments!!.getString(ARG_SERVICE_PROVIDER_LOGO_URL)
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.event.observe(this, Observer { handleEvent(it!!) })
        viewModel.viewState.observe(this, Observer { handleViewState(it!!) })
    }

    private fun handleEvent(action: Event) {
        when (action) {
            is C2BPaymentEvent.CancelPayment -> {
                activity?.run {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
            is C2BPaymentEvent.SetResult -> {
                when (val paymentStatus = action.paymentStatus) {
                    is PaymentResult.Success -> {
                        val intent = Intent().apply {
                            val bundle = Bundle().apply {
                                putString(
                                    MpesaSdk.ARG_RESULT_TRANSACTION_ID,
                                    paymentStatus.transactionId
                                )
                                putString(
                                    MpesaSdk.ARG_RESULT_CONVERSATION_ID,
                                    paymentStatus.conversationId
                                )
                            }
                            putExtras(bundle)
                        }
                        requireActivity().run {
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                    is PaymentResult.Error -> {
                        requireActivity().run {
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun handleViewState(viewState: C2BPaymentViewState) {
        when (viewState) {
            is C2BPaymentViewState.Idle -> renderIdleState(viewState)
            is C2BPaymentViewState.Authenticating -> renderAuthenticationView(viewState)
            C2BPaymentViewState.NetworkError,
            C2BPaymentViewState.UnknownError,
            C2BPaymentViewState.AuthenticationError,
            C2BPaymentViewState.InsufficientFundsError -> renderErrorView(viewState)
        }
    }

    private fun renderIdleState(viewState: C2BPaymentViewState.Idle) {
        paymentCard.render(
            amount = viewState.amount,
            phoneNumber = viewState.phoneNumber,
            providerCode = viewState.serviceProviderCode,
            providerLogo = viewState.serviceProviderLogo,
            providerName = viewState.serviceProviderName,
            payButtonIsEnabled = viewState.isSavingEnabled,
            onEditPhoneNumber = {
                viewModel.handleViewAction(C2BPaymentViewAction.EditPhoneNumber(it))
            },
            onPayButtonClicked = {
                viewModel.handleViewAction(C2BPaymentViewAction.PayButtonPressed)
            }
        )
        changeVisibility(
            errorViewIsGone = true,
            paymentViewIsGone = false,
            authenticationViewIsGone = true
        )
    }

    private fun renderAuthenticationView(viewState: C2BPaymentViewState.Authenticating) {
        authView.render(viewState.phoneNumber)
        changeVisibility(
            errorViewIsGone = true,
            paymentViewIsGone = true,
            authenticationViewIsGone = false
        )
    }

    private fun renderErrorView(viewState: C2BPaymentViewState) {
        var titleResId: Int = -1
        var descriptionResId: Int = -1
        var illustrationResId: Int = -1

        when (viewState) {
            C2BPaymentViewState.NetworkError -> {
                titleResId = R.string.c2b_payment_network_error_title
                illustrationResId = R.drawable.illustration_no_network
                descriptionResId = R.string.c2b_payment_network_error_description
            }
            C2BPaymentViewState.AuthenticationError -> {
                titleResId = R.string.c2b_payment_authentication_error_title
                illustrationResId = R.drawable.illustration_authentication_error
                descriptionResId = R.string.c2b_payment_authentication_error_description
            }
            C2BPaymentViewState.InsufficientFundsError -> {
                illustrationResId = R.drawable.illustration_insufficient_balance
                titleResId = R.string.c2b_payment_insufficient_balance_error_title
                descriptionResId = R.string.c2b_payment_isufficient_balance_error_description
            }
            C2BPaymentViewState.UnknownError -> {
                illustrationResId = R.drawable.illustration_generic_error
                titleResId = R.string.c2b_payment_failure_generic_error_title
                descriptionResId = R.string.c2b_payment_failure_generic_error_description
            }
        }
        errorView.render(
            titleResId = titleResId,
            descriptionResId = descriptionResId,
            illustrationResId = illustrationResId,
            actionButtonTextResId = R.string.c2b_payment_retry_button,
            onClickActionButton = {
                viewModel.handleViewAction(C2BPaymentViewAction.RetryPressed)
            }
        )
        changeVisibility(
            errorViewIsGone = false,
            paymentViewIsGone = true,
            authenticationViewIsGone = true
        )
    }

    private fun changeVisibility(
        errorViewIsGone: Boolean,
        paymentViewIsGone: Boolean,
        authenticationViewIsGone: Boolean
    ) {
        errorView.isGone = errorViewIsGone
        paymentCard.isGone = paymentViewIsGone
        authView.isGone = authenticationViewIsGone
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_c2b_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar.let {
            it.title = resources.getString(R.string.payment_activity_toolbar_title)
            it.setNavigationIcon(R.drawable.ic_close_24dp)
            it.setNavigationOnClickListener {
                viewModel.handleViewAction(C2BPaymentViewAction.CancelPressed)
            }
        }
    }
}