package at.toastiii.ktor.sessions.transport

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.util.date.*

const val DEFAULT_SESSION_MAX_AGE: Long = 7L * 24 * 3600 // 7 days

class CookieSessionTransport(
    private val name: String,
    private val configuration: CookieConfiguration
) : SessionTransport {
    override fun receive(call: ApplicationCall): String? {
        return call.request.cookies[name, configuration.encoding]
    }

    override fun send(call: ApplicationCall, value: String) {
        val maxAge = configuration.maxAgeInSeconds * 1000L

        val expires = when (maxAge) {
            0L -> null
            else -> GMTDate() + maxAge
        }

        val cookie = Cookie(name = name,
            value = value,
            encoding = configuration.encoding,
            maxAge = maxAge.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
            expires = expires,
            domain = configuration.domain,
            path = configuration.path,
            secure = configuration.secure,
            httpOnly = configuration.httpOnly,
            extensions = configuration.extensions
        )

        call.response.cookies.append(cookie)
    }

    override fun clear(call: ApplicationCall) {
        call.response.cookies.append(clearCookie())
    }


    private fun clearCookie(): Cookie = Cookie(
        name,
        "",
        configuration.encoding,
        maxAge = 0,
        domain = configuration.domain,
        path = configuration.path,
        secure = configuration.secure,
        httpOnly = configuration.httpOnly,
        extensions = configuration.extensions,
        expires = GMTDate.START
    )
}


class CookieConfiguration {
    /**
     * Specifies the number of seconds until the cookie expires.
     */
    var maxAgeInSeconds: Long = DEFAULT_SESSION_MAX_AGE
        set(newMaxAge) {
            require(newMaxAge >= 0) { "maxAgeInSeconds shouldn't be negative: $newMaxAge" }
            field = newMaxAge
        }

    /**
     * Specifies a cookie encoding.
     */
    var encoding: CookieEncoding = CookieEncoding.URI_ENCODING

    /**
     * Specifies the host to which the cookie is sent.
     */
    var domain: String? = null

    /**
     * Cookie path
     *
     * Specifies the cookie path.
     */
    var path: String? = "/"

    /**
     * Enables transferring cookies via a secure connection only and
     * protects session data from HTTPS downgrade attacks.
     */
    var secure: Boolean = false

    /**
     * Specifies whether cookie access is forbidden from JavaScript.
     */
    var httpOnly: Boolean = true

    /**
     * Allows you to add custom cookie attributes, which are not exposed explicitly.
     * For example, you can pass the `SameSite` attribute in the following way:
     * ```kotlin
     * cookie.extensions["SameSite"] = "lax"
     * ```
     */
    val extensions: MutableMap<String, String?> = mutableMapOf()
}