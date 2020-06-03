package mz.co.moovi.mpesalib.api

sealed class Response<T : Any> {
    data class Error<T : Any>(
        val code: Int,
        val data: T? = null,
        val throwable: Throwable
    ) : Response<T>()
    data class Success<T : Any>(val data: T) : Response<T>()
}