package mz.co.moovi.mpesalibui.extensions

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

fun <T : ViewModel> provideViewModel(scope: Fragment, initializer: () -> T): T {
    val vm = initializer.invoke()
    return ViewModelProviders.of(scope, vm.factory()).get(vm.javaClass)
}

fun <T : ViewModel> provideViewModel(scope: FragmentActivity, initializer: () -> T): T {
    val vm = initializer.invoke()
    return ViewModelProviders.of(scope, vm.factory()).get(vm.javaClass)
}

inline fun <reified T : ViewModel> provideParentViewModel(scope: Fragment): T {
    return ViewModelProviders.of(scope.requireActivity()).get(T::class.java)
}

fun ViewModel.factory(): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return this@factory as T
        }
    }
}

inline fun <reified FM : ViewModel> Fragment.provideFlowModel(): FM {
    return ViewModelProviders.of(this.requireActivity()).get(FM::class.java)
}
