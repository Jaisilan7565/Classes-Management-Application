package www.example.littlemeraki.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import www.example.littlemeraki.Fee.FeeModel;
import www.example.littlemeraki.Fee.Fee_Structure;
import www.example.littlemeraki.All_Dues.Due_Dates;
import www.example.littlemeraki.Login;
import www.example.littlemeraki.R;
import www.example.littlemeraki.Students.AddStudents;
import www.example.littlemeraki.TimeTable.Timetable;
import www.example.littlemeraki.Utility.DueNotification;
import www.example.littlemeraki.Utility.DueNotification_Service;
import www.example.littlemeraki.Utility.NetworkChangeListener;

public class Dashboard extends AppCompatActivity {

    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    androidx.appcompat.widget.Toolbar toolbar;
    com.google.android.material.card.MaterialCardView timetable_card,
            due_date_card, fee_structure_card, add_student_card;

    RecyclerView dash_recview;
    DashAdapter adapter;
    CardView all_students;
    TextView total_students, total_revenue, noti_history;
    String userEmail;
    List<String> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        timetable_card = findViewById(R.id.timetable_card);
        add_student_card = findViewById(R.id.add_student_card);
        due_date_card = findViewById(R.id.due_date_card);
        fee_structure_card = findViewById(R.id.fee_structure_card);
        dash_recview = findViewById(R.id.dash_recview);
        total_students = findViewById(R.id.total_students);
        total_revenue = findViewById(R.id.total_revenu);
        noti_history = findViewById(R.id.notification_history);
        all_students=findViewById(R.id.all_students);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail=userEmail.replaceAll("[@.-]","");
        } else {
            Intent intent= new Intent(this,Login.class);
            startActivity(intent);
            Toast.makeText(this, "Something went Wrong.\nNo user Found.", Toast.LENGTH_SHORT).show();
        }

        fee_structure_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fee_structure = new Intent(Dashboard.this, Fee_Structure.class);
                startActivity(fee_structure);
            }
        });

        add_student_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db_ref=FirebaseDatabase.getInstance().getReference().child(userEmail)
                        .child("fee_structure");
                db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        itemList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String item = (String) snapshot.child("course").getValue();
                            itemList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                        if (itemList.isEmpty()){
                            Toast.makeText(Dashboard.this, "Cannot Add student without Fee Structure.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent add_student = new Intent(Dashboard.this, AddStudents.class);
                            startActivity(add_student);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        due_date_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent due_date = new Intent(Dashboard.this, Due_Dates.class);
                startActivity(due_date);
            }
        });

        timetable_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db_ref=FirebaseDatabase.getInstance().getReference().child(userEmail)
                        .child("fee_structure");
                db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        itemList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String item = (String) snapshot.child("course").getValue();
                            itemList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                        if (itemList.isEmpty()){
                            Toast.makeText(Dashboard.this, "Cannot Set Timetable without Fee Structure.", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent timetable = new Intent(Dashboard.this, Timetable.class);
                            startActivity(timetable);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        all_students.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent all_students = new Intent(Dashboard.this, All_Students.class);
                startActivity(all_students);
            }
        });

        noti_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notification_history = new Intent(Dashboard.this, Notification_History.class);
                startActivity(notification_history);
            }
        });

        toolbar = findViewById(R.id.dash_toolbar);
        toolbar.inflateMenu(R.menu.logout_menu);

//  Logout Action through Toolbar
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.logout_app) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
                    builder.setMessage("Do you want to Logout?").setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent_logout = new Intent(Dashboard.this, Login.class);
                                    startActivity(intent_logout);
                                    finishAffinity();
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                return false;
            }
        });

        dash_recview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        FirebaseRecyclerOptions<FeeModel> options =
                new FirebaseRecyclerOptions.Builder<FeeModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child(userEmail).child("fee_structure"), FeeModel.class)
                        .build();

        adapter = new DashAdapter(options);
        dash_recview.setAdapter(adapter);

        Intent serviceIntent = new Intent(this, DueNotification_Service.class);
        startService(serviceIntent);

        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).child("students");
        db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int no_students = 0;
                long revenu_amount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    for (DataSnapshot ds1 : ds.getChildren()) {
                        no_students++;
                        long fee = Integer.parseInt((String) ds1.child("fee").getValue());
                        revenu_amount = fee + revenu_amount;
                    }
                }
                total_students.setText(String.valueOf(no_students) + "\nStudent's");

                NumberFormat formatter = new DecimalFormat("#0,000");
                total_revenue.setText("Rs. " +
                        String.valueOf(formatter.format(revenu_amount)) + "\nPer Month");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to Exit?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        System.exit(0);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        adapter.startListening();
        adapter.notifyDataSetChanged();


        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, DueNotification_Service.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long intervalMillis = 60 * 60 * 1000; // 1 hour in milliseconds
        long initialDelayMillis = 0;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + initialDelayMillis, intervalMillis, pendingIntent);

//        Intent serviceIntent = new Intent(this, DueNotification_Service.class);
//        startService(serviceIntent);

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}