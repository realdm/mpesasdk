package mz.co.moovi.mpesalibui.payment

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class C2BPaymentSuccess(val transactionId: String, val conversationId: String) : Parcelable