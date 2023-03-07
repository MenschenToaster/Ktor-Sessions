package at.toastiii.ktor.sessions

import io.ktor.util.*

@KtorDsl
class SessionsConfig {

    private val registered = ArrayList<SessionProvider<*>>()

    /**
     * Gets a list of session providers that are registered.
     */
    val providers: List<SessionProvider<*>>
        get() = registered.toList()

    /**
     * Registers a session [provider].
     */
    fun register(provider: SessionProvider<*>) {
        if(registered.any { it.name == provider.name || it.type == provider.type }) {
            throw IllegalArgumentException("A session provider with the same name or type is already registered")
        }

        registered.add(provider)
    }

}