package mz.co.moovi.mpesalibui.utils

import mz.co.moovi.mpesalib.api.MpesaMpesaServiceImpl
import mz.co.moovi.mpesalib.api.MpesaRepositoryImpl
import mz.co.moovi.mpesalib.api.MpesaService
import mz.co.moovi.mpesalib.config.MpesaConfig
import mz.co.moovi.mpesalibui.MpesaSdk.apiKey
import mz.co.moovi.mpesalibui.MpesaSdk.endpointUrl
import mz.co.moovi.mpesalibui.MpesaSdk.publicKey

object Injector {

    fun mPesaService(): MpesaService {
        val config = MpesaConfig(baseUrl = endpointUrl, apiKey = apiKey, publicKey = publicKey)
        val repository = MpesaRepositoryImpl(config)
        return MpesaMpesaServiceImpl(repository)
    }
}