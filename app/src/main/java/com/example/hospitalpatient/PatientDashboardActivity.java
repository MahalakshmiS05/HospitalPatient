package com.example.hospitalpatient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class PatientDashboardActivity extends AppCompatActivity {

    Button btnFindDoctor;
    Button btnBookAppointment;
    Button btnMyAppointment;
    Button btnQueueStatus;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_patient_dashboard);

        // Connect buttons
        btnFindDoctor =
                findViewById(R.id.btnFindDoctor);

        btnBookAppointment =
                findViewById(R.id.btnBookAppointment);

        btnMyAppointment =
                findViewById(R.id.btnMyAppointment);

        btnQueueStatus =
                findViewById(R.id.btnQueueStatus);

        btnLogout =
                findViewById(R.id.btnLogout);


        // FIND DOCTOR
        btnFindDoctor.setOnClickListener(view -> {

            Intent intent = new Intent(
                    PatientDashboardActivity.this,
                    DoctorListActivity.class
            );

            startActivity(intent);
        });


        // BOOK APPOINTMENT
        btnBookAppointment.setOnClickListener(view -> {

            Intent intent = new Intent(
                    PatientDashboardActivity.this,
                    DoctorListActivity.class
            );

            startActivity(intent);
        });


        // MY APPOINTMENT
        btnMyAppointment.setOnClickListener(view -> {

            Intent intent = new Intent(
                    PatientDashboardActivity.this,
                    MyAppointmentActivity.class
            );

            startActivity(intent);
        });


        // QUEUE STATUS
        btnQueueStatus.setOnClickListener(view -> {

            Intent intent = new Intent(
                    PatientDashboardActivity.this,
                    QueueStatusActivity.class
            );

            startActivity(intent);
        });


        // LOGOUT
        btnLogout.setOnClickListener(view -> {

            Intent intent = new Intent(
                    PatientDashboardActivity.this,
                    MainActivity.class
            );

            startActivity(intent);

            finish();
        });
    }
}