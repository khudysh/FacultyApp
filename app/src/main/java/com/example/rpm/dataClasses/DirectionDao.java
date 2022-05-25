package com.example.rpm.dataClasses;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DirectionDao {
    @Query("SELECT * FROM Direction")
    List<Direction> getAll();

    @Query("SELECT * FROM Direction WHERE facultyId IS :facultyId")
    List<Direction> getFacultyDepartments(int facultyId);

    @Query("SELECT * FROM Direction WHERE id = :id")
    Direction getOneById(int id);

    @Insert
    void insertAll(Direction... directions);

    @Update
    void update(Direction direction);

    @Delete
    void delete(Direction direction);
}
