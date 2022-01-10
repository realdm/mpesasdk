package mz.co.moovi.mpesalibui

import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import mz.co.moovi.mpesalibui.payment.C2BPaymentSuccess
import mz.co.moovi.mpesalibui.payment.c2b.C2BParameters
import mz.co.moovi.mpesalibui.payment.c2b.C2BResultContract

object MpesaSdk {

    const val PRODUCTION_BASE_URL = "https://api.vm.co.mz:18352"
    const val SANDBOX_BASE_URL = "https://api.sandbox.vm.co.mz:18352"

    private var initialized = false

    private lateinit var _apiKey: String
    val apiKey: String
        get() = _apiKey

    private lateinit var _serviceProviderCode: String
    val serviceProviderCode: String
        get() = _serviceProviderCode

    private lateinit var _serviceProviderName: String
    val serviceProviderName: String
        get() = _serviceProviderName

    private lateinit var _serviceProviderLogoUrl: String
    val serviceProviderLogoUrl: String
        get() = _serviceProviderLogoUrl

    private lateinit var _endpointUrl: String
    val endpointUrl: String
        get() = _endpointUrl

    private lateinit var _publicKey: String
    val publicKey: String
        get() = _publicKey

    fun init(
        apiKey: String,
        publicKey: String,
        endpointUrl: String,
        serviceProviderName: String,
        serviceProviderCode: String,
        serviceProviderLogoUrl: String
    ) {
        if (initialized) {
            throw IllegalArgumentException("SDK is already initialized")
        }
        _apiKey = apiKey
        _publicKey = publicKey
        _endpointUrl = endpointUrl
        _serviceProviderCode = serviceProviderCode
        _serviceProviderName = serviceProviderName
        _serviceProviderLogoUrl = serviceProviderLogoUrl
        initialized = true
    }

    /**
     * Starts the payment activity from a composable.
     */
    @Composable
    fun pay(
        amount: String,
        transactionReference: String,
        launcher: ManagedActivityResultLauncher<C2BParameters, C2BPaymentSuccess?>,
    ) {
        if (!initialized) {
            throw IllegalStateException("SDK must be initialized before making a payment")
        }
        val c2bParams = createC2BParameters(
            amount = amount,
            transactionReference = transactionReference
        )
        launcher.launch(c2bParams)
    }


    /**
     * Starts the payment activity using the new Activity for result API's
     */
    fun pay(
        amount: String,
        activity: AppCompatActivity,
        transactionReference: String,
        onPaymentComplete: (C2BPaymentSuccess?) -> Unit
    ) {
        if (!initialized) {
            throw IllegalArgumentException("SDK must be initialized before making a payment")
        }

        val makePayment = activity.registerForActivityResult(C2BResultContract()) {
            onPaymentComplete.invoke(it)
        }

        val c2bParams = createC2BParameters(
            amount = amount,
            transactionReference = transactionReference
        )
        makePayment.launch(c2bParams)
    }


    private fun createC2BParameters(amount: String, transactionReference: String): C2BParameters {
        return C2BParameters(
            amount = amount,
            providerName = serviceProviderName,
            providerCode = serviceProviderCode,
            providerLogo = serviceProviderLogoUrl,
            transactionRef = transactionReference
        )
    }
}