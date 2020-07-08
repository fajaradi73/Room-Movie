# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

-keep class org.ocpsoft.prettytime.i18n.**
# Parceler library
-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }
-keep class androidx.appcompat.widget.** { *; }
-dontwarn com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar
# Proguard configuration for Jackson 2.x (fasterxml package instead of codehaus package)
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-keepnames class com.fasterxml.jackson.** { *; }
-dontwarn com.fasterxml.jackson.databind.**
-dontwarn sun.misc.**

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
 long producerIndex;
long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef { rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef { rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent