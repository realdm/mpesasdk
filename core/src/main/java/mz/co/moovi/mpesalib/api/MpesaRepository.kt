package mz.co.moovi.mpesalib.api

import io.reactivex.Single

interface MpesaRepository {
    fun pay(paymentRequest: PaymentRequest): Single<PaymentResponse>
}