package mz.co.moovi.mpesalibui.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mz.co.moovi.mpesalibui.payment.PaymentViewModel
import mz.co.moovi.mpesalibui.utils.Injector

inline fun <reified T : ViewModel> Fragment.provideViewModel(): T {
	val factory = object : ViewModelProvider.Factory {
		override fun <T : ViewModel?> create(modelClass: Class<T>): T {
			return PaymentViewModel(Injector.mPesaService()) as T
		}
	}
	return ViewModelProvider(this, factory).get(T::class.java)
}
