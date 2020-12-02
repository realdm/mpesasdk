package mz.co.moovi.mpesalibui.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import mz.co.moovi.mpesalibui.MpesaSdk
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_CODE
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_LOGO_URL
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_SERVICE_PROVIDER_NAME
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_THIRD_PARTY_REFERENCE
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_TRANSACTION_AMOUNT
import mz.co.moovi.mpesalibui.MpesaSdk.ARG_TRANSACTION_REFERENCE
import mz.co.moovi.mpesalibui.R
import mz.co.moovi.mpesalibui.databinding.FragmentPaymentBinding
import mz.co.moovi.mpesalibui.databinding.ViewPaymentCardBinding
import mz.co.moovi.mpesalibui.extensions.provideViewModel
import mz.co.moovi.mpesalibui.ui.Action
import mz.co.moovi.mpesalibui.ui.ViewState

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding: FragmentPaymentBinding
    	get() = _binding!!

	private val paymentViewCardBinding: ViewPaymentCardBinding
		get() = ViewPaymentCardBinding.bind(binding.startPaymentCard)

    private val viewModel by lazy {
        provideViewModel<PaymentViewModel>().apply {
            val viewAction = PaymentViewAction.Init(amount = arguments!!.getString(ARG_TRANSACTION_AMOUNT)!!,
                    serviceProviderCode = arguments!!.getString(ARG_SERVICE_PROVIDER_CODE)!!,
                    transactionReference = arguments!!.getString(ARG_TRANSACTION_REFERENCE)!!,
                    thirdPartyReference = arguments!!.getString(ARG_THIRD_PARTY_REFERENCE)!!,
                    serviceProviderName = arguments!!.getString(ARG_SERVICE_PROVIDER_NAME)!!,
                    serviceProviderLogoUrl = arguments!!.getString(ARG_SERVICE_PROVIDER_LOGO_URL)!!)
            handleViewAction(viewAction)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        viewModel.action.observe(this, { handleAction(it!!) })
        viewModel.viewState.observe(this, { handleViewState(it!!) })
    }

    private fun handleAction(action: Action) {
        when (action) {
            is PaymentViewModelAction.Cancel -> {
                activity?.run {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }
            is PaymentViewModelAction.EnablePaymentButton -> {
				paymentViewCardBinding.payButton.isEnabled = true
            }
            is PaymentViewModelAction.DisablePaymentButton -> {
				paymentViewCardBinding.payButton.isEnabled = false
            }
            is PaymentViewModelAction.SendResult -> {
                when (action.paymentStatus) {
                    is PaymentStatus.Success -> {
                        val intent = Intent().apply {
                            val bundle = Bundle().apply {
                                putString(MpesaSdk.ARG_RESULT_TRANSACTION_ID,
                                        action.paymentStatus.transactionId)
                                putString(MpesaSdk.ARG_RESULT_CONVERSATION_ID,
                                        action.paymentStatus.conversationId)
                                putString(MpesaSdk.ARG_RESULT_THIRD_PARTY_REFERENCE,
                                        action.paymentStatus.thirdPartyReference)
                            }
                            putExtras(bundle)
                        }
                        activity?.run {
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun handleViewState(viewState: ViewState) {
        val state = viewState as PaymentViewState
		binding.paymentErrorCard.render(state.errorCard)
        binding.startPaymentCard.render(state.paymentCard)
        binding.paymentAuthenticationCard.render(state.authenticationCard)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
							  savedInstanceState: Bundle?): View {
		_binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
    }

    private fun setupUi() {
        setupToolbar()
        binding.paymentAuthenticationCard.handler = { viewModel.handleViewAction(it) }
        binding.paymentErrorCard.handler = { viewModel.handleViewAction(it) }
        binding.startPaymentCard.handler = { viewModel.handleViewAction(it) }
    }

    private fun setupToolbar() {
        binding.toolbar.let {
            it.title = resources.getString(R.string.payment_activity_toolbar_title)
            it.setNavigationIcon(R.drawable.ic_close_24dp)
            it.setNavigationOnClickListener {
                viewModel.handleViewAction(PaymentViewAction.Cancel)
            }
        }
    }
}