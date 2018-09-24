package mz.co.moovi.mpesalibui.payment

import mz.co.moovi.mpesalibui.ui.ViewAction

sealed class PaymentViewAction : ViewAction {
    object Retry : PaymentViewAction()
    object Cancel : PaymentViewAction()
    object MakePayment : PaymentViewAction()
    data class ShowError(val messageResId: Int) : PaymentViewAction()
    data class AddPhoneNumber(val phoneNumber: String) : PaymentViewAction()
    data class Init(
            val amount: String,
            val serviceProviderCode: String,
            val thirdPartyReference: String,
            val transactionReference: String,
            val serviceProviderName: String,
            val serviceProviderLogoUrl: String) : PaymentViewAction()
}