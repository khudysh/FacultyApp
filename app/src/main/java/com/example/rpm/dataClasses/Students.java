package com.example.rpm.dataClasses;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;


@Entity(foreignKeys = @ForeignKey(
        entity = Direction.class,
        parentColumns = "id",
        childColumns = "departmentId", onDelete = CASCADE))
public class Students {
    @PrimaryKey (autoGenerate = true)
    int id;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "second_name")
    public String secondName;

    @ColumnInfo(name = "form")
    public String form;

    public int departmentId;
}
