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

        // Initialize with application once
        MpesaSdk.init(
            apiKey = "your_api_key_here",
            publicKey = """your_public_key_here""",
            endpointUrl = MpesaSdk.SANDBOX_BASE_URL,
            serviceProviderName = "Spreepass",
            serviceProviderCode = "your_short_code_here",
            serviceProviderLogoUrl = "https://bit.ly/30B9TYJ")

         MpesaSdk.pay(activity = this, requestCode = 1, amount = "200", transactionReference = "234569")

        /**
         * calls the withMockAuthentication to fake the Pin authentication flow that would happen
         * on a real transaction.
         * You can define the pins based on the response type you want to simulate.
         */

        // MpesaSdk.withMockAuthentication(pins = listOf(MockAuthPin.Success(2222), MockAuthPin.NotEnoughFunds(2020)))
        // .pay(activity = this, requestCode = 1, amount = "200", transactionReference = "234569")
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
                        //Handle failure tell something to the user
                    }
                }
            }
        }
    }

}
