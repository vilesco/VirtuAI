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

-keep public class com.google.firebase.** {public *;}
-keep class com.google.android.gms.internal.** {public *;}
-keepclasseswithmembers class com.google.firebase.FirebaseException { *; }



# Keep the User class and its members
-keep class com.texttovoice.virtuai.data.model.User {
    *;
}

# Keep names of fields that are used in Firestore
-keepclassmembers class com.texttovoice.virtuai.data.model.User {
    <fields>;
}

# Prevent obfuscation of classes required for Firestore
-keep class com.google.firebase.firestore.** {
    *;
}

-dontwarn com.google.firebase.annotations.PreviewApi
-dontwarn com.google.firebase.messaging.TopicOperation$TopicOperations


# Keep Retrofit interfaces, methods, and annotations
-keep interface com.texttovoice.virtuai.** { *; }

# Keep classes that Retrofit uses for its annotation
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Keep Gson specific classes and methods
-keep class com.google.gson.** { *; }

# Keep fields that Gson uses
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Keep the names of fields annotated with @SerializedName
-keepattributes Annotation

# Keep your custom data class used with Retrofit
-keep class com.texttovoice.virtuai.data.model.GeneratedImage { *; }

# Keep fields in the data class if using Gson or another JSON parser
-keepclassmembers class com.texttovoice.virtuai.data.model.GeneratedImage {
    <fields>;
}


-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepattributes *Annotation*, InnerClasses
-keepattributes Signature
-keep class com.texttovoice.virtuai.data.model.** { *; }

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keep class * extends com.google.protobuf.GeneratedMessageLite { *; }
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-dontwarn com.google.android.gms.safetynet.SafetyNet
-dontwarn com.google.android.gms.safetynet.SafetyNetApi$AttestationResponse
-dontwarn com.google.android.gms.safetynet.SafetyNetClient