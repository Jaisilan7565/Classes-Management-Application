package www.example.littlemeraki.Fee;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.example.littlemeraki.Dashboard.Dashboard;
import www.example.littlemeraki.Login;
import www.example.littlemeraki.R;
import www.example.littlemeraki.Students.AddStudents;
import www.example.littlemeraki.Utility.NetworkChangeListener;

public class Fee_Structure extends AppCompatActivity {

    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    RecyclerView recview;
    feeAdapter adapter;
    FloatingActionButton add_btn;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_structure);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail=userEmail.replaceAll("[@.-]","");
        }

        add_btn = findViewById(R.id.float_add_btn);

        recview = findViewById(R.id.recview);
        recview.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<FeeModel> options =
                new FirebaseRecyclerOptions.Builder<FeeModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child(userEmail).child("fee_structure"), FeeModel.class)
                        .build();

        adapter = new feeAdapter(options);
        recview.setAdapter(adapter);


        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogPlus dialogPlus = DialogPlus.newDialog(Fee_Structure.this)
                        .setContentHolder(new ViewHolder(R.layout.add_fee))
                        .setBackgroundColorResId(R.color.transparent)
                        .setExpanded(true, 1000)
                        .create();

                View myView = dialogPlus.getHolderView();
                Spinner course = myView.findViewById(R.id.course);
                EditText class_name = myView.findViewById(R.id.class_name);
                EditText fee_amount = myView.findViewById(R.id.fee_amount);
                Button add_fee = myView.findViewById(R.id.add_fee_btn);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter
                        .createFromResource(getApplicationContext(), R.array.education_level, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                course.setAdapter(adapter);
                dialogPlus.show();

                add_fee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String new_tmp_course="";
                        final String tmp_course = String.valueOf(course.getSelectedItem());
                        final String tmp_class_name = class_name.getText().toString().trim();
                        final String tmp_fee_amount = fee_amount.getText().toString().trim();

                        if (tmp_course.matches("School")){
                            new_tmp_course="STD "+tmp_class_name;
                        } else if (tmp_course.matches("College")) {
                            new_tmp_course=tmp_class_name.toUpperCase();
                        }

                        if (tmp_class_name.isEmpty()) {
                            Toast.makeText(Fee_Structure.this, "Field can't be Empty.", Toast.LENGTH_SHORT).show();
                            class_name.requestFocus();
                        } else if (!tmp_class_name.matches("^[a-zA-Z0-9]+$")) {
                            Toast.makeText(Fee_Structure.this, "Field can't contain Special Characters.", Toast.LENGTH_SHORT).show();
                            class_name.requestFocus();
                        } else if (tmp_fee_amount.isEmpty()) {
                            Toast.makeText(Fee_Structure.this, "Please enter the Amount.", Toast.LENGTH_SHORT).show();
                            fee_amount.requestFocus();
                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("course", new_tmp_course);
                            map.put("fee", tmp_fee_amount);

                            DatabaseReference db_ref=FirebaseDatabase.getInstance().getReference().child(userEmail).child("fee_structure");

                            String finalNew_tmp_course = new_tmp_course;
                            db_ref.child(new_tmp_course).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        Toast.makeText(Fee_Structure.this, "Fee Data Already Exist.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        db_ref.child(finalNew_tmp_course).updateChildren(map)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Fee_Structure.this, "Saved Successfully.", Toast.LENGTH_SHORT).show();
                                                        dialogPlus.dismiss();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(Fee_Structure.this, "Something went wrong.", Toast.LENGTH_SHORT).show();
                                                        dialogPlus.dismiss();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

//                            hideKeyboard(v);
                            }
                        }

                    });
                }

        });

    }

    @Override
    public void onBackPressed() {
        Intent activity = new Intent(Fee_Structure.this, Dashboard.class);
        startActivity(activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);

        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}