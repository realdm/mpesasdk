package mz.co.moovi.mpesalibui.payment.c2b

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mz.co.moovi.mpesalib.api.MpesaService

class C2BPaymentViewModelFactory(
    private val mpesaService: MpesaService,
    private val c2BParameters: C2BParameters
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return C2BPaymentViewModel(mPesaService = mpesaService, c2BParameters) as T
    }
}