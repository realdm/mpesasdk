package mz.co.moovi.mpesalib.api

import mz.co.moovi.mpesalib.api.c2b.C2BPaymentRequest
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentResponse

interface MpesaService {
    suspend fun c2bPayment(paymentRequest: C2BPaymentRequest): Response<C2BPaymentResponse>
}