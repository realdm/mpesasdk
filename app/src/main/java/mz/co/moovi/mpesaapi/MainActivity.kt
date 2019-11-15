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

        /**
         * Initiate the B2C payment
         * This is useful when you are an middle man service provider and once a user of your app as
         * requested and paid the service {using the C2B payment above} and you want to transfer the
         * funds to the company or person who really offer the service the user wants.
         * i. e. after SpreePass sold ticket to the user, the app can immediately transfer the amount
         * to the Organize of the event
         *
         *
         */
        MpesaSdk.init(
                apiKey = "your_api_key",
                publicKey = "public_key",
                endpointUrl = MpesaSdk.SANDBOX_B2C_URL,
                serviceProviderName = "YourCompanyHere. i.e: SpreePass",
                serviceProviderCode = "company_short_code",
                serviceProviderLogoUrl = "url_to_your_logo",
                amount = "1993",
                companyMSIDN = "258850000111"
        )

        MpesaSdk.payB2C(transactionReference = "change_this_as_you_send_the_request")

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
