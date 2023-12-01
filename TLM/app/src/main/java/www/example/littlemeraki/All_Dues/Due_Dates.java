package www.example.littlemeraki.All_Dues;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import www.example.littlemeraki.Dashboard.Dashboard;
import www.example.littlemeraki.R;
import www.example.littlemeraki.Students.AddStudents;
import www.example.littlemeraki.Utility.NetworkChangeListener;

public class Due_Dates extends AppCompatActivity {

    NetworkChangeListener networkChangeListener=new NetworkChangeListener();
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView edit_reminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_late_due);

        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
        edit_reminder=findViewById(R.id.edit_reminder);

        tabLayout.setupWithViewPager(viewPager);

        Due_Dates_ViewPager_Adapter adapter=new Due_Dates_ViewPager_Adapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(new All_Current_Due(),"Current Dues");
        adapter.addFragment(new All_Upcoming_Due(),"Upcoming Dues");
        adapter.addFragment(new All_Passed_Due(),"Passed Dues");
        viewPager.setAdapter(adapter);

        edit_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent activity = new Intent(Due_Dates.this, Custom_Due_Message.class);
                startActivity(activity);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent activity = new Intent(Due_Dates.this, Dashboard.class);
        startActivity(activity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter=new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
    }
}