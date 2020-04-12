package mz.co.moovi.mpesalibui.extensions

import mz.co.moovi.mpesalibui.R

fun String.errorMessageResourceId(): Int {
    return when (this) {
        "INS_4" -> R.string.payment_response_ins_4
        "INS-5" -> R.string.payment_response_ins_5
        "INS-9" -> R.string.payment_response_ins_9
        "INS-10" -> R.string.payment_response_ins_10
        "INS-2051" -> R.string.payment_response_ins_2051
        "INS-2006" -> R.string.payment_response_ins_2006
        else -> R.string.payment_response_ins_unhandled_response
    }
}