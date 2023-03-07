package at.toastiii.ktor.sessions.storage

import at.toastiii.ktor.sessions.storage.serializer.SessionSerializer
import java.util.concurrent.ConcurrentHashMap

/*
 * Stores the session data in memory.
 */
class MemorySessionStorage<SessionData : Any>(
    private val serializer: SessionSerializer<SessionData, String>
) : SessionStorage<SessionData> {

    private val data = ConcurrentHashMap<String, String>()

    override suspend fun write(id: String, sessionData: SessionData) {
        data[id] = serializer.serialize(sessionData)
    }

    override suspend fun read(id: String): SessionData {
        return data[id]?.let { serializer.deserialize(id) } ?: throw NoSuchElementException("Session could not be found.")
    }

    override suspend fun invalidate(id: String) {
        data.remove(id) ?: throw NoSuchElementException("Session could not be found.")
    }
}