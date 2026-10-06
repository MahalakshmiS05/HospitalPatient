package com.example.hospitalpatient;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BookAppointmentActivity extends AppCompatActivity {

    TextView tvDoctorName;
    TextView tvSpecialization;
    TextView tvTimingTitle;

    LinearLayout dateContainer;
    LinearLayout slotContainer;

    Button btnBook;

    FirebaseAuth auth;
    FirebaseFirestore db;

    String doctorId;
    String doctorName;
    String specialization;

    String selectedDate = "";

    String selectedSlotId = "";
    String selectedStartTime = "";
    String selectedEndTime = "";

    int selectedCapacity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_book_appointment);

        // Connect XML views

        tvDoctorName =
                findViewById(R.id.tvDoctorName);

        tvSpecialization =
                findViewById(R.id.tvSpecialization);

        tvTimingTitle =
                findViewById(R.id.tvTimingTitle);

        dateContainer =
                findViewById(R.id.dateContainer);

        slotContainer =
                findViewById(R.id.slotContainer);

        btnBook =
                findViewById(R.id.btnBook);

        // Firebase

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        // Get doctor information

        doctorId =
                getIntent().getStringExtra("doctorId");

        doctorName =
                getIntent().getStringExtra("doctorName");

        specialization =
                getIntent().getStringExtra("specialization");

        tvDoctorName.setText(
                doctorName
        );

        tvSpecialization.setText(
                specialization
        );

        // Automatically load available dates

        loadAvailableDates();


        // Book appointment button

        btnBook.setOnClickListener(view -> {

            if (selectedDate.isEmpty()) {

                Toast.makeText(
                        BookAppointmentActivity.this,
                        "Please select a date",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (selectedSlotId.isEmpty()) {

                Toast.makeText(
                        BookAppointmentActivity.this,
                        "Please select a timing",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            bookAppointment();
        });
    }


    // =========================================================
    // LOAD AVAILABLE DATES
    // =========================================================

    private void loadAvailableDates() {

        dateContainer.removeAllViews();

        slotContainer.removeAllViews();

        tvTimingTitle.setVisibility(View.GONE);

        btnBook.setEnabled(false);

        db.collection("slots")
                .whereEqualTo(
                        "doctorId",
                        doctorId
                )
                .whereEqualTo(
                        "available",
                        true
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        TextView noDates =
                                new TextView(
                                        BookAppointmentActivity.this
                                );

                        noDates.setText(
                                "No available dates"
                        );

                        noDates.setTextSize(17);

                        dateContainer.addView(
                                noDates
                        );

                        return;
                    }


                    // Store unique dates

                    Set<String> dates =
                            new HashSet<>();


                    for (
                            QueryDocumentSnapshot document :
                            querySnapshot
                    ) {

                        String date =
                                document.getString("date");

                        if (date != null
                                && !date.isEmpty()) {

                            dates.add(date);
                        }
                    }


                    // Create date buttons

                    for (String date : dates) {

                        Button dateButton =
                                new Button(
                                        BookAppointmentActivity.this
                                );

                        dateButton.setText(
                                date
                        );

                        dateButton.setTextSize(17);

                        dateButton.setAllCaps(false);


                        LinearLayout.LayoutParams params =
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );

                        params.setMargins(
                                0,
                                0,
                                0,
                                12
                        );

                        dateButton.setLayoutParams(
                                params
                        );


                        dateContainer.addView(
                                dateButton
                        );


                        // Date click

                        dateButton.setOnClickListener(
                                view -> {

                                    selectedDate =
                                            date;

                                    // Reset old slot selection

                                    selectedSlotId = "";

                                    selectedStartTime = "";

                                    selectedEndTime = "";

                                    selectedCapacity = 0;

                                    btnBook.setEnabled(
                                            false
                                    );

                                    // Show timings

                                    loadAvailableSlots(
                                            date
                                    );
                                }
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            BookAppointmentActivity.this,
                            "Failed to load dates: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    // =========================================================
    // LOAD AVAILABLE TIMINGS
    // =========================================================

    private void loadAvailableSlots(String date) {

        slotContainer.removeAllViews();

        tvTimingTitle.setVisibility(
                View.VISIBLE
        );

        db.collection("slots")
                .whereEqualTo(
                        "doctorId",
                        doctorId
                )
                .whereEqualTo(
                        "date",
                        date
                )
                .whereEqualTo(
                        "available",
                        true
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {

                        TextView noSlots =
                                new TextView(
                                        BookAppointmentActivity.this
                                );

                        noSlots.setText(
                                "No timings available"
                        );

                        noSlots.setTextSize(17);

                        slotContainer.addView(
                                noSlots
                        );

                        return;
                    }


                    // Create timing buttons

                    for (
                            QueryDocumentSnapshot document :
                            querySnapshot
                    ) {

                        String slotId =
                                document.getId();

                        String startTime =
                                document.getString(
                                        "startTime"
                                );

                        String endTime =
                                document.getString(
                                        "endTime"
                                );

                        Long capacity =
                                document.getLong(
                                        "capacity"
                                );

                        if (capacity == null) {

                            capacity = 0L;
                        }


                        Button slotButton =
                                new Button(
                                        BookAppointmentActivity.this
                                );


                        slotButton.setText(
                                startTime
                                        + " - "
                                        + endTime
                        );

                        slotButton.setTextSize(17);

                        slotButton.setAllCaps(false);


                        LinearLayout.LayoutParams params =
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );

                        params.setMargins(
                                0,
                                0,
                                0,
                                12
                        );

                        slotButton.setLayoutParams(
                                params
                        );


                        slotContainer.addView(
                                slotButton
                        );


                        Long finalCapacity =
                                capacity;


                        // Timing click

                        slotButton.setOnClickListener(
                                view -> {

                                    selectedSlotId =
                                            slotId;

                                    selectedStartTime =
                                            startTime;

                                    selectedEndTime =
                                            endTime;

                                    selectedCapacity =
                                            finalCapacity.intValue();


                                    // Enable booking

                                    btnBook.setEnabled(
                                            true
                                    );


                                    Toast.makeText(
                                            BookAppointmentActivity.this,
                                            "Timing selected",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            BookAppointmentActivity.this,
                            "Failed to load timings: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    // =========================================================
    // BOOK APPOINTMENT
    // =========================================================

    private void bookAppointment() {

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    BookAppointmentActivity.this,
                    "Please login again",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        String patientId =
                auth.getCurrentUser().getUid();


        // Check existing bookings for this slot

        db.collection("appointments")
                .whereEqualTo(
                        "slotId",
                        selectedSlotId
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    int bookedCount =
                            querySnapshot.size();


                    // Check capacity

                    if (bookedCount >=
                            selectedCapacity) {

                        Toast.makeText(
                                BookAppointmentActivity.this,
                                "This slot is full",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }


                    // Generate token

                    int tokenNumber =
                            bookedCount + 1;


                    String appointmentId =
                            db.collection(
                                            "appointments"
                                    )
                                    .document()
                                    .getId();


                    // Appointment data

                    Map<String, Object> appointment =
                            new HashMap<>();


                    appointment.put(
                            "appointmentId",
                            appointmentId
                    );

                    appointment.put(
                            "patientId",
                            patientId
                    );

                    appointment.put(
                            "doctorId",
                            doctorId
                    );

                    appointment.put(
                            "doctorName",
                            doctorName
                    );

                    appointment.put(
                            "slotId",
                            selectedSlotId
                    );

                    appointment.put(
                            "date",
                            selectedDate
                    );

                    appointment.put(
                            "startTime",
                            selectedStartTime
                    );

                    appointment.put(
                            "endTime",
                            selectedEndTime
                    );

                    appointment.put(
                            "tokenNumber",
                            tokenNumber
                    );

                    appointment.put(
                            "status",
                            "PENDING"
                    );


                    // Save appointment

                    db.collection("appointments")
                            .document(appointmentId)
                            .set(appointment)
                            .addOnSuccessListener(
                                    unused -> {

                                        Toast.makeText(
                                                BookAppointmentActivity.this,
                                                "Appointment booked!"
                                                        + "\nToken: "
                                                        + tokenNumber,
                                                Toast.LENGTH_LONG
                                        ).show();

                                        finish();
                                    }
                            )
                            .addOnFailureListener(
                                    e -> {

                                        Toast.makeText(
                                                BookAppointmentActivity.this,
                                                "Booking failed: "
                                                        + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                            );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            BookAppointmentActivity.this,
                            "Could not check slot capacity: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}