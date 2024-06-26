package com.example.irg0;

import static com.example.irg0.helpers.Person.isPhoneNumber;
import static com.example.irg0.helpers.Person.phoneNumberPattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.irg0.R;
import com.example.irg0.helpers.Person;
import com.example.irg0.helpers.PersonBase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddFriendActivity extends AppCompatActivity {
    private Person addPerson = new Person();
    private EditText idEditText;
    private EditText ageEditText;
    private EditText nameEditText;
    private EditText infoEditText;

    public void onReturn(View view) {
        finish();
    }

    @SuppressLint("SetTextI18n")
    private ActivityResultLauncher<Intent> qrScanLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String number = data.getStringExtra("ID");
                        addPerson.setId(number);
                        idEditText.setText(number);
                    }
                }
            });

    public void onQRScanner(View view) {
        Intent qrIntent = new Intent(this, QRScanActivity.class);
        qrScanLauncher.launch(qrIntent);
    }

    public void onAdd(View view) {
        String number = idEditText.getText().toString();
        String name = nameEditText.getText().toString();
        if (isPhoneNumber(number) && !name.matches("^\\s*$")) {
            if (addPerson.getId() == -1) {
                addPerson.setId(number);
            }
            addPerson.setPhoneNumber(number);
            addPerson.setBirthday(ageEditText.getText().toString());
            addPerson.setName(name);
            addPerson.setInfo(infoEditText.getText().toString());

            MainActivity.base.addToBase(addPerson);
            addPerson = new Person();
            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();
        }

        clearEditTexts();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_friend);

        idEditText = findViewById(R.id.idEditText);
        ageEditText = findViewById(R.id.ageEditText);
        nameEditText = findViewById(R.id.nameEditText);
        infoEditText = findViewById(R.id.infoEditText);

        String receivedString = getIntent().getStringExtra("ID");
        if (receivedString != null) {
            Person p = MainActivity.base.getPerson(Integer.parseInt(receivedString));
            if (p == null) {
                return;
            }
            idEditText.setText(p.getPhoneNumber());
            LocalDate birthday = p.getBirthday();
            if (birthday != null) {
                ageEditText.setText(birthday.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            }
            nameEditText.setText(p.getName());
            String info = p.getInfo();
            if (!info.matches("\\s")) {
                infoEditText.setText(info);
            }
        }

        Button selectDateButton = findViewById(R.id.selectDateButton);
        selectDateButton.setOnClickListener(v -> showDatePickerDialog(AddFriendActivity.this));
    }

    private void showDatePickerDialog(Context context) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (view, year1, month1, dayOfMonth) -> {
                    ageEditText.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                }, year, month, day);

        datePickerDialog.show();
    }

    public void onDelete(View view) {
        String number = idEditText.getText().toString();
        int hash = Person.makeId(number);
        if (MainActivity.base.delete(hash)) {
            clearEditTexts();
            Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Unknown number", Toast.LENGTH_LONG).show();
        }


    }

    public void clearEditTexts() {
        idEditText.getText().clear();
        nameEditText.getText().clear();
        ageEditText.getText().clear();
        infoEditText.getText().clear();
    }
}