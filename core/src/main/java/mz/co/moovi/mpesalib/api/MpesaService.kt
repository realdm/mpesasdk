package mz.co.moovi.mpesalib.api

import io.reactivex.Single

interface MpesaService {
    fun pay(paymentRequest: PaymentRequest): Single<PaymentResponse>
}