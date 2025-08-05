// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.google.gms.google-services") version "4.4.3" apply false

    // โค้ดส่วนอื่นๆ ที่มีอยู่แล้ว
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // เพิ่มการตั้งค่าสำหรับ Compose Compiler ที่นี่
    alias(libs.plugins.kotlin.compose) apply false
}
