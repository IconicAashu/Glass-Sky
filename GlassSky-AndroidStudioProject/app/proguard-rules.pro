# Keep kotlinx serialization models
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepattributes *Annotation*,InnerClasses
-dontwarn kotlinx.serialization.**
