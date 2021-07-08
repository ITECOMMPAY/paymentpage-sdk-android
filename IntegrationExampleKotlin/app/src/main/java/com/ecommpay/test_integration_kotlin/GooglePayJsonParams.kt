package com.ecommpay.test_integration_kotlin

object GooglePayJsonParams {

    fun getJSON(): String {
       return """
            {
              "apiVersion": 2,
              "apiVersionMinor": 0,
              "merchantInfo": {
                "merchantName": "Example Merchant",
                "merchantId": "merchant_id"
              },
              "allowedPaymentMethods": [
                {
                  "type": "CARD",
                  "parameters": {
                    "allowedAuthMethods": ["PAN_ONLY", "CRYPTOGRAM_3DS"],
                    "allowedCardNetworks": ["AMEX", "DISCOVER", "MASTERCARD", "VISA"]
                  },
                  "tokenizationSpecification": {
                    "type": "PAYMENT_GATEWAY",
                    "parameters": {
                      "gateway": "ecommpay",
                      "gatewayMerchantId": "gateway_merchant_id"
                    }
                  }
                }
              ],
              "transactionInfo": {
                "totalPriceStatus": "FINAL",
                "totalPrice": "12.34",
                "currencyCode": "USD"
              }
            }
        """.trimIndent()
    }
}