package mz.co.moovi.mpesalib.config

import android.util.Base64
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

internal class KeyGenerator(config: MpesaConfig) {

    val bearerToken = generateBearerToken(config)

    private fun generateBearerToken(mpesaConfig: MpesaConfig): String {

        val keyFactory = KeyFactory.getInstance("RSA")

        val cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING")

        val decodedPublicKey = Base64.decode(mpesaConfig.publicKey, Base64.NO_WRAP)

        val publicKeySpec = X509EncodedKeySpec(decodedPublicKey)

        val publicKey = keyFactory.generatePublic(publicKeySpec)

        cipher.init(Cipher.PUBLIC_KEY, publicKey)

        val apiConfigKey = mpesaConfig.apiKey.toByteArray(Charsets.UTF_8)

        return String(Base64.encode(cipher.doFinal(apiConfigKey), Base64.NO_WRAP), Charsets.UTF_8)
    }
}