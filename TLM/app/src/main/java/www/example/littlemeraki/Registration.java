package www.example.littlemeraki;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import www.example.littlemeraki.Dashboard.Dashboard;
import www.example.littlemeraki.Utility.NetworkChangeListener;

public class Registration extends AppCompatActivity {

    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    TextView login_tab_btn;
    EditText reg_email, reg_pass, reg_confirm_pass;
    Button signup_btn;
    ImageView reg_logo;
    FirebaseAuth fAuth;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fAuth = FirebaseAuth.getInstance();

        login_tab_btn = findViewById(R.id.login_tab_btn);
        reg_email = findViewById(R.id.reg_email);
        reg_pass = findViewById(R.id.reg_pass);
        reg_confirm_pass = findViewById(R.id.reg_confirm_pass);
        signup_btn = findViewById(R.id.signup_btn);
        progress_bar = findViewById(R.id.registration_progressBar);
        reg_logo = findViewById(R.id.reg_logo);

//  Switching to Login Page through the TextView
        login_tab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(Registration.this, Login.class);
                startActivity(login);
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_right);
            }
        });


        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String temp_email = reg_email.getText().toString().trim();
                final String temp_password = reg_pass.getText().toString().trim();
                final String temp_cpassword = reg_confirm_pass.getText().toString().trim();

//  Validating the EditText Views.
                if (temp_email.isEmpty() && temp_password.isEmpty() && temp_cpassword.isEmpty()) {
                    Toast.makeText(Registration.this, "Fields are Empty", Toast.LENGTH_SHORT).show();
                } else if (temp_email.isEmpty()) {
                    reg_email.setError("Please Enter the Email.");
                    reg_email.requestFocus();
                } else if (!temp_email.matches("[a-z0-9]+@[a-z]+\\.[a-z]{2,3}")) {
                    reg_email.setError("Enter a Valid Email Address.");
                    reg_email.requestFocus();
                } else if (temp_password.isEmpty()) {
                    reg_pass.setError("Please Enter the Password.");
                    reg_pass.requestFocus();
                } else if (temp_password.length() <= 7) {
                    reg_pass.setError("Password must be of 8 or more Characters.");
                    reg_pass.requestFocus();
                } else if (temp_cpassword.isEmpty()) {
                    reg_confirm_pass.setError("Re-enter the Same Password");
                    reg_confirm_pass.requestFocus();
                } else if ((!temp_password.matches(temp_cpassword))) {
                    reg_confirm_pass.setError("Password Doesn't Match");
                    reg_confirm_pass.requestFocus();
                } else {
                    progress_bar.setVisibility(View.VISIBLE);

//  Creating a new account for the User
                    fAuth.createUserWithEmailAndPassword(temp_email, temp_cpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Registration.this, "Welcome...", Toast.LENGTH_SHORT).show();
                                Intent dash = new Intent(Registration.this, Dashboard.class);
                                startActivity(dash);
                                finish();
                                progress_bar.setVisibility(View.GONE);
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Registration.this, "User Already Exist with this Email.", Toast.LENGTH_SHORT).show();
                                progress_bar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(Registration.this, "Something went Wrong, Please Try Again.", Toast.LENGTH_SHORT).show();
                                progress_bar.setVisibility(View.GONE);
                            }
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

    //  Switching to Login Page on Back Press
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}