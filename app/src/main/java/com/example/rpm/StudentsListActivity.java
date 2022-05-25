package com.example.rpm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rpm.dataClasses.DataHandler;
import com.example.rpm.dataClasses.Students;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StudentsListActivity extends AppCompatActivity implements StudentsListAdapter.ISortByColumn {

    private Menu employeesMenu;
    private ListView listView;

    private DataHandler dataHandler = new DataHandler();

    private int departmentId;
    private String departmentName;
    private String departmentCode;
    private String departmentProfile;
    private ArrayList<Students> studentsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emloyees_list);

        initializeValues();
        if(departmentId != -1){
            getDatabaseInfo();
        }
        else{
            Toast.makeText(getApplicationContext(), "Не удалось получить данные", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void initializeValues(){
        listView = findViewById(R.id.students_list_view);
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        departmentId = args.getInt("departmentId");
        departmentName = args.getString("title");
        departmentCode = args.getString("code");
        departmentProfile = args.getString("profile");
        setTitle(departmentName);
        dataHandler.createOrConnectToDB(getApplicationContext());
    }
    private void getDatabaseInfo(){
        GetEmployees getEmployees = new GetEmployees();
        getEmployees.execute();
    }
    private void setListView(){
        StudentsListAdapter empAdapter = new StudentsListAdapter(this, R.layout.employee_element_listview, studentsList);
        listView.setAdapter(empAdapter);
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.employees_list_menu, menu);
        employeesMenu = menu;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_new_employee:{
                createOrChangeEmployee(true, null);
                return true;
            }
            case R.id.change_department_name:{
                AlertDialog inputDialog = new AlertDialog.Builder(StudentsListActivity.this).create();
                View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.input_department, null);
                inputDialog.setView(vv);
                inputDialog.setCancelable(true);
                ((EditText) vv.findViewById(R.id.input_department_name)).setText(departmentName);
                ((EditText) vv.findViewById(R.id.input_department_code)).setText(departmentCode);
                ((EditText) vv.findViewById(R.id.input_department_profile)).setText(departmentProfile);
                ((Button) vv.findViewById(R.id.add_department_accept)).setText("Изменить");
                ((Button) vv.findViewById(R.id.add_department_accept)).setOnClickListener(v->{

                    String newCode = ((EditText) vv.findViewById(R.id.input_department_code)).getText().toString();

                    newCode = newCode.trim();
                    String newName = ((EditText) vv.findViewById(R.id.input_department_name)).getText().toString();
                    newName = newName.trim();
                    String newProfile = ((EditText) vv.findViewById(R.id.input_department_profile)).getText().toString();
                    newProfile = newProfile.trim();

                    if(newCode.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Недопустимй код", Toast.LENGTH_SHORT)
                                .show();
                        inputDialog.cancel();
                        return;
                    }
                    if(newName.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Недопустимое направление", Toast.LENGTH_SHORT)
                                .show();
                        inputDialog.cancel();
                        return;
                    }
                    if(newProfile.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Недопустимый профиль", Toast.LENGTH_SHORT)
                                .show();
                        inputDialog.cancel();
                        return;
                    }

                    setTitle(((EditText) vv.findViewById(R.id.input_department_name)).getText().toString());
                    setTitle(((EditText) vv.findViewById(R.id.input_department_code)).getText().toString());
                    setTitle(((EditText) vv.findViewById(R.id.input_department_profile)).getText().toString());
                    dataHandler.updateDepartment(departmentId, ((EditText) vv.findViewById(R.id.input_department_name)).getText().toString());
                    dataHandler.updateDepartment(departmentId, ((EditText) vv.findViewById(R.id.input_department_code)).getText().toString());
                    dataHandler.updateDepartment(departmentId, ((EditText) vv.findViewById(R.id.input_department_profile)).getText().toString());
                    inputDialog.cancel();
                });
                ((Button) vv.findViewById(R.id.add_department_decline)).setOnClickListener(v->{
                    inputDialog.cancel();
                });
                inputDialog.show();
                return true;
            }
            case R.id.delete_department:{
                dataHandler.deleteDepartment(departmentId);
                Intent intent = new Intent(StudentsListActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void createOrChangeEmployee(boolean createNew, Students students){
        AlertDialog inputDialog = new AlertDialog.Builder(StudentsListActivity.this).create();
        View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.input_employee, null);
        inputDialog.setView(vv);
        inputDialog.setCancelable(true);

        EditText firstNameTextView = (EditText) vv.findViewById(R.id.input_emp_first_name);
        EditText secondNameTextView = (EditText) vv.findViewById(R.id.input_emp_second_name);
        EditText postTextView = (EditText) vv.findViewById(R.id.input_emp_post);
        Button acceptButton = (Button) vv.findViewById(R.id.add_emp_accept);

        if(!createNew){
            firstNameTextView.setText(students.firstName);
            secondNameTextView.setText(students.secondName);
            postTextView.setText(students.form);
            acceptButton.setText("Изменить");
            ((Button) vv.findViewById(R.id.add_emp_delete)).setVisibility(View.VISIBLE);
        }else{
            acceptButton.setText("Добавить");
            ((Button) vv.findViewById(R.id.add_emp_delete)).setVisibility(View.INVISIBLE);
        }

        acceptButton.setOnClickListener(v->{
            if(createNew){
                Students newStudents = new Students();
                newStudents.firstName = ((EditText) vv.findViewById(R.id.input_emp_first_name)).getText().toString();
                newStudents.secondName = ((EditText) vv.findViewById(R.id.input_emp_second_name)).getText().toString();
                newStudents.form = ((EditText) vv.findViewById(R.id.input_emp_post)).getText().toString();
                newStudents.departmentId = departmentId;
                dataHandler.addEmployee(newStudents);
                //setTitle("hui");
            }
            else{
                students.firstName = firstNameTextView.getText().toString();
                students.secondName = secondNameTextView.getText().toString();
                students.form = postTextView.getText().toString();
                dataHandler.updateEmployee(students);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GetEmployees getEmployees = new GetEmployees();
            getEmployees.execute();
            inputDialog.cancel();
        });
        ((Button) vv.findViewById(R.id.add_emp_decline)).setOnClickListener(v->{
            inputDialog.cancel();
        });
        ((Button) vv.findViewById(R.id.add_emp_delete)).setOnClickListener(v->{
            dataHandler.deleteEmployee(students);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GetEmployees getEmployees = new GetEmployees();
            getEmployees.execute();
            inputDialog.cancel();
        });
        inputDialog.show();
    }

    @Override
    public void sortByColumn(int columnId) {
        if(columnId == 0){
            Comparator<Students> comparator = (o1, o2) -> o1.firstName.compareTo(o2.firstName);
            Collections.sort(studentsList, comparator);
            setListView();
            Toast.makeText(getApplicationContext(), "Вы нажали на столбец с именем", Toast.LENGTH_SHORT)
                    .show();
        }
        if(columnId == 1){
            Comparator<Students> comparator = (o1, o2) -> o1.secondName.compareTo(o2.secondName);
            Collections.sort(studentsList, comparator);
            setListView();
            Toast.makeText(getApplicationContext(), "Вы нажали на столбец с фамилией", Toast.LENGTH_SHORT)
                    .show();
        }
        if(columnId == 2){
            Comparator<Students> comparator = (o1, o2) -> o1.form.compareTo(o2.form);
            Collections.sort(studentsList, comparator);
            setListView();
            Toast.makeText(getApplicationContext(), "Вы нажали на столбец с формой обучения", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void simpleClick(int position) {
        createOrChangeEmployee(false, studentsList.get(position));
    }

    class GetEmployees extends AsyncTask<Void, Void, ArrayList<Students>> {
        @Override
        protected ArrayList<Students> doInBackground(Void... unused) {
            return (ArrayList<Students>) dataHandler
                    .getDB()
                    .employeeDao()
                    .getDepartmentEmployees(departmentId);
        }
        @Override
        protected void onPostExecute(ArrayList<Students> studentsArrayList) {
            studentsList = studentsArrayList;
            if(studentsList.size() < 1){
                Toast.makeText(getApplicationContext(), "Пока нет студентов", Toast.LENGTH_SHORT)
                        .show();
            }else{
                Toast.makeText(getApplicationContext(), "Студенты загружены (" + String.valueOf(studentsList.size()) + ")", Toast.LENGTH_SHORT)
                        .show();
            }
            setListView();

        }
    }
}