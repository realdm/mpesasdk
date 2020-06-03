package mz.co.moovi.mpesalibui.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <T : ViewModel> provideViewModel(scope: AppCompatActivity, initializer: () -> T): T {
    val vm = initializer()
    return ViewModelProvider(scope, vm.factory()).get(vm.javaClass)
}

fun <T : ViewModel> provideViewModel(scope: Fragment, initializer: () -> T): T {
    val vm = initializer()
    return ViewModelProvider(scope, vm.factory()).get(vm.javaClass)
}

inline fun <reified T : ViewModel> provideParentViewModel(parent: FragmentActivity): T {
    return ViewModelProvider(parent).get(T::class.java)
}

private fun ViewModel.factory(): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return this@factory as T
        }
    }
}
