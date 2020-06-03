package mz.co.moovi.mpesalibui.payment.c2b

import mz.co.moovi.mpesalibui.ui.ViewAction

sealed class C2BPaymentViewAction : ViewAction {
    object RetryPressed : C2BPaymentViewAction()
    object CancelPressed : C2BPaymentViewAction()
    object PayButtonPressed : C2BPaymentViewAction()
    data class EditPhoneNumber(val phoneNumber: String) : C2BPaymentViewAction()
}