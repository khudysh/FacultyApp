package com.example.rpm.modelsDB;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Faculty.class,
        parentColumns = "id",
        childColumns = "facultyId", onDelete = CASCADE))
public class Direction {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "code")
    public String code;

    @ColumnInfo(name = "profile")
    public String profile;

    public int facultyId;

    @Ignore public boolean newObj;
    @Ignore public boolean changed;
}
