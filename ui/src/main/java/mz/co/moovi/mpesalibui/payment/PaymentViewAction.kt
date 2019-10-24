package mz.co.moovi.mpesalibui.payment

import mz.co.moovi.mpesalibui.ui.ViewAction

sealed class PaymentViewAction : ViewAction {
    object Retry : PaymentViewAction()
    object Cancel : PaymentViewAction()
    object MakePayment : PaymentViewAction()
    data class ProcessMockPin(val pin: Int?) : PaymentViewAction()
    data class ShowError(val messageResId: Int) : PaymentViewAction()
    data class AddPhoneNumber(val phoneNumber: String) : PaymentViewAction()
}