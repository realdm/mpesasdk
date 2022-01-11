package mz.co.moovi.mpesalibui.payment.c2b

import mz.co.moovi.mpesalibui.architecture.ViewState
import mz.co.moovi.mpesalibui.utils.ResolvableString

sealed class C2BPaymentViewState : ViewState {
    object Initializing : C2BPaymentViewState()
    data class ReadyToPay(
        val amount: String,
        val phoneNumber: String,
        val providerName: String,
        val providerLogo: String,
        val providerShortCode: String,
        val payButtonEnabled: Boolean
    ) : C2BPaymentViewState()

    data class ProcessingPayment(
        val phoneNumber: String,
        val providerName: String
    ) : C2BPaymentViewState()

    data class PaymentFailed(
        val title: ResolvableString,
        val description: ResolvableString
    ) : C2BPaymentViewState()

    data class PaymentSuccess(
        val amount: String,
        val providerName: String
    ) : C2BPaymentViewState()
}