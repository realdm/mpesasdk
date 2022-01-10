package mz.co.moovi.mpesalib.config

data class MpesaConfig(val baseUrl: String, val apiKey: String, val publicKey: String) {
    override fun toString(): String {
        return "Configuration Redacted"
    }
}