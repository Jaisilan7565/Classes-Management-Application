package www.example.littlemeraki.Course_Division;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import www.example.littlemeraki.Dashboard.Dashboard;
import www.example.littlemeraki.Fee.FeeModel;
import www.example.littlemeraki.Fee.feeAdapter;
import www.example.littlemeraki.R;
import www.example.littlemeraki.Students.AddStudents;
import www.example.littlemeraki.Utility.NetworkChangeListener;

public class Student_Info extends AppCompatActivity {

    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    String course_key, userEmail;
    TextView course_title;
    RecyclerView recview;
    LottieAnimationView no_data;
    List<String> itemList;
    StudentInfo_Adapter adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail=userEmail.replaceAll("[@.-]","");
        }

        course_key = getIntent().getStringExtra("course");
        toolbar=findViewById(R.id.course_title);
        toolbar.inflateMenu(R.menu.searchmenu);
        setSupportActionBar(toolbar);
        setTitle(course_key);

        recview = findViewById(R.id.student_info_recview);
        no_data=findViewById(R.id.no_students);

        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).child("students").child(course_key);
        recview.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<StudentInfo_Model> options =
                new FirebaseRecyclerOptions.Builder<StudentInfo_Model>()
                        .setQuery(db_ref, StudentInfo_Model.class)
                        .build();

        adapter = new StudentInfo_Adapter(options);
        recview.setAdapter(adapter);

        db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String item = (String) snapshot.child("name").getValue();
                    itemList.add(item);
                }
                adapter.notifyDataSetChanged();
                if (itemList.isEmpty()){
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent activity = new Intent(Student_Info.this, Dashboard.class);
        startActivity(activity);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.searchmenu,menu);

        MenuItem item=menu.findItem(R.id.search);

        SearchView searchView=(SearchView)item.getActionView();

        searchView.setQueryHint("Search by Name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String s) {

                processsearch(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processsearch(s);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void processsearch(String s)
    {
        FirebaseRecyclerOptions<StudentInfo_Model> options =
                new FirebaseRecyclerOptions.Builder<StudentInfo_Model>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child(userEmail).child("students")
                                .child(course_key).orderByChild("name").startAt(s).endAt(s+"\uf8ff"), StudentInfo_Model.class)
                        .build();

        adapter=new StudentInfo_Adapter(options);
        adapter.startListening();
        recview.setAdapter(adapter);
    }

}