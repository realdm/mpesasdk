package mz.co.moovi.mpesalibui.payment

import mz.co.moovi.mpesalibui.ui.ViewState

data class PaymentCardViewState(val amount: String,
                                val serviceProviderName: String,
                                val serviceProviderCode: String,
                                val serviceProviderLogo: String): ViewState