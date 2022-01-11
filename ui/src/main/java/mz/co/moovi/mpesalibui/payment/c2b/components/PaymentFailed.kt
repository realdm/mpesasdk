package mz.co.moovi.mpesalibui.payment.c2b.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.payment.c2b.C2BPaymentViewState

@Composable
fun PaymentFailed(
    modifier: Modifier = Modifier,
    viewState: C2BPaymentViewState.PaymentFailed,
    onRetry: () -> Unit
) {
    Column(modifier = modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.payment_failed))
        LottieAnimation(
            composition,
            modifier = Modifier.size(150.dp),
            iterations = 100,
            restartOnPlay = true
        )

        Text(
            text = viewState.title.resolve(LocalContext.current),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = viewState.description.resolve(LocalContext.current),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 24.dp)
                .fillMaxWidth(),
            onClick = {
                onRetry.invoke()
            }) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = R.string.c2b_payment_error_retry_button)
            )
        }
    }
}