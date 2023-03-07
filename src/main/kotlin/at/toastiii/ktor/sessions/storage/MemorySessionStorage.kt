@file:Suppress("UNCHECKED_CAST")

package at.toastiii.ktor.sessions.storage

import at.toastiii.ktor.sessions.Session
import java.util.concurrent.ConcurrentHashMap

/*
 * Stores the session data in memory.
 */
class MemorySessionStorage<SessionData : Session> : SessionStorage<SessionData> {

    private val data = ConcurrentHashMap<String, Session>()

    override suspend fun write(id: String, sessionData: SessionData) {
        data[id] = sessionData.clone()
    }

    override suspend fun read(id: String): SessionData {
        return data[id]?.let { it.clone() as SessionData } ?: throw NoSuchElementException("Session could not be found.")
    }

    override suspend fun invalidate(id: String) {
        data.remove(id) ?: throw NoSuchElementException("Session could not be found.")
    }
}