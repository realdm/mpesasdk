package mz.co.moovi.mpesalib.api

sealed class Response<T : Any> {
    data class Error<T : Any>(
        val code: Int,
        val error: T? = null,
        val throwable: Throwable
    ) : Response<T>()

    class UnknownError<T: Any>: Response<T>()
    class NetworkError<T : Any> : Response<T>()
    data class Success<T : Any>(val data: T) : Response<T>()
}