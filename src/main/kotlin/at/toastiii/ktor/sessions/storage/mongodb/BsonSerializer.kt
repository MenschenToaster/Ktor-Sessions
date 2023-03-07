package at.toastiii.ktor.sessions.storage.mongodb

import at.toastiii.ktor.sessions.storage.SessionSerializer
import com.google.gson.Gson
import org.bson.Document
import kotlin.reflect.KClass

typealias BsonSerializer<T> = SessionSerializer<T, Document>

class GsonBsonSerializer<Session : Any>(
    private val clazz: KClass<Session>,
    private val gson: Gson = Gson()
) : BsonSerializer<Session> {

    override fun serialize(from: Session): Document {
        return Document.parse(gson.toJson(from))
    }

    override fun deserialize(to: Document): Session {
        return gson.fromJson(to.toJson(), clazz.java)
    }

}