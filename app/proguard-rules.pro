# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/jca/Android/sdk/tools/proguard/proguard-android.txt
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
#-keepattributes *Annotation* # already present below!
-keepattributes SourceFile,LineNumberTable
# If you are using custom exceptions, add this line so that custom exception types are skipped during obfuscation:
-keep public class * extends java.lang.Exception
# -printmapping mapping.txt # ensure this is NOT present!!!
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**


-allowaccessmodification
-keepattributes *Annotation*,Signature,Exceptions,InnerClasses,EnclosingMethod

-dontoptimize

-keepclasseswithmembernames class * { native <methods>; }
-keepclassmembers enum * {public static **[] values();public static ** valueOf(java.lang.String);}

-dontshrink

-keep public class * implements com.coremedia.**
-keep public class * implements com.googlecode.**
-keep public class * implements com.mp4parser.**
-keep class com.coremedia.**
-keep class com.googlecode.** { *; }
-keep class com.mp4parser.** { *; }

# Mixpanel
-dontwarn com.mixpanel.**
-keep class com.mixpanel.android.abtesting.** { *; }
-keep class com.mixpanel.android.mpmetrics.** { *; }
-keep class com.mixpanel.android.surveys.** { *; }
-keep class com.mixpanel.android.util.** { *; }
-keep class com.mixpanel.android.java_websocket.** { *; }
-keep class **.R
-keep class **.R$* { <fields>; }

# Butterknife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Retrofit, OkHttp, Gson
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

#Okio
-dontwarn javax.xml.bind.DatatypeConverter
-dontwarn org.apache.commons.codec.binary.Base64
#okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

#Glide
-keep class org.aspectj.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}


-keepclassmembers class com.googlecode.**


# Aspect 클래스 보존
####-keep @org.aspectj.lang.annotation.Aspect class * { *; }
-keepclasseswithmembers class * {
  public static *** aspectOf();
}

# EventBus
-keepclassmembers class ** {
    public void onEvent*(**);
}

# LeakCanary. @See https://github.com/square/leakcanary/blob/master/leakcanary-android/consumer-proguard-rules.pro
-dontwarn com.squareup.haha.guava.**
-dontwarn com.squareup.haha.perflib.**
-dontwarn com.squareup.haha.trove.**
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.haha.** { *; }
-keep class com.squareup.leakcanary.** { *; }

# Marshmallow removed Notification.setLatestEventInfo()
-dontwarn android.app.Notification

# jcraft

-keep class com.jcraft.jsch.jce.*
-keep class * extends com.jcraft.jsch.KeyExchange
-keep class com.jcraft.jsch.**
-keep class com.jcraft.jzlib.ZStream
-keep class com.jcraft.jsch.Compression
-keep class org.ietf.jgss.*

#-libraryjars /jsch-0.1.53.jar
-dontwarn org.ietf.jgss.**

# Proguard Configuration for Realm (http://realm.io)
# For detailed discussion see: https://groups.google.com/forum/#!topic/realm-java/umqKCc50JGU
# Additionally you need to keep your Realm Model classes as well
# For example:
# -keep class com.yourcompany.realm.** { *; }

-keep class com.videonasocialmedia.vimojo.repository.project.RealmProject
-keep class com.videonasocialmedia.vimojo.repository.video.RealmVideo
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-dontwarn javax.**
-dontwarn io.realm.**

# Temporary fix of error with roughhike bottom bar at release build
#   with version: com.roughike:bottom-bar:2.0.2
# See: https://github.com/roughike/BottomBar/issues/456
-dontwarn com.roughike.bottombar.**

# Guava setup
# Configuration for Guava 18.0
#
# disagrees with instructions provided by Guava project: https://code.google.com/p/guava-libraries/wiki/UsingProGuardWithGuava

-keep class com.google.common.io.Resources {
    public static <methods>;
}
-keep class com.google.common.collect.Lists {
    public static ** reverse(**);
}
-keep class com.google.common.base.Charsets {
    public static <fields>;
}

-keep class com.google.common.base.Joiner {
    public static com.google.common.base.Joiner on(java.lang.String);
    public ** join(...);
}

-keep class com.google.common.collect.MapMakerInternalMap$ReferenceEntry
-keep class com.google.common.cache.LocalCache$ReferenceEntry

# http://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Guava 19.0
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Mp4parser
-dontwarn com.googlecode.mp4parser.**

# Samsung SDK
-keep class com.samsung.** { *; }
-keep class com.samsung.**$* { *; }
-dontwarn com.samsung.**
