package www.example.littlemeraki.Dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import www.example.littlemeraki.Personal_Dues.Personal_Due_Adapter;
import www.example.littlemeraki.R;

public class Notification_Adapter extends FirebaseRecyclerAdapter<Notification_Model,Notification_Adapter.NotificationViewHolder> {

    public Notification_Adapter(@NonNull FirebaseRecyclerOptions<Notification_Model> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull Notification_Adapter.NotificationViewHolder holder, int position, @NonNull Notification_Model model) {

        holder.title.setText(model.getTitle());
        holder.content.setText(model.getContent());
        holder.date.setText(model.getDate());

    }

    @NonNull
    @Override
    public Notification_Adapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent, false);
        return new Notification_Adapter.NotificationViewHolder(view);
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        TextView title,content,date;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.noti_title);
            content=itemView.findViewById(R.id.noti_content);
            date=itemView.findViewById(R.id.noti_date);

        }
    }
}
