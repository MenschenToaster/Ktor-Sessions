package at.toastiii.ktor.sessions.storage

interface SessionSerializer<From : Any, To : Any> {
    fun serialize(from: From): To
    fun deserialize(to: To): From
}