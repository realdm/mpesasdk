package mz.co.moovi.mpesalib.api

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentRequest
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentResponse
import mz.co.moovi.mpesalib.config.KeyGenerator
import mz.co.moovi.mpesalib.config.MpesaConfig
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit


class MpesaRepository(private val config: MpesaConfig) : MpesaService {

    companion object {
        private const val PAYMENT_TIMEOUT = 90000L
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
            .readTimeout(PAYMENT_TIMEOUT, TimeUnit.MILLISECONDS)
            .build()

        Retrofit.Builder()
            .client(okHttp)
            .baseUrl(config.baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }


    override fun c2bPayment(paymentRequest: C2BPaymentRequest): Flow<Response<C2BPaymentResponse>> {
        val bearerToken = """Bearer ${KeyGenerator(config).bearerToken}"""
        return flow {
            try {
                val paymentResponse =
                    api.c2bPayment(bearerToken = bearerToken, paymentRequest = paymentRequest)
                emit(Response.Success(paymentResponse))
            } catch (exception: Throwable) {
                val error = when (exception) {
                    is HttpException -> {
                        val jsonAdapter: JsonAdapter<C2BPaymentResponse> =
                            moshi.adapter(C2BPaymentResponse::class.java)

                        val errorBodyJson = exception.response()?.errorBody()?.string()

                        val errorBodyResponse = try {
                            jsonAdapter.fromJson(errorBodyJson)
                        } catch (e: Exception) {
                            Log.e(
                                LOG_TAG,
                                "Caught exception while parsing error json body. [Message: ${exception.message()}]"
                            )
                            null
                        }

                        if (errorBodyResponse == null) {
                            Response.UnknownError()
                        } else {
                            Response.Error(
                                code = exception.code(),
                                error = errorBodyResponse,
                                throwable = exception
                            )
                        }

                    }
                    is IOException,
                    is SocketTimeoutException -> {
                        Response.NetworkError()
                    }
                    else -> {
                        Response.UnknownError()
                    }
                }
                emit(error)
            }
        }
    }
}