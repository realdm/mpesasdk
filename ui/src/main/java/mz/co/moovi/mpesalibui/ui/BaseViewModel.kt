package mz.co.moovi.mpesalibui.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


abstract class BaseViewModel<TViewAction : ViewAction, TAction : Action, TViewState : ViewState> : ViewModel() {

    abstract val action: LiveData<TAction>

    abstract val viewState: LiveData<ViewState>

    abstract fun handleViewAction(viewAction: TViewAction)
}