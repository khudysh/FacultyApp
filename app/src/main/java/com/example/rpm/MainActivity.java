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

import com.example.rpm.controllers.ApiService;
import com.example.rpm.modelsDB.DataHandler;
import com.example.rpm.modelsDB.Direction;
import com.example.rpm.modelsDB.Students;
import com.example.rpm.modelsDB.Faculty;
import com.example.rpm.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.P)
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ActivityMainBinding binding;

    private NavigationView navigationView;
    private ListView directionsList;
    private Menu mainMenu;

    private DataHandler dataHandler = new DataHandler();
    private int currentFacultyId;

    private ArrayList<Faculty> allFaculties;
    private ArrayList<Direction> allDirections;
    private ArrayList<Direction> currentFacultyDirections;
    private ArrayList<Students> allStudents;
    private ApiService apiService = new ApiService();

    //Initializing
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeValues();

        getDatabaseInfo();

        createNavigationMenu();

    }

    private void initializeValues(){
        currentFacultyId = -1;
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        directionsList = (ListView) findViewById(R.id.directionsListView);
        directionsList.setOnItemClickListener((parent, view, position, id)->{
            Intent intent = new Intent(MainActivity.this, StudentsListActivity.class);
            int dirId = currentFacultyDirections.get(position).id;
            String title = currentFacultyDirections.get(position).name;
            String code = currentFacultyDirections.get(position).code;
            String profile = currentFacultyDirections.get(position).profile;
            intent.putExtra("directionId", dirId);
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
            for(Students student: allStudents){
                if(student.departmentId == direction.id) amount++;
            }
            str.add(direction.code + ' ' + direction.name + ' ' + direction.profile + " (Студентов: " + amount + ")");
        }
        directionsList.setAdapter(
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        str)
        );
    }

    private void getDatabaseInfo(){
        GetFaculties getFaculties = new GetFaculties();
        getFaculties.execute();
        GetDirections getDirections = new GetDirections();
        getDirections.execute();
        GetStudents getStudents = new GetStudents();
        getStudents.execute();
    }

    @Override public void onBackPressed() {
        DrawerLayout dl = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(dl.isDrawerOpen(GravityCompat.START)){
            dl.closeDrawer(GravityCompat.START);
            return;
        }
        if(currentFacultyId != -1){
            currentFacultyId = -1;
            directionsList.setAdapter(
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
            case R.id.add_faculty:{
                addOrChangeFaculty(true);
                return true;
            }
            case R.id.change_faculty_name:{
                addOrChangeFaculty(false);
                return true;
            }
            case R.id.add_direction:{
                AlertDialog inputDialog = new AlertDialog.Builder(MainActivity.this).create();
                View vv = (LinearLayout) getLayoutInflater().inflate(R.layout.input_direction, null);
                inputDialog.setView(vv);
                inputDialog.setCancelable(true);

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

                    Direction direction = new Direction();
                    direction.name = ((EditText) vv.findViewById(R.id.input_direction_name)).getText().toString();
                    direction.code = ((EditText) vv.findViewById(R.id.input_direction_code)).getText().toString();
                    direction.profile = ((EditText) vv.findViewById(R.id.input_direction_profile)).getText().toString();
                    direction.facultyId = currentFacultyId;

                    dataHandler.addDirection(direction);
                    sleep(500);
                    GetDirections getDirections = new GetDirections();
                    getDirections.execute();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                apiService.insertFaculty(direction.name);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    inputDialog.cancel();
                });
                ((Button) vv.findViewById(R.id.add_direction_decline)).setOnClickListener(v->{
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
        MenuItem addDepItem = mainMenu.findItem(R.id.add_direction);
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

    private void createNavigationMenu() {
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
        getDatabaseInfo();
        if(allDirections.size() > 0){
            searchForFacultyDirections();
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

    private void searchForFacultyDirections(){
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

    class GetDirections extends AsyncTask<Void, Void, ArrayList<Direction>> {
        @Override
        protected ArrayList<Direction> doInBackground(Void... unused) {
            return (ArrayList<Direction>) dataHandler.getDB().directionDao().getAll();
        }
        @Override
        protected void onPostExecute(ArrayList<Direction> directionArrayList) {
            allDirections = directionArrayList;
            if(currentFacultyId != -1){
                searchForFacultyDirections();
                setListViewAdapter();
                //((ArrayAdapter) departmentsList.getAdapter()).notifyDataSetChanged();
            }
            Toast.makeText(getApplicationContext(), "Направления подготовки загружены(" + String.valueOf(allDirections.size()) + ")", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    class GetStudents extends AsyncTask<Void, Void, ArrayList<Students>> {
        @Override
        protected ArrayList<Students> doInBackground(Void... unused) {
            return (ArrayList<Students>) dataHandler.getDB().studentDao().getAll();
        }
        @Override
        protected void onPostExecute(ArrayList<Students> studentsArrayList) {
            allStudents = studentsArrayList;
            Toast.makeText(getApplicationContext(), "Студенты загружены (" + String.valueOf(allStudents.size()) + ")", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}