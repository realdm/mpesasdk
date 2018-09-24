package mz.co.moovi.mpesalib.api

/**
 * Data class with all the fields required to perform a Customer to Business (C2B) transaction Payment to Mpesa API
 * @input_ThirdPartyReference: This is the reference of the third party system. When there are queries about transactions, this will usually be used to track a transaction.
 * @input_Amount: The amount for the transaction.
 * @input_CustomerMSISDN: MSISDN of the customer for the transaction ( Customer Vodacom Number)
 * @input_ServiceProviderCode: Shortcode of the business where funds will be deducted from.
 * @input_TransactionReference: This is the reference of the transaction for the customer or business making the transaction. This can be a smartcard number for a TV subscription or a reference number of a utility bill.
 */
data class PaymentRequest(val input_ThirdPartyReference: String,
                          val input_Amount: String,
                          val input_CustomerMSISDN: String,
                          val input_ServiceProviderCode: String,
                          val input_TransactionReference: String)