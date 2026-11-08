# Both Engine - ProGuard Rules

# Add project specific ProGuard rules here.
# Android default rules are already applied.

# Keep all classes in the com.bothengine package
-keep class com.bothengine.** { *; }

# Keep R class
-keep class **.R$* { *; }

# Keep native methods
-keep class * extends java.lang.Object {
    native <methods>;
}

# Keep OpenGL classes
-keep class javax.microedition.khronos.** { *; }
-keep class android.opengl.** { *; }
