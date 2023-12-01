package www.example.littlemeraki.TimeTable;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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

public class Course_Adapter extends FirebaseRecyclerAdapter<Timetable_Model, Course_Adapter.TimetableViewHolder> {

    private TextView course_name, mon_1, mon_2, tue_1, tue_2, wed_1, wed_2,
            thur_1, thur_2, fri_1, fri_2, sat_1, sat_2;
    AppCompatButton close_btn, update_btn;
    String userEmail, tmp_course_name, tmp_mon1, tmp_mon2, tmp_tue1, tmp_tue2, tmp_wed1, tmp_wed2,
            tmp_thur1, tmp_thur2, tmp_fri1, tmp_fri2, tmp_sat1, tmp_sat2;

    public Course_Adapter(@NonNull FirebaseRecyclerOptions<Timetable_Model> options, TextView course_name, TextView mon_1, TextView mon_2,
                          TextView tue_1, TextView tue_2, TextView wed_1, TextView wed_2, TextView thur_1, TextView thur_2, TextView fri_1,
                          TextView fri_2, TextView sat_1, TextView sat_2, AppCompatButton close_btn, AppCompatButton update_btn) {
        super(options);
        this.course_name = course_name;
        this.mon_1 = mon_1;
        this.mon_2 = mon_2;
        this.tue_1 = tue_1;
        this.tue_2 = tue_2;
        this.wed_1 = wed_1;
        this.wed_2 = wed_2;
        this.thur_1 = thur_1;
        this.thur_2 = thur_2;
        this.fri_1 = fri_1;
        this.fri_2 = fri_2;
        this.sat_1 = sat_1;
        this.sat_2 = sat_2;
        this.close_btn = close_btn;
        this.update_btn = update_btn;

    }

    private void updateTextViewText(String newText) {
        course_name.setText(newText);
    }

    void setSubjects(TextView week, String subject) {
        week.setText(subject.isEmpty() ? "-" : subject);
    }

    private void updateTimetable(String tmp_course_name) {
        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).
                child("timetable").child(tmp_course_name);

        db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("mon_1", "");
                    map.put("mon_2", "");
                    map.put("tue_1", "");
                    map.put("tue_2", "");
                    map.put("wed_1", "");
                    map.put("wed_2", "");
                    map.put("thur_1", "");
                    map.put("thur_2", "");
                    map.put("fri_1", "");
                    map.put("fri_2", "");
                    map.put("sat_1", "");
                    map.put("sat_2", "");

                    db_ref.updateChildren(map);
                    db_ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                tmp_mon1 = snapshot.child("mon_1").getValue().toString().trim();
                                tmp_mon2 = snapshot.child("mon_2").getValue().toString().trim();
                                tmp_tue1 = snapshot.child("tue_1").getValue().toString().trim();
                                tmp_tue2 = snapshot.child("tue_2").getValue().toString().trim();
                                tmp_wed1 = snapshot.child("wed_1").getValue().toString().trim();
                                tmp_wed2 = snapshot.child("wed_2").getValue().toString().trim();
                                tmp_thur1 = snapshot.child("thur_1").getValue().toString().trim();
                                tmp_thur2 = snapshot.child("thur_2").getValue().toString().trim();
                                tmp_fri1 = snapshot.child("fri_1").getValue().toString().trim();
                                tmp_fri2 = snapshot.child("fri_2").getValue().toString().trim();
                                tmp_sat1 = snapshot.child("sat_1").getValue().toString().trim();
                                tmp_sat2 = snapshot.child("sat_2").getValue().toString().trim();

                                setSubjects(mon_1, tmp_mon1);
                                setSubjects(mon_2, tmp_mon2);
                                setSubjects(tue_1, tmp_tue1);
                                setSubjects(tue_2, tmp_tue2);
                                setSubjects(wed_1, tmp_wed1);
                                setSubjects(wed_2, tmp_wed2);
                                setSubjects(thur_1, tmp_thur1);
                                setSubjects(thur_2, tmp_thur2);
                                setSubjects(fri_1, tmp_fri1);
                                setSubjects(fri_2, tmp_fri2);
                                setSubjects(sat_1, tmp_sat1);
                                setSubjects(sat_2, tmp_sat2);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    tmp_mon1 = snapshot.child("mon_1").getValue().toString().trim();
                    tmp_mon2 = snapshot.child("mon_2").getValue().toString().trim();
                    tmp_tue1 = snapshot.child("tue_1").getValue().toString().trim();
                    tmp_tue2 = snapshot.child("tue_2").getValue().toString().trim();
                    tmp_wed1 = snapshot.child("wed_1").getValue().toString().trim();
                    tmp_wed2 = snapshot.child("wed_2").getValue().toString().trim();
                    tmp_thur1 = snapshot.child("thur_1").getValue().toString().trim();
                    tmp_thur2 = snapshot.child("thur_2").getValue().toString().trim();
                    tmp_fri1 = snapshot.child("fri_1").getValue().toString().trim();
                    tmp_fri2 = snapshot.child("fri_2").getValue().toString().trim();
                    tmp_sat1 = snapshot.child("sat_1").getValue().toString().trim();
                    tmp_sat2 = snapshot.child("sat_2").getValue().toString().trim();

                    setSubjects(mon_1, tmp_mon1);
                    setSubjects(mon_2, tmp_mon2);
                    setSubjects(tue_1, tmp_tue1);
                    setSubjects(tue_2, tmp_tue2);
                    setSubjects(wed_1, tmp_wed1);
                    setSubjects(wed_2, tmp_wed2);
                    setSubjects(thur_1, tmp_thur1);
                    setSubjects(thur_2, tmp_thur2);
                    setSubjects(fri_1, tmp_fri1);
                    setSubjects(fri_2, tmp_fri2);
                    setSubjects(sat_1, tmp_sat1);
                    setSubjects(sat_2, tmp_sat2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onBindViewHolder(@NonNull Course_Adapter.TimetableViewHolder holder, int position, @NonNull Timetable_Model model) {
        holder.course.setText(model.getCourse());

        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().
                child(userEmail).child("fee_structure");
        if (course_name.getText().toString().trim().equals("Course Name")) {
            db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<String> itemList = new ArrayList<>();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String item = (String) ds.child("course").getValue();
                        itemList.add(item);
                    }
                    updateTextViewText(itemList.get(0));
                    updateTimetable(itemList.get(0));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        holder.course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTextViewText(model.getCourse());
                updateTimetable(model.getCourse());
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(v.getContext(), Dashboard.class);
                v.getContext().startActivity(back);
            }
        });

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final DialogPlus dialogPlus = DialogPlus.newDialog(v.getContext())
                        .setContentHolder(new ViewHolder(R.layout.timetable_update))
                        .setExpanded(true, 1300)
                        .setBackgroundColorResId(R.color.trans_white).create();

                View myview = dialogPlus.getHolderView();
                EditText mon_1 = myview.findViewById(R.id.u_mon_1);
                EditText mon_2 = myview.findViewById(R.id.u_mon_2);
                EditText tue_1 = myview.findViewById(R.id.u_tue_1);
                EditText tue_2 = myview.findViewById(R.id.u_tue_2);
                EditText wed_1 = myview.findViewById(R.id.u_wed_1);
                EditText wed_2 = myview.findViewById(R.id.u_wed_2);
                EditText thur_1 = myview.findViewById(R.id.u_thur_1);
                EditText thur_2 = myview.findViewById(R.id.u_thur_2);
                EditText fri_1 = myview.findViewById(R.id.u_fri_1);
                EditText fri_2 = myview.findViewById(R.id.u_fri_2);
                EditText sat_1 = myview.findViewById(R.id.u_sat_1);
                EditText sat_2 = myview.findViewById(R.id.u_sat_2);
                AppCompatButton update_btn = myview.findViewById(R.id.u_update_btn);

                DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).
                        child("timetable").child(course_name.getText().toString());
                db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String tmp_mon1 = snapshot.child("mon_1").getValue().toString().trim();
                        String tmp_mon2 = snapshot.child("mon_2").getValue().toString().trim();
                        String tmp_tue1 = snapshot.child("tue_1").getValue().toString().trim();
                        String tmp_tue2 = snapshot.child("tue_2").getValue().toString().trim();
                        String tmp_wed1 = snapshot.child("wed_1").getValue().toString().trim();
                        String tmp_wed2 = snapshot.child("wed_2").getValue().toString().trim();
                        String tmp_thur1 = snapshot.child("thur_1").getValue().toString().trim();
                        String tmp_thur2 = snapshot.child("thur_2").getValue().toString().trim();
                        String tmp_fri1 = snapshot.child("fri_1").getValue().toString().trim();
                        String tmp_fri2 = snapshot.child("fri_2").getValue().toString().trim();
                        String tmp_sat1 = snapshot.child("sat_1").getValue().toString().trim();
                        String tmp_sat2 = snapshot.child("sat_2").getValue().toString().trim();

                        mon_1.setText(tmp_mon1);
                        mon_2.setText(tmp_mon2);
                        tue_1.setText(tmp_tue1);
                        tue_2.setText(tmp_tue2);
                        wed_1.setText(tmp_wed1);
                        wed_2.setText(tmp_wed2);
                        thur_1.setText(tmp_thur1);
                        thur_2.setText(tmp_thur2);
                        fri_1.setText(tmp_fri1);
                        fri_2.setText(tmp_fri2);
                        sat_1.setText(tmp_sat1);
                        sat_2.setText(tmp_sat2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialogPlus.show();

                update_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String ms1=mon_1.getText().toString().trim();
                        String ms2=mon_2.getText().toString().trim();
                        String ts1=tue_1.getText().toString().trim();
                        String ts2=tue_2.getText().toString().trim();
                        String ws1=wed_1.getText().toString().trim();
                        String ws2=wed_2.getText().toString().trim();
                        String ths1=thur_1.getText().toString().trim();
                        String ths2=thur_2.getText().toString().trim();
                        String fs1=fri_1.getText().toString().trim();
                        String fs2=fri_2.getText().toString().trim();
                        String ss1=sat_1.getText().toString().trim();
                        String ss2=sat_2.getText().toString().trim();

                        Map<String,Object> map=new HashMap<>();
                        map.put("mon_1",ms1);
                        map.put("mon_2",ms2);
                        map.put("tue_1",ts1);
                        map.put("tue_2",ts2);
                        map.put("wed_1",ws1);
                        map.put("wed_2",ws2);
                        map.put("thur_1",ths1);
                        map.put("thur_2",ths2);
                        map.put("fri_1",fs1);
                        map.put("fri_2",fs2);
                        map.put("sat_1",ss1);
                        map.put("sat_2",ss2);

                        db_ref.updateChildren(map);
                        dialogPlus.dismiss();
                        updateTimetable(course_name.getText().toString());
                    }
                });

            }
        });

    }

    @NonNull
    @Override
    public Course_Adapter.TimetableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timetable_course_row, parent, false);
        return new Course_Adapter.TimetableViewHolder(view);
    }

    public class TimetableViewHolder extends RecyclerView.ViewHolder {
        TextView course;


        public TimetableViewHolder(@NonNull View itemView) {
            super(itemView);
            course = itemView.findViewById(R.id.timetable_course_name);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                userEmail = user.getEmail();
                userEmail = userEmail.replaceAll("[@.-]", "");
            }

        }
    }
}
