package com.ecommpay.ecommpaysdktestintegrationjava;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ecommpay.sdk.components.ECMPActivity;
import com.ecommpay.sdk.entities.ECMPPaymentInfo;
import com.ecommpay.sdk.entities.ECMPRecurrentInfo;

public class MainActivity extends AppCompatActivity {

    private static int PAY_ACTIVITY_REQUEST = 888;
    private static String SECRET = "your_secret";
    private static int PROJECT_ID = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create payment info with product information
        ECMPPaymentInfo paymentInfo = getPaymentInfoOnlyRequiredParams(); // getPaymentInfoAllParams

        // Signature should be generated on your server and delivered to your app
        String signature = SignatureGenerator.generateSignature(paymentInfo.getParamsForSignature(), SECRET);

        // Sign payment info
        paymentInfo.setSignature(signature);

        // Present Checkout UI
        startActivityForResult(ECMPActivity.buildIntent(this,
                paymentInfo),
                PAY_ACTIVITY_REQUEST);
    }

    // Handle SDK result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAY_ACTIVITY_REQUEST) {
            switch (resultCode) {
                case ECMPActivity.RESULT_SUCCESS:
                case ECMPActivity.RESULT_CANCELLED:
                case ECMPActivity.RESULT_DECLINE:
                case ECMPActivity.RESULT_FAILED:
                    break;
            }

            if(data != null && data.hasExtra(ECMPActivity.DATA_INTENT_EXTRA)) {
                String message = data.getStringExtra(ECMPActivity.DATA_INTENT_EXTRA);
            }
        }
    }

    // Payment Info
    ECMPPaymentInfo getPaymentInfoOnlyRequiredParams() {
        return new ECMPPaymentInfo(
                PROJECT_ID, // project ID that is assigned to you
                "internal_payment_id_1", // payment ID to identify payment in your system
                1999, // 19.99
                "USD"
        );
    }

    ECMPPaymentInfo getPaymentInfoAllParams() {
        return new ECMPPaymentInfo(
                PROJECT_ID, // project ID that is assigned to you
                "internal_payment_id_1", // payment ID to identify payment in your system
                1999, // 19.99
                "USD",
                "T-shirt with dog print",
                "10", // unique ID assigned to your customer
                ""
        );
    }

    // Additional
    void setDMSPayment(ECMPPaymentInfo paymentInfo) {
        paymentInfo.setCreditCardPaymentType(ECMPPaymentInfo.CreditCardPaymentType.Auth);
    }

    void setToken(ECMPPaymentInfo paymentInfo) {
        paymentInfo.setToken("token");
    }

    void setRecurrent(ECMPPaymentInfo paymentInfo) {
        ECMPRecurrentInfo recurrentInfo = new ECMPRecurrentInfo(
                "R", // type
                "11", // expiry month
                "2030", // expiry year
                "M", // recurrent period
                "12:00:00", // start time
                "12-02-2020", // start date
                "your_recurrent_id"); // recurrent payment ID
        paymentInfo.setRecurrent(recurrentInfo);
    }
}
