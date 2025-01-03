import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo

class MongoSetup private constructor() {

    private val mongoClient: MongoClient = KMongo.createClient(URI)
    val database: MongoDatabase = mongoClient.getDatabase(DATABASE_NAME)

    companion object {
        private const val URI = "mongodb+srv://justynaonceagainforgothermail:IBZaI1AOvW6vHKil@physio.ci0mw.mongodb.net/?retryWrites=true&w=majority&appName=Physio"
        private const val DATABASE_NAME = "Physio"

        @Volatile
        private var instance: MongoSetup? = null

        fun getInstance(): MongoSetup {
            return instance ?: synchronized(this) {
                instance ?: MongoSetup().also { instance = it }
            }
        }
    }

    fun closeConnection() {
        mongoClient.close()
    }
}
