package mz.co.moovi.mpesalibui.payment

import mz.co.moovi.mpesalibui.ui.Action

sealed class PaymentViewModelAction : Action {
    object Cancel : PaymentViewModelAction()
    object EnablePaymentButton : PaymentViewModelAction()
    object DisablePaymentButton : PaymentViewModelAction()
    data class SendResult(val paymentStatus: PaymentStatus) : PaymentViewModelAction()
}