package com.example.sellerapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.sellerapp.databinding.ActivityHotelBinding;
import com.example.sellerapp.databinding.ActivityProductBinding;
import com.example.sellerapp.viewmodels.HotelViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HotelActivity extends AppCompatActivity {

    private int[] REQUEST_LOAD_PICTURES = {101, 102, 103, 104};
    private int[] REQUEST_TAKE_PICTURES = {12341, 12342, 12343, 12344};
    private ImageView[] hotelPictures = new ImageView[4];
    private int currentImageNum;
    private HotelViewModel viewModel;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new HotelViewModel(getApplication());
        ActivityHotelBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_hotel);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbProductAdd = findViewById(R.id.tbHotelAdd);
        tbProductAdd.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().hasExtra("id_kamar")){
                    new MaterialAlertDialogBuilder(HotelActivity.this)
                            .setTitle("Perhatian!")
                            .setMessage("Apakah anda yakin akan keluar? Seluruh perubahan akan dibatalkan")
                            .setNegativeButton("Tidak", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .setPositiveButton("Ya", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                finish();
                            })
                            .show();
                }
                else{
                    finish();
                }
            }
        });

        if(getIntent().hasExtra("id_kamar")){
            viewModel.prepareUpdate(getIntent().getStringExtra("id_kamar"));
        }

        ProgressDialog loadDialog = new ProgressDialog(this);
        loadDialog.setCancelable(false);
        viewModel.getLoadingTitle().observe(this, title -> {
            loadDialog.setTitle(title);
        });
        viewModel.isAddLoading().observe(this, loading -> {
            if(loading){
                loadDialog.show();
            }
            else{
                loadDialog.dismiss();
            }
        });

        TextInputLayout[] tlS = new TextInputLayout[5];
        tlS[0] = findViewById(R.id.tlHotelName);
        tlS[1] = findViewById(R.id.tlHotelPrice);
        tlS[2] = findViewById(R.id.tlHotelTotal);
        tlS[3] = findViewById(R.id.tlHotelLength);
        tlS[4] = findViewById(R.id.tlHotelWidth);

        TextView tvPicError = findViewById(R.id.tvHotelErrorPicture);
        viewModel.getFieldErrors().observe(this, errors -> {
            for (int i=0;i<5;i++){
                tlS[i].setError(errors[i]);
            }
            tvPicError.setVisibility(View.VISIBLE);
            tvPicError.setText(errors[6]);
        });

        NestedScrollView svHotel = findViewById(R.id.svHotel);
        viewModel.getFocusedFieldNumber().observe(this, number -> {
            if(number < 5){
                tlS[number].getEditText().requestFocus();
            }
            else{
                svHotel.scrollTo(0, tvPicError.getBottom());
            }
        });

        viewModel.isHotelAdded().observe(this, added -> {
            finish();
        });

        hotelPictures[0] = findViewById(R.id.hotelPicture1);
        hotelPictures[1] = findViewById(R.id.hotelPicture2);
        hotelPictures[2] = findViewById(R.id.hotelPicture3);
        hotelPictures[3] = findViewById(R.id.hotelPicture4);
        for (int i=0;i<4;i++){
            final int index = i;
            viewModel.getHotelPicturesURL(i).observe(this, url -> {
                Glide.with(this).load(url).into(hotelPictures[index]);
            });
            hotelPictures[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] picOptions = {"Kamera", "Galeri"};
                    new MaterialAlertDialogBuilder(HotelActivity.this)
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
                                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                        startActivityForResult(uploadIntent, REQUEST_LOAD_PICTURES[index]);
                                    }
                                }
                            })
                            .show();
                }
            });
        }

        MaterialButton[] btnDeletePictures = new MaterialButton[4];
        btnDeletePictures[0] = findViewById(R.id.btnDeleteHotelPic1);
        btnDeletePictures[1] = findViewById(R.id.btnDeleteHotelPic2);
        btnDeletePictures[2] = findViewById(R.id.btnDeleteHotelPic3);
        btnDeletePictures[3] = findViewById(R.id.btnDeleteHotelPic4);
        for (int i=0;i<4;i++){
            final int index = i;
            btnDeletePictures[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hotelPictures[index].setImageResource(R.drawable.uploadimg);
                    viewModel.deleteHotelPicture(index);
                }
            });
        }

        viewModel.isHotelAdded().observe(this, added -> {
            Toast.makeText(this, "Berhasil "+viewModel.getLoadingTitle().getValue(), Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(HotelActivity.this, MainActivity.class);
            mainIntent.putExtra("Role", 2);
            mainIntent.putExtra("jumpTo", 2);
            startActivity(mainIntent);
        });

        MaterialButton btnDelete = findViewById(R.id.btnHotelDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(HotelActivity.this)
                        .setTitle("Perhatian!")
                        .setMessage("Apakah anda yakin akan menghapus kamar?")
                        .setNegativeButton("Tidak", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .setPositiveButton("Ya", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            viewModel.beginDeletes();
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(String.valueOf(requestCode).contains("1234")){
            data = new Intent();
        }

        if(data != null){
            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                hotelPictures[currentImageNum].setImageURI(resultUri);
                getImageViewBitmap(hotelPictures[currentImageNum], currentImageNum);
            }
            else if(String.valueOf(requestCode).contains("10")){
                String requestNum = String.valueOf(requestCode).substring(2);
                int reqNumInt = Integer.parseInt(requestNum) - 1;
                Uri selectedImage = data.getData();
                currentImageNum = reqNumInt;
                CropImage.activity(selectedImage).start(HotelActivity.this);
            }
            else if(String.valueOf(requestCode).contains("1234")){
                String requestNum = String.valueOf(requestCode).substring(4);
                int reqNumInt = Integer.parseInt(requestNum) - 1;
                File f = new File(photoPath);
                Uri photoUri = Uri.fromFile(f);
                currentImageNum = reqNumInt;
                CropImage.activity(photoUri).start(HotelActivity.this);
            }
        }
    }

    private void getImageViewBitmap(ImageView selectedPic, int index){
        selectedPic.setDrawingCacheEnabled(true);
        selectedPic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) selectedPic.getDrawable()).getBitmap();
        viewModel.uploadHotelPicture(index, Bitmap.createScaledBitmap(bitmap, 500, 500, false));
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

    @Override
    public void onBackPressed() {
        if(getIntent().hasExtra("id_kamar")){
            new MaterialAlertDialogBuilder(HotelActivity.this)
                    .setTitle("Perhatian!")
                    .setMessage("Apakah anda yakin akan keluar? Seluruh perubahan akan dibatalkan")
                    .setNegativeButton("Tidak", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    })
                    .setPositiveButton("Ya", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        super.onBackPressed();
                    })
                    .show();
        }
        else{
            super.onBackPressed();
        }
    }

    @BindingAdapter("android:checked")
    public static void setChecked(CompoundButton chk, boolean checked){
        chk.setChecked(checked);
    }
}