# Importing libraries
**Importing libraries in your project**

Listed below are the instructions on how to import the ecommpaySDK.aar library into your application

1. Download the ecommpaySDK.aar file;
2. Add the ecommpaySDK.aar file into your project;
3. Open your application module (build.gradle);
4. In the `android {}` section, add the following compilation parameters:

```
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
``` 
5. In the `dependencies {}` section, add the following dependencies:

```
//Ecommapy SDK
implementation files ('../libs/ecommpaySDK.aar')
//Retrofit
implementation 'com.squareup.retrofit2:retrofit:2.3.0'
implementation 'com.squareup.okhttp3:logging-interceptor:3.10.0'
implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
//Android
implementation 'io.card:android-sdk:5.5.1'
implementation 'androidx.appcompat:appcompat:1.0.0'
implementation 'androidx.legacy:legacy-support-v4:1.0.0'
implementation 'androidx.recyclerview:recyclerview:1.0.0'
implementation 'com.google.code.gson:gson:2.8.4'
implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.0.0'
implementation 'androidx.lifecycle:lifecycle-extensions:2.0.0'
implementation 'com.google.android.gms:play-services-wallet:18.1.3'
kapt 'androidx.lifecycle:lifecycle-compiler:2.0.0'
``` 
If some of the dependencies are already added in the dependencies {} section, do not add them again.
6. Add the following permissions in the `AndroidManifest.xml` file:
``` 
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
## Importing libraries via MavenCentral
The SDK for Android libraries can be imported via MavenCentral. To import the libraries via MavenCentral, you need to do the following:
1. Open your application module (`build.gradle`);
2. In the `repositories {}` section, specify the `mavenCentral` repository:
```
allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}
```
3. In the `dependencies {}` section, add the following dependencies:
```
implementation "com.ecommpay:paymentpage-sdk-android:LATEST_VERSION"
```
****
# Opening payment form
This section contains samples of payment form invocation code in Java and Kotlin.
## Opening payment form in Java
To open payment form, do the following:
1. Create an instance of the `ECMPPaymentInfo` class with all the required parameters and any number of optional parameters, for example:
```
ECMPPaymentInfo paymentInfo = new ECMPPaymentInfo(
    115,                        // Project ID
    "pid42582512",              // Payment ID, must be unique within the project
    1999,                       // Payment amount in minor currency units
    "USD"                       // Payment currency
    "T-shirt with dog print",   // Payment description
    "10",                       // Customer identifier in your project
    "US"                        // Region code
);
```
Here are the required parameters:
- `projectID` — project (merchant) ID assigned you;
- `paymentID` — payment ID, must be unique within the project;
- `paymentAmount` — payment amount in minor currency units;
- `paymentCurrency` — payment currency code according to ISO-4217 alpha-3.

Here are the optional parameters:
- `recurrentInfo` — object with the details of COF payment (more)
- `paymentDescription` — payment description (this parameter is available not only to the merchant, but also to the customer;
    + if paymentDescription is specified in the request, it is visible to the customer in the payment form (in the dialog box with information about the payment);
    + if this parameter is not specified in the request, it is not visible to the customer).
- `customerID` — customer ID;
- `regionCode` — customer country;
- `ActionType` — action type (Sale (by default), Auth, Tokenize, or Verify;
- `token` — card token;
- `forcePaymentMethod` — the identifier of the payment method which is opened to the customer without an option for the customer to select another payment method. The list of codes is provided in the IDs of payment methods supported on Payment Page section
- `hideSavedWallets` — hiding or displaying saved payment instruments in the payment form.
  **Possible values:**
    + true — saved payment instruments are hidden, they are not displayed in the payment form
    + false — saved payment instruments are displayed in the payment form
- `ECMPScreenDisplayMode` — object to manage display of the final page of the payment form and hide the final page if necessary.

  **The following parameters can be passed in the object:**
    + hide_success_final_page — the final page with the message about the performed payment is not displayed in the payment form;
    + hide_decline_final_page — the final page with the message about the declined payment is not displayed in the payment form.
2. Once all the payment parameters are collected, pack the parameters into a string for signing:
```
paymentInfo.getParamsForSignature();
```
3. Send the string to your back end.
4. Have your back end generate the signature on the basis of the string and your secret key.
5. Send the signature to the client app and add it in the paymentInfo object:
```
paymentInfo.setSignature(<signature>);
```
6. Open the payment form by using the following code:
```
startActivityForResult(ECMPActivity.buildIntent(this,
                    paymentInfo),
                    PAY_ACTIVITY_REQUEST);
```
****
## Response processing
To receive and process response with the payment processing results you need to override the `onActivityResult` method in activity from which you started `ECMPActivity`.
```
@Override
 protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
     if (requestCode == PAY_ACTIVITY_REQUEST) {
         switch (resultCode) {
             case ECMPActivity.RESULT_SUCCESS:
                 // Add your code to execute when checkout completes successfully
             case ECMPActivity.RESULT_CANCELLED:
                 // Add your code to execute when customer cancels checkout
             case ECMPActivity.RESULT_DECLINE:
                 // Add your code to execute when checkout is declined
             case ECMPActivity.RESULT_FAILED:
                 // Add your code to execute when checkout fails
                 break;
         }
         if (data != null && data.hasExtra(ECMPActivity.DATA_INTENT_EXTRA_ERROR)) {
            String error = data.getStringExtra(ECMPActivity.DATA_INTENT_EXTRA_ERROR);
         }
         if(data != null && data.hasExtra(ECMPActivity.DATA_INTENT_EXTRA_TOKEN)) {
             String token = data.getStringExtra(ECMPActivity.DATA_INTENT_EXTRA_TOKEN);
         }
         if(data != null && data.hasExtra(ECMPActivity.DATA_INTENT_SESSION_INTERRUPTED)) {
             String token = data.getStringExtra(ECMPActivity.DATA_INTENT_SESSION_INTERRUPTED);
         }
     }
 }
```
The following table lists the possible values of response codes as well as the corresponding constant names and description along with the suggested response actions:
| RESPONSE CODE | RESULT CONSTANT | DESCRIPTION |
| -----|------|------|
| 000 | RESULT_SUCCESS | Checkout successfully completed |
| 100 | RESULT_DECLINE | Operation was declined, for example because of insufficient funds |
| 301 | RESULT_CANCELLED | Operation was cancelled by the customer |
| 501 | RESULT_FAILED | An internal error occurred. You may need to contact technical support |
