package at.toastiii.ktor.sessions.storage

import kotlin.jvm.Throws

/*
 * Session storage is used to store the sessions in memory, in the database or somewhere else.
 * Read should either always return an immutable class or the objects returned by two read calls should never be the
 * same instance. They should return the appropriate data but the instance in memory should never be the same.
 */
interface SessionStorage<T : Any> {

    /**
     * Writes a session [sessionData] for [id].
     */
    suspend fun write(id: String, sessionData: T)

    /**
     * Reads a session with the [id] identifier.
     *
     * @throws NoSuchElementException when a session [id] is not found.
     */
    @Throws(NoSuchElementException::class)
    suspend fun read(id: String): T

    /**
     * Invalidates a session with the [id] identifier.
     * This method prevents a session [id] from being accessible after this call.
     *
     * @throws NoSuchElementException when a session [id] is not found.
     */
    @Throws(NoSuchElementException::class)
    suspend fun invalidate(id: String)
}