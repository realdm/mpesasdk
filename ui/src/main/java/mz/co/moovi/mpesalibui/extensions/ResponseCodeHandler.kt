package mz.co.moovi.mpesalibui.extensions

import mz.co.moovi.mpesalibui.R

fun String.errorMessageResourceId(): Int {
    return when (this) {
        "ISN-9" -> R.string.payment_response_ins_9
        "INS-2006" -> R.string.payment_response_ins_2006
        else -> R.string.payment_response_ins_unhandled_response
    }
}