package com.example.irg0;

import android.annotation.SuppressLint;
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
import androidx.annotation.XmlRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.irg0.helpers.Person;
import com.example.irg0.helpers.PersonBase;


public class MainActivity extends AppCompatActivity {
    public static PersonBase base = new PersonBase();

    private EditText idEditText;
    private EditText ageEditText;
    private EditText nameEditText;
    private EditText infoEditText;

    public void onIR(View view) {
        Toast.makeText(this, "IR ->", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, IRActivity.class);
        startActivity(intent);
    }

    public void onQRScanner(View view) {
        Toast.makeText(this, "QR ->", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, QRScanActivity.class);
        startActivity(intent);
    }

    public void onAdd(View view) {
        int id = base.addToBase(new Person(idEditText.getText().toString(),
                nameEditText.getText().toString(),
                ageEditText.getText().toString(),
                infoEditText.getText().toString()));

        Toast.makeText(this, id + "", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        idEditText = findViewById(R.id.idEditText);
        ageEditText = findViewById(R.id.ageEditText);
        nameEditText = findViewById(R.id.nameEditText);
        infoEditText = findViewById(R.id.infoEditText);
        Button selectDateButton = findViewById(R.id.selectDateButton);
        selectDateButton.setOnClickListener(v -> showDatePickerDialog(MainActivity.this));
    }

    private void showDatePickerDialog(Context context) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year1, month1, dayOfMonth) -> {
                    ageEditText.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                }, year, month, day);

        datePickerDialog.show();
    }
}

//Сохранение базы на устройстве
//Сканер qr для получения id при добавлении человека
//Блокировать ориентацию экрана