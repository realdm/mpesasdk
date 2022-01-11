package mz.co.moovi.mpesalibui.payment.c2b.components

import abcdar.io.ruca.ui.theme.MpesaSdkUiTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.google.accompanist.insets.navigationBarsWithImePadding
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.payment.c2b.C2BPaymentViewState
import mz.co.moovi.mpesalibui.ui.theme.Grey300
import mz.co.moovi.mpesalibui.ui.theme.Red400

@Composable
fun ReadyToPay(
    modifier: Modifier = Modifier,
    viewState: C2BPaymentViewState.ReadyToPay,
    onEditNumber: (String) -> Unit,
    onPay: () -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        /**
         * Amount
         */
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = viewState.amount,
                style = MaterialTheme.typography.h2,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.payment_card_currency),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(start = 4.dp, bottom = 16.dp)
                    .alpha(0.7f)
                    .align(Alignment.Bottom)
            )
        }

        Icon(
            painter = painterResource(id = R.drawable.ic_south),
            contentDescription = null
        )

        /**
         * Payment Description
         */

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = viewState.providerLogo, builder = {
                    transformations(CircleCropTransformation())
                }),
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .border(2.dp, Grey300, shape = CircleShape)

            )
            Text(
                text = viewState.providerName,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                text = viewState.providerShortCode,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Light
            )

        }

        Text(
            text = stringResource(id = R.string.payment_card_view_phone_number_prompt),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Light,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )

        /**
         * Phone Number Field
         */
        Row(
            modifier = Modifier.padding(top = 16.dp, start = 64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h4,
                text = stringResource(id = R.string.payment_card_country_code_prefix),
                color = Red400
            )
            BasicTextField(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 8.dp)
                    .focusable(enabled = true),
                textStyle = MaterialTheme.typography.h4,
                onValueChange = {
                    val number = it.take(9)
                    if (it.isEmpty() || it.isBlank()) {
                        onEditNumber.invoke(number)
                    } else {
                        if (it.last().isDigit()) {
                            onEditNumber.invoke(it.take(9))
                        }
                    }
                },
                maxLines = 1,
                value = viewState.phoneNumber,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        /**
         * Pay Button
         */
        Button(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 24.dp)
                .navigationBarsWithImePadding()
                .fillMaxWidth(),
            enabled = viewState.payButtonEnabled,
            onClick = {
                onPay.invoke()
            }) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = stringResource(id = R.string.payment_card_pay_button_text)
            )
        }

    }
}

@Preview
@Composable
fun ReadyToPayPreview() {
    MpesaSdkUiTheme {
        Surface {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                ReadyToPay(
                    viewState = C2BPaymentViewState.ReadyToPay(
                        amount = "20",
                        phoneNumber = "849873879",
                        payButtonEnabled = true,
                        providerShortCode = "7777",
                        providerName = "Vodacom MZ",
                        providerLogo = ""
                    ),
                    onEditNumber = {},
                    onPay = {
                    }
                )
            }
        }
    }
}