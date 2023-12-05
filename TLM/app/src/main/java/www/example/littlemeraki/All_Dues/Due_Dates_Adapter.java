package www.example.littlemeraki.All_Dues;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import www.example.littlemeraki.Course_Division.StudentInfo_Model;
import www.example.littlemeraki.Personal_Dues.Personal_Due_Dash;
import www.example.littlemeraki.R;

public class Due_Dates_Adapter extends RecyclerView.Adapter<Due_Dates_Adapter.ViewHolder> {

    private List<StudentInfo_Model> dataList;
    String userEmail, btn_visible, username;

    public Due_Dates_Adapter(List<StudentInfo_Model> studentsDataList, String btn_visible) {
        this.dataList = studentsDataList;
        this.btn_visible = btn_visible;
    }


    @NonNull
    @Override
    public Due_Dates_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_due_row, parent, false);
        return new Due_Dates_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Due_Dates_Adapter.ViewHolder holder, int position) {
        StudentInfo_Model item = dataList.get(position);

        if (btn_visible.equals("current") || btn_visible.equals("passed")) {
            holder.reminder_btn.setVisibility(View.VISIBLE);
        } else if (btn_visible.equals("upcoming")) {
            holder.reminder_btn.setVisibility(View.GONE);
        }

//        username = (item.getName() + item.getContact()).toLowerCase();

        holder.name.setText(item.getName());
        holder.course.setText(item.getCourse());
        holder.due_date.setText(item.getDue_date());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail = userEmail.replaceAll("[@.-]", "");
        }

        holder.reminder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference db_reminder = FirebaseDatabase.getInstance().getReference().child(userEmail).child("students")
                        .child(item.getCourse()).child((item.getName() + item.getContact()).toLowerCase());
                db_reminder.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String contact = snapshot.child("contact").getValue(String.class);
                        FirebaseDatabase.getInstance().getReference().child(userEmail).child("due_message").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (btn_visible.equals("current")) {

                                        String current_msg = snapshot.child("current_msg").getValue(String.class);
                                        String due_date = item.getDue_date().replaceAll(" ", "");
                                        String message = "\uD83D\uDED1 Reminder! \nToday \"" + due_date + "\" is the Due Date for Fee Payment. \n" + current_msg;
                                        String url = "https://wa.me/" + contact + "?text=" + Uri.encode(message);

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(url));
                                        holder.itemView.getContext().startActivity(intent);
                                    } else if (btn_visible.equals("passed")) {

                                        String passed_msg = snapshot.child("passed_msg").getValue(String.class);
                                        String due_date = item.getDue_date().replaceAll(" ", "");
                                        String message = "\uD83D\uDED1 Reminder! \n\"" + due_date + "\" was your Due Date for Fee Payment. \n" + passed_msg;
                                        String url = "https://wa.me/" + contact + "?text=" + Uri.encode(message);

                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(url));
                                        holder.itemView.getContext().startActivity(intent);
                                    }
                                } else {
                                    Intent intent = new Intent(holder.itemView.getContext(), Custom_Due_Message.class);
                                    holder.itemView.getContext().startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).child("students").
                        child(holder.course.getText().toString()).child((item.getName() + item.getContact()).toLowerCase());

                db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        String course = snapshot.child("course").getValue(String.class);
                        String fee = snapshot.child("fee").getValue(String.class);
                        String contact = snapshot.child("contact").getValue(String.class);
                        String due_date = snapshot.child("due_date").getValue(String.class);

                        Intent intent = new Intent(holder.itemView.getContext(), Personal_Due_Dash.class);
                        intent.putExtra("name", name);
                        intent.putExtra("course", course);
                        intent.putExtra("fee", fee);
                        intent.putExtra("contact", contact);
                        intent.putExtra("due_date", due_date);
                        intent.putExtra("backKey", "From_Dues");
                        holder.itemView.getContext().startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, course, due_date;
        AppCompatButton reminder_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.student_name);
            course = itemView.findViewById(R.id.student_course);
            due_date = itemView.findViewById(R.id.student_due_date);
            reminder_btn = itemView.findViewById(R.id.reminder_btn);
        }
    }

}
