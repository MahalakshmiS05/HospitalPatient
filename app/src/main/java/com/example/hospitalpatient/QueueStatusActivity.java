package com.example.hospitalpatient;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class QueueStatusActivity extends AppCompatActivity {

    TextView tvDoctor;
    TextView tvYourToken;
    TextView tvCurrentToken;
    TextView tvPeopleAhead;
    TextView tvStatus;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_queue_status);

        tvDoctor =
                findViewById(R.id.tvDoctor);

        tvYourToken =
                findViewById(R.id.tvYourToken);

        tvCurrentToken =
                findViewById(R.id.tvCurrentToken);

        tvPeopleAhead =
                findViewById(R.id.tvPeopleAhead);

        tvStatus =
                findViewById(R.id.tvStatus);

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        loadQueueStatus();
    }


    private void loadQueueStatus() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    QueueStatusActivity.this,
                    "Please login again",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        String patientId =
                auth.getCurrentUser().getUid();


        // Find patient's appointment
        db.collection("appointments")
                .whereEqualTo(
                        "patientId",
                        patientId
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        tvDoctor.setText(
                                "Doctor: -"
                        );

                        tvYourToken.setText(
                                "Your Token: -"
                        );

                        tvCurrentToken.setText(
                                "Current Token: -"
                        );

                        tvPeopleAhead.setText(
                                "People Ahead: -"
                        );

                        tvStatus.setText(
                                "Status: No appointment found"
                        );

                        return;
                    }


                    /*
                     * Get the latest appointment
                     * for the patient.
                     */

                    DocumentSnapshot appointment =
                            querySnapshot.getDocuments()
                                    .get(querySnapshot.size() - 1);


                    String doctorName =
                            appointment.getString(
                                    "doctorName"
                            );

                    String slotId =
                            appointment.getString(
                                    "slotId"
                            );

                    String status =
                            appointment.getString(
                                    "status"
                            );

                    Long tokenNumber =
                            appointment.getLong(
                                    "tokenNumber"
                            );


                    if (tokenNumber == null) {
                        tokenNumber = 0L;
                    }


                    final int myToken =
                            tokenNumber.intValue();


                    // Show doctor

                    tvDoctor.setText(
                            "Doctor: " + doctorName
                    );


                    // Show patient's token

                    tvYourToken.setText(
                            "Your Token: " + myToken
                    );


                    /*
                     * If appointment is not confirmed,
                     * queue has not started for this patient.
                     */

                    if (!"CONFIRMED".equals(status)) {

                        tvCurrentToken.setText(
                                "Current Token: -"
                        );

                        tvPeopleAhead.setText(
                                "People Ahead: -"
                        );

                        tvStatus.setText(
                                "Status: " + status
                        );

                        return;
                    }


                    /*
                     * Get all confirmed appointments
                     * for the same slot.
                     */

                    db.collection("appointments")
                            .whereEqualTo(
                                    "slotId",
                                    slotId
                            )
                            .whereEqualTo(
                                    "status",
                                    "CONFIRMED"
                            )
                            .get()
                            .addOnSuccessListener(
                                    confirmedAppointments -> {

                                        if (confirmedAppointments.isEmpty()) {

                                            tvCurrentToken.setText(
                                                    "Current Token: -"
                                            );

                                            tvPeopleAhead.setText(
                                                    "People Ahead: -"
                                            );

                                            tvStatus.setText(
                                                    "Status: CONFIRMED"
                                            );

                                            return;
                                        }


                                        int currentToken =
                                                Integer.MAX_VALUE;

                                        int peopleAhead = 0;


                                        /*
                                         * Find the smallest
                                         * confirmed token.
                                         */

                                        for (
                                                QueryDocumentSnapshot document :
                                                confirmedAppointments
                                        ) {

                                            Long token =
                                                    document.getLong(
                                                            "tokenNumber"
                                                    );

                                            if (token == null) {
                                                continue;
                                            }


                                            int tokenValue =
                                                    token.intValue();


                                            // Current token
                                            if (tokenValue <
                                                    currentToken) {

                                                currentToken =
                                                        tokenValue;
                                            }


                                            // Patients before you
                                            if (tokenValue <
                                                    myToken) {

                                                peopleAhead++;
                                            }
                                        }


                                        // If no token was found

                                        if (currentToken ==
                                                Integer.MAX_VALUE) {

                                            currentToken = 0;
                                        }


                                        tvCurrentToken.setText(
                                                "Current Token: "
                                                        + currentToken
                                        );


                                        tvPeopleAhead.setText(
                                                "People Ahead: "
                                                        + peopleAhead
                                        );


                                        if (peopleAhead == 0) {

                                            tvStatus.setText(
                                                    "Status: YOUR TURN"
                                            );

                                        } else {

                                            tvStatus.setText(
                                                    "Status: WAITING"
                                            );
                                        }
                                    })
                            .addOnFailureListener(e -> {

                                Toast.makeText(
                                        QueueStatusActivity.this,
                                        "Queue loading failed: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            });

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            QueueStatusActivity.this,
                            "Appointment loading failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}