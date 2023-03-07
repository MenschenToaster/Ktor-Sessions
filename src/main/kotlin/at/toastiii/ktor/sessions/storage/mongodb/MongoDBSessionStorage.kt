package at.toastiii.ktor.sessions.storage.mongodb

import at.toastiii.ktor.sessions.Session
import at.toastiii.ktor.sessions.storage.SessionStorage
import at.toastiii.ktor.sessions.util.plus
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.ReplaceOptions
import org.bson.Document
import java.time.Duration
import java.util.Date
import java.util.concurrent.TimeUnit

open class MongoDBSessionStorage<T : Session>(
    private val bsonSerializer: BsonSerializer<T>,
    private val collection: MongoCollection<Document>,
    private val expireAfter: Duration? = null
) : SessionStorage<T> {

    init {
        if(expireAfter != null) {
            //Delete object immediately after reaching the expireAt date
            collection.createIndex(
                Indexes.ascending("expireAt"), IndexOptions()
                    .expireAfter(0, TimeUnit.SECONDS)
            )
        }
    }

    override suspend fun write(id: String, sessionData: T) {
        val dbObject = Document("_id", id)
            .append("data", bsonSerializer.serialize(sessionData))
        if(expireAfter != null) {
            dbObject.append("expireAt", Date() + expireAfter)
        }

        collection.replaceOne(Filters.eq("_id", id), dbObject, ReplaceOptions().upsert(true))
    }

    override suspend fun read(id: String): T {
        val find = collection.find(Filters.eq("_id", id)).first()
            ?: throw NoSuchElementException("Session not found")

        return bsonSerializer.deserialize(find.get("data", Document::class.java))
    }

    override suspend fun invalidate(id: String) {
        val result = collection.deleteOne(Filters.eq("_id", id))

        if(result.deletedCount == 0L)
            throw NoSuchElementException("Session not found")
    }
}
