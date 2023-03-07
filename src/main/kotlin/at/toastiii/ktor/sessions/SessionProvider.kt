package at.toastiii.ktor.sessions

import at.toastiii.ktor.sessions.storage.SessionStorage
import at.toastiii.ktor.sessions.transport.SessionTransport
import kotlin.reflect.KClass

data class SessionProvider<S : Session>(
    val name: String,
    val type: KClass<S>,
    val transport: SessionTransport,
    val storage: SessionStorage<S>,
    val autoCommit: Boolean
)