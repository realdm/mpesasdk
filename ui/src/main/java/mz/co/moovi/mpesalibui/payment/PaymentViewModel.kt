package mz.co.moovi.mpesalibui.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.shopify.livedataktx.SingleLiveData
import io.reactivex.disposables.Disposable
import mz.co.moovi.mpesalib.api.MpesaService
import mz.co.moovi.mpesalib.api.PaymentRequest
import mz.co.moovi.mpesalib.api.PaymentResponse
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.extensions.errorMessageResourceId
import mz.co.moovi.mpesalibui.payment.authentication.PaymentAuthenticationCardViewState
import mz.co.moovi.mpesalibui.payment.error.PaymentErrorCardViewState
import mz.co.moovi.mpesalibui.ui.BaseViewModel
import mz.co.moovi.mpesalibui.ui.ViewState
import mz.co.moovi.mpesalibui.utils.ReferenceGenerator
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class PaymentViewModel(private val mpesaService: MpesaService, val args: PaymentArgs) : BaseViewModel<PaymentViewAction, PaymentAction, PaymentViewState>() {

    companion object {
        private const val VODACOM_NUMBER_MAX_DIGITS = 9
    }

    private val _action = SingleLiveData<PaymentAction>()
    override val action: LiveData<PaymentAction>
        get() = _action

    private val _viewState = MutableLiveData<ViewState>()
    override val viewState: LiveData<ViewState>
        get() = _viewState

    private var phoneNumber: String = ""
    private val amount: String = args.amount
    private var disposable: Disposable? = null
    private lateinit var thirdPartyReference: String
    private val serviceProviderName: String = args.serviceProviderName
    private val serviceProviderCode: String = args.serviceProviderCode
    private val transactionReference: String = args.transactionReference
    private val serviceProviderLogoUrl: String = args.serviceProviderLogoUrl

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

    private fun hasValidPhoneNumber(): Boolean = phoneNumber.isNotEmpty() && phoneNumber.length == VODACOM_NUMBER_MAX_DIGITS

    private fun onMakePayment() {
        val viewState = PaymentAuthenticationCardViewState(phoneNumber = "(+258)$phoneNumber")
        _viewState.postValue(PaymentViewState(authenticationCard = viewState))
        val paymentRequest = createPaymentRequest()
        disposable = mpesaService.pay(paymentRequest).subscribe { response, throwable -> handleResponse(response, throwable) }
    }


    private fun createPaymentRequest(): PaymentRequest {
        return PaymentRequest(
                input_Amount = amount,
                input_CustomerMSISDN = "258$phoneNumber",
                input_ServiceProviderCode = serviceProviderCode,
                input_TransactionReference = transactionReference,
                input_ThirdPartyReference = ReferenceGenerator.generateReference(transactionReference))
    }

    private fun handleResponse(response: PaymentResponse?, throwable: Throwable?) {
        response?.run {
            when (response.output_ResponseCode) {
                "INS-0" -> {
                    val action = PaymentAction.SendResult(PaymentStatus.Success(response.output_TransactionID, response.output_ConversationID))
                    _action.postValue(action)
                }
            }
        }

        throwable?.run {
            when (throwable) {
                is HttpException -> {
                    val json = throwable.response().errorBody()?.string()
                    val gsonResponse = Gson().fromJson(json, PaymentResponse::class.java)
                    val outputResponse = gsonResponse.output_ResponseCode
                    val errorMessageId = outputResponse?.errorMessageResourceId()
                            ?: R.string.payment_response_ins_unhandled_response
                    postErrorCardViewState(errorMessageId)
                }
                is UnknownHostException -> {
                    postErrorCardViewState(R.string.payment_response_network_error)
                }
                is SocketTimeoutException -> {
                    postErrorCardViewState(R.string.payment_response_ins_9)
                }
            }

            thirdPartyReference = ReferenceGenerator.generateReference(transactionReference)
        }
    }

    private fun postErrorCardViewState(messageResId: Int) {
        val errorCard = PaymentErrorCardViewState(messageResId = messageResId)
        val viewState = PaymentViewState(errorCard = errorCard)
        _viewState.postValue(viewState)
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }
}