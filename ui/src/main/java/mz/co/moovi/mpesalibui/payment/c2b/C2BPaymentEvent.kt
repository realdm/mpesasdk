package mz.co.moovi.mpesalibui.payment.c2b

import mz.co.moovi.mpesalibui.payment.PaymentResult
import mz.co.moovi.mpesalibui.ui.Event

sealed class C2BPaymentEvent : Event {
    object CancelPayment : C2BPaymentEvent()
    data class SetResult(val paymentStatus: PaymentResult) : C2BPaymentEvent()
}