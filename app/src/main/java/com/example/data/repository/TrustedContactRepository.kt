package com.example.data.repository

import com.example.data.local.TrustedContactDao
import com.example.data.model.TrustedContactEntity
import kotlinx.coroutines.flow.Flow

interface ITrustedContactRepository {
    fun getAllContacts(): Flow<List<TrustedContactEntity>>
    fun getActiveEscalationContacts(): Flow<List<TrustedContactEntity>>
    suspend fun getActiveEscalationContactsDirect(): List<TrustedContactEntity>
    suspend fun getContactById(id: Long): TrustedContactEntity?
    suspend fun insertContact(contact: TrustedContactEntity): Long
    suspend fun updateContact(contact: TrustedContactEntity)
    suspend fun deleteContact(contact: TrustedContactEntity)
    suspend fun deleteContactById(id: Long)
    suspend fun setEscalationEnabled(id: Long, enabled: Boolean)
    fun getContactCount(): Flow<Int>
}

class TrustedContactRepository(
    private val dao: TrustedContactDao
) : ITrustedContactRepository {
    override fun getAllContacts(): Flow<List<TrustedContactEntity>> = dao.getAllContacts()
    override fun getActiveEscalationContacts(): Flow<List<TrustedContactEntity>> = dao.getActiveEscalationContacts()
    override suspend fun getActiveEscalationContactsDirect(): List<TrustedContactEntity> = dao.getActiveEscalationContactsDirect()
    override suspend fun getContactById(id: Long): TrustedContactEntity? = dao.getContactById(id)
    override suspend fun insertContact(contact: TrustedContactEntity): Long = dao.insertContact(contact)
    override suspend fun updateContact(contact: TrustedContactEntity) = dao.updateContact(contact)
    override suspend fun deleteContact(contact: TrustedContactEntity) = dao.deleteContact(contact)
    override suspend fun deleteContactById(id: Long) = dao.deleteContactById(id)
    override suspend fun setEscalationEnabled(id: Long, enabled: Boolean) = dao.setEscalationEnabled(id, enabled)
    override fun getContactCount(): Flow<Int> = dao.getContactCount()
}
