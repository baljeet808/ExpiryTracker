package com.baljeet.expirytracker.util

enum class Status(val status: String) {
    ALL("All"),
    FRESH("Fresh"),
    EXPIRING("Expiring"),
    EXPIRED("Expired")
}