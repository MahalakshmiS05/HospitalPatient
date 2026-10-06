package com.example.hospitalpatient;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ViewAppointmentsActivity extends AppCompatActivity {

    LinearLayout appointmentContainer;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_appointments);

        appointmentContainer =
                findViewById(R.id.appointmentContainer);

        db = FirebaseFirestore.getInstance();

        loadAppointments();
    }

    private void loadAppointments() {

        appointmentContainer.removeAllViews();

        db.collection("appointments")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        TextView noAppointments =
                                new TextView(
                                        ViewAppointmentsActivity.this
                                );

                        noAppointments.setText(
                                "No appointments found"
                        );

                        noAppointments.setTextSize(18);

                        appointmentContainer.addView(
                                noAppointments
                        );

                        return;
                    }

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String appointmentId =
                                document.getId();

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

                        // Appointment layout
                        LinearLayout appointmentLayout =
                                new LinearLayout(
                                        ViewAppointmentsActivity.this
                                );

                        appointmentLayout.setOrientation(
                                LinearLayout.VERTICAL
                        );

                        appointmentLayout.setPadding(
                                20,
                                20,
                                20,
                                20
                        );

                        // Appointment details
                        TextView details =
                                new TextView(
                                        ViewAppointmentsActivity.this
                                );

                        details.setText(
                                "Doctor: " + doctorName
                                        + "\nDate: " + date
                                        + "\nTime: "
                                        + startTime
                                        + " - "
                                        + endTime
                                        + "\nToken: "
                                        + tokenNumber
                                        + "\nStatus: "
                                        + status
                        );

                        details.setTextSize(17);

                        appointmentLayout.addView(
                                details
                        );

                        // ONLY show buttons for PENDING
                        if ("PENDING".equals(status)) {

                            Button btnConfirm =
                                    new Button(
                                            ViewAppointmentsActivity.this
                                    );

                            btnConfirm.setText(
                                    "CONFIRM"
                            );

                            appointmentLayout.addView(
                                    btnConfirm
                            );

                            Button btnReject =
                                    new Button(
                                            ViewAppointmentsActivity.this
                                    );

                            btnReject.setText(
                                    "REJECT"
                            );

                            appointmentLayout.addView(
                                    btnReject
                            );

                            // Confirm
                            btnConfirm.setOnClickListener(
                                    view -> {

                                        updateStatus(
                                                appointmentId,
                                                "CONFIRMED"
                                        );
                                    });

                            // Reject
                            btnReject.setOnClickListener(
                                    view -> {

                                        updateStatus(
                                                appointmentId,
                                                "REJECTED"
                                        );
                                    });

                        } else {

                            // No buttons for CONFIRMED,
                            // REJECTED or COMPLETED

                            TextView statusText =
                                    new TextView(
                                            ViewAppointmentsActivity.this
                                    );

                            statusText.setText(
                                    "Appointment Status: "
                                            + status
                            );

                            statusText.setTextSize(18);

                            statusText.setPadding(
                                    0,
                                    15,
                                    0,
                                    15
                            );

                            appointmentLayout.addView(
                                    statusText
                            );
                        }

                        appointmentContainer.addView(
                                appointmentLayout
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            ViewAppointmentsActivity.this,
                            "Failed to load appointments: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void updateStatus(
            String appointmentId,
            String newStatus) {

        db.collection("appointments")
                .document(appointmentId)
                .update("status", newStatus)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            ViewAppointmentsActivity.this,
                            "Appointment "
                                    + newStatus,
                            Toast.LENGTH_SHORT
                    ).show();

                    // Reload screen
                    loadAppointments();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            ViewAppointmentsActivity.this,
                            "Failed to update: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}