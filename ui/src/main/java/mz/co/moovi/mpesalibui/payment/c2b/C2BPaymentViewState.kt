package mz.co.moovi.mpesalibui.payment.c2b

import mz.co.moovi.mpesalibui.ui.ViewState

sealed class C2BPaymentViewState : ViewState {
    data class Idle(
        val amount: String,
        val phoneNumber: String,
        val isSavingEnabled: Boolean,
        val serviceProviderName: String,
        val serviceProviderCode: String,
        val serviceProviderLogo: String
    ) : C2BPaymentViewState()

    data class Authenticating(
        val phoneNumber: String,
        val serviceProviderName: String
    ) : C2BPaymentViewState()

    object UnknownError : C2BPaymentViewState()
    object NetworkError : C2BPaymentViewState()
    object AuthenticationError : C2BPaymentViewState()
    object InsufficientFundsError : C2BPaymentViewState()
}