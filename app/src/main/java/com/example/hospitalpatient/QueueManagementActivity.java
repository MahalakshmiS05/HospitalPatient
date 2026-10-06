package com.example.hospitalpatient;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class QueueManagementActivity extends AppCompatActivity {

    LinearLayout queueContainer;
    Button btnRefreshQueue;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_queue_management);

        queueContainer =
                findViewById(R.id.queueContainer);

        btnRefreshQueue =
                findViewById(R.id.btnRefreshQueue);

        db = FirebaseFirestore.getInstance();

        loadQueue();

        btnRefreshQueue.setOnClickListener(view -> {
            loadQueue();
        });
    }

    private void loadQueue() {

        queueContainer.removeAllViews();

        db.collection("appointments")
                .whereEqualTo("status", "CONFIRMED")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        TextView noPatients =
                                new TextView(
                                        QueueManagementActivity.this
                                );

                        noPatients.setText(
                                "No patients waiting"
                        );

                        noPatients.setTextSize(18);

                        queueContainer.addView(
                                noPatients
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

                        Long tokenNumber =
                                document.getLong("tokenNumber");

                        if (tokenNumber == null) {
                            tokenNumber = 0L;
                        }

                        LinearLayout patientLayout =
                                new LinearLayout(
                                        QueueManagementActivity.this
                                );

                        patientLayout.setOrientation(
                                LinearLayout.VERTICAL
                        );

                        patientLayout.setPadding(
                                20,
                                20,
                                20,
                                20
                        );

                        TextView details =
                                new TextView(
                                        QueueManagementActivity.this
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
                        );

                        details.setTextSize(17);

                        patientLayout.addView(details);

                        Button btnComplete =
                                new Button(
                                        QueueManagementActivity.this
                                );

                        btnComplete.setText(
                                "COMPLETE TOKEN "
                                        + tokenNumber
                        );

                        patientLayout.addView(
                                btnComplete
                        );

                        queueContainer.addView(
                                patientLayout
                        );

                        String finalAppointmentId =
                                appointmentId;

                        btnComplete.setOnClickListener(view -> {

                            completeAppointment(
                                    finalAppointmentId
                            );
                        });
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            QueueManagementActivity.this,
                            "Failed to load queue: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void completeAppointment(
            String appointmentId) {

        db.collection("appointments")
                .document(appointmentId)
                .update("status", "COMPLETED")
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            QueueManagementActivity.this,
                            "Patient completed",
                            Toast.LENGTH_SHORT
                    ).show();

                    loadQueue();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            QueueManagementActivity.this,
                            "Failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}