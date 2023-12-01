package www.example.littlemeraki.All_Dues;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import www.example.littlemeraki.Course_Division.StudentInfo_Model;
import www.example.littlemeraki.R;

public class All_Current_Due extends Fragment {

    RecyclerView recview;
    MaterialCardView no_data;
    String userEmail;
    Due_Dates_Adapter adapter;
    AppCompatButton reminder_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all__current__due, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail = userEmail.replaceAll("[@.-]", "");
        }

        String btn_visible="current";

        no_data = view.findViewById(R.id.no_data_current_due);
        recview = view.findViewById(R.id.all_current_due_recview);
        recview.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));

        List<StudentInfo_Model> studentsDataList = new ArrayList<>();

        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).child("students");
        db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    String course_node = ds.getKey();

                    if (course_node != null) {
                        for (DataSnapshot ds2 : ds.getChildren()) {
                            String name = ds2.child("name").getValue(String.class);
                            String course = ds2.child("course").getValue(String.class);
                            String due_date = ds2.child("due_date").getValue(String.class);
                            String contact = ds2.child("contact").getValue(String.class);

                            SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault());
                            String currentDate = sdf.format(new Date());

                            String[] CDparts=currentDate.split(" / ");
                            String CD_date=CDparts[0].trim();
                            String CD_month=CDparts[1].trim();
                            String CD_year=CDparts[2].trim();

                            int date= Integer.parseInt(CD_date);
                            int month= Integer.parseInt(CD_month);
                            int year= Integer.parseInt(CD_year);

                            String FcurrentDate=date+" / "+month+" / "+year;

                            if (FcurrentDate.equals(due_date)) {
                                studentsDataList.add(new StudentInfo_Model(name, course, due_date, contact));
                            }

                        }
                        adapter = new Due_Dates_Adapter(studentsDataList,btn_visible);
                        recview.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }

                if (studentsDataList.isEmpty()) {
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

}