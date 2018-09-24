package mz.co.moovi.mpesalib.api

import io.reactivex.Single

class MpesaMpesaServiceImpl(private val mPesaRepository: MpesaRepository) : MpesaService {

    override fun pay(paymentRequest: PaymentRequest): Single<PaymentResponse> {
        return mPesaRepository.pay(paymentRequest)
    }
}