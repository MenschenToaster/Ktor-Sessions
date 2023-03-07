package at.toastiii.ktor.sessions.util

import io.ktor.util.*

/**
 * Generates a secure random session ID
 */
fun generateSessionId(): String = generateNonce() + generateNonce()