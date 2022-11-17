package com.ecommpay.test_integration_kotlin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.ecommpay.sdk.*
import com.ecommpay.sdk.threeDSecure.*
import com.ecommpay.test_integration_kotlin.Utils.showMessage
import com.google.android.gms.wallet.PaymentDataRequest
import java.util.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    //If you use deprecated onActivityResult()
    //private val PAY_ACTIVITY_REQUEST = 888

    private val PROJECT_ID = 123
    private val SECRET = "your_secret"
    private val RANDOM_PAYMENT_ID = "test_integration_ecommpay_${getRandomNumber()}"
    private var paymentInfo by Delegates.notNull<ECMPPaymentInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //STEP 1: Create payment info object with product information
        paymentInfo = getPaymentInfoOnlyRequiredParams() // getPaymentInfoAllParams

        //STEP 2: Signature should be generated on your server and delivered to your app
        val signature = SignatureGenerator.generateSignature(
            paramsToSign = getParamsForSigning() ?: "",
            secret = SECRET
        )

        //STEP 3: Sign payment info object
        setSignature(signature)

        //STEP 4: Create the intent of SDK
        val SDKIntent = ECMPActivity.buildIntent(this, paymentInfo)

        //STEP 5: Present Checkout UI (default theme is light)
        startActivityForResult.launch(SDKIntent)

        //Additional STEP (if necessary): custom theme
        //And then override STEP 4 like that:
        //val SDKIntent = ECMPActivity.buildIntent(this, paymentInfo, setupCustomTheme())

        //Additional STEP (if necessary): add additional fields
        setupAdditionalFields()
        //Or you can do this like that:
        //addEcmpScreenDisplayModes()

        //Additional STEP (if necessary): add recurrent info
        setupRecurrentInfo()

        //Additional STEP (if necessary): add 3DS
        setupThreeDSecureParams()

        //Additional STEP (if necessary): add google pay
        setupGooglePay()

        //Additional STEP (if necessary): add funding fields for google pay
        setupRecipientInfo()

        //Additional STEP (if necessary): custom behaviour of SDK
        setupScreenDisplayModes()
    }

    //STEP 6: Handle SDK result
    private val startActivityForResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            when (result.resultCode) {
                ECMPActivity.RESULT_SUCCESS -> {
                    showMessage(this, "Success")
                }
                ECMPActivity.RESULT_CANCELLED -> {
                    showMessage(this, "Cancelled")
                }
                ECMPActivity.RESULT_DECLINE -> {
                    showMessage(this, "Decline")
                }
                ECMPActivity.RESULT_FAILED -> {
                    showMessage(this, "Failed")
                }
            }
            val error = data?.getStringExtra(ECMPActivity.DATA_INTENT_EXTRA_ERROR)
            val token = data?.getStringExtra(ECMPActivity.DATA_INTENT_EXTRA_TOKEN)
            val isSessionInterrupted = data?.getBooleanExtra(ECMPActivity.DATA_INTENT_SESSION_INTERRUPTED, false)
        }

    //Only for testing
    private fun getRandomNumber(): String {
        val randomNumber = Random().nextInt(9999) + 1000
        return randomNumber.toString()
    }

    //Getters for payment info
    //Default payment action type is ActionType.Sale
    private fun getPaymentInfoOnlyRequiredParams() = ECMPPaymentInfo(
        PROJECT_ID, // Project ID that is assigned to you
        RANDOM_PAYMENT_ID, // Payment ID to identify payment in your system
        100, // 1.00
        "USD" // Payment currency (see the ISO 4217 codes list)
    )

    //Default payment action type is ActionType.Sale
    private fun getPaymentInfoAllParams() = ECMPPaymentInfo(
        PROJECT_ID, // Project ID that is assigned to you
        RANDOM_PAYMENT_ID, // Payment ID to identify payment in your system
        100, // 1.00
        "USD", // Payment currency (see the ISO 4217 codes list)
        "T-shirt with dog print",
        "10", // unique ID assigned to your customer
        "" //Payment region code (see the ISO 3166-2 codes list)
    )

    //Get params for signing payment (do it only after create paymentInfo object)
    private fun getParamsForSigning() = paymentInfo.paramsForSignature

    //Getters for all params payment info
    private fun getSignature() = paymentInfo.signature

    private fun getProjectId() = paymentInfo.projectId

    private fun getPaymentId() = paymentInfo.paymentId

    private fun getPaymentAmount() = paymentInfo.paymentAmount

    private fun getPaymentCurrency() = paymentInfo.paymentCurrency

    private fun getPaymentDescription() = paymentInfo.paymentDescription

    private fun getCustomerId() = paymentInfo.customerId

    private fun getRegionCode() = paymentInfo.regionCode

    //Default value is mobile device language
    private fun getLanguageCode() = paymentInfo.languageCode

    private fun getToken() = paymentInfo.token

    private fun getReceiptData() = paymentInfo.receiptData

    private fun getBankId() = paymentInfo.bankId

    private fun getHideSavedWallets() = paymentInfo.hideSavedWallets

    private fun getForcePaymentMethod() = paymentInfo.forcePaymentMethod

    //Default payment action type is ActionType.Sale
    private fun getAction() = paymentInfo.action

    //Setters for payment info

    private fun setSignature(signature: String) = paymentInfo.apply { this.signature = signature }

    //Set the custom language code (see the ISO 639-1 codes list)
    private fun setLanguageCode() = paymentInfo.apply { languageCode = "language code" }

    private fun setToken() = paymentInfo.apply { token = "token" }

    private fun setReceiptData() = paymentInfo.apply { receiptData = "receipt data" }

    // if you want to hide the saved cards, pass the value - true
    private fun setHideSavedWallets() = paymentInfo.apply { hideSavedWallets = false }

    // For forced opening of the payment method, pass its code. Example: qiwi, card ...
    private fun setForcePaymentMethod() = paymentInfo.apply { forcePaymentMethod = "card" }

    private fun setBankId() = paymentInfo.apply { bankId = 123 }

    //Additional getters and setters
    //RecurrentInfo
    private fun getECMPRecurrentInfo() = paymentInfo.ecmpRecurrentInfo

    private fun setRecurrentInfo(recurrentInfo: ECMPRecurrentInfo?) =
        paymentInfo.setRecurrent(recurrentInfo)

    //Screen Display Mode
    private fun getScreenDisplayModes() = paymentInfo.ecmpScreenDisplayModes
    private fun setEcmpScreenDisplayModes(ecmpScreenDisplayModes: List<ECMPScreenDisplayMode>?) =
        paymentInfo.setEcmpScreenDisplayMode(ecmpScreenDisplayModes)

    //Alternative variant of setter
    private fun addEcmpScreenDisplayModes() =
        paymentInfo
            .addEcmpScreenDisplayMode("hide_decline_final_page")
            .addEcmpScreenDisplayMode("hide_success_final_page")

    //AdditionalFields
    private fun getECMPAdditionalFields() = paymentInfo.ecmpAdditionalFields
    private fun setECMPAdditionalFields(ecmpAdditionalFields: Array<ECMPAdditionalField>?) =
        paymentInfo.apply { this.ecmpAdditionalFields = ecmpAdditionalFields }


    //Handling funding for google pay
    private fun getRecipientInfo() = paymentInfo.ecmpRecipientInfo
    private fun setRecipientInfo(ecmpRecipientInfo: ECMPRecipientInfo?) =
        paymentInfo.apply { this.ecmpRecipientInfo = ecmpRecipientInfo }

    //3DS Info
    private fun getEcmpThreeDSecureInfo() = paymentInfo.ecmpThreeDSecureInfo
    private fun setEcmpThreeDSecureInfo(ecmpThreeDSecureInfo: ECMPThreeDSecureInfo?) =
        paymentInfo.apply { this.ecmpThreeDSecureInfo = ecmpThreeDSecureInfo }

    //Setters for custom payment action type (Auth, Tokenize, Verify)
    private fun setDMSPayment() =
        paymentInfo.apply { action = ECMPPaymentInfo.ActionType.Auth }

    private fun setActionTokenize() =
        paymentInfo.apply { action = ECMPPaymentInfo.ActionType.Tokenize }

    private fun setActionVerify() =
        paymentInfo.apply { action = ECMPPaymentInfo.ActionType.Verify }

    private fun setAction(action: ECMPPaymentInfo.ActionType) =
        paymentInfo.apply { this.action = action }


    private fun setupCustomTheme(): ECMPTheme {
        val customTheme = ECMPTheme.getDarkTheme()
        with(customTheme) {
            fullScreenBackgroundColor = Color.GREEN
            showShadow = false
            //etc
        }
        return customTheme
    }

    private fun setupRecurrentInfo() {
        val recurrentInfo = ECMPRecurrentInfo(
            "R", // type
            "20", // expiry day
            "11", // expiry month
            "2030", // expiry year
            "M", // recurrent period
            "12:00:00", // start time
            "12-02-2020", // start date
            "your_recurrent_id") // recurrent payment ID
        // Additional options if needed
        recurrentInfo.setAmount(1000)
        recurrentInfo.setSchedule(
            arrayOf(
                ECMPRecurrentInfoSchedule("20-10-2020", 1000),
                ECMPRecurrentInfoSchedule("20-10-2020", 1000)
            )
        )
        setRecurrentInfo(recurrentInfo)
    }

    private fun setupAdditionalFields() =
        setECMPAdditionalFields(arrayOf(
            ECMPAdditionalField(
                ECMPAdditionalFieldEnums.AdditionalFieldType.customer_first_name, "Mark"),
            ECMPAdditionalField(
                ECMPAdditionalFieldEnums.AdditionalFieldType.billing_country, "US")))

    private fun setupScreenDisplayModes() =
        setEcmpScreenDisplayModes(arrayListOf(
            ECMPScreenDisplayMode.HIDE_DECLINE_FINAL_PAGE,
            ECMPScreenDisplayMode.HIDE_SUCCESS_FINAL_PAGE
        ))

    private fun setupRecipientInfo() {
        val ecmpRecipientInfo = ECMPRecipientInfo(
            "Wallet owner's name",
            "Your wallet id",
            "Country",
            null,
            null,
            null,
            null,
            null,
        )
        setRecipientInfo(ecmpRecipientInfo)
    }

    // Setup 3D Secure parameters
    private fun setupThreeDSecureParams() {
        val threeDSecureInfo = ECMPThreeDSecureInfo()

        val threeDSecurePaymentInfo = ECMPThreeDSecurePaymentInfo()

        threeDSecurePaymentInfo
            .setReorder("01") // This parameter indicates whether the cardholder is reordering previously purchased merchandise.
            .setPreorderPurchase("01") // This parameter indicates whether cardholder is placing an order for merchandise with a future availability or release date.
            .setPreorderDate("01-10-2019") // The date the preordered merchandise will be available.Format: dd-mm-yyyy.
            .setChallengeIndicator("01") // This parameter indicates whether challenge flow is requested for this payment.
            .challengeWindow = "01" // The dimensions of a window in which authentication page opens.

        val threeDSecureGiftCardInfo = ECMPThreeDSecureGiftCardInfo()

        threeDSecureGiftCardInfo
            .setAmount(12345) // Amount of payment with prepaid or gift card denominated in minor currency units.
            .setCurrency("USD") // Currency of payment with prepaid or gift card in the ISO 4217 alpha-3 format
            .count = 1 // Total number of individual prepaid or gift cards/codes used in purchase.

        threeDSecurePaymentInfo.giftCard = threeDSecureGiftCardInfo // object with information about payment with prepaid card or gift card.

        val threeDSecureCustomerInfo = ECMPThreeDSecureCustomerInfo()
        threeDSecureCustomerInfo
            .setAddressMatch("Y") //The parameter indicates whether the customer billing address matches the address specified in the shipping object.
            .setHomePhone("79105211111") // Customer home phone number.
            .setWorkPhone("73141211111") // Customer work phone number.
            .billingRegionCode = "ABC" // State, province, or region code in the ISO 3166-2 format. Example: SPE for Saint Petersburg, Russia.

        val threeDSecureAccountInfo = ECMPThreeDSecureAccountInfo() // object with account information on record with merchant

        threeDSecureAccountInfo
            .setActivityDay(22) // Number of card payment attempts in the last 24 hours.
            .setActivityYear(222) // Number of card payment attempts in the last 365 days.
            .setAdditional("gamer12345") // Additional customer account information, for instance arbitrary customer ID.
            .setAgeIndicator("01") // Number of days since the customer account was created.
            .setDate("01-10-2019") // Account creation date.
            .setChangeDate("01-10-2019") // Last account change date except for password change or password reset.
            .setChangeIndicator("01") // Number of days since last customer account update, not including password change or reset.
            .setPassChangeDate("01") // Last password change or password reset date.
            .setPassChangeIndicator("01-10-2019") // Number of days since the last password change or reset.
            .setProvisionAttempts(16) // Number of attempts to add card details in customer account in the last 24 hours.
            .setPurchaseNumber(12) // Number of purchases with this cardholder account in the previous six months.
            .setAuthData("login_0102") // Any additional log in information in free text.
            .setAuthTime("01-10-2019 13:12") // Account log on date and time.
            .setAuthMethod("01") // Authentication type the customer used to log on to the account when placing the order.
            .setSuspiciousActivity("01") // Suspicious activity detection result.
            .setPaymentAge("01-10-2019") // Card record creation date.
            .paymentAgeIndicator = "01" //  Number of days since the payment card details were saved in a customer account.

        val threeDSecureShippingInfo = ECMPThreeDSecureShippingInfo() // object that contains shipment details

        threeDSecureShippingInfo
            .setType("01") //Shipment indicator.
            .setDeliveryTime("01") //Shipment terms.
            .setDeliveryEmail("test@gmail.com") // The email to ship purchased digital content, if the customer chooses email delivery.
            .setAddressUsageIndicator("01") // Number of days since the first time usage of the shipping address.
            .setAddressUsage("01-10-2019") // First shipping address usage date in the dd-mm-yyyy format.
            .setCity("Moscow") // Shipping city.
            .setCountry("RU") // Shipping country in the ISO 3166-1 alpha-2 format
            .setAddress("Lenina street 12") // Shipping address.
            .setPostal("109111") // Shipping postbox number.
            .setRegionCode("MOW") // State, province, or region code in the ISO 3166-2 format.
            .nameIndicator = "01" // Shipment recipient flag.

        val threeDSecureMpiResultInfo = ECMPThreeDSecureMpiResultInfo() // object that contains information about previous customer authentication

        threeDSecureMpiResultInfo
            .setAcsOperationId("321412-324-sda23-2341-adf12341234") // The ID the issuer assigned to the previous customer operation and returned in the acs_operation_id parameter inside the callback with payment processing result. Maximum 30 characters.
            .setAuthenticationFlow("01") // The flow the issuer used to authenticate the cardholder in the previous operation and returned in the authentication_flow parameter of the callback with payment processing results.
            .authenticationTimestamp = "21323412321324" // Date and time of the previous successful customer authentication as returned in the mpi_timestamp parameter inside the callback message with payment processing result.

        threeDSecureCustomerInfo
            .setAccountInfo(threeDSecureAccountInfo) // object with account information on record with merchant
            .setMpiResultInfo(threeDSecureMpiResultInfo) // object that contains information about previous customer authentication
            .shippingInfo = threeDSecureShippingInfo // object that contains shipment details


        threeDSecureInfo.threeDSecureCustomerInfo = threeDSecureCustomerInfo
        threeDSecureInfo.threeDSecurePaymentInfo = threeDSecurePaymentInfo

        setEcmpThreeDSecureInfo(threeDSecureInfo)
    }

    private fun setupGooglePay() {
        paymentInfo.merchantId = "your merchant id"
        paymentInfo.paymentDataRequest = PaymentDataRequest.fromJson(GooglePayJsonParams.getJSON())
    }
}