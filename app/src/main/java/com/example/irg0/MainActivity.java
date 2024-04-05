package com.example.irg0;

import static com.example.irg0.helpers.Person.isPhoneNumber;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.XmlRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.irg0.helpers.Person;
import com.example.irg0.helpers.PersonBase;


public class MainActivity extends AppCompatActivity {
    public static PersonBase base = new PersonBase();

    private Person addPerson = new Person();
    private EditText idEditText;
    private EditText ageEditText;
    private EditText nameEditText;
    private EditText infoEditText;

    public void onIR(View view) {
        Intent intent = new Intent(this, IRActivity.class);
        startActivity(intent);
    }

    public void onFriendList(View view) {
        Intent intent = new Intent(this, AddFriendActivity.class);
        startActivity(intent);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
    }
}

//Сохранение базы на устройстве
//Сканер qr для получения id при добавлении человека
//Блокировать ориентацию экрана