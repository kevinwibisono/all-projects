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
import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemHotelCheckoutBinding;
import my.istts.finalproject.viewmodels.itemviewmodels.HotelCheckoutItemVM;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;

public class HotelCheckoutAdapter extends RecyclerView.Adapter<HotelCheckoutAdapter.ViewHolder> {
    private ArrayList<HotelCheckoutItemVM> checkoutVMs = new ArrayList<>();
    private Context ctx;
    private onSubtotalChanged onSubtotalChangedCallback;
    private onBookBeginDateChanged onBookBeginDateChangedCallback;
    private onBookEndDateChanged onBookEndDateChangedCallback;
    private long minDate = 0, maxDate = 0;

    public HotelCheckoutAdapter(onSubtotalChanged onSubtotalChangedCallback, onBookBeginDateChanged onBookBeginDateChangedCallback, onBookEndDateChanged onBookEndDateChangedCallback) {
        this.onSubtotalChangedCallback = onSubtotalChangedCallback;
        this.onBookBeginDateChangedCallback = onBookBeginDateChangedCallback;
        this.onBookEndDateChangedCallback = onBookEndDateChangedCallback;
    }

    public void setCheckoutVMs(ArrayList<HotelCheckoutItemVM> checkoutVMs) {
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
        ItemHotelCheckoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_hotel_checkout, parent, false);
        return new HotelCheckoutAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.setViewmodel(checkoutVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        long tomorrow = System.currentTimeMillis() + (24 * 3600 * 1000);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.sellerPart.getLayoutParams();
        if(position == 0){
            params.setMargins(30, 30, 30, 0);
        }
        else{
            params.setMargins(30, 120, 30, 0);
        }
        holder.sellerPart.requestLayout();

        holder.edHotelBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String dateTime = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                        holder.edHotelBegin.setText(dateTime);
                        checkoutVMs.get(position).setBeginHotelBooking(calendar.getTime());
                        onBookBeginDateChangedCallback.onChanged();
                        minDate = calendar.getTimeInMillis() + (24 * 3600 * 1000);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                if(maxDate != 0){
                    datePickerDialog.getDatePicker().setMaxDate(maxDate);
                }
                datePickerDialog.getDatePicker().setMinDate(tomorrow);

                datePickerDialog.show();

            }
        });

        holder.edHotelEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendarEnd = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        calendarEnd.set(Calendar.YEAR, year);
                        calendarEnd.set(Calendar.MONTH, monthOfYear);
                        calendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String dateTime = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                        holder.edHotelEnd.setText(dateTime);
                        checkoutVMs.get(position).setEndHotelBooking(calendarEnd.getTime());
                        onBookBeginDateChangedCallback.onChanged();
                        maxDate = calendarEnd.getTimeInMillis() - (24 * 3600 * 1000);
                    }
                }, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH));

                if(minDate != 0){
                    datePickerDialog.getDatePicker().setMinDate(minDate);
                }
                else{
                    datePickerDialog.getDatePicker().setMinDate(tomorrow);
                }
                datePickerDialog.show();

            }
        });

        checkoutVMs.get(position).getPjInput().getSellerPic().observe((LifecycleOwner) ctx, pic->{
            Glide.with(ctx).load(pic).into(holder.ivHotelOwner);
        });

        checkoutVMs.get(position).getPicture().observe((LifecycleOwner) ctx, pic->{
            Glide.with(ctx).load(pic).into(holder.ivHotel);
        });

        checkoutVMs.get(position).getPjInput().getSubtotal().observe((LifecycleOwner) ctx, subtotal->{
            onSubtotalChangedCallback.onChanged();
        });

    }

    public interface onSubtotalChanged{
        void onChanged();
    }

    public interface onBookBeginDateChanged{
        void onChanged();
    }

    public interface onBookEndDateChanged{
        void onChanged();
    }

    @Override
    public int getItemCount() {
        return checkoutVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHotelCheckoutBinding binding;
        LinearLayout sellerPart;
        TextInputEditText edHotelBegin, edHotelEnd;
        ImageView ivHotel, ivHotelOwner;
        public ViewHolder(ItemHotelCheckoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            sellerPart = itemView.findViewById(R.id.itemHCSeller);
            edHotelBegin = itemView.findViewById(R.id.edHotelBegin);
            edHotelEnd = itemView.findViewById(R.id.edHotelEnd);
            ivHotel = itemView.findViewById(R.id.ivHotelCheckout);
            ivHotelOwner = itemView.findViewById(R.id.ivHCSellerPic);
        }
    }
}
