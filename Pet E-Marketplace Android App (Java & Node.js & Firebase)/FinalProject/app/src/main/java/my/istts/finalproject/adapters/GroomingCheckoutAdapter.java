package my.istts.finalproject.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.GroomingCheckoutActivity;
import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemGroomingCheckoutBinding;

import my.istts.finalproject.viewmodels.itemviewmodels.GroomingCheckoutItemVM;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class GroomingCheckoutAdapter extends RecyclerView.Adapter<GroomingCheckoutAdapter.ViewHolder> {
    private ArrayList<GroomingCheckoutItemVM> checkoutVMs = new ArrayList<>();
    private Context ctx;
    private onSubtotalChanged onSubtotalChangedCallback;
    private onGroomingDateChanged onGroomingDateChangedCallback;

    public GroomingCheckoutAdapter(onSubtotalChanged onSubtotalChangedCallback, onGroomingDateChanged onGroomingDateChangedCallback) {
        this.onSubtotalChangedCallback = onSubtotalChangedCallback;
        this.onGroomingDateChangedCallback = onGroomingDateChangedCallback;
    }

    public void setCheckoutVMs(ArrayList<GroomingCheckoutItemVM> checkoutVMs) {
        this.checkoutVMs = checkoutVMs;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        ctx = recyclerView.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroomingCheckoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_grooming_checkout, parent, false);
        return new GroomingCheckoutAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.setViewmodel(checkoutVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        long tomorrow = System.currentTimeMillis() + (24 * 3600 * 1000);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.partSeller.getLayoutParams();
        if(position == 0){
            params.setMargins(30, 30, 30, 0);
        }
        else{
            params.setMargins(30, 120, 30, 0);
        }
        holder.partSeller.requestLayout();

        checkoutVMs.get(position).getPjInput().getSellerPic().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivSeller);
        });

        checkoutVMs.get(position).getPjInput().getSubtotal().observe((LifecycleOwner) ctx, subtotal->{
            onSubtotalChangedCallback.onChanged();
        });

        checkoutVMs.get(position).getPjInput().getTglGrooming().observe((LifecycleOwner) ctx, subtotal->{
            onGroomingDateChangedCallback.onChanged();
        });

        holder.edGroomingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog datePicker = new DatePickerDialog(
                        ctx,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                String dateTime = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;

                                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                                        .setTimeFormat(TimeFormat.CLOCK_24H)
                                        .setHour(12).setMinute(0)
                                        .setTitleText("Tentukan Waktu Grooming")
                                        .build();
                                GroomingCheckoutActivity activity = (GroomingCheckoutActivity) ctx;
                                timePicker.show(activity.getSupportFragmentManager(), "");
                                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Date groomingDate = myCalendar.getTime();
                                        groomingDate.setHours(timePicker.getHour());
                                        groomingDate.setMinutes(timePicker.getMinute());
                                        holder.edGroomingDate.setText(dateTime+"   "+timePicker.getHour()+":"+timePicker.getMinute());
                                        checkoutVMs.get(position).setGroomingDate(groomingDate);
                                        onGroomingDateChangedCallback.onChanged();
                                    }
                                });
                            }
                        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                datePicker.getDatePicker().setMinDate(tomorrow);
                datePicker.show();
            }
        });

    }

    public interface onSubtotalChanged{
        void onChanged();
    }

    public interface onGroomingDateChanged{
        void onChanged();
    }

    @Override
    public int getItemCount() {
        return checkoutVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemGroomingCheckoutBinding binding;
        LinearLayout partSeller;
        ImageView ivSeller;
        TextInputEditText edGroomingDate;
        public ViewHolder(ItemGroomingCheckoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            partSeller = itemView.findViewById(R.id.itemPCSeller);
            ivSeller = itemView.findViewById(R.id.ivGCSellerPic);
            edGroomingDate = itemView.findViewById(R.id.edGCDate);
        }
    }
}
