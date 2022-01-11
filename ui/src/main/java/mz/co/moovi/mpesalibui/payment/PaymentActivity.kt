package mz.co.moovi.mpesalibui.payment

import abcdar.io.ruca.ui.theme.MpesaSdkUiTheme
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import mz.co.moovi.mpesalibui.navigation.C2BNavCommands
import mz.co.moovi.mpesalibui.payment.c2b.C2BParameters
import mz.co.moovi.mpesalibui.payment.c2b.C2BPayment

class PaymentActivity : ComponentActivity() {

    companion object {
        const val C2B_PARAMS = "c2b_params"
        const val C2B_RESULTS = "c2b_results"
    }

    private val c2bParams
        get() = intent?.extras?.getParcelable<C2BParameters>(C2B_PARAMS)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MpesaSdkUiTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    Surface {
                        RenderContent()
                    }
                }
            }
        }
    }

    @Composable
    fun RenderContent() {
        NavHost(navController = rememberNavController(), startDestination = getStartDestination()) {
            composable(
                route = C2BNavCommands.root.destination,
                arguments = C2BNavCommands.root.namedArgs
            ) {
                C2BPayment(
                    c2BParameters = c2bParams!!,
                    onCancelPayment = {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    },
                    onCompletePayment = {
                        setResult(
                            Activity.RESULT_OK,
                            Intent().apply {
                                putExtra(C2B_RESULTS, it)
                            }
                        )
                        finish()
                    }
                )
            }
        }
    }

    private fun getStartDestination(): String {
        if (c2bParams != null) {
            return C2BNavCommands.root.createRoute(listOf(c2bParams!!))
        } else {
            throw IllegalStateException("Invalid params. other API's not implemented yet")
        }
    }
}
