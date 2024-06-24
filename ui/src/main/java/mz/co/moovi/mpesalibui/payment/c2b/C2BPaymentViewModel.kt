package mz.co.moovi.mpesalibui.payment.c2b

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mz.co.moovi.mpesalib.api.MpesaService
import mz.co.moovi.mpesalib.api.Response
import mz.co.moovi.mpesalib.api.TransactionResponseCode
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentRequest
import mz.co.moovi.mpesalib.api.toTransactionResponseCode
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.payment.C2BPaymentSuccess
import mz.co.moovi.mpesalibui.utils.ReferenceGenerator
import mz.co.moovi.mpesalibui.utils.resolvableString

class C2BPaymentViewModel constructor(
    private val mPesaService: MpesaService,
    private val c2bParameters: C2BParameters
) : ViewModel() {

    private val _event = MutableSharedFlow<C2BPaymentEvent>()
    val event: Flow<C2BPaymentEvent>
        get() = _event

    private val _viewState = MutableStateFlow<C2BPaymentViewState>(C2BPaymentViewState.Initializing)
    val viewState: Flow<C2BPaymentViewState>
        get() = _viewState

    private var state = State(phoneNumber = "")

    init {
        emitViewState(readyToPayViewState())
    }

    fun handleViewAction(viewAction: C2BPaymentViewAction) {
        when (viewAction) {
            is C2BPaymentViewAction.RetryPressed -> retry()
            is C2BPaymentViewAction.CancelPressed -> cancel()
            is C2BPaymentViewAction.PayButtonPressed -> makePayment()
            is C2BPaymentViewAction.EditPhoneNumber -> editPhoneNumber(viewAction)
        }
    }

    private fun cancel() {
        emitEvent(C2BPaymentEvent.CancelPayment)
    }

    private fun retry() {
        emitViewState(readyToPayViewState())
    }

    private fun editPhoneNumber(viewAction: C2BPaymentViewAction.EditPhoneNumber) {
        editState {
            it.copy(phoneNumber = viewAction.phoneNumber)
        }
        emitViewState(readyToPayViewState())
    }

    private fun makePayment() {
        launch { emitViewState(authenticatingViewState()) }
        launch {
            mPesaService.c2bPayment(createRequest()).collect { response ->
                when (response) {
                    is Response.Error -> {
                        when (val code =
                            response.error?.responseCode?.toTransactionResponseCode()) {
                            is TransactionResponseCode.UserCode -> handleUserTransactionResponseCode(
                                code
                            )
                            is TransactionResponseCode.ServiceCode -> handleServiceTransactionResponseCode(
                                code
                            )
                            is TransactionResponseCode.DeveloperCode -> handleDeveloperTransactionResponseCode(
                                code
                            )
                            null -> {}
                        }
                    }
                    is Response.Success -> {
                        val data = response.data
                        when (data.responseCode.toTransactionResponseCode()) {
                            is TransactionResponseCode.ServiceCode.TransactionSuccess -> {
                                emitViewState(
                                    C2BPaymentViewState.PaymentSuccess(
                                        amount = c2bParameters.amount,
                                        providerName = c2bParameters.providerName
                                    )
                                )
                                emitEvent(
                                    C2BPaymentEvent.SetResult(
                                        C2BPaymentSuccess(
                                            transactionId = data.transactionId!!,
                                            conversationId = data.conversationId!!
                                        )
                                    ),
                                    delay = 3500
                                )
                            }
                            else -> {
                                emitViewState(
                                    createErrorViewState(
                                        title = R.string.c2b_payment_payment_generic_error_title,
                                        description = R.string.c2b_payment_payment_generic_error_description
                                    )
                                )
                            }
                        }
                    }
                    is Response.NetworkError -> {
                        emitViewState(
                            createErrorViewState(
                                title = R.string.c2b_payment_payment_generic_error_title,
                                description = R.string.c2b_payment_user_error_network_error
                            )
                        )
                    }
                    is Response.UnknownError -> {
                        emitViewState(
                            createErrorViewState(
                                title = R.string.c2b_payment_payment_generic_error_title,
                                description = R.string.c2b_payment_payment_generic_error_description
                            )
                        )
                    }
                }
            }
        }
    }

    private fun handleUserTransactionResponseCode(code: TransactionResponseCode.UserCode) {
        when (code) {
            is TransactionResponseCode.UserCode.InsuficientBalance -> {
                emitViewState(
                    createErrorViewState(
                        title = R.string.c2b_payment_payment_generic_error_title,
                        description = R.string.c2b_payment_user_error_insuficient_balance_message
                    )
                )
            }
            is TransactionResponseCode.UserCode.TransactionCancelledByCustomer -> {
                emitViewState(
                    createErrorViewState(
                        title = R.string.c2b_payment_payment_generic_error_title,
                        description = R.string.c2b_payment_user_error_cancelled_authentication
                    )
                )
            }
            TransactionResponseCode.UserCode.UserNotActive,
            TransactionResponseCode.UserCode.CustomerAccountNotActive -> {
                emitViewState(
                    createErrorViewState(
                        title = R.string.c2b_payment_payment_generic_error_title,
                        description = R.string.c2b_payment_user_error_account_not_active
                    )
                )
            }
            TransactionResponseCode.UserCode.CustomerProfileHasProblems -> {
                emitViewState(
                    createErrorViewState(
                        title = R.string.c2b_payment_payment_generic_error_title,
                        description = R.string.c2b_payment_user_error_customer_has_problems
                    )
                )
            }
            TransactionResponseCode.UserCode.InvalidNumber -> {
                emitViewState(
                    createErrorViewState(
                        title = R.string.c2b_payment_payment_generic_error_title,
                        description = R.string.c2b_payment_user_error_invalid_number
                    )
                )
            }

        }
    }

    private fun handleDeveloperTransactionResponseCode(code: TransactionResponseCode.DeveloperCode) {
        when (code) {
            TransactionResponseCode.DeveloperCode.InvalidSecurityCredential -> {
                emitViewState(
                    createErrorViewState(
                        title = R.string.c2b_payment_payment_generic_error_title,
                        description = R.string.c2b_payment_payment_generic_error_description
                    )
                )
            }
            else -> {
                emitViewState(
                    createErrorViewState(
                        title = R.string.c2b_payment_payment_generic_error_title,
                        description = R.string.c2b_payment_payment_generic_error_description
                    )
                )
            }
        }
    }

    private fun handleServiceTransactionResponseCode(code: TransactionResponseCode.ServiceCode) {
        when (code) {
            TransactionResponseCode.ServiceCode.TransactionSuccess -> {}
            else -> {
                emitViewState(
                    createErrorViewState(
                        title = R.string.c2b_payment_payment_generic_error_title,
                        description = R.string.c2b_payment_payment_generic_error_description
                    )
                )
            }
        }
    }

    private fun createErrorViewState(
        title: Int,
        description: Int
    ): C2BPaymentViewState.PaymentFailed {
        return C2BPaymentViewState.PaymentFailed(
            title = resolvableString(title),
            description = resolvableString(description)
        )
    }

    private fun createRequest(): C2BPaymentRequest {
        return C2BPaymentRequest(
            amount = c2bParameters.amount,
            customerMSISDN = "258${state.phoneNumber}",
            serviceProviderCode = c2bParameters.providerCode,
            transactionReference = c2bParameters.transactionRef,
            thirdPartyReference = ReferenceGenerator.generateReference(c2bParameters.transactionRef)
        )
    }

    private fun readyToPayViewState(): C2BPaymentViewState.ReadyToPay {
        return C2BPaymentViewState.ReadyToPay(
            amount = c2bParameters.amount,
            phoneNumber = state.phoneNumber,
            payButtonEnabled = hasValidPhoneNumber(),
            providerShortCode = c2bParameters.providerCode,
            providerLogo = c2bParameters.providerLogo,
            providerName = c2bParameters.providerName
        )
    }

    private fun authenticatingViewState(): C2BPaymentViewState.ProcessingPayment {
        return C2BPaymentViewState.ProcessingPayment(
            phoneNumber = state.phoneNumber,
            providerName = c2bParameters.providerName
        )
    }

    private fun hasValidPhoneNumber(): Boolean {
        val phoneNumber = state.phoneNumber
        val isValidMozambiqueNumber = if(phoneNumber.isEmpty()) {
            false
        } else {
            when (phoneNumber.take(2).toInt()) {
                in 82..89 -> true
                else -> false
            }
        }
        return phoneNumber.isNotEmpty() && phoneNumber.length == 9 && isValidMozambiqueNumber
    }

    data class State(val phoneNumber: String)

    private fun editState(editor: (State) -> State) {
        this.state = editor.invoke(state)
    }

    private fun emitEvent(event: C2BPaymentEvent, delay: Long? = null) {
        launch {
            delay?.run { delay(this) }
            _event.emit(event)
        }
    }

    private fun emitViewState(viewState: C2BPaymentViewState) {
        launch {
            _viewState.emit(viewState)
        }
    }

    private fun launch(executor: suspend () -> Unit) {
        viewModelScope.launch {
            executor.invoke()
        }
    }
}