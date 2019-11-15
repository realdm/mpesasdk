package mz.co.moovi.mpesalib.api

import io.reactivex.Single

interface MpesaService {
    fun pay(paymentRequest: PaymentRequest): Single<PaymentResponse>

    /**
     * B2C payment fun
     * [request] payment request
     */
    fun payB2C(request: PaymentRequest): Single<PaymentResponse>
}