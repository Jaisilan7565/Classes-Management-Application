package www.example.littlemeraki.Dashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import www.example.littlemeraki.All_Dues.Due_Dates;
import www.example.littlemeraki.Personal_Dues.Personal_Due_Adapter;
import www.example.littlemeraki.Personal_Dues.Personal_Due_Dash;
import www.example.littlemeraki.Personal_Dues.Personal_Due_Model;
import www.example.littlemeraki.R;

public class Notification_History extends AppCompatActivity {

    RecyclerView noti_recview;
    AppCompatButton clear;
    String userEmail;
    Notification_Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail = userEmail.replaceAll("[@.-]", "");
        }

        noti_recview=findViewById(R.id.notification_recview);
        clear=findViewById(R.id.clear_notification_btn);

        DatabaseReference rec_db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).
                child("notifications");

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(Notification_History.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        noti_recview.setLayoutManager(mLayoutManager);
        FirebaseRecyclerOptions<Notification_Model> options =
                new FirebaseRecyclerOptions.Builder<Notification_Model>()
                        .setQuery(rec_db_ref, Notification_Model.class)
                        .build();

        adapter = new Notification_Adapter(options);
        noti_recview.setAdapter(adapter);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rec_db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Notification_History.this);
                                builder.setMessage("Are you sure you want to Delete all the Notifications?").setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                rec_db_ref.removeValue();
                                                dialog.dismiss();
                                            }
                                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else if (!snapshot.exists()) {
                                Toast.makeText(Notification_History.this, "No Notifications to Remove", Toast.LENGTH_SHORT).show();
                            }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Notification_History.this, Dashboard.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}