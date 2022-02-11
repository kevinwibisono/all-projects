package com.example.sellerapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sellerapp.adapters.ClinicScheduleAdapter;
import com.example.sellerapp.adapters.PaketGroomingAdapter;
import com.example.sellerapp.inputclasses.ClinicSchedule;
import com.example.sellerapp.databinding.ActivitySellerDetailBinding;
import com.example.sellerapp.viewmodels.SellerDetailViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SellerDetailActivity extends AppCompatActivity {

    private int REQUEST_ADDRESS_PICKER = 3;
    private int REQUEST_ADDRESS_PICKER_SHOP = 98;
    private int REQUEST_ADD_GROOMING = 83;
    private int[] REQUEST_TAKE_PICTURES = {12340, 12341, 12342, 12343, 12344};
    private int[] REQUEST_LOAD_PICTURES = {100, 101, 102, 103, 104};
    private ImageView[] images = new ImageView[5];
    private SellerDetailViewModel viewModel;
    private TextInputEditText edAddress, edShopAddress;
    private String photoPath;
    private int currentImageNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new SellerDetailViewModel(getApplication());

        ActivitySellerDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_seller_detail);
        binding.setLifecycleOwner(this);
        binding.setViewmodel(viewModel);

        MaterialToolbar tbSellerDetail = findViewById(R.id.tbSellerDetail);
        tbSellerDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MaterialToolbar tbSellerUpdate = findViewById(R.id.tbSellerUpdate);
        tbSellerUpdate.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tbSellerUpdate.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.menuUpdate){
                    //account setting
                    if(viewModel.saveSellerDetail()){
                        viewModel.uploadChosenPictures();
                    }
                }
                return false;
            }
        });

        RecyclerView rvClinicSchedule = findViewById(R.id.rvClinicSchedule);
        rvClinicSchedule.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        ClinicScheduleAdapter adapter = new ClinicScheduleAdapter(new ClinicScheduleAdapter.scheduleChanged() {
            @Override
            public void scheduleChanged(ClinicSchedule schedule, int index) {
                viewModel.setClinicSchedule(schedule, index);
            }
        });
        rvClinicSchedule.setAdapter(adapter);
        viewModel.getUpdatingSchedules().observe(this, schedules->{
            adapter.setSchedules(schedules);
            adapter.notifyDataSetChanged();
        });


        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        viewModel.getLoadingTitle().observe(this, dialog::setTitle);
        viewModel.isLoading().observe(this, loading -> {
            if(loading){
                dialog.show();
            }
            else{
                dialog.dismiss();
            }
        });


        int role = getIntent().getIntExtra("role", -1);
        viewModel.initializeFields(role);

        images[0] = findViewById(R.id.ivSellerPhoto);
        images[1] = findViewById(R.id.ivPoster1);
        images[2] = findViewById(R.id.ivPoster2);
        images[3] = findViewById(R.id.ivPoster3);
        images[4] = findViewById(R.id.ivPoster4);
        for (int i=0;i<5;i++){
            final int index = i;
            viewModel.getPicURLs(i).observe(this, url -> {
                Glide.with(this).load(url).into(images[index]);
            });
            images[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setPicture(index);
                }
            });
        }

        ScrollView svSellerDetail = findViewById(R.id.svSellerDetail);

        TextInputLayout[] tlS = {findViewById(R.id.tlSellerName), findViewById(R.id.tlClinicAddress), findViewById(R.id.tlShopAddress)};
        TextView[] tvErrors = {findViewById(R.id.tvErrorProfile), findViewById(R.id.tvErrorPoster), findViewById(R.id.tvErrorCourier),
                findViewById(R.id.tvErrorAppoSchedules), findViewById(R.id.tvErrorAppo), findViewById(R.id.tvErrorGroom)};
        viewModel.getErrors().observe(this, errors -> {
            for (int i = 0; i < errors.length; i++) {
                if(i < 3){
                    tlS[i].setError(errors[i]);
                }
                else{
                    tvErrors[i-3].setText(errors[i]);
                }
            }
        });

        viewModel.getFocusedNumber().observe(this, number -> {
            if(number < 3){
                tlS[number].getEditText().requestFocus();
            }
            else{
                svSellerDetail.fullScroll(tvErrors[number-3].getBottom());
            }
        });

        MaterialButton btnAdd = findViewById(R.id.btnSellerSave);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewModel.saveSellerDetail()){
                    new MaterialAlertDialogBuilder(SellerDetailActivity.this)
                            .setTitle("Perhatian!")
                            .setMessage("Setelah memilih, anda tidak dapat mengubah kembali role dari sebuah akun")
                            .setNegativeButton("Tidak", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .setPositiveButton("Ya", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                viewModel.uploadChosenPictures();
                            })
                            .show();
                }
            }
        });

        viewModel.isSuccessAdding().observe(this, success -> {
            Intent mainIntent = new Intent(SellerDetailActivity.this, MainActivity.class);
            int peran = viewModel.getRole().getValue();
            mainIntent.putExtra("Role", peran);
            //jika peran adalah 0 (shopping) atau 2 (hotel), maka setelah update jumpTo 4/fragment account
            //jika peran adalah 1 (groomer) atau 3 (clinic), maka setelah update jumpTo ke 3/fragment account
            //  (kedua peran tidak memiliki fragment produk/kamar)

            //setelah menghitung jumpto dikali dengan 0 (false) atau 1 (true)
            //  jika sedang update, maka jumpto seperti hitungan diatas
            //  jika sedang tidak update, maka baru register pertama kali, jumpto harus bernilai 0
            mainIntent.putExtra("jumpTo", (4 - (peran % 2)) * viewModel.isUpdating().getValue());
            startActivity(mainIntent);
        });

        edAddress = findViewById(R.id.edClinicAddress);
        edAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SellerDetailActivity.this, AddressPickerActivity.class), REQUEST_ADDRESS_PICKER);
            }
        });

        edShopAddress = findViewById(R.id.edShopAddress);
        edShopAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SellerDetailActivity.this, AddressPickerActivity.class), REQUEST_ADDRESS_PICKER_SHOP);
            }
        });

        MaterialButton[] btnDeletePicture = {findViewById(R.id.btnDeletePoster1), findViewById(R.id.btnDeletePoster2), findViewById(R.id.btnDeletePoster3), findViewById(R.id.btnDeletePoster4)};
        for (int i = 0; i < 4; i++) {
            final int index = i;
            btnDeletePicture[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    images[index + 1].setImageResource(R.drawable.uploadimghorizontal);
                    viewModel.deletePicturesBitmaps(index + 1);
                }
            });
        }

        CheckBox selfPickupCheckBox = findViewById(R.id.cbSelfPickup);
        selfPickupCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.activateSelfPickup();
            }
        });

        PaketGroomingAdapter groomingAdapter = new PaketGroomingAdapter(new PaketGroomingAdapter.onGroomingPackDelete() {
            @Override
            public void onDelete(int pos) {
                viewModel.removeGroomerPackage(pos);
            }
        }, new PaketGroomingAdapter.onGroomingPackUpdate() {
            @Override
            public void onUpdate(String id_pack) {
                Intent groomingDetailIntent = new Intent(SellerDetailActivity.this, AddGroomingPackActivity.class);
                groomingDetailIntent.putExtra("id_pack", id_pack);
                startActivityForResult(groomingDetailIntent, REQUEST_ADD_GROOMING);
            }
        });

        RecyclerView rvGroomingPacks = findViewById(R.id.rvGroomingPackages);
        rvGroomingPacks.setAdapter(groomingAdapter);

        viewModel.getGroomingPackages().observe(this, paket->{
            groomingAdapter.setPackVMs(paket);
            groomingAdapter.notifyDataSetChanged();
        });

        MaterialButton addGroomingPack = findViewById(R.id.btnAddGroomerPack);
        addGroomingPack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(SellerDetailActivity.this, AddGroomingPackActivity.class), REQUEST_ADD_GROOMING);
            }
        });

    }

    public void setPicture(int index){
        String[] picOptions = {"Camera", "Gallery"};
        new MaterialAlertDialogBuilder(SellerDetailActivity.this)
                .setTitle("Pilih Gambar Dari")
                .setItems(picOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            dispatchTakePictureIntent(index);
                        }
                        else{
                            Intent uploadIntent = new Intent(
                                    Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(uploadIntent, REQUEST_LOAD_PICTURES[index]);
                        }
                    }
                })
                .show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(String.valueOf(requestCode).contains("1234")){
            data = new Intent();
        }

        if(data != null){
            if(resultCode == RESULT_OK){
                //drpd menggunakan for, ambil index dari substring an request code
                if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    viewModel.setPicturesBitmaps(getImageviewBitmap(images[currentImageNum], resultUri), currentImageNum);
                }
                else if(String.valueOf(requestCode).contains("10")){
                    String requestNum = String.valueOf(requestCode).substring(2);
                    int reqNumInt = Integer.parseInt(requestNum);
                    Uri selectedImage = data.getData();
                    currentImageNum = reqNumInt;
                    CropImage.activity(selectedImage).start(SellerDetailActivity.this);
                }
                else if(String.valueOf(requestCode).contains("1234")){
                    String requestNum = String.valueOf(requestCode).substring(4);
                    int reqNumInt = Integer.parseInt(requestNum);
                    File f = new File(photoPath);
                    Uri photoUri = Uri.fromFile(f);
                    currentImageNum = reqNumInt;
                    CropImage.activity(photoUri).start(SellerDetailActivity.this);
                }
            }
            else{
                receiveDataFromIntent(data, requestCode);
            }
        }
    }

    private void receiveDataFromIntent(Intent data, int requestCode){
        if(requestCode == REQUEST_ADDRESS_PICKER){
            String alamat = data.getStringExtra("alamat");
            String koordinat = data.getStringExtra("koordinat");
            viewModel.setAddress(alamat);
            viewModel.setCoordinate(koordinat);
        }
        else if(requestCode == REQUEST_ADDRESS_PICKER_SHOP){
            String alamat = data.getStringExtra("alamat");
            String koordinat = data.getStringExtra("koordinat");
            viewModel.setAddress(alamat);
            viewModel.setCoordinate(koordinat);
        }
        else{
            String harga = data.getStringExtra("harga");
            String nama = data.getStringExtra("nama");
            if(data.hasExtra("id_pack")){
                viewModel.updateGroomerPackage(Integer.parseInt(harga), nama, data.getStringExtra("id_pack"));
            }
            else{
                viewModel.addGroomerPackage(Integer.parseInt(harga), nama);
            }
        }
    }

    private void dispatchTakePictureIntent(int index) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.istts.sellerapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURES[index]);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        photoPath = image.getAbsolutePath();
        return image;
    }

    private Bitmap getImageviewBitmap(ImageView iv, Uri imageUri){
        iv.setImageURI(imageUri);
        iv.setDrawingCacheEnabled(true);
        iv.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        return Bitmap.createScaledBitmap(bitmap, 600, 400, false);
    }

    @BindingAdapter("android:checked")
    public static void setChecked(CompoundButton chk, boolean checked){
        chk.setChecked(checked);
    }
}