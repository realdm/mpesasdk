package mz.co.moovi.mpesalibui.payment

import mz.co.moovi.mpesalibui.payment.authentication.PaymentAuthenticationCardViewState
import mz.co.moovi.mpesalibui.payment.error.PaymentErrorCardViewState
import mz.co.moovi.mpesalibui.ui.ViewState

data class PaymentViewState(
        val paymentCard: PaymentCardViewState? = null,
        val errorCard: PaymentErrorCardViewState? = null,
        val authenticationCard: PaymentAuthenticationCardViewState? = null) : ViewState
