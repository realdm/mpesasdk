package mz.co.moovi.mpesalibui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import mz.co.moovi.mpesalibui.payment.PaymentActivity
import mz.co.moovi.mpesalibui.utils.ReferenceGenerator

object MpesaSdk {

    const val ARG_RESULT_TRANSACTION_ID = "transaction_id"
    const val ARG_RESULT_CONVERSATION_ID = "conversation_id"

    const val ARG_SERVICE_PROVIDER_NAME = "arg_provider_name"
    const val ARG_SERVICE_PROVIDER_CODE = "arg_provider_code"
    const val ARG_TRANSACTION_AMOUNT = "arg_transaction_amount"
    const val ARG_THIRD_PARTY_REFERENCE = "arg_third_party_reference"
    const val ARG_TRANSACTION_REFERENCE = "arg_transaction_reference"
    const val ARG_SERVICE_PROVIDER_LOGO_URL = "arg_service_provider_logo_urls"

    const val SANDBOX_BASE_URL = "https://api.sandbox.vm.co.mz:18346"
    const val PRODUCTION_BASE_URL = ""

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

    fun init(apiKey: String,
             serviceProviderName: String,
             serviceProviderCode: String,
             serviceProviderLogoUrl: String = "") {

        if(initialized) {
            throw IllegalArgumentException("SDK is already initialized")
        }
        _apiKey = apiKey
        _serviceProviderCode = serviceProviderCode
        _serviceProviderName = serviceProviderName
        _serviceProviderLogoUrl = serviceProviderLogoUrl
        initialized = true
    }

    val publicKey = """MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAmptSWqV7cGUUJJhUBxsMLonux24u+FoTlrb+4Kgc6092JIszmI1QUoMohaDDXSVueXx6IXwYGsjjWY32HGXj1iQhkALXfObJ4DqXn5h6E8y5/xQYNAyd5bpN5Z8r892B6toGzZQVB7qtebH4apDjmvTi5FGZVjVYxalyyQkj4uQbbRQjgCkubSi45Xl4CGtLqZztsKssWz3mcKncgTnq3DHGYYEYiKq0xIj100LGbnvNz20Sgqmw/cH+Bua4GJsWYLEqf/h/yiMgiBbxFxsnwZl0im5vXDlwKPw+QnO2fscDhxZFAwV06bgG0oEoWm9FnjMsfvwm0rUNYFlZ+TOtCEhmhtFp+Tsx9jPCuOd5h2emGdSKD8A6jtwhNa7oQ8RtLEEqwAn44orENa1ibOkxMiiiFpmmJkwgZPOG/zMCjXIrrhDWTDUOZaPx/lEQoInJoE2i43VN/HTGCCw8dKQAwg0jsEXau5ixD0GUothqvuX3B9taoeoFAIvUPEq35YulprMM7ThdKodSHvhnwKG82dCsodRwY428kg2xM/UjiTENog4B6zzZfPhMxFlOSFX4MnrqkAS+8Jamhy1GgoHkEMrsT5+/ofjCx0HjKbT5NuA2V/lmzgJLl3jIERadLzuTYnKGWxVJcGLkWXlEPYLbiaKzbJb2sYxt+Kt5OxQqC1MCAwEAAQ=="""

    fun pay(activity: Activity, requestCode: Int, amount: String, transactionReference: String) {
        if (!initialized) {
            throw IllegalArgumentException("SDK was not initialized")
        }

        val intent = Intent(activity, PaymentActivity::class.java).apply {
            val args = createPaymentBundle(amount, transactionReference)
            putExtras(args)
        }

        activity.startActivityForResult(intent, requestCode)
    }

    fun pay(fragment: Fragment, requestCode: Int, amount: String, transactionReference: String) {
        if (!initialized) {
            throw IllegalArgumentException("SDK was not initialized")
        }

        val intent = Intent(fragment.context, PaymentActivity::class.java).apply {
            val args = createPaymentBundle(amount, transactionReference)
            putExtras(args)
        }

        fragment.startActivityForResult(intent, requestCode)
    }

    private fun createPaymentBundle(amount: String, transactionReference: String): Bundle {
        return PaymentActivity.packArgs(
                amount = amount,
                transactionReference = transactionReference,
                serviceProviderName = serviceProviderName,
                serviceProviderCode = serviceProviderCode,
                serviceProviderLogoUrl = serviceProviderLogoUrl,
                thirdPartyReference = ReferenceGenerator.generateReference(transactionReference))
    }
}