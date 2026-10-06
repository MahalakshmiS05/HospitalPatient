package com.example.hospitalpatient;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    Button btnAddDoctor;
    Button btnSetSlot;
    Button btnViewAppointments;
    Button btnQueue;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_dashboard);

        btnAddDoctor =
                findViewById(R.id.btnAddDoctor);

        btnSetSlot =
                findViewById(R.id.btnSetSlot);

        btnViewAppointments =
                findViewById(R.id.btnViewAppointments);

        btnQueue =
                findViewById(R.id.btnQueue);

        btnLogout =
                findViewById(R.id.btnLogout);

        // ADD DOCTOR
        btnAddDoctor.setOnClickListener(view -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    AddDoctorActivity.class
            );

            startActivity(intent);
        });

        // SET SLOT
        btnSetSlot.setOnClickListener(view -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    SetSlotActivity.class
            );

            startActivity(intent);
        });

        // VIEW APPOINTMENTS
        btnViewAppointments.setOnClickListener(view -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    ViewAppointmentsActivity.class
            );

            startActivity(intent);
        });

        // QUEUE MANAGEMENT
        btnQueue.setOnClickListener(view -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    QueueManagementActivity.class
            );

            startActivity(intent);
        });

        // LOGOUT
        btnLogout.setOnClickListener(view -> {

            Intent intent = new Intent(
                    AdminDashboardActivity.this,
                    MainActivity.class
            );

            startActivity(intent);

            finish();
        });
    }
}