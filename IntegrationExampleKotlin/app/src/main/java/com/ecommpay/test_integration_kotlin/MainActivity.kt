package com.ecommpay.test_integration_kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ecommpay.sdk.components.ECMPActivity
import com.ecommpay.sdk.entities.ECMPPaymentInfo
import android.content.Intent
import com.ecommpay.sdk.entities.ECMPRecurrentInfo


class MainActivity : AppCompatActivity() {

    private val PAY_ACTIVITY_REQUEST = 888
    private val SECRET = "your_secret"
    private val PROJECT_ID = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create payment info with product information
        val paymentInfo = getPaymentInfoOnlyRequiredParams() // getPaymentInfoAllParams

        // Signature should be generated on your server and delivered to your app
        val signature = SignatureGenerator.generateSignature(paymentInfo.paramsForSignature, SECRET)

        // Sign payment info
        paymentInfo.signature = signature

        // Present Checkout UI
        startActivityForResult(
            ECMPActivity.buildIntent(
                this,
                paymentInfo
            ),
            PAY_ACTIVITY_REQUEST
        )
    }

    // Handle SDK result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PAY_ACTIVITY_REQUEST) {
            when (resultCode) {
                ECMPActivity.RESULT_SUCCESS -> {}
                ECMPActivity.RESULT_CANCELLED -> {}
                ECMPActivity.RESULT_DECLINE -> {}
                ECMPActivity.RESULT_FAILED -> {}
            }
            val message = data?.getStringExtra(ECMPActivity.DATA_INTENT_EXTRA)
        }
    }

    // Payment Info
    internal fun getPaymentInfoOnlyRequiredParams(): ECMPPaymentInfo {
        return ECMPPaymentInfo(
            PROJECT_ID, // project ID that is assigned to you
            "internal_payment_id_1", // payment ID to identify payment in your system
            1999, // 19.99
            "USD"
        )
    }

    internal fun getPaymentInfoAllParams(): ECMPPaymentInfo {
        return ECMPPaymentInfo(
            PROJECT_ID, // project ID that is assigned to you
            "internal_payment_id_1", // payment ID to identify payment in your system
            1999, // 19.99
            "USD",
            "T-shirt with dog print",
            "10", // unique ID assigned to your customer
            ""
        )
    }

    // Additional
    internal fun setDMSPayment(paymentInfo: ECMPPaymentInfo) {
        paymentInfo.creditCardPaymentType = ECMPPaymentInfo.CreditCardPaymentType.Auth
    }

    internal fun setToken(paymentInfo: ECMPPaymentInfo) {
        paymentInfo.token = "token"
    }

    internal fun setRecurrent(paymentInfo: ECMPPaymentInfo) {
        val recurrentInfo = ECMPRecurrentInfo(
            "R", // type
            "11", // expiry month
            "2030", // expiry year
            "M", // recurrent period
            "12:00:00", // start time
            "12-02-2020", // start date
            "your_recurrent_id") // recurrent payment ID
        paymentInfo.setRecurrent(recurrentInfo)
    }
}
