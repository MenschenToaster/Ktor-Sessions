package at.toastiii.ktor.sessions

import at.toastiii.ktor.sessions.util.generateSessionId
import io.ktor.server.application.*
import io.ktor.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.reflect.KClass


/**
 * Gets a current session or fails if the [SessionPlugin] plugin is not installed.
 * @throws MissingApplicationPluginException
 */
val ApplicationCall.sessions: SessionsContext
    get() = attributes.getOrNull(SessionDataKey) ?: throw IllegalStateException("Sessions are unavailable.")

internal val SessionDataKey = AttributeKey<SessionsContextImpl>("ToastSessionKey")

interface SessionsContext {

    fun set(name: String, value: Session?)

    suspend fun get(name: String): Session?

    fun clear(name: String)

    fun findName(type: KClass<*>): String

    suspend fun commit(name: String)

    suspend fun autoCommit()
}

internal class SessionsContextImpl(
    val instances: Map<String, SessionInstance<*>>
) : SessionsContext {
    override fun set(name: String, value: Session?) {
        val instance = instances[name] ?: throw IllegalStateException("No session provider with the name $name found.")

        setTyped(instance, value)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <S : Session> setTyped(data: SessionInstance<S>, value: Any?) = data.setValue(value as S)

    override suspend fun get(name: String): Session? {
        val instance = instances[name] ?: throw IllegalStateException("No session provider with the name $name found.")

        return instance.getValue()
    }

    override fun clear(name: String) = set(name, null)

    override fun findName(type: KClass<*>): String {
        val instance = instances.entries.firstOrNull { it.value.provider.type == type }
            ?: throw IllegalArgumentException("No session provider with type $type found.")
        return instance.value.provider.name
    }

    override suspend fun commit(name: String) {
        val instance = instances[name] ?: throw IllegalStateException("No session provider with the name $name found.")

        instance.commit()
    }

    override suspend fun autoCommit() {
        instances.forEach {
            if(it.value.shouldAutoCommit()) {
                it.value.commit()
            }
        }
    }

}

inline fun <reified T : Session> SessionsContext.clear(): Unit = clear(findName(T::class))

@Suppress("UNCHECKED_CAST")
suspend fun <T : Session> SessionsContext.get(klass: KClass<T>): T? = get(findName(klass)) as T?

suspend inline fun <reified T : Session> SessionsContext.get(): T? = get(T::class)

inline fun <reified T : Session> SessionsContext.set(value: T?): Unit = set(findName(T::class), value)


suspend inline fun <reified T : Session> SessionsContext.getOrSet(name: String = findName(T::class), generator: () -> T): T {
    val result = get<T>()

    if (result != null) {
        return result
    }

    return generator().apply {
        set(name, this)
    }
}


class SessionInstance<S : Session>(
    internal var id: String?,
    internal val provider: SessionProvider<S>,
    private val autoCommit: Boolean
) {

    private var isInitialized = false
    private var initialData: S? = null
    private var current: S? = null

    private var readLock = ReentrantLock()
    private var commitLock = ReentrantLock()

    private var committed: Boolean = false

    suspend fun getValue(): S? {
        //No id - no data
        if(id == null)
            return null

        readLock.lock()
        try {
            if(!isInitialized) {
                try {
                    initialData = provider.storage.read(id!!)
                } catch (e: NoSuchElementException) {
                    //Ignore
                }

                @Suppress("UNCHECKED_CAST")
                current = initialData?.clone() as S?
                isInitialized = true
            }

            return current
        } finally {
            readLock.unlock()
        }
    }

    fun setValue(data: S?) {
        if(committed)
            throw IllegalStateException("Already committed session.")
        if(id == null)
            id = generateSessionId()
        current = data
    }


    suspend fun commit() {
        if(id == null)
            return

        commitLock.lock()
        val current = this.current

        try {
            if(committed)
                throw IllegalStateException("Already committed session.")

            if(current == null) {
                provider.storage.invalidate(id!!)
            } else {
                provider.storage.write(id!!, current)
            }

            committed = true
        } finally {
            commitLock.unlock()
        }
    }

    fun shouldAutoCommit(): Boolean {
        return autoCommit && id != null && !committed && current != initialData
    }

}