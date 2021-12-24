package com.ecommpay.ecommpaysdktestintegrationjava;

import android.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

class SignatureGenerator {

    static String generateSignature(String paramsToSign, String secret) {
        String signature = "";

        try {
            signature = hmac(paramsToSign, secret);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return signature;
    }

    private static String hmac(String value, String key) throws Exception {
        byte[] byteKey = key.getBytes("UTF-8");
        final String HMAC_SHA512 = "HmacSHA512";
        Mac sha512HMAC = Mac.getInstance(HMAC_SHA512);
        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
        sha512HMAC.init(keySpec);
        byte[] mac_data = sha512HMAC.
                doFinal(value.getBytes("UTF-8"));
        byte[] result = Base64.encode(mac_data, Base64.NO_WRAP);
        return new String(result, "UTF-8");
    }
}
