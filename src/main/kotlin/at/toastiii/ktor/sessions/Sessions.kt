package at.toastiii.ktor.sessions

import at.toastiii.ktor.sessions.util.BeforeSend
import io.ktor.server.application.*
import io.ktor.util.*

internal val SessionProvidersKey = AttributeKey<List<SessionProvider<*>>>("ToastSessionProvidersKey")

val SessionPlugin: RouteScopedPlugin<SessionsConfig> = createRouteScopedPlugin("ToastSessions", ::SessionsConfig) {
    val providers = pluginConfig.providers.toList()

    application.attributes.put(SessionProvidersKey, providers)

    onCall { call ->
        val sessionContext = SessionsContextImpl(
            providers.map {
                val sessionId = it.transport.receive(call)

                SessionInstance(sessionId, it, it.autoCommit)
            }.associateBy { it.provider.name }
        )
        call.attributes.put(SessionDataKey, sessionContext)
    }

    on(BeforeSend) { call ->
        //Ignore call if sessions are not initialized
        val sessionData = call.attributes.getOrNull(SessionDataKey) ?: return@on

        sessionData.instances.forEach { (_, entry) ->
            val id = entry.id ?: return@forEach

            entry.provider.transport.send(call, id)
        }

        sessionData.autoCommit()
    }
}