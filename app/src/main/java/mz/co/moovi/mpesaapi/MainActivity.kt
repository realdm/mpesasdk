package mz.co.moovi.mpesaapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import mz.co.moovi.mpesalibui.MpesaSdk

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ///Initialize with application once
        MpesaSdk.init(
                apiKey = "your_api_key_here",
                publicKey = """your_public_key_here""",
                endpointUrl = MpesaSdk.SANDBOX_BASE_URL,
                serviceProviderName = "Spreepass",
                serviceProviderCode = "your_short_code_here",
                serviceProviderLogoUrl = "https://bit.ly/30B9TYJ") // Spreepass logo

        MpesaSdk.pay(activity = this, requestCode = 1, amount = "1", transactionReference = "keep_changing_this_as_you_test")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //Handle success send info to your server
                    }
                    Activity.RESULT_CANCELED -> {
                        //Handle failure tell something to the user
                    }
                }
            }
        }
    }

}
