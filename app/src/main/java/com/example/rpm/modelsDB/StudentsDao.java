package com.example.rpm.modelsDB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentsDao {
    @Query("SELECT * FROM Students")
    List<Students> getAll();

    @Query("SELECT * FROM Students WHERE departmentId IS :departmentId")
    List<Students> getDirectionStudents(int departmentId);

    @Insert
    void insertAll(Students... students);

    @Update
    void update(Students students);

    @Delete
    void delete(Students students);
}