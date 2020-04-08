package mz.co.moovi.mpesalibui.payment

sealed class PaymentStatus() {
    data class Success(val transactionId: String, val conversationId: String,
                       val thirdPartyReference: String): PaymentStatus()
}