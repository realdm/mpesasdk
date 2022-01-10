package mz.co.moovi.mpesalib.api

import mz.co.moovi.mpesalib.api.c2b.C2BPaymentRequest
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface MpesaApi {
    @Headers("Content-Type: application/json", "Origin: developer.mpesa.vm.co.mz")
    @POST("/ipg/v1x/c2bPayment/singleStage/")
    suspend fun c2bPayment(
        @Header("Authorization") bearerToken: String,
        @Body paymentRequest: C2BPaymentRequest
    ): C2BPaymentResponse
}