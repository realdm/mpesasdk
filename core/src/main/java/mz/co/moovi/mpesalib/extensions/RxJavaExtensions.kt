package mz.co.moovi.mpesalib.extensions

import io.reactivex.Scheduler
import io.reactivex.Single

fun <T : Any> Single<T>.async(subscribeOn: Scheduler, observeOn: Scheduler): Single<T> {
    return subscribeOn(subscribeOn)
            .observeOn(observeOn)
}