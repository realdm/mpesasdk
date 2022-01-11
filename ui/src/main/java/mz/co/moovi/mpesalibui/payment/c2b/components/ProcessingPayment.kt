package mz.co.moovi.mpesalibui.payment.c2b.components

import abcdar.io.ruca.ui.theme.MpesaSdkUiTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.payment.c2b.C2BPaymentViewState

@Composable
fun ProcessingPayment(
    modifier: Modifier = Modifier,
    viewState: C2BPaymentViewState.ProcessingPayment
) {
    Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.coin))
        LottieAnimation(composition, modifier = Modifier.size(150.dp), iterations = 100, restartOnPlay = true)

        Text(
            text = stringResource(id = R.string.c2b_payment_processing_payment_title),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.c2b_payment_processing_payment_description, viewState.phoneNumber),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun ProcessingPaymentPreview() {
    MpesaSdkUiTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ProcessingPayment(
                    viewState = C2BPaymentViewState.ProcessingPayment(
                        providerName = "Abcdar.io",
                        phoneNumber = "+258829873879"
                    )
                )
            }
        }
    }
}