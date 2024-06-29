package com.example.medscape20.util

fun isPermissionGranted(
    permissions: Array<String>,
    grantResults: IntArray,
    askedPermission: String
): Boolean {
    for (i in permissions.indices) {
        if (permissions[i] == askedPermission) {
            return grantResults[i] == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }
    return false
}