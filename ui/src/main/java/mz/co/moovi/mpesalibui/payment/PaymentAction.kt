package mz.co.moovi.mpesalibui.payment

import mz.co.moovi.mpesalibui.ui.Action

sealed class PaymentAction : Action {
    object Cancel : PaymentAction()
    object EnablePaymentButton : PaymentAction()
    object DisablePaymentButton : PaymentAction()
    data class SendResult(val paymentStatus: PaymentStatus) : PaymentAction()
    data class ShowMockPinDialog(val providerName: String, val amount: String, val reference: String) : PaymentAction()
}