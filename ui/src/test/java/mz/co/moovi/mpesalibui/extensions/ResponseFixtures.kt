package mz.co.moovi.mpesalibui.extensions

import mz.co.moovi.mpesalib.api.c2b.C2BPaymentResponse
import mz.co.moovi.mpesalibui.utils.TestFixtureLoader
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

inline fun <reified T : Any> String.getHttpErrorFixture(code: Int): HttpException {
    val jsonResponse = TestFixtureLoader.getFixtureJson(this)
    val responseBody = ResponseBody.create(MediaType.parse("application/json"), jsonResponse)
    val response = Response.error<C2BPaymentResponse>(code, responseBody)
    return HttpException(response)
}