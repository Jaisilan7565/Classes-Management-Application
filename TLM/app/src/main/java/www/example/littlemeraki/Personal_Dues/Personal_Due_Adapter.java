package www.example.littlemeraki.Personal_Dues;

import android.app.DatePickerDialog;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.Map;

import www.example.littlemeraki.R;

public class Personal_Due_Adapter extends FirebaseRecyclerAdapter<Personal_Due_Model, Personal_Due_Adapter.DueViewHolder> {

    TextView due_date;
    String userEmail, tmp_username, tmp_course;

    public Personal_Due_Adapter(@NonNull FirebaseRecyclerOptions<Personal_Due_Model> options, TextView due_date, String tmp_course, String tmp_username) {
        super(options);
        this.due_date = due_date;
        this.tmp_username = tmp_username;
        this.tmp_course = tmp_course;
    }

    private void updateTextViewText(String newText) {
        due_date.setText(newText);
    }

    int i = 0;

    @Override
    protected void onBindViewHolder(@NonNull Personal_Due_Adapter.DueViewHolder holder, int position, @NonNull Personal_Due_Model model) {

        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).child("students").
                child(tmp_course).child(tmp_username);

        db_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tmp_due_date = String.valueOf(snapshot.child("due_date").getValue());
                updateTextViewText(tmp_due_date);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();

        holder.rec_due_date.setText(model.getDue_date());
        holder.rec_due_paid.setText(model.getPaid_date());
        holder.rec_due_amount.setText("Rs." + model.getDue_fee());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (i == 2) {
                            DialogPlus dialogPlus = DialogPlus.newDialog(holder.itemView.getContext())
                                    .setContentHolder(new ViewHolder(R.layout.due_update_dialog))
                                    .setBackgroundColorResId(R.color.transparent)
                                    .setExpanded(true, 1400)
                                    .create();

                            View myView = dialogPlus.getHolderView();
                            TextView dialog_due_date = myView.findViewById(R.id.dialog_due_date);
                            TextView dialog_fee = myView.findViewById(R.id.dialog_due_fee);
                            TextView dialog_paid_date = myView.findViewById(R.id.dialog_paid_date);
                            LinearLayout dialog_edit_btn = myView.findViewById(R.id.dialog_edit_btn);

                            dialog_due_date.setText(model.getDue_date());
                            dialog_fee.setText("Rs." + model.getDue_fee());
                            dialog_paid_date.setText(model.getPaid_date());

                            String date_4_split = dialog_paid_date.getText().toString().trim();
                            String[] selectedDate = date_4_split.split(" / ");

                            int year = Integer.parseInt(String.valueOf(selectedDate[2]));
                            int month = Integer.parseInt(String.valueOf(selectedDate[1]))-1;
                            int date = Integer.parseInt(String.valueOf(selectedDate[0]));

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

                                    datePickerDialog = new DatePickerDialog(holder.itemView.getContext(), dateSetListener, year, month, date);
                                    datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                                    datePickerDialog.show();
                                }
                            });

                            dialogPlus.show();

                            dialog_edit_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String tmp_due_date = dialog_due_date.getText().toString();
                                    String tmp_paid_date = dialog_paid_date.getText().toString().trim();

                                    DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).
                                            child("paid_due").child(tmp_course).child(tmp_username);

                                    final String[] node_value = new String[1];
                                    final boolean[] found = {false};

                                    db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                String new_due_date = String.valueOf(ds.child("due_date").getValue());

                                                if (new_due_date.equals(tmp_due_date)) {
                                                    node_value[0] = ds.getKey();
                                                    found[0] =true;
                                                    break;
                                                }

                                            }
                                            if (found[0]){
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("paid_date", tmp_paid_date);
                                                db_ref.child(node_value[0]).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(holder.itemView.getContext(), "Updated Successfully !", Toast.LENGTH_SHORT).show();
                                                        dialogPlus.dismiss();
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(holder.itemView.getContext(), "Not Found", Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            });

                        }
                        i = 0;
                    }
                }, 500);
            }
        });

    }

    @NonNull
    @Override
    public Personal_Due_Adapter.DueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.due_paid_card, parent, false);
        return new Personal_Due_Adapter.DueViewHolder(view);
    }

    public class DueViewHolder extends RecyclerView.ViewHolder {
        TextView rec_due_date, rec_due_paid, rec_due_amount;

        public DueViewHolder(@NonNull View itemView) {
            super(itemView);
            rec_due_date = itemView.findViewById(R.id.rec_due_date);
            rec_due_paid = itemView.findViewById(R.id.rec_due_paid);
            rec_due_amount = itemView.findViewById(R.id.rec_due_amount);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                userEmail = user.getEmail();
                userEmail = userEmail.replaceAll("[@.-]", "");
            }


        }
    }
}
