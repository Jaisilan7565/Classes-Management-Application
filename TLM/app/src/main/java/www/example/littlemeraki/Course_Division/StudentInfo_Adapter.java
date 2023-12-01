package www.example.littlemeraki.Course_Division;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import www.example.littlemeraki.Personal_Dues.Personal_Due_Dash;
import www.example.littlemeraki.R;

public class StudentInfo_Adapter extends FirebaseRecyclerAdapter<StudentInfo_Model, StudentInfo_Adapter.InfoViewHolder> {

    public StudentInfo_Adapter(@NonNull FirebaseRecyclerOptions<StudentInfo_Model> options) {
        super(options);
    }

    Context s_context;
    private static final int REQUEST_CALL = 1;

    String selectedItem, oldsi;
    int i = 0;


    @Override
    protected void onBindViewHolder(@NonNull StudentInfo_Adapter.InfoViewHolder holder, int position, @NonNull StudentInfo_Model model) {
        holder.name.setText(model.getName());
        holder.institute.setText(model.getInstitute());
        holder.course.setText(model.getCourse());
        holder.fee.setText(model.getFee());
        holder.contact.setText(model.getContact());
        holder.joining_date.setText(model.getJoining_date());

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(s_context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) s_context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + holder.contact.getText().toString()));
                    s_context.startActivity(callIntent);
                }
            }
        });

        holder.due.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(s_context.getApplicationContext(), Personal_Due_Dash.class);
                intent.putExtra("name", model.getName());
                intent.putExtra("course", model.getCourse());
                intent.putExtra("fee", model.getFee());
                intent.putExtra("contact", model.getContact());
                intent.putExtra("due_date", model.getDue_date());
                intent.putExtra("backKey", "From_Student_Card");
                s_context.startActivity(intent);
            }
        });

        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" + holder.contact.getText().toString();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                s_context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (i == 2) {
                            final DialogPlus dialogPlus = DialogPlus.newDialog(holder.contact.getContext()).setContentHolder(new ViewHolder(R.layout.student_info_update)).setExpanded(true, 1000).setContentBackgroundResource(R.color.teal_200).create();

                            View myview = dialogPlus.getHolderView();
                            final EditText name = myview.findViewById(R.id.update_s_name);
                            final EditText institute = myview.findViewById(R.id.update_s_institute);
                            final Spinner course = myview.findViewById(R.id.update_s_class);
                            final EditText fee = myview.findViewById(R.id.update_s_fee);
                            final EditText contact = myview.findViewById(R.id.update_s_contact);
                            AppCompatButton update_btn = myview.findViewById(R.id.update_btn);

                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("fee_structure");
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<String> itemList = new ArrayList<>();

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String item = (String) snapshot.child("course").getValue();
                                        itemList.add(item);
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(s_context.getApplicationContext(), android.R.layout.simple_spinner_item, itemList);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    course.setAdapter(adapter);

                                    int spinnerPosition = adapter.getPosition(model.getCourse());
                                    course.setSelection(spinnerPosition);
                                    selectedItem = course.getSelectedItem().toString();
                                    oldsi = course.getSelectedItem().toString();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    selectedItem = (String) parent.getItemAtPosition(position);

                                    databaseReference.child(selectedItem).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (!selectedItem.equals(oldsi)) {
                                                String amount = (String) snapshot.child("fee").getValue();
                                                fee.setText(amount);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            name.setText(model.getName());
                            institute.setText(model.getInstitute());
                            fee.setText(model.getFee());
                            contact.setText(model.getContact());

                            dialogPlus.show();

                            update_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String tmp_s_name = name.getText().toString().trim();
                                    String tmp_institute = institute.getText().toString().trim();
                                    String tmp_course = selectedItem;
                                    String tmp_fee = fee.getText().toString().trim();
                                    String tmp_due_date= model.getDue_date();
                                    String tmp_contact = contact.getText().toString().trim();
                                    String tmp_j_date = model.getJoining_date();
                                    String new_student = (tmp_s_name + tmp_contact).toLowerCase();
                                    String old_student = (model.getName() + model.getContact()).toLowerCase();
                                    String old_course = model.getCourse();

                                    if (tmp_s_name.isEmpty() || tmp_s_name.length() < 3) {
                                        name.setError("Enter a Valid Name.");
                                        name.requestFocus();
                                    } else if (tmp_institute.isEmpty()) {
                                        institute.setError("Feild can't be Empty.");
                                        institute.requestFocus();
                                    } else if (tmp_contact.isEmpty()) {
                                        contact.setError("Contact Can't be Empty.");
                                        contact.requestFocus();
                                    } else if (tmp_fee.isEmpty()) {
                                        Toast.makeText(s_context.getApplicationContext(), "Fee Can't be Empty.", Toast.LENGTH_SHORT).show();
                                    } else if (!tmp_s_name.matches("^[a-zA-Z ]*$")) {
                                        name.setError("Only Alphabets Allowed.");
                                        name.requestFocus();
                                    } else if (!tmp_institute.matches("^[a-zA-Z ]*$")) {
                                        institute.setError("Only Alphabets Allowed.");
                                        institute.requestFocus();
                                    } else if (tmp_contact.length() != 10 || !tmp_contact.matches("^[6789]\\d{9}$")) {
                                        contact.setError("Enter a Valid Mobile Number.");
                                        contact.requestFocus();
                                    } else if (!tmp_s_name.isEmpty() && !tmp_contact.isEmpty() && !tmp_fee.isEmpty() && !tmp_institute.isEmpty()) {
                                        DatabaseReference db_ref2 = FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("paid_due");
                                        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("students");

                                        if (!tmp_s_name.equals(model.getName()) || !tmp_contact.equals(model.getContact()) || !tmp_course.equals(model.getCourse())) {

                                            db_ref2.child(old_course).child(old_student).get().addOnSuccessListener(dataSnapshot2 -> {
                                                db_ref2.child(tmp_course).child(new_student).setValue(dataSnapshot2.getValue());
                                                db_ref2.child(old_course).child(old_student).removeValue();
                                            });

                                            db_ref.child(old_course).child(old_student).get().addOnSuccessListener(dataSnapshot -> {
                                                FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("students").child(tmp_course).child(new_student).setValue(dataSnapshot.getValue());
                                                FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("students").child(old_course).child(old_student).removeValue();
                                            });

                                        }

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("name", tmp_s_name);
                                        map.put("institute", tmp_institute);
                                        map.put("course", tmp_course);
                                        map.put("due_date",tmp_due_date);
                                        map.put("fee", tmp_fee);
                                        map.put("joining_date", tmp_j_date);
                                        map.put("contact", tmp_contact);

                                        db_ref.child(tmp_course).child(new_student).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists() && snapshot.child("name").getValue().equals(tmp_s_name) && snapshot.child("institute").getValue().equals(tmp_institute) && snapshot.child("course").getValue().equals(tmp_course) && snapshot.child("fee").getValue().equals(tmp_fee) && snapshot.child("contact").getValue().equals(tmp_contact) && snapshot.child("joining_date").getValue().equals(tmp_j_date)) {
                                                    Toast.makeText(s_context.getApplicationContext(), "Student Already Exist.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    db_ref.child(tmp_course).child(new_student).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(s_context.getApplicationContext(), "Updated Successfully.", Toast.LENGTH_SHORT).show();
//                                                            Intent intent = new Intent(s_context.getApplicationContext(), Dashboard.class);
//                                                            s_context.startActivity(intent);
                                                            dialogPlus.dismiss();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });


                                    }

                                }
                            });

                        }
                        i = 0;
                    }
                }, 500);

            }
        });

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.course.getContext());
                builder.setTitle("Delete Now ?");
                builder.setMessage("Remove the Student from your Data.");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String tmp_course_name = model.getCourse();
                        String tmp_userid = (model.getName() + model.getContact()).toLowerCase();
                        FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("students").child(tmp_course_name).child(tmp_userid).removeValue();
                        FirebaseDatabase.getInstance().getReference().child(holder.userEmail).child("paid_due").child(tmp_course_name).child(tmp_userid).removeValue();
                        Toast.makeText(s_context.getApplicationContext(), "Deleted Successfully.", Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(s_context.getApplicationContext(), "Student isn't Deleted.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }

    @NonNull
    @Override
    public StudentInfo_Adapter.InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_info_card, parent, false);

        s_context = parent.getContext();

        return new StudentInfo_Adapter.InfoViewHolder(view);
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder {
        TextView name, institute, course, fee, contact, joining_date, delete_btn;
        AppCompatButton call, whatsapp, due;
        String userEmail;

        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.s_card_name);
            institute = itemView.findViewById(R.id.s_card_institute);
            course = itemView.findViewById(R.id.s_card_course);
            fee = itemView.findViewById(R.id.s_card_fee);
            contact = itemView.findViewById(R.id.s_card_contact);
            joining_date = itemView.findViewById(R.id.s_card_j_date);
            call = itemView.findViewById(R.id.phone_call_btn);
            whatsapp = itemView.findViewById(R.id.whatsapp_btn);
            delete_btn = itemView.findViewById(R.id.s_card_delete_btn);
            due = itemView.findViewById(R.id.due_btn);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                userEmail = user.getEmail();
                userEmail = userEmail.replaceAll("[@.-]", "");
            }

        }
    }
}
