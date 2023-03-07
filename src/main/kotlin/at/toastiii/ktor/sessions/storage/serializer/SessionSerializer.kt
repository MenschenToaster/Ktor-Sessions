package at.toastiii.ktor.sessions.storage.serializer

interface SessionSerializer<From : Any, To : Any> {
    fun serialize(from: From): To
    fun deserialize(to: To): From
}