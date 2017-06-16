# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/alvaro/Android/Sdk/tools/proguard/proguard-android.txt
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

## setup for improving crashlytics reports
-keepattributes *Annotation* # already present below!
-keepattributes SourceFile,LineNumberTable
# If you are using custom exceptions, add this line so that custom exception types are skipped during obfuscation:
-keep public class * extends java.lang.Exception
# -printmapping mapping.txt # ensure this is NOT present!!!
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
