package com.example.hospitalpatient;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SetSlotActivity extends AppCompatActivity {

    EditText etDoctorId, etDate, etCapacity;

    Button btnStartTime, btnEndTime, btnSaveSlot;

    String startTime = "";
    String endTime = "";

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_slot);

        etDoctorId = findViewById(R.id.etDoctorId);
        etDate = findViewById(R.id.etDate);
        etCapacity = findViewById(R.id.etCapacity);

        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnSaveSlot = findViewById(R.id.btnSaveSlot);

        db = FirebaseFirestore.getInstance();


        // START TIME
        btnStartTime.setOnClickListener(view -> {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog =
                    new TimePickerDialog(
                            SetSlotActivity.this,

                            (timePicker, selectedHour, selectedMinute) -> {

                                String amPm;

                                if (selectedHour >= 12) {
                                    amPm = "PM";
                                } else {
                                    amPm = "AM";
                                }

                                int hour12 = selectedHour % 12;

                                if (hour12 == 0) {
                                    hour12 = 12;
                                }

                                startTime = String.format(
                                        "%02d:%02d %s",
                                        hour12,
                                        selectedMinute,
                                        amPm
                                );

                                btnStartTime.setText(
                                        "START TIME: " + startTime
                                );
                            },

                            hour,
                            minute,
                            false
                    );

            dialog.show();
        });


        // END TIME
        btnEndTime.setOnClickListener(view -> {

            Calendar calendar = Calendar.getInstance();

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog =
                    new TimePickerDialog(
                            SetSlotActivity.this,

                            (timePicker, selectedHour, selectedMinute) -> {

                                String amPm;

                                if (selectedHour >= 12) {
                                    amPm = "PM";
                                } else {
                                    amPm = "AM";
                                }

                                int hour12 = selectedHour % 12;

                                if (hour12 == 0) {
                                    hour12 = 12;
                                }

                                endTime = String.format(
                                        "%02d:%02d %s",
                                        hour12,
                                        selectedMinute,
                                        amPm
                                );

                                btnEndTime.setText(
                                        "END TIME: " + endTime
                                );
                            },

                            hour,
                            minute,
                            false
                    );

            dialog.show();
        });


        // SAVE SLOT
        btnSaveSlot.setOnClickListener(view -> {

            String doctorId =
                    etDoctorId.getText().toString().trim();

            String date =
                    etDate.getText().toString().trim();

            String capacityText =
                    etCapacity.getText().toString().trim();


            if (doctorId.isEmpty()
                    || date.isEmpty()
                    || startTime.isEmpty()
                    || endTime.isEmpty()
                    || capacityText.isEmpty()) {

                Toast.makeText(
                        SetSlotActivity.this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }


            int capacity =
                    Integer.parseInt(capacityText);


            Map<String, Object> slot =
                    new HashMap<>();

            slot.put("doctorId", doctorId);
            slot.put("date", date);
            slot.put("startTime", startTime);
            slot.put("endTime", endTime);
            slot.put("capacity", capacity);
            slot.put("bookedCount", 0);
            slot.put("available", true);


            db.collection("slots")
                    .add(slot)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(
                                SetSlotActivity.this,
                                "Slot saved successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                SetSlotActivity.this,
                                "Failed: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    });
        });
    }
}