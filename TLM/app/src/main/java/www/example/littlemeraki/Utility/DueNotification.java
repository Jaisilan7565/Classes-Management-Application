package www.example.littlemeraki.Utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import www.example.littlemeraki.All_Dues.Due_Dates;
import www.example.littlemeraki.All_Dues.Due_Dates_Adapter;
import www.example.littlemeraki.Course_Division.StudentInfo_Model;
import www.example.littlemeraki.R;

public class DueNotification extends BroadcastReceiver {

    public static final String CHANNEL_ID = "Due_Channel";
    public static final int NOTIFICATION_ID = 100;
    String userEmail;


    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
            userEmail = userEmail.replaceAll("[@.-]", "");
        }

        DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference().child(userEmail);
        db_ref.child("students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                int j = 0;
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String course_node = ds.getKey();

                    if (course_node != null) {
                        for (DataSnapshot ds2 : ds.getChildren()) {
                            String name = ds2.child("name").getValue(String.class);
                            String course = ds2.child("course").getValue(String.class);
                            String due_date = ds2.child("due_date").getValue(String.class);

                            String[] namePart = name.split("\\s+");

                            SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy");
                            String currentDate = sdf.format(new Date());

                            String[] CDparts = currentDate.split(" / ");
                            String CD_date = CDparts[0].trim();
                            String CD_month = CDparts[1].trim();
                            String CD_year = CDparts[2].trim();

                            int date = Integer.parseInt(CD_date);
                            int month = Integer.parseInt(CD_month);
                            int year = Integer.parseInt(CD_year);

                            String FcurrentDate = date + " / " + month + " / " + year;

                            if (FcurrentDate.equals(due_date)) {
                                i = i + 1;
                                if (i == 1) {
                                    String title = "Today is the Due Date for " + namePart[0] + " from " + course;
                                    String content = "Check Today's Due to send Reminder.";
                                    createNotification(context, title, content);

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("title", title);
                                    map.put("content", content);
                                    map.put("date", FcurrentDate);
                                    db_ref.child("notifications").child(ts).updateChildren(map);
                                } else if (i > 1) {
                                    String title = "Today is the Due Date for " + i + " Student's";
                                    String content = "Check Today's Due to send Reminder.";
                                    createNotification(context, title, content);

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("title", title);
                                    map.put("content", content);
                                    map.put("date", FcurrentDate);
                                    db_ref.child("notifications").child(ts).updateChildren(map);
                                }

                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void createNotification(Context context, String title, String content) {

        Intent intent = new Intent(context, Due_Dates.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Due Notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_mascot)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

}
