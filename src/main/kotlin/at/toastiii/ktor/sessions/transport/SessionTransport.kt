package at.toastiii.ktor.sessions.transport

import io.ktor.server.application.*

/**
 * A session transport used to [receive], [send], or [clear] a session from/to an [ApplicationCall].
 */
interface SessionTransport {
    /**
     * Gets a session information from a [call] and returns a [String] if success or null if failed.
     */
    fun receive(call: ApplicationCall): String?

    /**
     * Sets a session information represented by [value] to a [call].
     */
    fun send(call: ApplicationCall, value: String)

    /**
     * Clears a session information from a specific [call].
     */
    fun clear(call: ApplicationCall)
}