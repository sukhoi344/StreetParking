# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in I:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep class weborb.** {*;}
-keep class com.backendless.**{*;}
-keep class chau.streetparking.datamodels.** {*;}

-dontwarn com.backendless.**
-dontwarn weborb.**
-dontwarn com.makeramen.roundedimageview.**

# these are for the Braintree
-dontwarn com.google.android.gms.**
-dontwarn android.support.v4.**
