# StepPay Proguard & R8 Optimization Rules

# Keep Room Entities & Daos
-keep class com.steppay.app.data.local.entity.** { *; }
-keep class * extends androidx.room.RoomDatabase

# Keep Firebase Data Models & Firestore
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.auth.** { *; }
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
}

# Keep AdMob SDK
-keep class com.google.android.gms.ads.** { *; }

# Keep Coil & Osmdroid
-keep class coil.** { *; }
-keep class org.osmdroid.** { *; }

# Kotlin Serialization
-keepattributes *Annotation*,ElementValueAttribute,InnerClasses,Signature,EnclosingMethod
-keepclassmembers class * {
    *** Companion;
}

# Suppress Warnings
-dontwarn org.osmdroid.**
-dontwarn com.google.firebase.**
