package com.example.rpm.dataClasses;

import android.content.Context;

import java.util.List;

public class DataHandler {

    private AppDatabase db;
    private FacultyDao facultyDao;
    private DirectionDao departmentDao;
    private StudentsDao studentsDao;

    public void createOrConnectToDB(Context context){
        db = AppDatabase.getInstance(context);

        facultyDao = db.facultyDao();
        departmentDao = db.departmentDao();
        studentsDao = db.employeeDao();
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


    //Department

    public void addDepartment(Direction direction){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                departmentDao.insertAll(direction);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void addDepartment(int id, String name, int facultyId){
        Direction dep = new Direction();
        dep.id = id;
        dep.name = name;
        dep.facultyId = facultyId;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                departmentDao.insertAll(dep);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void addDepartment(String name, int facultyId){
        Direction dep = new Direction();
        dep.name = name;
        dep.facultyId = facultyId;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<Direction> deps = departmentDao.getAll();
                dep.id = deps.size();
                departmentDao.insertAll(dep);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void deleteDepartment(int id){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Direction dep = departmentDao.getOneById(id);
                departmentDao.delete(dep);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void updateDepartment(int id, String newName){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Direction dep = departmentDao.getOneById(id);
                dep.name = newName;
                departmentDao.update(dep);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    //Employee
    public void addEmployee(Students students){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                studentsDao.insertAll(students);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void updateEmployee(Students students){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                studentsDao.update(students);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    public void deleteEmployee(Students students){
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
