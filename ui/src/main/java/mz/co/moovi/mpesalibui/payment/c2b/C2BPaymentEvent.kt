package mz.co.moovi.mpesalibui.payment.c2b

import mz.co.moovi.mpesalibui.payment.C2BPaymentSuccess
import mz.co.moovi.mpesalibui.architecture.Event

sealed class C2BPaymentEvent : Event {
    object CancelPayment : C2BPaymentEvent()
    data class SetResult(val c2BPaymentSuccess: C2BPaymentSuccess) : C2BPaymentEvent()
}