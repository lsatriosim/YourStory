import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.yourstory.network.local.UserLogin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<UserLogin> {
        return dataStore.data.map { preferences ->
            UserLogin(
                preferences[NAME_KEY] ?:"",
                preferences[ID_KEY] ?:"",
                preferences[TOKEN_KEY] ?:"",
            )
        }
    }

    suspend fun saveUser(user: UserLogin) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[ID_KEY] = user.userId
            preferences[TOKEN_KEY] = user.token
        }
    }

    suspend fun clearUser() {
        dataStore.edit {
            it.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val ID_KEY = stringPreferencesKey("userId")
        private val TOKEN_KEY = stringPreferencesKey("token")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}