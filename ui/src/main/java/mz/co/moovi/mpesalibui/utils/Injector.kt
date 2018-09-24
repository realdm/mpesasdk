package mz.co.moovi.mpesalibui.utils

import mz.co.moovi.mpesalib.api.MpesaMpesaServiceImpl
import mz.co.moovi.mpesalib.api.MpesaRepositoryImpl
import mz.co.moovi.mpesalib.api.MpesaService
import mz.co.moovi.mpesalib.config.MpesaConfig
import mz.co.moovi.mpesalibui.MpesaSdk.SANDBOX_BASE_URL
import mz.co.moovi.mpesalibui.MpesaSdk.apiKey
import mz.co.moovi.mpesalibui.MpesaSdk.publicKey

object Injector {

    fun mPesaService(): MpesaService {
        val config = MpesaConfig(baseUrl = SANDBOX_BASE_URL, apiKey = apiKey, publicKey = publicKey)
        val repository = MpesaRepositoryImpl(config)
        return MpesaMpesaServiceImpl(repository)
    }
}