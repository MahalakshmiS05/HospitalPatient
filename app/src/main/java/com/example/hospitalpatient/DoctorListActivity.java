package com.example.hospitalpatient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DoctorListActivity extends AppCompatActivity {

    LinearLayout doctorContainer;
    FirebaseFirestore db;

    SharedPreferences sharedPreferences;

    String patientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        sharedPreferences = (SharedPreferences) getSharedPreferences("hospitalpatient", MODE_PRIVATE);
        patientEmail = sharedPreferences.getString("userEmail", "");

        doctorContainer = findViewById(R.id.doctorContainer);

        db = FirebaseFirestore.getInstance();

        loadDoctors();

        Toast.makeText(getApplicationContext(), patientEmail, Toast.LENGTH_SHORT).show();
    }

    private void loadDoctors() {

        db.collection("doctors")
                .whereEqualTo("available", true)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String doctorId = document.getString("doctorId");
                            String name = document.getString("name");
                            String specialization =
                                    document.getString("specialization");

                            TextView doctorText = new TextView(
                                    DoctorListActivity.this
                            );

                            doctorText.setText(
                                    name + "\n" + specialization
                            );

                            doctorText.setTextSize(18);
                            doctorText.setPadding(20, 25, 20, 25);

                            doctorContainer.addView(doctorText);

                            doctorText.setOnClickListener(view -> {

                                Intent intent = new Intent(
                                        DoctorListActivity.this,
                                        BookAppointmentActivity.class
                                );

                                intent.putExtra("doctorId", doctorId);
                                intent.putExtra("doctorName", name);
                                intent.putExtra(
                                        "specialization",
                                        specialization
                                );

                                startActivity(intent);
                            });
                        }

                    } else {

                        Toast.makeText(
                                DoctorListActivity.this,
                                "Failed: " +
                                        task.getException().getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}