# Android-MerchantSDK

This is a sample application for the SumUp Android SDK. 

**Preview build - All APIs and dependencies are subject to change. MinSDK is currently 10 but will soon be 15**

SumUp Android SDK [Changelog](https://github.com/sumup/Android-MerchantSDK/blob/master/CHANGELOG.md)



[Full SumUp API Documentation](https://sumup.com/integration)

##How to use the SDK

+ Create a SumUp account and get an affiliate key [here](https://me.sumup.com/integration-tools)

+ Add the repository to your gradle dependencies 
	```groovy
	allprojects {
	    repositories {
	        maven { url 'https://maven.sumup.com/releases' }
	    }
	}
	```

	Add the dependency to a module
	```groovy
	compile 'com.sumup:merchant-sdk:1.52.1@aar'
	```

+ Provide a callback activity
	```xml
	<activity android:name="ResultActivity"  android:label="Payment Result">
	  <intent-filter>
	    <action android:name="com.example.ResultActivity"></action>
	    <category android:name="android.intent.category.DEFAULT"></category>
	    <category android:name="android.intent.category.BROWSABLE"></category>
	  </intent-filter>
	</activity>
	```
	
+ Initialize the SumUp components in your Application
	```java
	public class SampleApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		SumUpState.init(this);
	}

	@Override
	public void onConfigurationChanged(android.content.res.Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		SumUpState.Instance().updateLocales();
	}
	```
 



+ Make a payment
	```java
    SumUpPayment payment = SumUpPayment.builder()
            //mandatory parameters
            .affiliateKey("abcd1234wxyz")
            .productAmount(1.23)
            .currency("EUR")
            // optional: add details
            .productTitle("Taxi Ride")
            .receiptEmail("customer@mail.com")
            .receiptSMS("+3531234567890")
            // optional: Add metadata
            .addAdditionalInfo("AccountId", "taxi0334")
            .addAdditionalInfo("From", "Berlin")
            .addAdditionalInfo("To", "Paris")
            //optional: foreign transaction ID, must be unique for each merchant!
            .foreignTransactionId(UUID.randomUUID().toString())
            .build();

    SumUpAPI.openPaymentActivity(MainActivity.this, ResponseActivity.class, payment);
	```

#Additional Documentation

+ To log out the currently logged in SumUp account, call 
 ```java 
 	SumUpAPI.logout();
 ```


+ To enable ProGuard, add the following to your gradle file
	
   ```grovy
   buildTypes {
        release {
            //All ProGuard rules required by the SumUp SDK are packaged with the library
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt')
        }
    }
   ```


