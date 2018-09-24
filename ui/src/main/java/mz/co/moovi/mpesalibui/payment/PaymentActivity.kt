package mz.co.moovi.mpesalibui.payment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import mz.co.moovi.mpesalibui.MpesaSdk
import mz.co.moovi.mpesalibui.R

class PaymentActivity : AppCompatActivity() {

    companion object {
        fun packArgs(amount: String,
                     serviceProviderName: String,
                     serviceProviderCode: String,
                     thirdPartyReference: String,
                     transactionReference: String, serviceProviderLogoUrl: String): Bundle {

            return Bundle().apply {
                putString(MpesaSdk.ARG_TRANSACTION_AMOUNT, amount)
                putString(MpesaSdk.ARG_SERVICE_PROVIDER_NAME, serviceProviderName)
                putString(MpesaSdk.ARG_SERVICE_PROVIDER_CODE, serviceProviderCode)
                putString(MpesaSdk.ARG_THIRD_PARTY_REFERENCE, thirdPartyReference)
                putString(MpesaSdk.ARG_TRANSACTION_REFERENCE, transactionReference)
                putString(MpesaSdk.ARG_SERVICE_PROVIDER_LOGO_URL, serviceProviderLogoUrl)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val fragment = PaymentFragment().apply {
            arguments = intent.extras
        }

        supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }
}
