package mz.co.moovi.mpesalibui.payment.c2b

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import mz.co.moovi.mpesalibui.payment.C2BPaymentSuccess
import mz.co.moovi.mpesalibui.payment.PaymentActivity
import mz.co.moovi.mpesalibui.payment.PaymentActivity.Companion.C2B_PARAMS
import mz.co.moovi.mpesalibui.payment.PaymentActivity.Companion.C2B_RESULTS

class C2BResultContract : ActivityResultContract<C2BParameters, C2BPaymentSuccess?>() {
    override fun createIntent(context: Context, input: C2BParameters): Intent {
        return Intent(context, PaymentActivity::class.java).apply {
            putExtra(C2B_PARAMS, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): C2BPaymentSuccess? {
        return when (resultCode) {
            Activity.RESULT_OK -> {
                intent?.getParcelableExtra(C2B_RESULTS)!!
            }
            else -> null
        }
    }
}
