package my.istts.finalproject.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ItemProductCheckoutBinding;
import my.istts.finalproject.inputclasses.PJInput;
import my.istts.finalproject.viewmodels.itemviewmodels.ProductCheckoutItemVM;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class ProductCheckoutAdapter extends RecyclerView.Adapter<ProductCheckoutAdapter.ViewHolder> {
    private ArrayList<ProductCheckoutItemVM> checkoutVMs = new ArrayList<>();
    private Context ctx;
    private openOngkirOptions openOngkirs;
    private openVouchers openVouchers;
    private onSubtotalChanged onSubtotalChangedCallback;
    private onCancelVoucher onCancelVoucherCallback;
    private onOpenNote onOpenNoteCallback;

    public ProductCheckoutAdapter(openOngkirOptions openOngkirs, ProductCheckoutAdapter.openVouchers openVouchers, onSubtotalChanged onSubtotalChangedCallback, onCancelVoucher onCancelVoucherCallback, onOpenNote onOpenNoteCallback) {
        this.openOngkirs = openOngkirs;
        this.openVouchers = openVouchers;
        this.onSubtotalChangedCallback = onSubtotalChangedCallback;
        this.onCancelVoucherCallback = onCancelVoucherCallback;
        this.onOpenNoteCallback = onOpenNoteCallback;
    }

    public void setCheckoutVMs(ArrayList<ProductCheckoutItemVM> checkoutVMs) {
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
        ItemProductCheckoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_product_checkout, parent, false);
        return new ProductCheckoutAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.setViewmodel(checkoutVMs.get(position));
        holder.binding.setLifecycleOwner((LifecycleOwner) ctx);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.partSeller.getLayoutParams();
        if(position == 0){
            params.setMargins(30, 30, 30, 0);
        }
        else{
            params.setMargins(30, 120, 30, 0);
        }
        holder.partSeller.requestLayout();

        checkoutVMs.get(position).getPicture().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivProduct);
        });

        checkoutVMs.get(position).getPjInput().getSellerPic().observe((LifecycleOwner) ctx, picture->{
            Glide.with(ctx).load(picture).into(holder.ivSeller);
        });

        checkoutVMs.get(position).getPjInput().getSubtotal().observe((LifecycleOwner) ctx, subtotal->{
            onSubtotalChangedCallback.onChanged();
        });

        checkoutVMs.get(position).getDiscountValue().observe((LifecycleOwner) ctx, value ->{
            if(value > 0){
                holder.tvHarga.setPaintFlags(holder.tvHarga.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else{
                holder.tvHarga.setPaintFlags(holder.tvHarga.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });

        holder.otherOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOngkirs.onShowOptions(checkoutVMs.get(position).getPjInput().getemail_seller());
            }
        });

        holder.chooseOngkir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOngkirs.onShowOptions(checkoutVMs.get(position).getPjInput().getemail_seller());
            }
        });

        holder.choosePromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openVouchers.onShowVouchers(checkoutVMs.get(position).getPjInput().getemail_seller());
            }
        });

        holder.cancelPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelVoucherCallback.onCancelled(checkoutVMs.get(position).getPjInput().getemail_seller());
            }
        });

        String[] kurirNames = {"pickup", "jne", "pos", "tiki"};
        int[] kurirDrawable = {R.drawable.self_pickup, R.drawable.logo_jne, R.drawable.logo_pos, R.drawable.logo_tiki};

        checkoutVMs.get(position).getPjInput().getCourier().observe((LifecycleOwner) ctx, kurirName ->{
            if(!kurirName.equals("")){
                int matchLogo = -1;
                for (int i = 0; i < 4; i++) {
                    if(kurirNames[i].equals(kurirName)){
                        matchLogo = kurirDrawable[i];
                    }
                }
                holder.ivOngkir.setImageResource(matchLogo);
            }
        });

        holder.edNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOpenNoteCallback.onOpen(checkoutVMs.get(position).getPjInput());
            }
        });

    }

    public interface openOngkirOptions{
        //berisi urutan/id toko
        void onShowOptions(String hp_seller);
    }

    public interface openVouchers{
        //berisi id/notelp user
        void onShowVouchers(String hp_seller);
    }

    public interface onCancelVoucher{
        //berisi id/notelp user
        void onCancelled(String hp_seller);
    }

    public interface onSubtotalChanged{
        //berisi id/notelp user
        void onChanged();
    }

    public interface onOpenNote{
        //berisi id/notelp user
        void onOpen(PJInput pjInput);
    }

    @Override
    public int getItemCount() {
        return checkoutVMs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemProductCheckoutBinding binding;
        LinearLayout partSeller, chooseOngkir, choosePromo;
        TextView otherOptions, tvHarga;
        ImageView ivProduct, ivSeller, cancelPromo, ivOngkir;
        TextInputEditText edNote;
        public ViewHolder(ItemProductCheckoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            View itemView = binding.getRoot();
            partSeller = itemView.findViewById(R.id.itemPCSeller);
            chooseOngkir = itemView.findViewById(R.id.partPCOngkirChoose);
            otherOptions = itemView.findViewById(R.id.openOngkirOptions);
            tvHarga = itemView.findViewById(R.id.tvHargaProductNormal);
            choosePromo = itemView.findViewById(R.id.partPCPromoChoose);
            ivSeller = itemView.findViewById(R.id.ivPCSellerPic);
            ivProduct = itemView.findViewById(R.id.ivPCProductPic);
            ivOngkir = itemView.findViewById(R.id.logoOngkir);
            cancelPromo = itemView.findViewById(R.id.deletePCChosenPromo);
            edNote = itemView.findViewById(R.id.edPJNote);
        }
    }
}
