package mz.co.moovi.mpesalibui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import mz.co.moovi.mpesalibui.payment.PaymentActivity
import mz.co.moovi.mpesalibui.payment.devtools.MockAuthInfo
import mz.co.moovi.mpesalibui.payment.devtools.MockAuthPin

object MpesaSdk {

    const val ARG_RESULT_TRANSACTION_ID = "transaction_id"
    const val ARG_RESULT_CONVERSATION_ID = "conversation_id"
    const val ARG_SERVICE_PROVIDER_NAME = "arg_provider_name"
    const val ARG_SERVICE_PROVIDER_CODE = "arg_provider_code"
    const val ARG_TRANSACTION_AMOUNT = "arg_transaction_amount"
    const val ARG_TRANSACTION_REFERENCE = "arg_transaction_reference"
    const val ARG_SERVICE_PROVIDER_LOGO_URL = "arg_service_provider_logo_urls"

    const val PRODUCTION_BASE_URL = "https://api.vm.co.mz:18346"
    const val SANDBOX_BASE_URL = "https://api.sandbox.vm.co.mz:18346"

    private var initialized = false

    private var _mockAuthInfo: MockAuthInfo? = null
    val mockAuthInfo: MockAuthInfo?
        get() = _mockAuthInfo

    val hasMockAuthEnabled: Boolean
        get() = mockAuthInfo != null

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

    fun pay(activity: Activity, requestCode: Int, amount: String, transactionReference: String) {
        if (!initialized) {
            throw IllegalArgumentException("SDK was not initialized")
        }

        val intent = Intent(activity, PaymentActivity::class.java).apply {
            val args = createPaymentBundle(amount = amount, transactionReference = transactionReference)
            putExtras(args)
        }

        activity.startActivityForResult(intent, requestCode)
    }

    fun pay(fragment: Fragment, requestCode: Int, amount: String, transactionReference: String) {
        if (!initialized) {
            throw IllegalArgumentException("SDK was not initialized")
        }

        val intent = Intent(fragment.context, PaymentActivity::class.java).apply {
            val args = createPaymentBundle(amount = amount, transactionReference = transactionReference)
            putExtras(args)
        }

        fragment.startActivityForResult(intent, requestCode)
    }

    fun withMockAuthentication(pins: List<MockAuthPin>): MpesaSdk {
        if (!initialized) {
            throw IllegalArgumentException("SDK was not initialized")
        }

        if (!BuildConfig.DEBUG) {
            throw IllegalArgumentException("This mode should only be used in the debug build.")
        }
        _mockAuthInfo = MockAuthInfo(pins = pins)
        return this
    }

    private fun createPaymentBundle(amount: String, transactionReference: String): Bundle {
        return PaymentActivity.packArgs(
                amount = amount,
                serviceProviderName = serviceProviderName,
                serviceProviderCode = serviceProviderCode,
                transactionReference = transactionReference,
                serviceProviderLogoUrl = serviceProviderLogoUrl)
    }
}