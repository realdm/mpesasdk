package mz.co.moovi.mpesalibui.payment.c2b

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopify.livedataktx.SingleLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mz.co.moovi.mpesalib.api.MpesaService
import mz.co.moovi.mpesalib.api.Response
import mz.co.moovi.mpesalib.api.TransactionResponseCode
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentRequest
import mz.co.moovi.mpesalib.api.toTransactionResponseCode
import mz.co.moovi.mpesalibui.payment.PaymentResult
import mz.co.moovi.mpesalibui.ui.ViewAction
import mz.co.moovi.mpesalibui.utils.ReferenceGenerator

class C2BPaymentViewModel(
    private val arguments: Arguments,
    private val mpesaService: MpesaService
) :
    ViewModel() {

    companion object {
        private const val COUNTRY_CODE_PREFIX = "(+258)"
    }

    private val _event = SingleLiveData<C2BPaymentEvent>()
    val event: LiveData<C2BPaymentEvent>
        get() = _event

    private val _viewState = MutableLiveData<C2BPaymentViewState>()
    val viewState: LiveData<C2BPaymentViewState>
        get() = _viewState

    private var state = State(phoneNumber = "")

    init {
        _viewState.postValue(createIdleViewState())
    }

    fun handleViewAction(viewAction: ViewAction) {
        when (viewAction) {
            is C2BPaymentViewAction.RetryPressed -> retry()
            is C2BPaymentViewAction.CancelPressed -> cancel()
            is C2BPaymentViewAction.PayButtonPressed -> onMakePayment()
            is C2BPaymentViewAction.EditPhoneNumber -> editPhoneNumber(viewAction)
        }
    }

    private fun cancel() {
        _event.postValue(C2BPaymentEvent.CancelPayment)
    }

    private fun retry() {
        _viewState.postValue(createIdleViewState())
    }

    private fun editPhoneNumber(viewAction: C2BPaymentViewAction.EditPhoneNumber) {
        state = state.copy(phoneNumber = viewAction.phoneNumber)
        _viewState.postValue(createIdleViewState())
    }

    private fun onMakePayment() {
        _viewState.postValue(createAuthenticatingViewState())
        viewModelScope.launch(viewModelScope.coroutineContext + Dispatchers.IO) {
            val response = mpesaService.c2bPayment(createRequest())
            when (response) {
                is Response.Error -> {
                    when (response.data?.responseCode?.toTransactionResponseCode()) {
                        TransactionResponseCode.INVALID_CREDENTIALS -> {
                            _viewState.postValue(C2BPaymentViewState.AuthenticationError)
                        }
                        TransactionResponseCode.INSUFFICIENT_BALANCE -> {
                            _viewState.postValue(C2BPaymentViewState.InsufficientFundsError)
                        }
                        else -> {
                            _viewState.postValue(C2BPaymentViewState.UnknownError)
                        }
                    }
                }
                is Response.Success -> {
                    val data = response.data
                    when (data.responseCode.toTransactionResponseCode()) {
                        TransactionResponseCode.TRANSACTION_SUCCESSFUL -> {
                            _event.postValue(
                                C2BPaymentEvent.SetResult(
                                    PaymentResult.Success(
                                        transactionId = data.transactionId!!,
                                        conversationId = data.conversationId!!
                                    )
                                )
                            )
                        }
                        else -> {
                            _viewState.postValue(C2BPaymentViewState.UnknownError)
                        }
                    }
                }
            }
        }
    }


    private fun createRequest(): C2BPaymentRequest {
        return C2BPaymentRequest(
            amount = arguments.amount,
            customerMSISDN = "258${state.phoneNumber}",
            serviceProviderCode = arguments.serviceProviderCode,
            transactionReference = arguments.transactionReference,
            thirdPartyReference = ReferenceGenerator.generateReference(arguments.transactionReference)
        )
    }

    private fun createIdleViewState(): C2BPaymentViewState.Idle {
        return C2BPaymentViewState.Idle(
            amount = arguments.amount,
            phoneNumber = state.phoneNumber,
            isSavingEnabled = hasValidPhoneNumber(),
            serviceProviderCode = arguments.serviceProviderCode,
            serviceProviderLogo = arguments.serviceProviderLogo,
            serviceProviderName = arguments.serviceProviderName
        )
    }

    private fun createAuthenticatingViewState(): C2BPaymentViewState.Authenticating {
        return C2BPaymentViewState.Authenticating(
            phoneNumber = state.phoneNumber,
            serviceProviderName = arguments.serviceProviderName
        )
    }

    private fun hasValidPhoneNumber(): Boolean {
        val phoneNumber = state.phoneNumber
        val isValidVodacomMOZNumber = when (phoneNumber.take(2)) {
            "84", "85" -> true
            else -> false
        }
        return phoneNumber.isNotEmpty() && phoneNumber.length == 9 && isValidVodacomMOZNumber
    }

    data class State(val phoneNumber: String)

    data class Arguments(
        val amount: String,
        val serviceProviderLogo: String,
        val serviceProviderCode: String,
        val serviceProviderName: String,
        val transactionReference: String
    )
}