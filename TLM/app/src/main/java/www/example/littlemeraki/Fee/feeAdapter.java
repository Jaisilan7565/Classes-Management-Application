package www.example.littlemeraki.Fee;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
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

import www.example.littlemeraki.Login;
import www.example.littlemeraki.R;

public class feeAdapter extends FirebaseRecyclerAdapter<FeeModel, feeAdapter.FeeViewHolder> {

    public feeAdapter(@NonNull FirebaseRecyclerOptions<FeeModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull feeAdapter.FeeViewHolder holder, int position, @NonNull FeeModel model) {

        holder.fee.setText(model.getFee());
        holder.course.setText(model.getCourse());

        holder.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.course.getContext())
                        .setContentHolder(new ViewHolder(R.layout.fee_update_dialog))
                        .setContentBackgroundResource(R.color.transparent)
                        .setExpanded(true, 1000)
                        .create();

                View view= dialogPlus.getHolderView();
                TextView up_course_name=view.findViewById(R.id.up_course_name);
                EditText up_course_fee=view.findViewById(R.id.up_course_fee);
                ImageView up_edit_btn=view.findViewById(R.id.up_edit_btn);
                ImageView up_delete_btn=view.findViewById(R.id.up_delete_btn);

                up_course_name.setText(model.getCourse());
                up_course_fee.setText(model.getFee());
                dialogPlus.show();

                up_edit_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String v_course_name=up_course_name.getText().toString().trim();
                        String v_course_fee=up_course_fee.getText().toString().trim();

                        if (v_course_fee.isEmpty()){
                            up_course_fee.setError("Field can't be Empty.");
                            up_course_fee.requestFocus();
                        } else {
                            Map<String, Object> map = new HashMap<>();
                            map.put("fee", v_course_fee);
                            FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("fee_structure").child(v_course_name).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            dialogPlus.dismiss();
                                            Toast.makeText(view.getContext(), "Updated Successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(view.getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    });
//                            hideKeyboard(v);
                        }

                    }
                });

                up_delete_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.course.getContext());
                        builder.setTitle("Delete Warning !!!");
                        builder.setMessage("Deleting will remove Student's Data and Timetable of this Batch and Fee Structure will be removed from the List.");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String tmp_course_name = model.getCourse();

                                FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("fee_structure")
                                        .child(tmp_course_name).removeValue();
                                FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("students")
                                        .child(tmp_course_name).removeValue();
                                FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("timetable")
                                        .child(tmp_course_name).removeValue();
                                FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("paid_due")
                                        .child(tmp_course_name).removeValue();

                                Toast.makeText(view.getContext(), "Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                dialogPlus.dismiss();

                            }
                        });

                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(view.getContext(), "Fee Data isn't Deleted.", Toast.LENGTH_SHORT).show();
                                dialogPlus.dismiss();
                            }
                        });
//                        hideKeyboard(v);
                        builder.show();
                    }
                });

            }
        });

    }

    @NonNull
    @Override
    public feeAdapter.FeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.fee_row,parent,false);
        return new FeeViewHolder(view);
    }

    public class FeeViewHolder extends RecyclerView.ViewHolder {

        TextView course,fee;
        ImageView edit_btn;
        String userEmail;
        public FeeViewHolder(View view) {
            super(view);
            edit_btn=view.findViewById(R.id.edit_btn);
            course=view.findViewById(R.id.course_name);
            fee=view.findViewById(R.id.course_fee);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                userEmail = user.getEmail();
                userEmail=userEmail.replaceAll("[@.-]","");
            }
        }
    }

}
