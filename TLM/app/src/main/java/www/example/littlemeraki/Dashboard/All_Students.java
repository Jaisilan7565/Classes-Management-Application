package www.example.littlemeraki.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import www.example.littlemeraki.Course_Division.StudentInfo_Adapter;
import www.example.littlemeraki.Course_Division.StudentInfo_Model;
import www.example.littlemeraki.Course_Division.Student_Info;
import www.example.littlemeraki.R;
import www.example.littlemeraki.Utility.NetworkChangeListener;

public class All_Students extends AppCompatActivity {

    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    String userEmail;
    int no_students=0;
    LottieAnimationView no_data;
    List<String> itemList;
    ListView listView;
    TextView toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_students);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail=userEmail.replaceAll("[@.-]","");
        }

        toolbar=findViewById(R.id.all_students_title);

        listView =findViewById(R.id.all_students_list);
        no_data=findViewById(R.id.no_all_students);

        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).child("students");

        db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemList = new ArrayList<>();
                for (DataSnapshot course : dataSnapshot.getChildren()) {
                    for (DataSnapshot student : course.getChildren()){
                        String name = (String) student.child("name").getValue();
                        String sclass=(String) student.child("course").getValue();
                        String item= sclass + " ---> " + name;
                        itemList.add(item);
                        no_students++;
                    }
                }
//                adapter.notifyDataSetChanged();
                if (itemList.isEmpty()){
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);

                    ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(All_Students.this, R.layout.name_row, R.id.classNname, itemList);
                    listView.setAdapter(arrayAdapter);

                }

                if (no_students==0){
                    Toast.makeText(All_Students.this, "No Students to Show", Toast.LENGTH_SHORT).show();
                }else {
                    toolbar.setText("Total Students: "+ no_students);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onBackPressed() {
        Intent activity = new Intent(All_Students.this, Dashboard.class);
        startActivity(activity);
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}