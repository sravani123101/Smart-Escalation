package com.example.data.repository

import com.example.data.local.MedicineDao
import com.example.data.model.MedicineEntity
import kotlinx.coroutines.flow.Flow

interface IMedicineRepository {
    fun getAllMedicines(): Flow<List<MedicineEntity>>
    suspend fun getMedicineById(id: Long): MedicineEntity?
    fun getMedicineByIdFlow(id: Long): Flow<MedicineEntity?>
    suspend fun insertMedicine(medicine: MedicineEntity): Long
    suspend fun updateMedicine(medicine: MedicineEntity)
    suspend fun deleteMedicine(medicine: MedicineEntity)
    suspend fun deleteMedicineById(id: Long)
    suspend fun updateStock(id: Long, newStock: Int)
    suspend fun decrementStock(id: Long)
    fun getLowStockMedicines(): Flow<List<MedicineEntity>>
    fun getMedicineCount(): Flow<Int>
}

class MedicineRepository(
    private val dao: MedicineDao
) : IMedicineRepository {
    override fun getAllMedicines(): Flow<List<MedicineEntity>> = dao.getAllMedicines()
    override suspend fun getMedicineById(id: Long): MedicineEntity? = dao.getMedicineById(id)
    override fun getMedicineByIdFlow(id: Long): Flow<MedicineEntity?> = dao.getMedicineByIdFlow(id)
    override suspend fun insertMedicine(medicine: MedicineEntity): Long = dao.insertMedicine(medicine)
    override suspend fun updateMedicine(medicine: MedicineEntity) = dao.updateMedicine(medicine)
    override suspend fun deleteMedicine(medicine: MedicineEntity) = dao.deleteMedicine(medicine)
    override suspend fun deleteMedicineById(id: Long) = dao.deleteMedicineById(id)
    override suspend fun updateStock(id: Long, newStock: Int) = dao.updateStock(id, newStock)
    override suspend fun decrementStock(id: Long) = dao.decrementStock(id)
    override fun getLowStockMedicines(): Flow<List<MedicineEntity>> = dao.getLowStockMedicines()
    override fun getMedicineCount(): Flow<Int> = dao.getMedicineCount()
}
