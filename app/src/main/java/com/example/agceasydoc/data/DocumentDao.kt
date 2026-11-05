package com.example.agceasydoc.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM saved_documents ORDER BY lastModified DESC LIMIT 10")
    fun getRecentDocuments(): Flow<List<SavedDocument>>

    @Query("SELECT * FROM saved_documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): SavedDocument?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: SavedDocument): Long

    @Update
    suspend fun updateDocument(document: SavedDocument)

    @Delete
    suspend fun deleteDocument(document: SavedDocument)

    @Query("DELETE FROM saved_documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Long)
}