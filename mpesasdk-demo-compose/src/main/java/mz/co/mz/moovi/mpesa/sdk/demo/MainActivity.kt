package mz.co.mz.moovi.mpesa.sdk.demo

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mz.co.moovi.mpesalibui.MpesaSdk
import mz.co.moovi.mpesalibui.payment.C2BPaymentSuccess
import mz.co.moovi.mpesalibui.payment.c2b.C2BResultContract
import mz.co.mz.moovi.mpesa.sdk.demo.ui.theme.MpesasdkTheme
import java.util.*

class MainActivity : ComponentActivity() {

    companion object {
        const val apiKey = "your_api_key_here"
        const val publicKey = """your_public_key_here"""
        const val providerName = "your_company_name_here"
        const val businessShortCode = "your_short_code_here"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSdk()
        setContent {
            MpesasdkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    RenderContent()
                }
            }
        }
    }

    private fun initializeSdk() {
        MpesaSdk.init(
            apiKey = apiKey,
            publicKey = publicKey,
            serviceProviderName = providerName,
            serviceProviderCode = businessShortCode,
            endpointUrl = MpesaSdk.SANDBOX_BASE_URL,
            serviceProviderLogoUrl = "https://bit.ly/30B9TYJ"
        )
    }
}

@Composable
fun RenderContent() {
    var amount by remember {
        mutableStateOf("0")
    }

    var launchPayment by remember {
        mutableStateOf(false)
    }

    val result = remember { mutableStateOf<C2BPaymentSuccess?>(null) }
    val launcher = rememberLauncherForActivityResult(C2BResultContract()) {
        result.value = it
        launchPayment = false
    }

    result.value?.run {
        launchPayment = false
        Toast.makeText(LocalContext.current, "Payment result is: $this", Toast.LENGTH_LONG).show()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = {
                    Text("Valor a pagar")
                }
            )

            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                onClick = {
                    result.value = null
                    launchPayment = true
                }) {
                Text(modifier = Modifier.padding(8.dp), text = "Pagar com M-pesa")
            }
        }

        if (launchPayment) {
            MpesaSdk.pay(
                amount = amount,
                launcher = launcher,
                transactionReference = UUID.randomUUID().toString().take(6)
            )
        }
    }


}

@Preview(showBackground = true)
@Composable
fun RenderContentPreview() {
    MpesasdkTheme {
        RenderContent()
    }
}