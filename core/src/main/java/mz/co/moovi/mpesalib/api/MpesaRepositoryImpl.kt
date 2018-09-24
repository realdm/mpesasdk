package mz.co.moovi.mpesalib.api

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mz.co.moovi.mpesalib.config.KeyGenerator
import mz.co.moovi.mpesalib.config.MpesaConfig
import mz.co.moovi.mpesalib.extensions.async
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MpesaRepositoryImpl(private val config: MpesaConfig) : MpesaRepository {

    companion object {
        private const val TIME_OUT_IN_MILLIS = 90000L
    }

    private val retrofit by lazy {
        val okHttp = OkHttpClient.Builder()
                .readTimeout(TIME_OUT_IN_MILLIS, TimeUnit.SECONDS)
                .build()

        Retrofit.Builder()
                .client(okHttp)
                .baseUrl(config.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    private val mPesaApi by lazy {
        retrofit.create(MpesaApi::class.java)
    }

    override fun pay(paymentRequest: PaymentRequest): Single<PaymentResponse> {
        val bearerToken = "Bearer ${KeyGenerator(config).bearerToken}"
        return mPesaApi.pay(paymentRequest = paymentRequest, bearerToken = bearerToken)
                .async(subscribeOn = Schedulers.newThread(), observeOn = AndroidSchedulers.mainThread())
    }
}