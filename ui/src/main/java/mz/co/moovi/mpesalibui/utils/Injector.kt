package mz.co.moovi.mpesalibui.utils

import mz.co.moovi.mpesalib.api.MpesaMpesaServiceImpl
import mz.co.moovi.mpesalib.api.MpesaRepositoryImpl
import mz.co.moovi.mpesalib.api.MpesaService
import mz.co.moovi.mpesalib.config.MpesaConfig
import mz.co.moovi.mpesalibui.MpesaSdk

object Injector {

    fun mPesaService(): MpesaService {
        val mpesaConfig = MpesaConfig(baseUrl = MpesaSdk.endpointUrl, apiKey = MpesaSdk.apiKey,
                publicKey = MpesaSdk.publicKey,
                httpLoggingInterceptor = MpesaSdk.httpLoggingInterceptor)
        val repository = MpesaRepositoryImpl(mpesaConfig)
        return MpesaMpesaServiceImpl(repository)
    }
}