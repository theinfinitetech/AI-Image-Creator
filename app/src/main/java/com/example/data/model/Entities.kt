package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_wallet")
data class UserWallet(
    @PrimaryKey val id: Int = 1,
    val credits: Int = 50,
    val lastClaimedAt: Long = 0L
)

@Entity(tableName = "creations")
data class Creation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prompt: String,
    val enhancedPrompt: String,
    val styleName: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isShared: Boolean = false,
    val likes: Int = 0,
    val isLikedByMe: Boolean = false,
    val authorName: String = "You"
)

@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val creationId: Int,
    val authorName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "collection_creations", primaryKeys = ["collectionId", "creationId"])
data class CollectionCreationCrossRef(
    val collectionId: Int,
    val creationId: Int
)

