package mz.co.moovi.mpesalib.api

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface MpesaApi {

    @Headers("Content-Type: application/json", "Origin: developer.mpesa.vm.co.mz")
    @POST("/ipg/v1/c2bpayment/")
    fun pay(@Header("Authorization") bearerToken: String, @Body paymentRequest: PaymentRequest): Single<PaymentResponse>


}