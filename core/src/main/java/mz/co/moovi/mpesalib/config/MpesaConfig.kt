package mz.co.moovi.mpesalib.config

import okhttp3.logging.HttpLoggingInterceptor

data class MpesaConfig(val baseUrl: String, val apiKey: String, val publicKey: String,
					   val httpLoggingInterceptor: HttpLoggingInterceptor? = null)