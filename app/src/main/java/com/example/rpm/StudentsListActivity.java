package com.example.rpm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rpm.controllers.ApiService;
import com.example.rpm.modelsDB.DataHandler;
import com.example.rpm.modelsDB.Students;
import com.example.rpm.modelsJSON.CountForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class StudentsListActivity extends AppCompatActivity implements StudentsListAdapter.ISortByColumn {

    private Menu studentsMenu;
    private ListView listView;

    private DataHandler dataHandler = new DataHandler();

    private int directionId;
    private String directionName;
    private String directionCode;
    private String directionProfile;
    private ArrayList<Students> studentsList = new ArrayList<>();
    private ApiService apiService = new ApiService();
    private CountForm countform = new CountForm();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);

        initializeValues();
        if(directionId != -1){
            getDatabaseInfo();
        }
        else{
            Toast.makeText(getApplicationContext(), "Не удалось получить данные", Toast.LENGTH_SHORT)
                    .show();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    countform = apiService.selectCount(directionName);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        sleep(1000);
        setTitle(countform.title + " Б: " +countform.countBudget + " Д: " + countform.countDogovor);
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeValues(){
        listView = findViewById(R.id.students_list_view);
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        directionId = args.getInt("directionId");
        directionName = args.getString("title");
        directionCode = args.getString("code");
        directionProfile = args.getString("profile");
        setTitle(directionName);
        dataHandler.createOrConnectToDB(getApplicationContext());
    }
    private void getDatabaseInfo(){
        GetStudents getStudents = new GetStudents();
        getStudents.execute();
    }
    private void setListView(){
        StudentsListAdapter empAdapter = new StudentsListAdapter(this, R.layout.students_element_listview, studentsList);
        listView.setAdapter(empAdapter);
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.students_list_menu, menu);
        studentsMenu = menu;
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.add_new_student:{
                createOrChangeStudent(true, null);
                return true;
            }
            case R.id.change_direction_name:{
                AlertDialog inputDialog = new AlertDialog.Builder(StudentsListActivity.this).create();
                View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.input_direction, null);
                inputDialog.setView(vv);
                inputDialog.setCancelable(true);
                ((EditText) vv.findViewById(R.id.input_direction_name)).setText(directionName);
                ((EditText) vv.findViewById(R.id.input_direction_code)).setText(directionCode);
                ((EditText) vv.findViewById(R.id.input_direction_profile)).setText(directionProfile);
                ((Button) vv.findViewById(R.id.add_direction_accept)).setText("Изменить");
                ((Button) vv.findViewById(R.id.add_direction_accept)).setOnClickListener(v->{

                    String newCode = ((EditText) vv.findViewById(R.id.input_direction_code)).getText().toString();

                    newCode = newCode.trim();
                    String newName = ((EditText) vv.findViewById(R.id.input_direction_name)).getText().toString();
                    newName = newName.trim();
                    String newProfile = ((EditText) vv.findViewById(R.id.input_direction_profile)).getText().toString();
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

                    setTitle(((EditText) vv.findViewById(R.id.input_direction_name)).getText().toString());
                    setTitle(((EditText) vv.findViewById(R.id.input_direction_code)).getText().toString());
                    setTitle(((EditText) vv.findViewById(R.id.input_direction_profile)).getText().toString());
                    dataHandler.updateDirection(directionId, ((EditText) vv.findViewById(R.id.input_direction_name)).getText().toString());
                    dataHandler.updateDirection(directionId, ((EditText) vv.findViewById(R.id.input_direction_code)).getText().toString());
                    dataHandler.updateDirection(directionId, ((EditText) vv.findViewById(R.id.input_direction_profile)).getText().toString());
                    inputDialog.cancel();
                });
                ((Button) vv.findViewById(R.id.add_direction_decline)).setOnClickListener(v->{
                    inputDialog.cancel();
                });
                inputDialog.show();
                return true;
            }
            case R.id.delete_direction:{
                dataHandler.deleteDirection(directionId);
                Intent intent = new Intent(StudentsListActivity.this, MainActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void createOrChangeStudent(boolean createNew, Students students){
        AlertDialog inputDialog = new AlertDialog.Builder(StudentsListActivity.this).create();
        View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.input_student, null);
        inputDialog.setView(vv);
        inputDialog.setCancelable(true);

        EditText firstNameTextView = (EditText) vv.findViewById(R.id.input_std_first_name);
        EditText secondNameTextView = (EditText) vv.findViewById(R.id.input_std_second_name);
        EditText postTextView = (EditText) vv.findViewById(R.id.input_std_form);
        Button acceptButton = (Button) vv.findViewById(R.id.add_std_accept);


        if(!createNew){
            firstNameTextView.setText(students.firstName);
            secondNameTextView.setText(students.secondName);
            postTextView.setText(students.form);
            acceptButton.setText("Изменить");
            ((Button) vv.findViewById(R.id.add_std_delete)).setVisibility(View.VISIBLE);
        }else{
            acceptButton.setText("Добавить");
            ((Button) vv.findViewById(R.id.add_std_delete)).setVisibility(View.INVISIBLE);
        }

        acceptButton.setOnClickListener(v->{
            if(createNew){
                Students newStudents = new Students();
                newStudents.firstName = ((EditText) vv.findViewById(R.id.input_std_first_name)).getText().toString();
                newStudents.secondName = ((EditText) vv.findViewById(R.id.input_std_second_name)).getText().toString();
                newStudents.form = ((EditText) vv.findViewById(R.id.input_std_form)).getText().toString();
                newStudents.departmentId = directionId;
                dataHandler.addStudent(newStudents);
                int bud = Integer.parseInt(countform.countBudget);
                int dog = Integer.parseInt(countform.countDogovor);
                if (newStudents.form.equals("Budget")) bud++;
                if (newStudents.form.equals("Dogovor")) dog++;
                String budS = String.valueOf(bud);
                String dogS = String.valueOf(dog);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            apiService.insertCount(budS, dogS, directionName);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                sleep(1000);

            }
            else{
                students.firstName = firstNameTextView.getText().toString();
                students.secondName = secondNameTextView.getText().toString();
                students.form = postTextView.getText().toString();
                dataHandler.updateStudent(students);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GetStudents getStudents = new GetStudents();
            getStudents.execute();
            inputDialog.cancel();
        });
        ((Button) vv.findViewById(R.id.add_std_decline)).setOnClickListener(v->{
            inputDialog.cancel();
        });
        ((Button) vv.findViewById(R.id.add_std_delete)).setOnClickListener(v->{
            dataHandler.deleteStudent(students);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GetStudents getStudents = new GetStudents();
            getStudents.execute();
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
        createOrChangeStudent(false, studentsList.get(position));
    }

    class GetStudents extends AsyncTask<Void, Void, ArrayList<Students>> {
        @Override
        protected ArrayList<Students> doInBackground(Void... unused) {
            return (ArrayList<Students>) dataHandler
                    .getDB()
                    .studentDao()
                    .getDirectionStudents(directionId);
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