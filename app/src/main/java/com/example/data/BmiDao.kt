package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BmiDao {
    @Query("SELECT * FROM bmi_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<BmiRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BmiRecord)

    @Query("DELETE FROM bmi_records WHERE id = :id")
    suspend fun deleteRecordById(id: Int)

    @Query("DELETE FROM bmi_records")
    suspend fun clearAllRecords()
}
