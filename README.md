# Ktor (stateful) sessions
This is a library inspired by the official ktor session plugin except its 
only fetching the session when it's actually requested by your code, and it has the ability 
to fully customize how sessions are stored in the database by providing it the full object
and not an already serialized string.

It **does not** support transformers for data sent to clients(e.g. encryption) and you are required to always store
data on the server. 

**This library is JVM only**

# Supported storage types
MongoDB and in memory (for development only)
