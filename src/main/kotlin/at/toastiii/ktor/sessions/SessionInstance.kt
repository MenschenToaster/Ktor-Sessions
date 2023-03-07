package at.toastiii.ktor.sessions

import java.util.concurrent.locks.ReentrantLock

class SessionInstance<S : Session>(
    private val id: String,
    private val provider: SessionProvider<S>,
    private val autoCommit: Boolean
) {

    private var isInitialized = false
    private var initialData: S? = null
    private var current: S? = null

    private var readLock = ReentrantLock()
    private var commitLock = ReentrantLock()

    private var committed: Boolean = false

    suspend fun getValue(): S? {
        readLock.lock()
        try {
            if(!isInitialized) {
                initialData = provider.storage.read(id)
                current = initialData
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
        current = data
    }


    suspend fun commit() {
        commitLock.lock()
        val current = this.current

        try {
            if(committed)
                throw IllegalStateException("Already committed session.")

            if(current == null) {
                provider.storage.invalidate(id)
            } else {
                provider.storage.write(id, current)
            }

            committed = true
        } finally {
            commitLock.unlock()
        }
    }

    fun shouldAutoCommit(): Boolean {
        return autoCommit && current != initialData
    }

}