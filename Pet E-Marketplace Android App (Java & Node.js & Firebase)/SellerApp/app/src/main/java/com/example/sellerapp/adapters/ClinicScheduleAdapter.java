package com.example.sellerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sellerapp.R;
import com.example.sellerapp.inputclasses.ClinicSchedule;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

public class ClinicScheduleAdapter extends RecyclerView.Adapter<ClinicScheduleAdapter.ViewHolder> {
    private ClinicSchedule[] schedules;
    private Context activityContext;
    private scheduleChanged scheduleChangedListener;

    public ClinicScheduleAdapter( scheduleChanged listener) {
        scheduleChangedListener = listener;
    }

    public void setSchedules(ClinicSchedule[] schedules) {
        this.schedules = schedules;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        activityContext = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        View v = inf.inflate(R.layout.item_clinic_schedule,parent,false);
        return new ClinicScheduleAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvDay.setText(schedules[position].getDay());
        holder.todayOpen.setChecked(schedules[position].isOpen());
        holder.edOpen.setText(schedules[position].getOpenHourMinute());
        holder.edClose.setText(schedules[position].getCloseHourMinute());

        holder.edOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12).setMinute(0)
                        .setTitleText("Tentukan waktu buka klinik")
                        .build();
                timePicker.show(((AppCompatActivity) activityContext).getSupportFragmentManager(), "");
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.edOpen.setText(combinedHourMinute(timePicker));
                        schedules[position].setOpenHourMinute(combinedHourMinute(timePicker));
                        scheduleChangedListener.scheduleChanged(schedules[position], position);
                    }
                });
            }
        });

        holder.edClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12).setMinute(0)
                        .setTitleText("Tentukan waktu tutup klinik")
                        .build();
                timePicker.show(((AppCompatActivity) activityContext).getSupportFragmentManager(), "");
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.edClose.setText(combinedHourMinute(timePicker));
                        schedules[position].setCloseHourMinute(combinedHourMinute(timePicker));
                        scheduleChangedListener.scheduleChanged(schedules[position], position);
                    }
                });
            }
        });

        if(schedules[position].isOpen()){
            holder.partDetermindHour.setVisibility(View.VISIBLE);
        }
        else{
            holder.partDetermindHour.setVisibility(View.GONE);
        }

        holder.todayOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    holder.partDetermindHour.setVisibility(View.VISIBLE);
                }
                else{
                    holder.partDetermindHour.setVisibility(View.GONE);
                }
                schedules[position].setOpen(b);
                scheduleChangedListener.scheduleChanged(schedules[position], position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return schedules.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText edOpen, edClose;
        SwitchMaterial todayOpen;
        LinearLayout partDetermindHour;
        TextView tvDay;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edOpen = itemView.findViewById(R.id.edScheduleOpen);
            edClose = itemView.findViewById(R.id.edScheduleClose);
            todayOpen = itemView.findViewById(R.id.swScheduleOpen);
            partDetermindHour = itemView.findViewById(R.id.layoutDetSchedule);
            tvDay = itemView.findViewById(R.id.tvScheduleDay);
        }
    }

    public interface scheduleChanged{
        void scheduleChanged(ClinicSchedule schedule, int index);
    }

    private String combinedHourMinute(MaterialTimePicker t){
        String pickedTime = "";
        String pickedHour = String.valueOf(t.getHour());
        String pickedMinute = String.valueOf(t.getMinute());
        if(pickedHour.length() <= 1){
            pickedTime += "0";
        }
        pickedTime += pickedHour;
        pickedTime += ":";

        if(pickedMinute.length() == 1){
            pickedTime += "0";
        }
        pickedTime += pickedMinute;
        return pickedTime;
    }
}
