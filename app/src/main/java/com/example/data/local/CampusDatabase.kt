package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AssignmentEntity
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ClassScheduleEntity
import com.example.data.model.SemesterResultEntity
import com.example.data.model.StudyNoteEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.SubjectGradeEntity
import com.example.data.model.UserProfileEntity

@Database(
    entities = [
        SubjectEntity::class,
        AttendanceRecordEntity::class,
        ClassScheduleEntity::class,
        AssignmentEntity::class,
        SemesterResultEntity::class,
        SubjectGradeEntity::class,
        StudyNoteEntity::class,
        ChatMessageEntity::class,
        UserProfileEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CampusDatabase : RoomDatabase() {
    abstract fun campusDao(): CampusDao

    companion object {
        @Volatile
        private var INSTANCE: CampusDatabase? = null

        fun getDatabase(context: Context): CampusDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CampusDatabase::class.java,
                    "campusmate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
