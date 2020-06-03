package mz.co.moovi.mpesaapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mz.co.moovi.mpesalibui.MpesaSdk

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val apiKey = "your_key_here"
        val publicKey = """your_public_key_here"""
        val providerName = "your_business_name_here"
        val businessShortCode = "your_business_short_code_here"

        MpesaSdk.init(
            apiKey = apiKey,
            publicKey = publicKey,
            serviceProviderName = providerName,
            serviceProviderCode = businessShortCode,
            endpointUrl = MpesaSdk.SANDBOX_BASE_URL,
            serviceProviderLogoUrl = "https://bit.ly/30B9TYJ"
        )

        MpesaSdk.pay(
            amount = "10",
            activity = this,
            requestCode = 1,
            transactionReference = "test-13123"
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show()
                    }
                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(this, "Payment Unsuccessful", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

}
