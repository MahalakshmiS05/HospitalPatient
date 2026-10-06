package com.example.hospitalpatient;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MyAppointmentActivity extends AppCompatActivity {

    LinearLayout appointmentContainer;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_appointment);

        appointmentContainer =
                findViewById(R.id.appointmentContainer);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadMyAppointments();
    }

    private void loadMyAppointments() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    MyAppointmentActivity.this,
                    "Please login again",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String patientId =
                auth.getCurrentUser().getUid();

        db.collection("appointments")
                .whereEqualTo("patientId", patientId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    appointmentContainer.removeAllViews();

                    if (querySnapshot.isEmpty()) {

                        TextView noAppointment =
                                new TextView(
                                        MyAppointmentActivity.this
                                );

                        noAppointment.setText(
                                "No appointments found"
                        );

                        noAppointment.setTextSize(18);

                        appointmentContainer.addView(
                                noAppointment
                        );

                        return;
                    }

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String doctorName =
                                document.getString("doctorName");

                        String date =
                                document.getString("date");

                        String startTime =
                                document.getString("startTime");

                        String endTime =
                                document.getString("endTime");

                        String status =
                                document.getString("status");

                        Long tokenNumber =
                                document.getLong("tokenNumber");

                        if (tokenNumber == null) {
                            tokenNumber = 0L;
                        }

                        TextView appointment =
                                new TextView(
                                        MyAppointmentActivity.this
                                );

                        appointment.setText(
                                "DOCTOR: " + doctorName
                                        + "\n\nDATE: " + date
                                        + "\n\nTIME: "
                                        + startTime
                                        + " - "
                                        + endTime
                                        + "\n\nTOKEN NUMBER: "
                                        + tokenNumber
                                        + "\n\nSTATUS: "
                                        + status
                        );

                        appointment.setTextSize(18);

                        appointment.setPadding(
                                30,
                                30,
                                30,
                                30
                        );

                        appointmentContainer.addView(
                                appointment
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            MyAppointmentActivity.this,
                            "Failed to load appointment: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}