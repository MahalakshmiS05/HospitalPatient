package com.example.hospitalpatient;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddDoctorActivity extends AppCompatActivity {

    EditText etDoctorId;
    EditText etDoctorName;
    EditText etSpecialization;

    Button btnAddDoctor;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_doctor);

        etDoctorId =
                findViewById(R.id.etDoctorId);

        etDoctorName =
                findViewById(R.id.etDoctorName);

        etSpecialization =
                findViewById(R.id.etSpecialization);

        btnAddDoctor =
                findViewById(R.id.btnAddDoctor);

        db = FirebaseFirestore.getInstance();

        btnAddDoctor.setOnClickListener(view -> {

            String doctorId =
                    etDoctorId.getText().toString().trim();

            String doctorName =
                    etDoctorName.getText().toString().trim();

            String specialization =
                    etSpecialization.getText().toString().trim();

            if (doctorId.isEmpty()
                    || doctorName.isEmpty()
                    || specialization.isEmpty()) {

                Toast.makeText(
                        AddDoctorActivity.this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Map<String, Object> doctor =
                    new HashMap<>();

            doctor.put("doctorId", doctorId);
            doctor.put("name", doctorName);
            doctor.put("specialization", specialization);
            doctor.put("available", true);

            db.collection("doctors")
                    .document(doctorId)
                    .set(doctor)
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                AddDoctorActivity.this,
                                "Doctor added successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                        etDoctorId.setText("");
                        etDoctorName.setText("");
                        etSpecialization.setText("");
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                AddDoctorActivity.this,
                                "Failed to add doctor: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });
    }
}