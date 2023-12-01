package www.example.littlemeraki.All_Dues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import www.example.littlemeraki.R;

public class Custom_Due_Message extends AppCompatActivity {

    AppCompatButton submit_btn, edit_msg_btn;
    EditText current_msg, passed_msg;
    LinearLayout edit_msg_views;
    String userEmail;
    CardView msg_view_card;
    TextView today_view_msg,passed_view_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_due_message);

        submit_btn = findViewById(R.id.submit_msg_btn);
        edit_msg_btn = findViewById(R.id.edit_msg_btn);
        current_msg = findViewById(R.id.custom_current_msg);
        passed_msg = findViewById(R.id.custom_passed_msg);
        edit_msg_views = findViewById(R.id.msg_edit_views);
        today_view_msg =findViewById(R.id.today_msg);
        passed_view_msg=findViewById(R.id.passed_msg);
        msg_view_card=findViewById(R.id.msg_view_card);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail = userEmail.replaceAll("[@.-]", "");
        }
        FirebaseDatabase.getInstance().getReference().child(userEmail).child("due_message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String strCurrent_msg = snapshot.child("current_msg").getValue(String.class);
                    String strPassed_msg = snapshot.child("passed_msg").getValue(String.class);

                    today_view_msg.setText("\uD83D\uDED1 Reminder! \nToday \"[Date]\" is the Due Date for Fee Payment. \n"+strCurrent_msg);
                    passed_view_msg.setText("\uD83D\uDED1 Reminder! \n\"[Date]\" was your Due Date for Fee Payment. \n" +strPassed_msg);

                    current_msg.setText(strCurrent_msg);
                    passed_msg.setText(strPassed_msg);

                } else {
                    Toast.makeText(Custom_Due_Message.this, "Please Add a Message.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        edit_msg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_msg_views.setVisibility(View.VISIBLE);
                msg_view_card.setVisibility(View.GONE);
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail).child("due_message");

                String c_msg = current_msg.getText().toString().trim();
                String p_msg = passed_msg.getText().toString().trim();

                Map<String, Object> map = new HashMap<>();
                map.put("current_msg", c_msg);
                map.put("passed_msg", p_msg);

                db_ref.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Custom_Due_Message.this, "Successfully Updated!", Toast.LENGTH_SHORT).show();
                        edit_msg_views.setVisibility(View.GONE);
                        msg_view_card.setVisibility(View.VISIBLE);
//                        Intent intent = new Intent(Custom_Due_Message.this, Due_Dates.class);
//                        startActivity(intent);
                    }
                });

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent activity = new Intent(Custom_Due_Message.this, Due_Dates.class);
        startActivity(activity);
    }

}