package com.example.rpm.modelsDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class Faculty {
    @PrimaryKey(autoGenerate = true) public int id;

    @ColumnInfo(name = "name") public String name;

    @Ignore public boolean newObj;
    @Ignore public boolean changed;

    @Ignore public static Faculty findFacultyById(ArrayList<Faculty> searchingList, int facultyId){
        for(Faculty faculty:searchingList){
            if(faculty.id == facultyId){
                return faculty;
            }
        }
        return null;
    }
}
