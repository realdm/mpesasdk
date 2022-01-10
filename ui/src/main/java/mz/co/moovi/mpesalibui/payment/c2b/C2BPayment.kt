package mz.co.moovi.mpesalibui.payment.c2b

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.imePadding
import kotlinx.coroutines.flow.collect
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.payment.C2BPaymentSuccess
import mz.co.moovi.mpesalibui.payment.c2b.components.PaymentFailed
import mz.co.moovi.mpesalibui.payment.c2b.components.PaymentSuccess
import mz.co.moovi.mpesalibui.payment.c2b.components.ProcessingPayment
import mz.co.moovi.mpesalibui.payment.c2b.components.ReadyToPay
import mz.co.moovi.mpesalibui.utils.Injector

@Composable
fun C2BPayment(
    c2BParameters: C2BParameters,
    onCompletePayment: (C2BPaymentSuccess) -> Unit,
    onCancelPayment: () -> Unit
) {
    val viewModel = viewModel<C2BPaymentViewModel>(
        factory = C2BPaymentViewModelFactory(
            c2BParameters = c2BParameters,
            mpesaService = Injector.mPesaService
        )
    )
    val viewState =
        viewModel.viewState.collectAsState(initial = C2BPaymentViewState.Initializing).value

    LaunchedEffect("c2payment") {
        viewModel.event.collect {
            when (it) {
                C2BPaymentEvent.CancelPayment -> {
                    onCancelPayment.invoke()
                }
                is C2BPaymentEvent.SetResult -> {
                    onCompletePayment.invoke(it.c2BPaymentSuccess)
                }
            }
        }
    }

    BackHandler {
        viewModel.handleViewAction(C2BPaymentViewAction.CancelPressed)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.surface,
                title = {
                    Text(text = stringResource(id = R.string.payment_activity_toolbar_title))
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(CircleShape)
                            .clickable {
                                viewModel.handleViewAction(C2BPaymentViewAction.CancelPressed)
                            },
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                    )
                },
                elevation = 0.dp
            )
        }
    ) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (viewState) {
                C2BPaymentViewState.Initializing -> {}
                is C2BPaymentViewState.PaymentFailed -> PaymentFailed(
                    viewState = viewState,
                    onRetry = {
                        viewModel.handleViewAction(C2BPaymentViewAction.RetryPressed)
                    }
                )
                is C2BPaymentViewState.ProcessingPayment -> ProcessingPayment(viewState = viewState)
                is C2BPaymentViewState.ReadyToPay -> {
                    ReadyToPay(
                        viewState = viewState,
                        onEditNumber = {
                            viewModel.handleViewAction(C2BPaymentViewAction.EditPhoneNumber(it))
                        },
                        onPay = {
                            viewModel.handleViewAction(C2BPaymentViewAction.PayButtonPressed)
                        }
                    )
                }
                is C2BPaymentViewState.PaymentSuccess -> PaymentSuccess(viewState = viewState)
            }
        }
    }
}