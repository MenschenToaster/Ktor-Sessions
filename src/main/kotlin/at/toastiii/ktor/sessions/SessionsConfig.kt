package at.toastiii.ktor.sessions

import at.toastiii.ktor.sessions.storage.SessionStorage
import at.toastiii.ktor.sessions.transport.CookieConfiguration
import at.toastiii.ktor.sessions.transport.CookieSessionTransport
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

    inline fun <reified T : Session> cookie(
        name: String, storage: SessionStorage<T>,
        autoCommit: Boolean = true, cookieName: String = name,
        builder: CookieConfiguration.() -> Unit = {}
    ) {

        register(
            SessionProvider(
                name = name,
                type = T::class,
                storage = storage,
                autoCommit = autoCommit,
                transport = CookieSessionTransport(cookieName, CookieConfiguration().apply { builder() }),
            )
        )
    }
}