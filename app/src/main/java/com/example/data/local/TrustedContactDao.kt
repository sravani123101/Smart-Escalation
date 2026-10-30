package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TrustedContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrustedContactDao {
    @Query("SELECT * FROM trusted_contacts ORDER BY escalationPriority ASC")
    fun getAllContacts(): Flow<List<TrustedContactEntity>>

    @Query("SELECT * FROM trusted_contacts WHERE isEscalationEnabled = 1 ORDER BY escalationPriority ASC")
    fun getActiveEscalationContacts(): Flow<List<TrustedContactEntity>>

    @Query("SELECT * FROM trusted_contacts WHERE isEscalationEnabled = 1 ORDER BY escalationPriority ASC")
    suspend fun getActiveEscalationContactsDirect(): List<TrustedContactEntity>

    @Query("SELECT * FROM trusted_contacts WHERE id = :id LIMIT 1")
    suspend fun getContactById(id: Long): TrustedContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: TrustedContactEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<TrustedContactEntity>): List<Long>

    @Update
    suspend fun updateContact(contact: TrustedContactEntity)

    @Delete
    suspend fun deleteContact(contact: TrustedContactEntity)

    @Query("DELETE FROM trusted_contacts WHERE id = :id")
    suspend fun deleteContactById(id: Long)

    @Query("UPDATE trusted_contacts SET isEscalationEnabled = :enabled WHERE id = :id")
    suspend fun setEscalationEnabled(id: Long, enabled: Boolean)

    @Query("SELECT COUNT(*) FROM trusted_contacts")
    fun getContactCount(): Flow<Int>
}
