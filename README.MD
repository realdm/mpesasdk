# M-pesa SDK

[![](https://jitpack.io/v/realdm/mpesasdk.svg)](https://jitpack.io/#realdm/mpesasdk)

M-pesa sdk is an unofficial sdk with the aim to make it easy to integrate with the Vodacom Mozambique
M-pesa payments api on android apps.

## Usage

### Getting M-Pesa Credentials

Create an account on the Vodacom Mozambique M-pesa [website](https://developer.mpesa.vm.co.mz/) in 
order to get the credentials and full documentation of the API

### Core

This library provides the core components to perform the network operation to the M-pesa api. 
This is the library that powers the ui library and you can use it to create your own ui implementation
of the SDK that better suits your project needs.

This library makes the network calls using Retrofit and the responses are adapter to RxJava2 types
using the RxJava2Adapter.

#### Create an M-pesa service
```kotlin
fun createMpesaService(): MpesaService {
   val config = MpesaConfig(baseUrl = API_BASE_URL, apiKey = apiKey, publicKey = publicKey)
   val repository = MpesaRepositoryImpl(config)
   return MpesaMpesaServiceImpl(repository)
}
```

#### Make a payment request

##### Create a payment request
```kotlin
val paymentRequest = PaymentRequest(
   input_Amount = amount,
   input_CustomerMSISDN = "25884$phoneNumber",
   input_ThirdPartyReference = thirdPartyReference,
   input_ServiceProviderCode = serviceProviderCode,
   input_TransactionReference = transactionReference)
```

##### Perform the network call

```kotlin
 val mpesaService = createMpesaService()
 val disposable = mpesaService.pay(paymentRequest)
                .subscribe { response, throwable -> handleResponse(response, throwable) }
```

##### Handle the response
```kotlin
private fun handleResponse(response: PaymentResponse?, throwable: Throwable?) {
   response?.run {
       val responseCode = response.output_ResponseCode
           when (responseCode) {
                //Do Something depending on the response code
           }
       }

   throwable?.run {
       when (throwable) {
           is HttpException -> {
               val json = throwable.response().errorBody()?.string()
               val response = Gson().fromJson(json, PaymentResponse::class.java)
                    //Do something
           }
           is UnknownHostException -> {
                //Do something
           }
           is SocketTimeoutException -> {
                    //Do Something
           }
       }
   }
} 
```

#### Download
Add a gradle dependency to your project:
```groovy
implementation 'com.github.realdm.mpesasdk:core:0.0.4'
```
Latest versions available in [Jitpack Repository](https://jitpack.io/#realdm/mpesasdk)

----


### Ui
This library allows the easy integration of the M-pesa payments on an android app with a ui backed
by the core library that handles everything needed to make a payment.

#### Initialize the SDK
Initialize the SDK on the Application `onCreate` callback.
Note: Calling init more than once will throw an IllegalStateException.

```kotlin
MpesaSdk.init(
   apiKey = "m_pesa_api_key",
   publicKey = "your_public_key",
   endpointUrl = "the_endpoint_url",
   serviceProviderCode = "m_pesa_service_provider_code",
   serviceProviderName = "service_provider_company_display_name",
   serviceProviderLogoUrl = "https://www.vodacom.co.za/vodacom/home/images/header/vodacom_icon.77b545abd1d7a4e0808b5a2255438f64.png")
```

Note: The endpoint url's for the sandbox and production environment are constants in the MpesaSdk with the names
      SANDBOX_BASE_URL and PRODUCTION_BASE_URL respectively.  

#### Make Payment
Make the payment by calling the `pay` provided by the M-pesa sdk. 
This method call will start an activity for result that will show the M-Pesa payment Ui to process
the payment and send a result back when it is done.

The `pay` method can be called from a `Fragment` or an `Activity` and it takes as parameters the 
`request code` used for the `startActivityForResult` call, the `amount` to be processed and a 
`transaction reference` of the payment.

The transaction reference will uniquely identify the product or service being paid.

##### Make the payment call
```kotlin
MpesaSdk.pay(activity = this,requestCode = 1, amount = "1", transactionReference = "T12335C")
```

##### Handle the result

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when(requestCode) {
         1 -> {
              when(resultCode) {
                   Activity.RESULT_OK -> {
                        val transactionId = data?.extras.getString(MpesaSdk.ARG_RESULT_TRANSACTION_ID)
                        val conversationId = data?.extras.getString(MpesaSdk.ARG_RESULT_CONVERSATION_ID)
                        //Do Something
                   }
                   Activity.RESULT_CANCELED -> {
                        //Do Something
                   }
              }
         }
    }
}
```

### Screenshots

Make payment

<img src="/Documentation/ui-screenshots/make_payment.png" height="480" width="270" alt="Alt text"/>

Processing Payment

<img src="/Documentation/ui-screenshots/processing_payment.png" height="480" width="270" alt="Alt text"/>

Payment Failure

<img src="/Documentation/ui-screenshots/payment_failed.png" height="480" width="270" alt="Alt text"/>

#### Download
Add a gradle dependency to your project:
```groovy
implementation 'com.github.realdm.mpesasdk:ui:0.0.4'
```
Latest versions available in [Jitpack Repository](https://jitpack.io/#realdm/mpesasdk)

----

### MIT LICENSE
```
Copyright 2018 Dario Mungoi

Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```
