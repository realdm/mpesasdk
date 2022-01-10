package mz.co.moovi.mpesalib.api

sealed class TransactionResponseCode {
    sealed class ServiceCode : TransactionResponseCode() {
        object InternalError : ServiceCode()
        object RequestTimeout : ServiceCode()
        object TransactionFailed : ServiceCode()
        object DirectDebitMissing : ServiceCode()
        object TransactionSuccess : ServiceCode()
        object UnknownMpesaStatus : DeveloperCode()
        object TemporaryServiceOverload : ServiceCode()
        object DirectDebitAlreadyExists : ServiceCode()
    }

    sealed class DeveloperCode : TransactionResponseCode() {
        object InvalidApi : DeveloperCode()
        object NotAuthorized : DeveloperCode()
        object InvalidAmountUsed : DeveloperCode()
        object InvalidOperationType : DeveloperCode()
        object DuplicateTransaction : DeveloperCode()
        object InvalidShortCodeUsed : DeveloperCode()
        object InvalidReferenceUsed : DeveloperCode()
        object InvalidTransactionId : DeveloperCode()
        object NotAllParametersProvided : DeveloperCode()
        object InvalidSecurityCredential : DeveloperCode()
        object ParameterValidationFailed : DeveloperCode()
        object InvalidTransactionReference : DeveloperCode()
        object InvalidInitiatiorIdentifier : DeveloperCode()
        object InvalidThirdPartyReferenceUsed : DeveloperCode()
    }

    sealed class UserCode : TransactionResponseCode() {
        object UserNotActive : UserCode()
        object InvalidNumber : UserCode()
        object InsuficientBalance : UserCode()
        object CustomerAccountNotActive : UserCode()
        object CustomerProfileHasProblems : UserCode()
        object TransactionCancelledByCustomer : UserCode()
    }
}

fun String.toTransactionResponseCode(): TransactionResponseCode {
    return when (this) {
        "INS-0" -> TransactionResponseCode.ServiceCode.TransactionSuccess
        "INS-1" -> TransactionResponseCode.ServiceCode.InternalError
        "INS-2" -> TransactionResponseCode.DeveloperCode.InvalidApi
        "INS-4" -> TransactionResponseCode.UserCode.UserNotActive
        "INS-5" -> TransactionResponseCode.UserCode.TransactionCancelledByCustomer
        "INS-6" -> TransactionResponseCode.ServiceCode.TransactionFailed
        "INS-9" -> TransactionResponseCode.ServiceCode.RequestTimeout
        "INS-10" -> TransactionResponseCode.DeveloperCode.DuplicateTransaction
        "INS-13" -> TransactionResponseCode.DeveloperCode.InvalidShortCodeUsed
        "INS-14" -> TransactionResponseCode.DeveloperCode.InvalidReferenceUsed
        "INS-15" -> TransactionResponseCode.DeveloperCode.InvalidAmountUsed
        "INS-16" -> TransactionResponseCode.ServiceCode.TemporaryServiceOverload
        "INS-17" -> TransactionResponseCode.DeveloperCode.InvalidTransactionReference
        "INS-18" -> TransactionResponseCode.DeveloperCode.InvalidTransactionId
        "INS-19" -> TransactionResponseCode.DeveloperCode.InvalidThirdPartyReferenceUsed
        "INS-20" -> TransactionResponseCode.DeveloperCode.NotAllParametersProvided
        "INS-21" -> TransactionResponseCode.DeveloperCode.ParameterValidationFailed
        "INS-22" -> TransactionResponseCode.DeveloperCode.InvalidOperationType
        "INS-23" -> TransactionResponseCode.ServiceCode.UnknownMpesaStatus
        "INS-24" -> TransactionResponseCode.DeveloperCode.InvalidInitiatiorIdentifier
        "INS-25" -> TransactionResponseCode.DeveloperCode.InvalidSecurityCredential
        "INS-26" -> TransactionResponseCode.DeveloperCode.NotAuthorized
        "INS-993" -> TransactionResponseCode.ServiceCode.DirectDebitMissing
        "INS-994" -> TransactionResponseCode.ServiceCode.DirectDebitAlreadyExists
        "INS-995" -> TransactionResponseCode.UserCode.CustomerProfileHasProblems
        "INS-996" -> TransactionResponseCode.UserCode.CustomerAccountNotActive
        "INS-2051" -> TransactionResponseCode.UserCode.InvalidNumber
        "INS-2006" -> TransactionResponseCode.UserCode.InsuficientBalance
        else -> TransactionResponseCode.ServiceCode.UnknownMpesaStatus
    }
}