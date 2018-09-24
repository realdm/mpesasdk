package mz.co.moovi.mpesalibui.payment

sealed class PaymentStatus() {
    data class Error(val messageResId: Int): PaymentStatus()
    data class Success(val transactionId: String, val conversationId: String): PaymentStatus()
}