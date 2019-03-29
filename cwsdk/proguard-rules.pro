# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\android\sdk\android-sdk-r23/tools/proguard/proguard-android.txt
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

#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5       # 指定代码的压缩级别
-dontusemixedcaseclassnames     # 是否使用大小写混合
-dontskipnonpubliclibraryclasses        # 指定不去忽略非公共的库类
-dontskipnonpubliclibraryclassmembers       # 指定不去忽略包可见的库类的成员
-dontpreverify      # 混淆时是否做预校验
-verbose        # 混淆时是否记录日志
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------
-ignorewarnings     # 是否忽略检测，（是）
#---------------------------------默认保留区---------------------------------
#-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application

#广点通
-keep class com.qq.e.comm.** {*;}
-keep class com.qq.e.ads.** {*;}
-keep interface com.qq.e.comm.** {*;}
-keep interface com.qq.e.ads.** {*;}
-keep class com.example.administrator.myapplication.** {*;}
-keep class com.xdad.XDAPI {*;}
-keep class com.xdad.qq {*;}
-keep class com.xdad.AActivity {*;}
#所有供开发者使用接口
-keep interface com.xdad.*Listener {*;}
-keep interface com.xdad.*Listener {*;}
-keep class com.xdad.*View {*;}

#穿山甲
-keep class com.androidquery.** {*;}
-keep interface com.androidquery.** {*;}

#掌中
-keep public class saifn.ubh.of.wqr.nmfi32.Entrance{*;}

#-repackageclasses com.wy