package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AccountEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.ChannelEntity
import com.example.data.model.EpisodeEntity
import com.example.data.model.EpgProgramEntity
import com.example.data.model.MovieEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SeriesEntity
import com.example.data.model.WatchProgressEntity

@Database(
    entities = [
        AccountEntity::class,
        ProfileEntity::class,
        CategoryEntity::class,
        ChannelEntity::class,
        MovieEntity::class,
        SeriesEntity::class,
        EpisodeEntity::class,
        EpgProgramEntity::class,
        WatchProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun iptvDao(): IptvDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noor_iptv_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
