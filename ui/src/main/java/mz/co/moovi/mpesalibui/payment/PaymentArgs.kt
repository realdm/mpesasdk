package mz.co.moovi.mpesalibui.payment

data class PaymentArgs(
        val amount: String,
        val serviceProviderName: String,
        val serviceProviderCode: String,
        val transactionReference: String,
        val serviceProviderLogoUrl: String)