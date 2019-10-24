package mz.co.moovi.mpesalibui.payment.devtools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.shopify.livedataktx.SingleLiveData
import mz.co.moovi.mpesalibui.MpesaSdk
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.payment.PaymentAction
import mz.co.moovi.mpesalibui.payment.PaymentArgs
import mz.co.moovi.mpesalibui.payment.PaymentCardViewState
import mz.co.moovi.mpesalibui.payment.PaymentStatus
import mz.co.moovi.mpesalibui.payment.PaymentViewAction
import mz.co.moovi.mpesalibui.payment.PaymentViewState
import mz.co.moovi.mpesalibui.payment.authentication.PaymentAuthenticationCardViewState
import mz.co.moovi.mpesalibui.payment.error.PaymentErrorCardViewState
import mz.co.moovi.mpesalibui.ui.BaseViewModel
import mz.co.moovi.mpesalibui.ui.ViewState
import mz.co.moovi.mpesalibui.utils.ReferenceGenerator

class MockAuthPaymentViewModel(args: PaymentArgs) : BaseViewModel<PaymentViewAction, PaymentAction, PaymentViewState>() {

    private val _action = SingleLiveData<PaymentAction>()
    override val action: LiveData<PaymentAction>
        get() = _action

    private val _viewState = MutableLiveData<ViewState>()
    override val viewState: LiveData<ViewState>
        get() = _viewState

    private var phoneNumber: String = ""
    private val amount: String = args.amount
    private val serviceProviderName: String = args.serviceProviderName
    private val serviceProviderCode: String = args.serviceProviderCode
    private val transactionReference: String = args.transactionReference
    private val serviceProviderLogoUrl: String = args.serviceProviderLogoUrl
    private val pins = MpesaSdk.mockAuthInfo?.pins
            ?: throw IllegalStateException("At least the success pin must be specified to use Mock auth mode")

    init {
        onInit()
    }

    private fun onInit() {
        val paymentCard = PaymentCardViewState(
                amount = amount,
                serviceProviderName = serviceProviderName,
                serviceProviderCode = serviceProviderCode,
                serviceProviderLogo = serviceProviderLogoUrl)

        val viewState = PaymentViewState(paymentCard = paymentCard)
        _viewState.postValue(viewState)

        postPayButtonStateAction()
    }

    override fun handleViewAction(viewAction: PaymentViewAction) {
        when (viewAction) {
            is PaymentViewAction.Retry -> onRetry()
            is PaymentViewAction.Cancel -> onCancel()
            is PaymentViewAction.MakePayment -> onMakePayment()
            is PaymentViewAction.AddPhoneNumber -> onAddNumber(viewAction)
            is PaymentViewAction.ProcessMockPin -> onProcessMockPin(viewAction)
            is PaymentViewAction.ShowError -> postErrorCardViewState(viewAction.messageResId)
        }
    }

    private fun onCancel() {
        _action.postValue(PaymentAction.Cancel)
    }

    private fun onRetry() {
        val paymentCard = PaymentCardViewState(
                amount = amount,
                serviceProviderName = serviceProviderName,
                serviceProviderCode = serviceProviderCode,
                serviceProviderLogo = serviceProviderLogoUrl)
        val viewState = PaymentViewState(paymentCard = paymentCard)
        _viewState.postValue(viewState)
    }

    private fun onAddNumber(viewAction: PaymentViewAction.AddPhoneNumber) {
        phoneNumber = viewAction.phoneNumber
        postPayButtonStateAction()
    }

    private fun postPayButtonStateAction() {
        val action = if (hasValidPhoneNumber()) {
            PaymentAction.EnablePaymentButton
        } else {
            PaymentAction.DisablePaymentButton
        }
        _action.postValue(action)
    }

    private fun hasValidPhoneNumber(): Boolean {
        return phoneNumber.isNotEmpty() && phoneNumber.length == 9
    }

    private fun onMakePayment() {
        val viewState = PaymentAuthenticationCardViewState(phoneNumber = "(+258)$phoneNumber")
        _viewState.postValue(PaymentViewState(authenticationCard = viewState))
        _action.postValue(PaymentAction.ShowMockPinDialog(
                amount = amount,
                providerName = serviceProviderName,
                reference = ReferenceGenerator.generateReference(transactionReference)
        ))
    }

    private fun postErrorCardViewState(messageResId: Int) {
        val errorCard = PaymentErrorCardViewState(messageResId = messageResId)
        val viewState = PaymentViewState(errorCard = errorCard)
        _viewState.postValue(viewState)
    }

    private fun onProcessMockPin(viewAction: PaymentViewAction.ProcessMockPin) {
        when (pins.find { it.pin == viewAction.pin }) {
            is MockAuthPin.Success -> {
                val action = PaymentAction.SendResult(PaymentStatus.Success("mock_transaction_id", "mock_conversation_id"))
                _action.postValue(action)
            }
            is MockAuthPin.NotEnoughFunds -> postErrorCardViewState(R.string.payment_response_ins_2006)
            else -> postErrorCardViewState(R.string.payment_response_ins_unhandled_response)
        }
    }
}