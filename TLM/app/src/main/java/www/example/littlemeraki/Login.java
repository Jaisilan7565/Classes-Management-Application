package www.example.littlemeraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import www.example.littlemeraki.Dashboard.Dashboard;
import www.example.littlemeraki.Utility.NetworkChangeListener;

public class Login extends AppCompatActivity {

    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    TextView forgot_btn, signup_tab_btn;
    EditText email, password;
    Button login_btn;
    ImageView eye, login_logo;
    FirebaseAuth fAuth;
    ProgressBar login_pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        forgot_btn = findViewById(R.id.forgot_btn);
        signup_tab_btn = findViewById(R.id.signup_tab_btn);
        login_logo = findViewById(R.id.login_logo);
        login_pb=findViewById(R.id.login_progressBar);

//  Password Toggle Feature
        eye = findViewById(R.id.eye);
        eye.setImageResource(R.drawable.baseline_visibility_off);
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())) {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    eye.setImageResource(R.drawable.baseline_visibility_off);
                } else {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    eye.setImageResource(R.drawable.baseline_visibility);
                }
            }
        });

//  Intending to Registration Page
        signup_tab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(Login.this, Registration.class);
                startActivity(register);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

//  Firebase Authentication if the user is currently signed in
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            Intent dash = new Intent(Login.this, Dashboard.class);
            startActivity(dash);
            finish();
        }

//  Login Function.
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String temp_email = email.getText().toString().trim();
                final String temp_password = password.getText().toString().trim();

//  Validating the EditText Views.
                if (temp_email.isEmpty() && temp_password.isEmpty()) {
                    Toast.makeText(Login.this, "Fields are Empty", Toast.LENGTH_SHORT).show();
                } else if (temp_email.isEmpty()) {
                    email.setError("Please Enter the Email.");
                    email.requestFocus();
                } else if (!temp_email.matches("[a-z0-9]+@[a-z]+\\.[a-z]{2,3}")) {
                    email.setError("Enter a Valid Email Address.");
                    email.requestFocus();
                } else if (temp_password.isEmpty()) {
                    password.setError("Please Enter the Password.");
                    password.requestFocus();
                } else if (temp_password.length() <= 7) {
                    password.setError("Password must be of 8 or more Characters.");
                    password.requestFocus();
                } else {
                    login_pb.setVisibility(View.VISIBLE);

//  Signing in Function.
                    fAuth.signInWithEmailAndPassword(temp_email, temp_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this, "Welcome...", Toast.LENGTH_SHORT).show();
                                Intent dash = new Intent(Login.this, Dashboard.class);
                                startActivity(dash);
                                finish();
                                login_pb.setVisibility(View.GONE);
                            } else if (!task.isSuccessful()) {
                                Toast.makeText(Login.this, "Invalid Email or Password.", Toast.LENGTH_SHORT).show();
                                login_pb.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

        forgot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String temp_email = email.getText().toString().trim();

                if (temp_email.isEmpty()) {
                    Toast.makeText(getApplication(), "Enter Your Registered Email Address to Reset Password.", Toast.LENGTH_LONG).show();
                    email.setError("Please Enter the Email Address.");
                    email.requestFocus();
                }else if (!temp_email.matches("[a-z0-9]+@[a-z]+\\.[a-z]{2,3}")) {
                    Toast.makeText(getApplication(), "Enter Your Registered Email Address to Reset Password.", Toast.LENGTH_LONG).show();
                    email.setError("Enter a Valid Email Address.");
                    email.requestFocus();
                }else {
                    login_pb.setVisibility(View.VISIBLE);

                    fAuth.sendPasswordResetEmail(temp_email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Login.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Login.this, "Failed to send reset email, Enter a valid email.", Toast.LENGTH_SHORT).show();
                                    }
                                    login_pb.setVisibility(View.GONE);
                                }
                            });
                }

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
    }

    //  Confirmation for Exiting/Killing the App Entirely
    @Override
    public void onBackPressed() {
        finishAffinity();
        System.exit(0);

    }
}