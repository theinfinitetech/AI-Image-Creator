package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents the user's wallet for storing credits.
 */
@Entity(tableName = "user_wallet")
data class UserWallet(
    @PrimaryKey val id: Int = 1,
    val credits: Int = DEFAULT_CREDITS,
    val lastClaimedAt: Long = 0L
) {
    companion object {
        private const val DEFAULT_CREDITS = 50
    }
}

/**
 * Represents an AI-generated art creation.
 */
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
    val authorName: String = DEFAULT_AUTHOR_NAME
) {
    companion object {
        private const val DEFAULT_AUTHOR_NAME = "You"
    }
}

/**
 * Represents a comment on a creation.
 */
@Entity(tableName = "comments")
data class Comment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val creationId: Int,
    val authorName: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Represents a user collection for organizing creations.
 */
@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Cross-reference entity for many-to-many relationship between collections and creations.
 */
@Entity(
    tableName = "collection_creations",
    primaryKeys = ["collectionId", "creationId"]
)
data class CollectionCreationCrossRef(
    val collectionId: Int,
    val creationId: Int
)
