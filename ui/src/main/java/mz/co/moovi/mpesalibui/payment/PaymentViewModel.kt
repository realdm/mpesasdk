package mz.co.moovi.mpesalibui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.shopify.livedataktx.SingleLiveData
import io.reactivex.disposables.Disposable
import mz.co.moovi.mpesalib.api.PaymentRequest
import mz.co.moovi.mpesalib.api.PaymentResponse
import mz.co.moovi.mpesalib.api.MpesaService
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.extensions.errorMessageResourceId
import mz.co.moovi.mpesalibui.payment.authentication.PaymentAuthenticationCardViewState
import mz.co.moovi.mpesalibui.payment.error.PaymentErrorCardViewState
import mz.co.moovi.mpesalibui.ui.Action
import mz.co.moovi.mpesalibui.ui.ViewAction
import mz.co.moovi.mpesalibui.ui.ViewState
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class PaymentViewModel(private val mpesaService: MpesaService) : ViewModel() {

    private val _action = SingleLiveData<Action>()
    val action: LiveData<Action>
        get() = _action

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState>
        get() = _viewState

    private lateinit var amount: String
    private var phoneNumber: String = ""
    private lateinit var disposable: Disposable
    private lateinit var serviceProviderName: String
    private lateinit var serviceProviderCode: String
    private lateinit var thirdPartyReference: String
    private lateinit var transactionReference: String
    private lateinit var serviceProviderLogoUrl: String

    fun handleViewAction(viewAction: ViewAction) {
        when (viewAction) {
            is PaymentViewAction.Retry -> onRetry()
            is PaymentViewAction.Cancel -> onCancel()
            is PaymentViewAction.Init -> onInit(viewAction)
            is PaymentViewAction.MakePayment -> onMakePayment()
            is PaymentViewAction.AddPhoneNumber -> onAddNumber(viewAction)
            is PaymentViewAction.ShowError -> postErrorCardViewState(viewAction.messageResId)

        }
    }

    private fun onCancel() {
        _action.postValue(PaymentViewModelAction.Cancel)
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

    private fun onInit(viewAction: PaymentViewAction.Init) {
        amount = viewAction.amount
        serviceProviderCode = viewAction.serviceProviderCode
        thirdPartyReference = viewAction.thirdPartyReference
        serviceProviderName = viewAction.serviceProviderName
        transactionReference = viewAction.transactionReference
        serviceProviderLogoUrl = viewAction.serviceProviderLogoUrl

        val paymentCard = PaymentCardViewState(
                amount = amount,
                serviceProviderName = serviceProviderName,
                serviceProviderCode = serviceProviderCode,
                serviceProviderLogo = viewAction.serviceProviderLogoUrl)

        val viewState = PaymentViewState(paymentCard = paymentCard)
        _viewState.postValue(viewState)
    }

    private fun onAddNumber(viewAction: PaymentViewAction.AddPhoneNumber) {
        phoneNumber = viewAction.phoneNumber
        val isValid = !phoneNumber.isEmpty() && phoneNumber.length == 7
        val action = if (isValid) PaymentViewModelAction.EnablePaymentButton else PaymentViewModelAction.DisablePaymentButton
        _action.postValue(action)
    }

    private fun onMakePayment() {
        val paymentRequest = createPaymentRequest()
        disposable = mpesaService.pay(paymentRequest)
                .subscribe { response, throwable -> handleResponse(response, throwable) }

        val viewState = PaymentAuthenticationCardViewState(phoneNumber = "(+258) 84$phoneNumber")
        _viewState.postValue(PaymentViewState(authenticationCard = viewState))
    }

    private fun createPaymentRequest(): PaymentRequest {
        return PaymentRequest(
                input_Amount = amount,
                input_CustomerMSISDN = "25884$phoneNumber",
                input_ThirdPartyReference = thirdPartyReference,
                input_ServiceProviderCode = serviceProviderCode,
                input_TransactionReference = transactionReference)
    }

    private fun handleResponse(response: PaymentResponse?, throwable: Throwable?) {
        response?.run {
            val responseCode = response.output_ResponseCode
            when (responseCode) {
                "INS-0" -> {
                    val action = PaymentViewModelAction.SendResult(PaymentStatus.Success(response.output_TransactionID, response.output_ConversationID))
                    _action.postValue(action)
                }
            }
        }

        throwable?.run {
            when (throwable) {
                is HttpException -> {
                    val json = throwable.response().errorBody()?.string()
                    val response = Gson().fromJson(json, PaymentResponse::class.java)
                    val errorMessageId = response.output_ResponseCode.errorMessageResourceId()
                    postErrorCardViewState(errorMessageId)
                }
                is UnknownHostException -> {
                    postErrorCardViewState(R.string.payment_response_network_error)
                }
                is SocketTimeoutException -> {
                    postErrorCardViewState(R.string.payment_response_ins_9)
                }
            }
        }
    }

    private fun postErrorCardViewState(messageResId: Int) {
        val errorCard = PaymentErrorCardViewState(messageResId = messageResId)
        val viewState = PaymentViewState(errorCard = errorCard)
        _viewState.postValue(viewState)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

}