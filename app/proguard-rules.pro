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

-dontwarn rx.**

#Glide
-keep class org.aspectj.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

##--- Begin:retrofit, okio, okhttp ----

# Retrofit. http://square.github.io/retrofit/
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
-keepattributes *Annotation*
# Extra, according to https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-retrofit2.pro
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembernames interface * {
        @retrofit.http.* <methods>;
}

## -------------End: Retrofit2 ---

# Okio rule https://github.com/square/okio#proguard
-dontwarn okio.**
#Extra https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-okio.pro
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-dontwarn javax.xml.bind.DatatypeConverter
-dontwarn org.apache.commons.codec.binary.Base64

# Okhttp https://github.com/square/okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
# Extra https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-okhttp3.pro
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

##--- End:retrofit, okio, okhttp ----

##--- Begin:GSON ---- https://medium.com/@abangkis/retrofit-2-and-the-three-body-problem-f8a93039aeb2
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# keep enum so gson can deserialize it
-keepclassmembers enum * { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.videonasocialmedia.vimojo.vimojoapiclient.model.** { *; }
-keep class com.videonasocialmedia.vimojo.sync.model.** { *; }

##--- End:GSON ----

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

-keep class com.videonasocialmedia.vimojo.cut.repository.datasource.RealmProject
-keep class com.videonasocialmedia.vimojo.asset.repository.datasource.RealmVideo
-keep class com.videonasocialmedia.vimojo.repository.music.datasource.RealmMusic
-keep class com.videonasocialmedia.vimojo.composition.repository.datasource.RealmTrack
-keep class com.videonasocialmedia.vimojo.repository.upload.datasource.RealmUpload
-keep class com.videonasocialmedia.vimojo.cameraSettings.repository.RealmCameraSettings

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

# Mp4parser, coremedia
-dontwarn com.googlecode.mp4parser.**

-keep public class * implements com.coremedia.**
-keep public class * implements com.googlecode.**
-keep public class * implements com.mp4parser.**
-keep class com.coremedia.**
-keep class com.googlecode.** { *; }
-keep class com.mp4parser.** { *; }

# Samsung SDK
-keep class com.samsung.** { *; }
-keep class com.samsung.**$* { *; }
-dontwarn com.samsung.**

# dagger2 https://github.com/google/dagger/issues/645
-dontwarn com.google.errorprone.annotations.*