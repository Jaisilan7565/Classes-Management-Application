package www.example.littlemeraki.Students;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.example.littlemeraki.Dashboard.Dashboard;
import www.example.littlemeraki.Login;
import www.example.littlemeraki.R;
import www.example.littlemeraki.Utility.NetworkChangeListener;

public class AddStudents extends AppCompatActivity {

    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    EditText s_name, institute, fee, contact;
    TextView j_date;
    Spinner s_class;
    AppCompatButton discard, submit;
    String selectedItem, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students);

        s_name=findViewById(R.id.add_s_name);
        institute=findViewById(R.id.add_s_institute);
        s_class=findViewById(R.id.add_s_class);
        fee=findViewById(R.id.add_s_fee);
        contact=findViewById(R.id.add_s_contact);
        j_date=findViewById(R.id.add_s_date);

        discard=findViewById(R.id.discard_btn);
        submit=findViewById(R.id.submit_btn);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail=userEmail.replaceAll("[@.-]","");
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(userEmail).child("fee_structure");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> itemList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String item = (String) snapshot.child("course").getValue();
                    itemList.add(item);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddStudents.this, android.R.layout.simple_spinner_item, itemList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s_class.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        s_class.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = (String) parent.getItemAtPosition(position);
                databaseReference.child(selectedItem).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String amount= (String) snapshot.child("fee").getValue();
                        fee.setText(amount);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Calendar calendar= Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int date=calendar.get(Calendar.DATE);

        j_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener =new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        j_date.setText(dayOfMonth+" / "+(month+1)+" / "+year);
                    }
                };
                DatePickerDialog datePickerDialog = null;

                datePickerDialog = new DatePickerDialog(AddStudents.this, dateSetListener, year, month, date);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                datePickerDialog.show();
            }
        });

        discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tmp_s_name=s_name.getText().toString().trim();
                String tmp_institute=institute.getText().toString().trim();
                String tmp_contact=contact.getText().toString().trim();
                String tmp_j_date=j_date.getText().toString().trim();

                if(!tmp_s_name.isEmpty() || !tmp_institute.isEmpty() ||  !tmp_contact.isEmpty() || !tmp_j_date.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddStudents.this);
                    builder.setMessage("Do you want to Discard?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(AddStudents.this, "Discarded Successfully.", Toast.LENGTH_SHORT).show();
                                    Intent activity = new Intent(AddStudents.this, Dashboard.class);
                                    startActivity(activity);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Intent activity = new Intent(AddStudents.this, Dashboard.class);
                    startActivity(activity);
                }
            }
        });
        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp_s_name=s_name.getText().toString().trim();
                String tmp_institute=institute.getText().toString().trim();
                String tmp_course=selectedItem;
                String tmp_fee=fee.getText().toString().trim();
                String tmp_contact=contact.getText().toString().trim();
                String tmp_j_date=j_date.getText().toString().trim();
                String new_username=(tmp_s_name+tmp_contact).toLowerCase();
                
                if (tmp_s_name.isEmpty() || tmp_s_name.length() < 3) {
                    s_name.setError("Enter a Valid Name.");
                    s_name.requestFocus();
                } else if (tmp_institute.isEmpty()) {
                    institute.setError("Feild can't be Empty.");
                    institute.requestFocus();
                } else if (tmp_contact.isEmpty()) {
                    contact.setError("Contact Can't be Empty.");
                    contact.requestFocus();
                } else if (tmp_fee.isEmpty()) {
                    Toast.makeText(AddStudents.this, "Fee Can't be Empty.", Toast.LENGTH_SHORT).show();
                } else if (tmp_j_date.isEmpty()) {
                    Toast.makeText(AddStudents.this, "Please Select the Joining Date.", Toast.LENGTH_SHORT).show();
                } else if (!tmp_s_name.matches("^[a-zA-Z ]*$")) {
                    s_name.setError("Only Alphabets Allowed.");
                    s_name.requestFocus();
                } else if (!tmp_institute.matches("^[a-zA-Z ]*$")) {
                    institute.setError("Only Alphabets Allowed.");
                    institute.requestFocus();
                } else if (tmp_contact.length() != 10 || !tmp_contact.matches("^[6789]\\d{9}$")){
                    contact.setError("Enter a Valid Mobile Number.");
                    contact.requestFocus();
                } else if (!tmp_s_name.isEmpty() && !tmp_contact.isEmpty() && !tmp_fee.isEmpty() &&
                        !tmp_institute.isEmpty() && !tmp_j_date.isEmpty()) {
                    DatabaseReference db_ref=FirebaseDatabase.getInstance().getReference().child(userEmail).
                    child("students");

                    int due_month;
                    int new_date;
                    String[] due_date=tmp_j_date.split(" / ");
                    int date=Integer.parseInt(due_date[0]);
                    int month= Integer.parseInt(due_date[1]);
                    int year= Integer.parseInt(due_date[2]);
                    if (month==12){
                        new_date= date;
                        due_month=1;
                        year=year+1;
                    }else {
                        new_date=date;
                        due_month = month + 1;
                        year= year;
                    }
                    if (due_month==2 && (date==29 || date==30 || date==31)){
                        new_date=28;
                    }

                    String tmp_final_due_date=new_date+" / "+due_month+" / "+year;

                    Map<String, Object> map = new HashMap<>();
                    map.put("name",tmp_s_name);
                    map.put("institute",tmp_institute);
                    map.put("course",tmp_course);
                    map.put("fee",tmp_fee);
                    map.put("contact",tmp_contact);
                    map.put("joining_date",tmp_j_date);
                    map.put("due_date",tmp_final_due_date);

                    db_ref.child(tmp_course).child(new_username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                Toast.makeText(AddStudents.this, "Student Already Exist.", Toast.LENGTH_SHORT).show();
                            } else {
                                db_ref.child(tmp_course).child(new_username).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(AddStudents.this, "Submitted Successfully.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AddStudents.this,Dashboard.class);
                                            startActivity(intent);
                                        }
                                    });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                } 

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent activity = new Intent(AddStudents.this, Dashboard.class);
        startActivity(activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
    }

}