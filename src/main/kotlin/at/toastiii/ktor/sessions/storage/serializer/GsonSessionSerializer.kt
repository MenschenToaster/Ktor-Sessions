package at.toastiii.ktor.sessions.storage.serializer

import com.google.gson.Gson
import kotlin.reflect.KClass

class GsonSessionSerializer<T : Any>(
    private val clazz: KClass<T>,
    private val gson: Gson = Gson()
) : SessionSerializer<T, String> {
    override fun serialize(from: T): String = gson.toJson(from)
    override fun deserialize(to: String): T = gson.fromJson(to, clazz.java)
}