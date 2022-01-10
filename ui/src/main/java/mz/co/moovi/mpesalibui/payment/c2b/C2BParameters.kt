package mz.co.moovi.mpesalibui.payment.c2b

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class C2BParameters(
    val amount: String,
    val providerName: String,
    val providerCode: String,
    val providerLogo: String,
    val transactionRef: String
) : Parcelable