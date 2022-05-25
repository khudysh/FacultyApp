package com.example.rpm;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.rpm.controllers.ApiService;
import com.example.rpm.dataClasses.DataHandler;
import com.example.rpm.dataClasses.Direction;
import com.example.rpm.dataClasses.Students;
import com.example.rpm.dataClasses.Faculty;
import com.example.rpm.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import com.google.gson.*;

@RequiresApi(api = Build.VERSION_CODES.P)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    Gson g = new Gson();


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private NavigationView navigationView;
    private ListView departmentsList;
    private Menu mainMenu;

    private DataHandler dataHandler = new DataHandler();
    private int currentFacultyId;
    private ApiService apiService = new ApiService();
    private ArrayList<Faculty> allFaculties;
    private ArrayList<Direction> allDirections;
    private ArrayList<Direction> currentFacultyDirections;
    private ArrayList<Students> allStudents;



//    public void executePerson() throws Exception {
//        final OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .url("http://94.103.188.48/FacultyApp/students.php")
//                .build();
//        Response response = client.newCall(request).executePerson();
//        if(response.isSuccessful()){
//            System.out.println(response.code());
//            Gson g = new Gson();
//            JSONArray json = new JSONArray(response.body().string());
//            //Log.d("MyLog",""+);
//            for (int i=0; i < json.length(); i++) {
//                Person person = g.fromJson(json.getJSONObject(i).toString(), Person.class);
//                allPerson.add(person);
//                Log.d("MyLogi",person.toString());
//            }
//            Log.d("MyLog",response.body().string());
//        }
//    }

    //Initializing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    apiService.executePerson();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeValues();

        getDatabaseInfo();

        createNavigationMenu();

    }

    private void initializeValues(){
        currentFacultyId = -1;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        departmentsList = (ListView) findViewById(R.id.departmentsListView);
        departmentsList.setOnItemClickListener((parent, view, position, id)->{
            Intent intent = new Intent(MainActivity.this, StudentsListActivity.class);
            int depId = currentFacultyDirections.get(position).id;
            String title = currentFacultyDirections.get(position).name;
            String code = currentFacultyDirections.get(position).code;
            String profile = currentFacultyDirections.get(position).profile;
            intent.putExtra("departmentId", depId);
            intent.putExtra("title", title);
            intent.putExtra("code", code);
            intent.putExtra("profile", profile);
            startActivity(intent);
        });

        dataHandler.createOrConnectToDB(getApplicationContext());
    }
    private void setListViewAdapter(){
        ArrayList<String> str = new ArrayList<>();
        for(Direction direction : currentFacultyDirections){
            int amount = 0;
            for(Students emp: allStudents){
                if(emp.departmentId == direction.id) amount++;
            }
            str.add(direction.code + ' ' + direction.name + ' ' + direction.profile + " (Студентов: " + amount + ")");
        }
        departmentsList.setAdapter(
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        str)
        );
    }
    private void getDatabaseInfo(){
        GetFaculties getFaculties = new GetFaculties();
        getFaculties.execute();
        GetDepartments getDepartments = new GetDepartments();
        getDepartments.execute();
        GetEmployees getEmployees = new GetEmployees();
        getEmployees.execute();
    }

    @Override public void onBackPressed() {
        DrawerLayout dl = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(dl.isDrawerOpen(GravityCompat.START)){
            dl.closeDrawer(GravityCompat.START);
            return;
        }
        if(currentFacultyId != -1){
            currentFacultyId = -1;
            departmentsList.setAdapter(
                    new ArrayAdapter<String>(this,
                            android.R.layout.simple_list_item_1)
            );
            changeMenuOptions(false);
            setTitle("Faculty App");
        }
    }

    //Menu
    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mainMenu = menu;
        return true;
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.refresh_page:{
                getDatabaseInfo();
                return true;
            }
            case R.id.add_faculty:{
                addOrChangeFaculty(true);
                return true;
            }
            case R.id.change_faculty_name:{
                addOrChangeFaculty(false);
                return true;
            }
            case R.id.add_department:{
                AlertDialog inputDialog = new AlertDialog.Builder(MainActivity.this).create();
                View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.input_department, null);
                inputDialog.setView(vv);
                inputDialog.setCancelable(true);

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

                    Direction direction = new Direction();
                    direction.name = ((EditText) vv.findViewById(R.id.input_department_name)).getText().toString();
                    direction.code = ((EditText) vv.findViewById(R.id.input_department_code)).getText().toString();
                    direction.profile = ((EditText) vv.findViewById(R.id.input_department_profile)).getText().toString();
                    direction.facultyId = currentFacultyId;

                    dataHandler.addDepartment(direction);
                    sleep(500);
                    GetDepartments getDepartments = new GetDepartments();
                    getDepartments.execute();

                    inputDialog.cancel();
                });
                ((Button) vv.findViewById(R.id.add_department_decline)).setOnClickListener(v->{
                    inputDialog.cancel();
                });
                inputDialog.show();
                return true;
            }
            case R.id.delete_faculty:{
                changeMenuOptions(false);
                setTitle("Faculty App");
                dataHandler.deleteFaculty(Faculty.findFacultyById(allFaculties, currentFacultyId));
                currentFacultyId = -1;
                sleep(500);
                getDatabaseInfo();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeMenuOptions(boolean visible){
        MenuItem addDepItem = mainMenu.findItem(R.id.add_department);
        MenuItem change = mainMenu.findItem(R.id.change_faculty_name);
        MenuItem deleteFaculty = mainMenu.findItem(R.id.delete_faculty);

        addDepItem.setVisible(visible);
        change.setVisible(visible);
        deleteFaculty.setVisible(visible);
    }

    private void addOrChangeFaculty(boolean createNew){
        AlertDialog inputDialog = new AlertDialog.Builder(MainActivity.this).create();
        View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.input_new_faculty_layout, null);
        inputDialog.setView(vv);
        inputDialog.setCancelable(true);
        EditText editFacultyName = (EditText) vv.findViewById(R.id.editFacultyName);
        Button accept = (Button) vv.findViewById(R.id.addFacultyAccept);

        if(!createNew){
            accept.setText("Изменить");
            editFacultyName.setText(Faculty.findFacultyById(allFaculties, currentFacultyId).name);
        }
        else accept.setText("Добавить");
        accept.setOnClickListener(v->{

            String newName = editFacultyName.getText().toString();
            newName = newName.trim();
            if(newName.isEmpty()){
                Toast.makeText(getApplicationContext(), "Недопустимое имя факультета", Toast.LENGTH_SHORT)
                        .show();
                inputDialog.cancel();
                return;
            }

            if(createNew){
                dataHandler.addFaculty(editFacultyName.getText().toString());
            }
            else{
                setTitle(editFacultyName.getText().toString());
                Faculty faculty = Faculty.findFacultyById(allFaculties, currentFacultyId);
                faculty.name = editFacultyName.getText().toString();
                dataHandler.updateFaculty(faculty);
            }
            sleep(500);
            GetFaculties getFaculties = new GetFaculties();
            getFaculties.execute();
            inputDialog.cancel();
        });
        ((Button) vv.findViewById(R.id.addFacultyDecline)).setOnClickListener(v->{
            inputDialog.cancel();
        });
        inputDialog.show();
    }

    //Navigation Drawer

    private void createNavigationMenu(){
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, myToolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void updateNavigationMenuValues(){
        navigationView.getMenu().clear();

        if(allFaculties.size() > 0){
            for(Faculty faculty: allFaculties){
                navigationView.getMenu().add(Menu.NONE, faculty.id, Menu.NONE, faculty.name);
            }
        }
        else navigationView.getMenu().add(Menu.NONE, 0, Menu.NONE, "Еще нет факультетов");


        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        changeMenuOptions(true);
        currentFacultyId = item.getItemId();
        setTitle(Faculty.findFacultyById(allFaculties, currentFacultyId).name);

        if(allDirections.size() > 0){
            searchForFacultyDepartments();
            setListViewAdapter();
        }
        DrawerLayout dl = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(dl.isDrawerOpen(GravityCompat.START)){
            dl.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void searchForFacultyDepartments(){
        currentFacultyDirections = new ArrayList<>();
        for(Direction direction : allDirections){
            if(direction.facultyId == currentFacultyId) currentFacultyDirections.add(direction);
        }
    }



    class GetFaculties extends AsyncTask<Void, Void, ArrayList<Faculty>> {
        @Override
        protected ArrayList<Faculty> doInBackground(Void... unused) {
            return (ArrayList<Faculty>) dataHandler.getDB().facultyDao().getAll();
        }
        @Override
        protected void onPostExecute(ArrayList<Faculty> faculties) {
            allFaculties = faculties;
            Toast.makeText(getApplicationContext(), "Факультеты загружены(" + String.valueOf(allFaculties.size()) + ")", Toast.LENGTH_SHORT)
                    .show();
            updateNavigationMenuValues();
        }
    }
    class GetDepartments extends AsyncTask<Void, Void, ArrayList<Direction>> {
        @Override
        protected ArrayList<Direction> doInBackground(Void... unused) {
            return (ArrayList<Direction>) dataHandler.getDB().departmentDao().getAll();
        }
        @Override
        protected void onPostExecute(ArrayList<Direction> directionArrayList) {
            allDirections = directionArrayList;
            if(currentFacultyId != -1){
                searchForFacultyDepartments();
                setListViewAdapter();
                //((ArrayAdapter) departmentsList.getAdapter()).notifyDataSetChanged();
            }
            Toast.makeText(getApplicationContext(), "Направления подготовки загружены(" + String.valueOf(allDirections.size()) + ")", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    class GetEmployees extends AsyncTask<Void, Void, ArrayList<Students>> {
        @Override
        protected ArrayList<Students> doInBackground(Void... unused) {
            return (ArrayList<Students>) dataHandler.getDB().employeeDao().getAll();
        }
        @Override
        protected void onPostExecute(ArrayList<Students> studentsArrayList) {
            allStudents = studentsArrayList;
            Toast.makeText(getApplicationContext(), "Студенты загружены (" + String.valueOf(allStudents.size()) + ")", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}