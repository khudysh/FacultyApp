package com.example.rpm.dataClasses;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;
@Dao
public interface FacultyDao {
    @Query("SELECT * FROM faculty")
    List<Faculty> getAll();

    @Query("SELECT * FROM faculty WHERE id = :id")
    List<Faculty> getById(int id);

    @Query("SELECT * FROM faculty WHERE id = :id")
    Faculty getOneById(int id);

    @Insert
    void insertAll(Faculty... faculties);

    @Update
    void update(Faculty faculty);

    @Delete
    void delete(Faculty faculty);
}
