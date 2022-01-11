package mz.co.moovi.mpesalibui.utils

import mz.co.moovi.mpesalib.api.MpesaRepository
import mz.co.moovi.mpesalib.config.MpesaConfig
import mz.co.moovi.mpesalibui.MpesaSdk.apiKey
import mz.co.moovi.mpesalibui.MpesaSdk.endpointUrl
import mz.co.moovi.mpesalibui.MpesaSdk.publicKey

object Injector {
    val mPesaService by lazy {
        val config = MpesaConfig(baseUrl = endpointUrl, apiKey = apiKey, publicKey = publicKey)
        MpesaRepository(config)
    }
}