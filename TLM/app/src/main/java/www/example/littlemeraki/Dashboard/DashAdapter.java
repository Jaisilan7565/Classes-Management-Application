package www.example.littlemeraki.Dashboard;

import android.content.ClipData;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;

import www.example.littlemeraki.Course_Division.Student_Info;
import www.example.littlemeraki.Fee.FeeModel;
import www.example.littlemeraki.R;
import www.example.littlemeraki.TimeTable.Timetable;

public class DashAdapter extends FirebaseRecyclerAdapter<FeeModel, DashAdapter.DashViewHolder> {

    public DashAdapter(@NonNull FirebaseRecyclerOptions<FeeModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DashAdapter.DashViewHolder holder, int position, @NonNull FeeModel model) {
        holder.course.setText(model.getCourse());
        holder.course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), Student_Info.class);
                intent.putExtra("course", model.getCourse());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);

            }
        });
    }

    @NonNull
    @Override
    public DashAdapter.DashViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_course_row,parent,false);
        return new DashAdapter.DashViewHolder(view);
    }

    public class DashViewHolder extends RecyclerView.ViewHolder {
        TextView course;
        public DashViewHolder(@NonNull View itemView) {
            super(itemView);
            course=itemView.findViewById(R.id.dash_course_name);
        }
    }
}
