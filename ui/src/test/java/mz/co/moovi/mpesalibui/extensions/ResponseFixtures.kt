package mz.co.moovi.mpesalibui.extensions

import com.google.gson.Gson
import mz.co.moovi.mpesalib.api.PaymentResponse
import mz.co.moovi.mpesalibui.utils.TestFixtureLoader
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

inline fun <reified T : Any> String.getFixture(): T {
    val gson = Gson()
    val jsonResponse = TestFixtureLoader.getFixtureJson(this)
    return gson.fromJson(jsonResponse, T::class.java)
}

inline fun <reified T : Any> String.getHttpErrorFixture(code: Int): HttpException {
    val jsonResponse = TestFixtureLoader.getFixtureJson(this)
    val responseBody = ResponseBody.create(MediaType.parse("application/json"), jsonResponse)
    val response = Response.error<PaymentResponse>(code, responseBody)
    return HttpException(response)
}