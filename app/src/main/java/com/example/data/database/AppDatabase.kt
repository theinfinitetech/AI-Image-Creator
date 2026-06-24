package com.example.data.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.Comment
import com.example.data.model.Creation
import com.example.data.model.UserWallet
import com.example.data.model.CollectionCreationCrossRef
import com.example.data.model.CollectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserWalletDao {
    @Query("SELECT * FROM user_wallet WHERE id = 1")
    fun getWalletFlow(): Flow<UserWallet?>

    @Query("SELECT * FROM user_wallet WHERE id = 1")
    suspend fun getWallet(): UserWallet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: UserWallet)
}

@Dao
interface CreationDao {
    @Query("SELECT * FROM creations ORDER BY timestamp DESC")
    fun getAllCreations(): Flow<List<Creation>>

    @Query("SELECT * FROM creations WHERE isShared = 1 ORDER BY timestamp DESC")
    fun getSharedCreations(): Flow<List<Creation>>

    @Query("SELECT * FROM creations WHERE id = :id")
    fun getCreationByIdFlow(id: Int): Flow<Creation?>

    @Query("SELECT * FROM creations WHERE id = :id")
    suspend fun getCreationById(id: Int): Creation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCreation(creation: Creation): Long

    @Update
    suspend fun updateCreation(creation: Creation)

    @Query("DELETE FROM creations WHERE id = :id")
    suspend fun deleteCreationById(id: Int)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE creationId = :creationId ORDER BY timestamp ASC")
    fun getCommentsForCreationFlow(creationId: Int): Flow<List<Comment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: Comment): Long
}

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY timestamp DESC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :id")
    suspend fun getCollectionById(id: Int): CollectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity): Long

    @Query("DELETE FROM collections WHERE id = :id")
    suspend fun deleteCollectionById(id: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCollectionCreation(crossRef: CollectionCreationCrossRef)

    @Query("DELETE FROM collection_creations WHERE collectionId = :collectionId AND creationId = :creationId")
    suspend fun deleteCollectionCreation(collectionId: Int, creationId: Int)

    @Query("""
        SELECT c.* FROM creations c
        INNER JOIN collection_creations cc ON c.id = cc.creationId
        WHERE cc.collectionId = :collectionId
        ORDER BY c.timestamp DESC
    """)
    fun getCreationsInCollection(collectionId: Int): Flow<List<Creation>>

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM collection_creations 
            WHERE collectionId = :collectionId AND creationId = :creationId
        )
    """)
    suspend fun isCreationInCollection(collectionId: Int, creationId: Int): Boolean
}

@Database(
    entities = [
        UserWallet::class,
        Creation::class,
        Comment::class,
        CollectionEntity::class,
        CollectionCreationCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userWalletDao(): UserWalletDao
    abstract fun creationDao(): CreationDao
    abstract fun commentDao(): CommentDao
    abstract fun collectionDao(): CollectionDao
}
