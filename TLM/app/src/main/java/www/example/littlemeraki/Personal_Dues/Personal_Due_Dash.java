package www.example.littlemeraki.Personal_Dues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.example.littlemeraki.All_Dues.Due_Dates;
import www.example.littlemeraki.Course_Division.Student_Info;
import www.example.littlemeraki.R;
import www.example.littlemeraki.Utility.NetworkChangeListener;

public class Personal_Due_Dash extends AppCompatActivity {

    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    TextView name, course, fee, contact, due_date, total_fee_amount;
    FloatingActionButton add_paid_btn, roll_back_btn;
    String userEmail, tmp_course, tmp_username, backKey;
    RecyclerView due_recview;
    Personal_Due_Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_due);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail = userEmail.replaceAll("[@.-]", "");
        }

        name = findViewById(R.id.due_card_name);
        course = findViewById(R.id.due_card_course);
        fee = findViewById(R.id.due_card_fee);
        contact = findViewById(R.id.due_card_contact);
        due_date = findViewById(R.id.due_card_date);
        total_fee_amount = findViewById(R.id.total_fee_amount);
        add_paid_btn = findViewById(R.id.float_add_paid_fee_btn);
        roll_back_btn = findViewById(R.id.float_undo_btn);
        due_recview = findViewById(R.id.due_card_recview);

        name.setText(getIntent().getStringExtra("name"));
        course.setText(getIntent().getStringExtra("course"));
        fee.setText(getIntent().getStringExtra("fee"));
        contact.setText(getIntent().getStringExtra("contact"));
        due_date.setText(getIntent().getStringExtra("due_date"));

        backKey = getIntent().getStringExtra("backKey");
        tmp_course = getIntent().getStringExtra("course");
        tmp_username = (name.getText().toString().trim() + contact.getText().toString().trim()).toLowerCase();

        DatabaseReference rec_db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).
                child("paid_due").child(course.getText().toString()).child(tmp_username);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(Personal_Due_Dash.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        due_recview.setLayoutManager(mLayoutManager);
        FirebaseRecyclerOptions<Personal_Due_Model> options =
                new FirebaseRecyclerOptions.Builder<Personal_Due_Model>()
                        .setQuery(rec_db_ref, Personal_Due_Model.class)
                        .build();

        adapter = new Personal_Due_Adapter(options, due_date, tmp_course, tmp_username);
        due_recview.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child(userEmail).child("paid_due").
                child(course.getText().toString()).child(tmp_username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                for (DataSnapshot ds :snapshot.getChildren()){
                    if (ds.child("due_fee").exists()){
                        int value= Integer.parseInt(String.valueOf(ds.child("due_fee").getValue()));
                        if (value!=0){
                            total+=value;
                        }
                    }
                }
                total_fee_amount.setText("Rs. "+String.valueOf(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add_paid_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPlus dialogPlus = DialogPlus.newDialog(Personal_Due_Dash.this)
                        .setContentHolder(new ViewHolder(R.layout.add_paid_fee_dialogue))
                        .setBackgroundColorResId(R.color.transparent)
                        .setExpanded(true, 1400)
                        .create();

                View myView = dialogPlus.getHolderView();
                TextView dialog_due_date = myView.findViewById(R.id.add_duedate);
                TextView dialog_fee = myView.findViewById(R.id.add_due_fee);
                TextView dialog_paid_date = myView.findViewById(R.id.add_paid_date);
                androidx.appcompat.widget.AppCompatButton dialog_paid_btn = myView.findViewById(R.id.paid_submit_btn);

                dialog_due_date.setText(due_date.getText().toString());
                dialog_fee.setText(fee.getText().toString());

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int date = calendar.get(Calendar.DATE);

                dialog_paid_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dialog_paid_date.setText(dayOfMonth + " / " + (month + 1) + " / " + year);
                            }
                        };
                        DatePickerDialog datePickerDialog = null;

                        datePickerDialog = new DatePickerDialog(Personal_Due_Dash.this, dateSetListener, year, month, date);
                        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                        datePickerDialog.show();
                    }
                });

                dialogPlus.show();

                dialog_paid_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tmp_due_date = dialog_due_date.getText().toString();
                        String tmp_due_fee = dialog_fee.getText().toString().trim();
                        String tmp_paid_date = dialog_paid_date.getText().toString().trim();

                        Long tsLong = System.currentTimeMillis() / 1000;
                        String ts = tsLong.toString();
//                        String due_date_node = tmp_due_date.replaceAll(" / ", "");

                        if (!tmp_paid_date.isEmpty()) {
                            DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).
                                    child("paid_due").child(tmp_course).child(tmp_username);
                            Map<String, Object> map = new HashMap<>();
                            map.put("due_date", tmp_due_date);
                            map.put("due_fee", tmp_due_fee);
                            map.put("paid_date", tmp_paid_date);

                            db_ref.child(ts).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Personal_Due_Dash.this, "Recorded!", Toast.LENGTH_SHORT).show();
                                    dialogPlus.dismiss();
                                }
                            });

                            FirebaseDatabase.getInstance().getReference().child(userEmail).child("students").child(tmp_course).
                                    child(tmp_username).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String j_date = String.valueOf(snapshot.child("joining_date").getValue());
                                            String[] nj_date = j_date.split(" / ");
                                            String join_date = nj_date[0];

                                            int due_month;
                                            int new_date;
                                            String[] due_date = tmp_due_date.split(" / ");
                                            int date = Integer.parseInt(nj_date[0]);
                                            int month = Integer.parseInt(due_date[1]);
                                            int year = Integer.parseInt(due_date[2]);

                                            if (month == 12) {
                                                new_date = date;
                                                due_month = 1;
                                                year = year + 1;
                                            } else {
                                                new_date = date;
                                                due_month = month + 1;
                                                year = year;
                                            }
                                            if (due_month == 2 && (date == 29 || date == 30 || date == 31)) {
                                                new_date = 28;
                                            } else {
                                                new_date = date;
                                            }


                                            String tmp_final_due_date = new_date + " / " + due_month + " / " + year;

                                            Map<String, Object> map2 = new HashMap<>();
                                            map2.put("due_date", tmp_final_due_date);

                                            FirebaseDatabase.getInstance().getReference().child(userEmail).
                                                    child("students").child(tmp_course).child(tmp_username).updateChildren(map2);

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        } else {
                            Toast.makeText(Personal_Due_Dash.this, "Please Set the Paid Date.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        roll_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Personal_Due_Dash.this);
                builder.setTitle("Rollback Warning !!!");
                builder.setMessage("Rollback will deleting the Latest Fee Paid Record.");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).
                                child("paid_due").child(tmp_course).child(tmp_username);

                        db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                List<String> itemList = new ArrayList<>();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String item = (String) ds.child("due_date").getValue();
                                    itemList.add(item);
                                }

                                if (!itemList.isEmpty()) {
                                    int position = itemList.size() - 1;
                                    String itemName = itemList.get(position);

                                    final String[] node_value = new String[1];
                                    final boolean[] found = {false};
                                    db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                String new_due_date = String.valueOf(ds.child("due_date").getValue());
                                                if (itemName.equals(new_due_date)) {
                                                    node_value[0] = ds.getKey();
                                                    found[0] = true;
                                                    break;

                                                }
                                            }
                                            if (found[0]) {
                                                db_ref.child(node_value[0]).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(Personal_Due_Dash.this, "Record Deleted Successfully !", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(Personal_Due_Dash.this, "Not Found", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    FirebaseDatabase.getInstance().getReference().child(userEmail).child("students").
                                            child(tmp_course).child(tmp_username).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String tmp_student_due_date = String.valueOf(snapshot.child("due_date").getValue());
                                                    String tmp_student_join_date = String.valueOf(snapshot.child("joining_date").getValue());

                                                    String[] current_student_due = tmp_student_due_date.split(" / ");
                                                    String[] joining_date = tmp_student_join_date.split(" / ");

                                                    int cs_due_date = Integer.parseInt(joining_date[0]);
                                                    int cs_due_month = Integer.parseInt(current_student_due[1]);
                                                    int cs_due_year = Integer.parseInt(current_student_due[2]);

                                                    int date;
                                                    int month;
                                                    int year;

                                                    if (cs_due_month == 1) {
                                                        date = cs_due_date;
                                                        month = 12;
                                                        year = cs_due_year - 1;
                                                    } else {
                                                        date = cs_due_date;
                                                        month = cs_due_month - 1;
                                                        year = cs_due_year;
                                                    }
                                                    if (cs_due_month == 3 && (cs_due_date == 29 || cs_due_date == 30 || cs_due_date == 31)) {
                                                        date = 28;
                                                    } else {
                                                        date = cs_due_date;
                                                    }


                                                    String last_due = date + " / " + month + " / " + year;

                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("due_date", last_due);

                                                    FirebaseDatabase.getInstance().getReference().child(userEmail).child("students").
                                                            child(tmp_course).child(tmp_username).updateChildren(map);

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                } else {
                                    Toast.makeText(Personal_Due_Dash.this, "Nothing to Rollback...", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(Personal_Due_Dash.this, "Fee Paid Record isn't Deleted.", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();

            }
        });
    }

    @Override
    public void onBackPressed() {

        if (backKey.equals("From_Student_Card")){
            Intent intent = new Intent(Personal_Due_Dash.this, Student_Info.class);
            intent.putExtra("course",tmp_course);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (backKey.equals("From_Dues")) {
            Intent intent = new Intent(Personal_Due_Dash.this, Due_Dates.class);
            startActivity(intent);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();

        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}