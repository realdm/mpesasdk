package mz.co.moovi.mpesalib.api

import kotlinx.coroutines.flow.Flow
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentRequest
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentResponse

interface MpesaService {
    fun c2bPayment(paymentRequest: C2BPaymentRequest): Flow<Response<C2BPaymentResponse>>
}