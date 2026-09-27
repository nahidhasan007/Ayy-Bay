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

# Keep line numbers for readable crash stack traces from real users.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Google Sign-In (Credential Manager) parses the ID token credential via Parcelable/Bundle --
# keep its data classes intact even though its AAR should already ship consumer rules for this.
-keep class com.google.android.libraries.identity.googleid.** { *; }
-keep class androidx.credentials.** { *; }

# Room entities/DAOs are referenced from KSP-generated code by name; keep entity fields so
# column mapping can't silently break under obfuscation.
-keep class com.ayybay.app.data.local.entity.** { *; }
-keep class com.ayybay.app.domain.model.** { *; }