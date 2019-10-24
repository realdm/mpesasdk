package mz.co.moovi.mpesalibui.payment.devtools

sealed class MockAuthPin(val pin: Int) {
    data class Success(val value: Int): MockAuthPin(pin = value)
    data class NotEnoughFunds(val value: Int): MockAuthPin(pin = value)
}