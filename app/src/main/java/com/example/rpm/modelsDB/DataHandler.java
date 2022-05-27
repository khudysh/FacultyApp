package com.example.rpm.modelsDB;

import android.content.Context;

import java.util.List;

public class DataHandler {

    private AppDatabase db;
    private FacultyDao facultyDao;
    private DirectionDao directionDao;
    private StudentsDao studentsDao;

    public void createOrConnectToDB(Context context){
        db = AppDatabase.getInstance(context);

        facultyDao = db.facultyDao();
        directionDao = db.directionDao();
        studentsDao = db.studentDao();
    }
    public AppDatabase getDB(){
        return db;
    }


    //Faculty
    public void addFaculty(String name){
        Faculty faculty = new Faculty();
        faculty.name = name;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                facultyDao.insertAll(faculty);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void addFaculty(int id, String name){
        Faculty faculty = new Faculty();
        faculty.id = id;
        faculty.name = name;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                facultyDao.insertAll(faculty);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void deleteFaculty(Faculty faculty){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                facultyDao.delete(faculty);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void updateFaculty(int id, String newName){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Faculty faculty = facultyDao.getOneById(id);
                faculty.name = newName;
                facultyDao.update(faculty);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public  void updateFaculty(Faculty faculty){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                facultyDao.update(faculty);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    //Direction

    public void addDirection(Direction direction){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                directionDao.insertAll(direction);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void addDirection(int id, String name, int facultyId){
        Direction dir = new Direction();
        dir.id = id;
        dir.name = name;
        dir.facultyId = facultyId;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                directionDao.insertAll(dir);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void addDirection(String name, int facultyId){
        Direction dir = new Direction();
        dir.name = name;
        dir.facultyId = facultyId;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Direction> direcs = directionDao.getAll();
                dir.id = direcs.size();
                directionDao.insertAll(dir);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void deleteDirection(int id){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Direction dir = directionDao.getOneById(id);
                directionDao.delete(dir);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void updateDirection(int id, String newName){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Direction dir = directionDao.getOneById(id);
                dir.name = newName;
                directionDao.update(dir);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    //Student
    public void addStudent(Students students){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                studentsDao.insertAll(students);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void updateStudent(Students students){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                studentsDao.update(students);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void deleteStudent(Students students){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                studentsDao.delete(students);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
