package www.example.littlemeraki.TimeTable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.example.littlemeraki.Dashboard.Dashboard;
import www.example.littlemeraki.R;
import www.example.littlemeraki.Students.AddStudents;
import www.example.littlemeraki.Utility.NetworkChangeListener;

public class Timetable extends AppCompatActivity {

    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    String userEmail;
    TextView course_name,mon_1,mon_2,tue_1,tue_2,wed_1,wed_2,
            thur_1,thur_2,fri_1,fri_2,sat_1,sat_2;

    AppCompatButton close_btn,update_btn;

    RecyclerView time_recview;
    Course_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail=userEmail.replaceAll("[@.-]","");
        }

        course_name=findViewById(R.id.t_course_name);
        mon_1=findViewById(R.id.mon_1);
        tue_1=findViewById(R.id.tue_1);
        wed_1=findViewById(R.id.wed_1);
        thur_1=findViewById(R.id.thur_1);
        fri_1=findViewById(R.id.fri_1);
        sat_1=findViewById(R.id.sat_1);

        mon_2=findViewById(R.id.mon_2);
        tue_2=findViewById(R.id.tue_2);
        wed_2=findViewById(R.id.wed_2);
        thur_2=findViewById(R.id.thur_2);
        fri_2=findViewById(R.id.fri_2);
        sat_2=findViewById(R.id.sat_2);

        close_btn=findViewById(R.id.back_btn);
        update_btn=findViewById(R.id.update_btn);

        time_recview=findViewById(R.id.time_recview);

        time_recview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        FirebaseRecyclerOptions<Timetable_Model> options =
                new FirebaseRecyclerOptions.Builder<Timetable_Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child(userEmail).child("fee_structure"), Timetable_Model.class)
                        .build();

        adapter = new Course_Adapter(options,course_name,mon_1,mon_2,tue_1,tue_2,
                wed_1,wed_2,thur_1,thur_2,fri_1,fri_2,sat_1,sat_2,close_btn,update_btn);
        time_recview.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        Intent activity = new Intent(Timetable.this, Dashboard.class);
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