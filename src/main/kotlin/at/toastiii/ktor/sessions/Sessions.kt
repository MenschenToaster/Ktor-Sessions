package at.toastiii.ktor.sessions

import io.ktor.server.application.*
import io.ktor.util.*

internal val SessionProvidersKey = AttributeKey<List<SessionProvider<*>>>("ToastSessionProvidersKey")

val SessionPlugin: RouteScopedPlugin<SessionsConfig> = createRouteScopedPlugin("ToastSessions", ::SessionsConfig) {
    val providers = pluginConfig.providers.toList()

    application.attributes.put(SessionProvidersKey, providers)
}