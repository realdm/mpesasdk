package mz.co.moovi.mpesalibui.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.isA
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import mz.co.moovi.mpesalib.api.MpesaService
import mz.co.moovi.mpesalib.api.PaymentResponse
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.extensions.getFixture
import mz.co.moovi.mpesalibui.extensions.getHttpErrorFixture
import mz.co.moovi.mpesalibui.payment.authentication.PaymentAuthenticationCardViewState
import mz.co.moovi.mpesalibui.payment.error.PaymentErrorCardViewState
import mz.co.moovi.mpesalibui.ui.Action
import mz.co.moovi.mpesalibui.ui.ViewState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import java.net.UnknownHostException

@RunWith(JUnit4::class)
class PaymentViewModelTests {

    companion object {
        private val successPaymentResponse = "responses/payment/PaymentResponse_Success_ISN_0".getFixture<PaymentResponse>()
        private val timeOutPaymentResponse = "responses/payment/PaymentResponse_Error_ISN_9".getHttpErrorFixture<PaymentResponse>(408)
    }

    @get:Rule val mockitorule = MockitoJUnit.rule()
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var mpesaService: MpesaService
    @Mock private lateinit var actionObserver: Observer<Action>
    @Mock private lateinit var viewStateObserver: Observer<ViewState>

    private lateinit var viewModel: PaymentViewModel

    private val amount = "200"
    private val serviceProviderCode = "171717"
    private val transactionReference = "T2345CR"
    private val serviceProviderName = "Service Provider Name"
    private val serviceProviderLogoUrl = "service_provider_logo_url"

    @Before
    fun setup() {
        viewModel = PaymentViewModel(mpesaService, args = PaymentArgs(
                amount = amount,
                serviceProviderName = serviceProviderName,
                serviceProviderCode = serviceProviderCode,
                transactionReference = transactionReference,
                serviceProviderLogoUrl = serviceProviderLogoUrl))
    }

    @Test
    fun `on init view action emit a payment card view state`() {
        init()
    }

    @Test
    fun `enable payment button when a valid number lenght is added`() {
        val viewAction = PaymentViewAction.AddPhoneNumber(phoneNumber = "841112222")
        viewModel.handleViewAction(viewAction)
        assertEquals(PaymentAction.EnablePaymentButton, getAction())
    }

    @Test
    fun `on make payment emit payment processing card view state`() {
        init()
        val phoneNumber = "841112222"
        viewModel.handleViewAction(PaymentViewAction.AddPhoneNumber(phoneNumber = phoneNumber))

        doReturn(Single.just(successPaymentResponse)).whenever(mpesaService).pay(isA())
        viewModel.handleViewAction(PaymentViewAction.MakePayment)

        val expectedViewState = PaymentViewState(
                authenticationCard = PaymentAuthenticationCardViewState(
                        phoneNumber = "(+258)$phoneNumber"
                ))
        assertEquals(expectedViewState, getViewState())
    }

    @Test
    fun `on make payment successfully emit success result`() {
        init()
        val phoneNumber = "1112222"
        viewModel.handleViewAction(PaymentViewAction.AddPhoneNumber(phoneNumber = phoneNumber))

        doReturn(Single.just(successPaymentResponse)).whenever(mpesaService).pay(isA())
        viewModel.handleViewAction(PaymentViewAction.MakePayment)

        val expectedAction = PaymentAction.SendResult(
                paymentStatus = PaymentStatus.Success(
                        transactionId = successPaymentResponse.output_TransactionID,
                        conversationId = successPaymentResponse.output_ConversationID))
        assertEquals(expectedAction, getAction())
    }

    @Test
    fun `on make payment with a network error emit error card view state`() {
        init()
        val phoneNumber = "1112222"
        viewModel.handleViewAction(PaymentViewAction.AddPhoneNumber(phoneNumber = phoneNumber))

        doReturn(Single.error<PaymentResponse>(UnknownHostException())).whenever(mpesaService).pay(isA())
        viewModel.handleViewAction(PaymentViewAction.MakePayment)

        val expectedViewState = PaymentViewState(errorCard = PaymentErrorCardViewState(
                messageResId = R.string.payment_response_network_error))
        assertEquals(expectedViewState, getViewState())
    }

    @Test
    fun `on time out to authenticate payment emit error card view state`() {
        init()

        val viewAction = PaymentViewAction.AddPhoneNumber(phoneNumber = "1112222")
        viewModel.handleViewAction(viewAction)

        doReturn(Single.error<PaymentResponse>(timeOutPaymentResponse)).whenever(mpesaService).pay(isA())
        viewModel.handleViewAction(PaymentViewAction.MakePayment)

        val expectedViewState = PaymentViewState(errorCard = PaymentErrorCardViewState(
                messageResId = R.string.payment_response_ins_9))
        assertEquals(expectedViewState, getViewState())
    }

    private fun init() {

        val expectedViewState = PaymentViewState(paymentCard = PaymentCardViewState(
                amount =amount,
                serviceProviderName = serviceProviderName,
                serviceProviderCode = serviceProviderCode,
                serviceProviderLogo = serviceProviderLogoUrl))

        assertEquals(expectedViewState, getViewState())
        assertEquals(PaymentAction.DisablePaymentButton, getAction())
    }

    private fun getViewState(): ViewState {
        return viewModel.viewState.apply {
            observeForever(viewStateObserver)
        }.value!!
    }

    private fun getAction(): Action {
        return viewModel.action.apply {
            observeForever(actionObserver)
        }.value!!
    }
}