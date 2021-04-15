package com.itheamc.parlaymanager.repositories;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.itheamc.parlaymanager.models.Leg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Leg.class}, version = 1, exportSchema = false)
public abstract class LegsDatabase extends RoomDatabase {
    public abstract LegDao legDao();

    private static volatile LegsDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService executorService =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static LegsDatabase getDatabase(final Context context) {
        if (instance == null) {
            synchronized (LegsDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            LegsDatabase.class, "legs_database")
                            .build();
                }
            }
        }
        return instance;
    }
}
