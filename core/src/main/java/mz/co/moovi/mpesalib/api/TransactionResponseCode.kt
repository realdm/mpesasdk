package mz.co.moovi.mpesalib.api

enum class TransactionResponseCode {
    UNKNOWN_CODE,
    TRANSACTION_FAILED,
    INVALID_CREDENTIALS,
    INSUFFICIENT_BALANCE,
    TRANSACTION_SUCCESSFUL
}

fun String.toTransactionResponseCode(): TransactionResponseCode {
    return when (this) {
        "INS-6" -> TransactionResponseCode.TRANSACTION_FAILED
        "INS-0" -> TransactionResponseCode.TRANSACTION_SUCCESSFUL
        "INS-2001" -> TransactionResponseCode.INVALID_CREDENTIALS
        "INS-2006" -> TransactionResponseCode.INSUFFICIENT_BALANCE
        else -> TransactionResponseCode.UNKNOWN_CODE
    }
}