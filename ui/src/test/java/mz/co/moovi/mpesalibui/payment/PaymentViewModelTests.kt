package mz.co.moovi.mpesalibui.payment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.isA
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import mz.co.moovi.mpesalib.api.MpesaService
import mz.co.moovi.mpesalib.api.c2b.C2BPaymentResponse
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.extensions.getFixture
import mz.co.moovi.mpesalibui.extensions.getHttpErrorFixture
import mz.co.moovi.mpesalibui.payment.authentication.PaymentAuthenticationCardViewState
import mz.co.moovi.mpesalibui.payment.c2b.C2BPaymentEvent
import mz.co.moovi.mpesalibui.payment.c2b.C2BPaymentViewModel
import mz.co.moovi.mpesalibui.payment.c2b.C2BPaymentViewAction
import mz.co.moovi.mpesalibui.payment.error.PaymentErrorCardViewState
import mz.co.moovi.mpesalibui.ui.Event
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
        private val successPaymentResponse = "responses/payment/PaymentResponse_Success_ISN_0".getFixture<C2BPaymentResponse>()
        private val timeOutPaymentResponse = "responses/payment/PaymentResponse_Error_ISN_9".getHttpErrorFixture<C2BPaymentResponse>(408)
    }

    @get:Rule val mockitorule = MockitoJUnit.rule()
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var mpesaService: MpesaService
    @Mock private lateinit var actionObserver: Observer<Event>
    @Mock private lateinit var viewStateObserver: Observer<ViewState>

    private lateinit var viewModel: C2BPaymentViewModel

    @Before
    fun setup() {
        viewModel =
            C2BPaymentViewModel(mpesaService)
    }

    @Test
    fun `on INIT view action emit a payment card view state`() {
        init()
    }

    @Test
    fun `enable payment button when a valid number lenght is added`() {
        val viewAction = C2BPaymentViewAction.AddPhoneNumber(phoneNumber = "1112222")
        viewModel.handleViewAction(viewAction)
        assertEquals(C2BPaymentEvent.EnablePaymentButton, getAction())
    }

    @Test
    fun `on make payment emit payment processing card view state`() {
        init()
        val phoneNumber = "1112222"
        viewModel.handleViewAction(C2BPaymentViewAction.AddPhoneNumber(phoneNumber = phoneNumber))

        doReturn(Single.just(successPaymentResponse)).whenever(mpesaService).c2bPayment(isA())
        viewModel.handleViewAction(C2BPaymentViewAction.PayButtonPressed)

        val expectedViewState = PaymentViewState(
                authenticationCard = PaymentAuthenticationCardViewState(
                        phoneNumber = "(+258) 84${phoneNumber}"
                ))
        assertEquals(expectedViewState, getViewState())
    }

    @Test
    fun `on make payment successfully emit success result`() {
        init()
        val phoneNumber = "1112222"
        viewModel.handleViewAction(C2BPaymentViewAction.AddPhoneNumber(phoneNumber = phoneNumber))

        doReturn(Single.just(successPaymentResponse)).whenever(mpesaService).c2bPayment(isA())
        viewModel.handleViewAction(C2BPaymentViewAction.PayButtonPressed)

        val expectedAction = C2BPaymentEvent.SetResult(
                paymentStatus = PaymentResult.Success(
                        transactionId = successPaymentResponse.transactionId,
                        conversationId = successPaymentResponse.conversationId))
        assertEquals(expectedAction, getAction())
    }

    @Test
    fun `on make payment with a network error emit error card view state`() {
        init()
        val phoneNumber = "1112222"
        viewModel.handleViewAction(C2BPaymentViewAction.AddPhoneNumber(phoneNumber = phoneNumber))

        doReturn(Single.error<C2BPaymentResponse>(UnknownHostException())).whenever(mpesaService).c2bPayment(isA())
        viewModel.handleViewAction(C2BPaymentViewAction.PayButtonPressed)

        val expectedViewState = PaymentViewState(errorCard = PaymentErrorCardViewState(
                messageResId = R.string.payment_response_network_error))
        assertEquals(expectedViewState, getViewState())
    }

    @Test
    fun `on time out to authenticate payment emit error card view state`() {
        init()

        val viewAction = C2BPaymentViewAction.AddPhoneNumber(phoneNumber = "1112222")
        viewModel.handleViewAction(viewAction)

        doReturn(Single.error<C2BPaymentResponse>(timeOutPaymentResponse)).whenever(mpesaService).c2bPayment(isA())
        viewModel.handleViewAction(C2BPaymentViewAction.PayButtonPressed)

        val expectedViewState = PaymentViewState(errorCard = PaymentErrorCardViewState(
                messageResId = R.string.payment_response_ins_9))
        assertEquals(expectedViewState, getViewState())
    }

    private fun init() {
        val viewAction = C2BPaymentViewAction.Init(
                amount = "200",
                serviceProviderCode = "171717",
                transactionReference = "T2345CR",
                serviceProviderName = "Service Provider Name",
                thirdPartyReference = "third_party_reference",
                serviceProviderLogoUrl = "service_provider_logo_url")

        viewModel.handleViewAction(viewAction)

        val expectedViewState = PaymentViewState(paymentCard = PaymentCardViewState(
                amount = viewAction.amount,
                serviceProviderName = viewAction.serviceProviderName,
                serviceProviderCode = viewAction.serviceProviderCode,
                serviceProviderLogo = viewAction.serviceProviderLogoUrl))

        assertEquals(expectedViewState, getViewState())
        assertEquals(C2BPaymentEvent.DisablePaymentButton, getAction())
    }

    private fun getViewState(): ViewState {
        return viewModel.viewState.apply {
            observeForever(viewStateObserver)
        }.value!!
    }

    private fun getAction(): Event {
        return viewModel.event.apply {
            observeForever(actionObserver)
        }.value!!
    }
}