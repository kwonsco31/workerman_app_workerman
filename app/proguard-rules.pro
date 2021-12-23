# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\bada\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

 -keep class com.google.** {*;}
 -keep class android.support.** {*;}
 -keep class org.apache.** {*;}
 -keep class org.json.** {*;}
 -keep class lombok.** {*;}
 -keep class com.android.** {*;}
 -keep class javax.annotation.** {*;}

# -keep class com.workerman.app.Defines { *; }
# -keep com.workerman.app.manager.*
# -keep com.workerman.app.net.model.*

 -keep @android.support.annotation.Keep class *

 -keep @android.support.annotation.Keep class * {
     *;
 }

-keep class * {
    @android.support.annotation.Keep *;
}

#Keep classes that are referenced on the AndroidManifest
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment

-keep public class * extends m.client.android.library.core.control.AbstractInterface {
public protected *;
}

-dontwarn com.google.android.gms.**
-dontwarn lombok.**
-dontwarn javax.**
-dontwarn org.apache.**
-dontwarn com.squareup.**
-dontwarn com.sun.**
-dontwarn **retrofit**

# Don't note duplicate definition (Legacy Apche Http Client)
-dontnote android.net.http.*
-dontnote org.apache.http.**
-dontnote sun.misc.Unsafe
-dontnote com.google.gson.internal.UnsafeAllocator

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*
-keepattributes EnclosingMethod

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

-dontoptimize # 최적화 하지 않기
-dontshrink # 사용하지 않는 메소드 유지
#-keep class com.example.classname # ClassNotFoundException에러나 난독화를 진행하지 않고 유지하는 옵션
#-keepclassmembers class com.example.classname { 접근제어자 *; } # 특정 클래스의 맴버 원상태 유지
-keepattributes InnerClasses # 내부클래스 원상태 유지 적용



-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# EventBus Start
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}
# EventBus End

#Glide Start
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#Glide End

# android support start
-libraryjars libs
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
# android support end
#tapjoy start
-keep class com.ablar.android.common.** { *; }
-keep interface com.ablar.android.common.** { *; }
#tapjoy end

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

#com.tapjoy.internal.x: can't find referenced class com.ablar.android.common.R$string

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase