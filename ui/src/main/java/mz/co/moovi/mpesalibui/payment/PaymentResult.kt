package mz.co.moovi.mpesalibui.payment

sealed class PaymentResult() {
    object Error : PaymentResult()
    data class Success(val transactionId: String, val conversationId: String) : PaymentResult()
}