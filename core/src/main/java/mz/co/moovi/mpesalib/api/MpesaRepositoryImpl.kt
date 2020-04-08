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


class MpesaRepositoryImpl(private val mpesaConfig: MpesaConfig) : MpesaRepository {

	private val retrofit by lazy {

		val okHttpClientBuilder = OkHttpClient.Builder()
				.readTimeout(0, TimeUnit.MILLISECONDS)

		mpesaConfig.httpLoggingInterceptor?.let {
			okHttpClientBuilder.addInterceptor(it)
		}

		val okHttpClient = okHttpClientBuilder.build()

		Retrofit.Builder()
				.client(okHttpClient)
				.baseUrl(mpesaConfig.baseUrl)
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
	}

	private val mPesaApi by lazy {
		retrofit.create(MpesaApi::class.java)
	}

	override fun pay(paymentRequest: PaymentRequest): Single<PaymentResponse> {
		val bearerToken = "Bearer ${KeyGenerator(mpesaConfig).bearerToken}"
		return mPesaApi.pay(paymentRequest = paymentRequest, bearerToken = bearerToken)
				.async(subscribeOn = Schedulers.newThread(), observeOn = AndroidSchedulers.mainThread())
	}
}