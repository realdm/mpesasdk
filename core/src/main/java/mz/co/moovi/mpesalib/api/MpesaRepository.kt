package mz.co.moovi.mpesalib.api

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentRequest
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentResponse
import mz.co.moovi.mpesalib.config.KeyGenerator
import mz.co.moovi.mpesalib.config.MpesaConfig
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit


class MpesaRepository(private val config: MpesaConfig) : MpesaService {

    companion object {
        private val LOG_TAG = MpesaRepository::class.simpleName
    }

    private val api by lazy {
        retrofit.create(MpesaApi::class.java)
    }

    private val moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    private val retrofit by lazy {
        val okHttp = OkHttpClient.Builder()
            .readTimeout(Integer.MAX_VALUE.toLong(), TimeUnit.MILLISECONDS)
            .build()

        Retrofit.Builder()
            .client(okHttp)
            .baseUrl(config.baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    override suspend fun c2bPayment(paymentRequest: C2BPaymentRequest): Response<C2BPaymentResponse> {
        val bearerToken = """Bearer ${KeyGenerator(config).bearerToken}"""
        return try {
            val paymentResponse =
                api.c2bPayment(bearerToken = bearerToken, paymentRequest = paymentRequest)
            Response.Success(paymentResponse)
        } catch (exception: Throwable) {
            when (exception) {
                is HttpException -> {
                    val jsonAdapter: JsonAdapter<C2BPaymentResponse> =
                        moshi.adapter(C2BPaymentResponse::class.java)

                    val errorBodyJson = exception.response()?.errorBody()?.string()
                    Log.d(LOG_TAG, "Payment failed. Json error body : $errorBodyJson")

                    val errorBodyResponse = try {
                        jsonAdapter.fromJson(errorBodyJson)
                    } catch (e: Exception) {
                        Log.e(
                            LOG_TAG,
                            "Caught exception while parsing error json body. [Message: ${exception.message()}]"
                        )
                        null
                    }

                    Response.Error(
                        code = exception.code(),
                        data = errorBodyResponse,
                        throwable = exception
                    )
                }
                else -> {
                    Response.Error(
                        code = 400,
                        data = null,
                        throwable = exception
                    )
                }
            }
        }
    }
}