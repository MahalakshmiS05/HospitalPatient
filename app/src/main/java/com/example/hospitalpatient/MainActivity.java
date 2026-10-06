package com.example.hospitalpatient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;

    FirebaseAuth auth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        sharedPreferences =
                getSharedPreferences("hospitalpatient", MODE_PRIVATE);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();

        // LOGIN
        btnLogin.setOnClickListener(view -> {

            String email =
                    etEmail.getText().toString().trim();

            String password =
                    etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                        MainActivity.this,
                        "Please enter email and password",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // ADMIN LOGIN
            if (email.equals("admin@hospital.com")
                    && password.equals("admin123")) {

                // IMPORTANT:
                // Also login the admin to Firebase Authentication

                auth.signInWithEmailAndPassword(
                                email,
                                password
                        )
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                SharedPreferences.Editor editor =
                                        sharedPreferences.edit();

                                editor.putString(
                                        "userType",
                                        "admin"
                                );

                                editor.putString(
                                        "userEmail",
                                        email
                                );

                                editor.apply();

                                Toast.makeText(
                                        MainActivity.this,
                                        "Admin login successful",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(
                                        MainActivity.this,
                                        AdminDashboardActivity.class
                                );

                                startActivity(intent);

                                finish();

                            } else {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Admin Firebase login failed: "
                                                + task.getException()
                                                .getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });

            } else {

                // PATIENT LOGIN
                auth.signInWithEmailAndPassword(
                                email,
                                password
                        )
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {

                                SharedPreferences.Editor editor =
                                        sharedPreferences.edit();

                                editor.putString(
                                        "userType",
                                        "patient"
                                );

                                editor.putString(
                                        "userEmail",
                                        email
                                );

                                editor.apply();

                                Toast.makeText(
                                        MainActivity.this,
                                        "Login successful",
                                        Toast.LENGTH_SHORT
                                ).show();

                                Intent intent = new Intent(
                                        MainActivity.this,
                                        PatientDashboardActivity.class
                                );

                                startActivity(intent);

                                finish();

                            } else {

                                Toast.makeText(
                                        MainActivity.this,
                                        "Login failed: "
                                                + task.getException()
                                                .getMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        });
            }
        });

        // REGISTER
        btnRegister.setOnClickListener(view -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }
}