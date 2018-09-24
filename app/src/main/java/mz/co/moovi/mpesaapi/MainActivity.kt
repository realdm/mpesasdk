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
                serviceProviderCode = "171717",
                serviceProviderName = "Vodacom",
                apiKey = "woyih8h5xvpblitop07elf58l4hpbt08",
                serviceProviderLogoUrl = "https://www.vodacom.co.za/vodacom/home/images/header/vodacom_icon.77b545abd1d7a4e0808b5a2255438f64.png" )

        MpesaSdk.pay(activity = this, requestCode = 1, amount = "1", transactionReference = "T12335C")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            1 -> {
                when(resultCode) {
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
