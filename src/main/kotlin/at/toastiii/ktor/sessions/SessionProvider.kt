package at.toastiii.ktor.sessions

import kotlin.reflect.KClass

data class SessionProvider<S : Any>(
    val name: String,
    val type: KClass<S>,

)

fun main() {
    SessionProvider<SessionsConfig>("", SessionsConfig::class).copy()
}