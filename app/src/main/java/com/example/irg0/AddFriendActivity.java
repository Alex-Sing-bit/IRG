package com.example.irg0;

import static com.example.irg0.helpers.Person.isPhoneNumber;

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
        Button selectDateButton = findViewById(R.id.selectDateButton);
        selectDateButton.setOnClickListener(v -> showDatePickerDialog(AddFriendActivity.this));
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