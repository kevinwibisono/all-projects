package com.example.sellerapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sellerapp.databinding.ActivityAddVariasiBinding;
import com.example.sellerapp.databinding.ActivityProductBinding;
import com.example.sellerapp.viewmodels.AddVariasiViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddVariasiActivity extends AppCompatActivity {

    private int REQUEST_LOAD_PICTURE = 1;
    private int REQUEST_TAKE_PICTURE = 2;
    private int RESPONSE_VARIANT = 111;
    private AddVariasiViewModel viewModel;
    private ImageView ivPicture;
    public static Bitmap varianPicture;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new AddVariasiViewModel(getApplication());
        ActivityAddVariasiBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_variasi);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbBack = findViewById(R.id.tbVariasiAdd);
        tbBack.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(getIntent().hasExtra("id_variasi")){
            viewModel.getVariantDetail(getIntent().getStringExtra("id_variasi"));
        }


        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setTitle("Mendapatkan Variasi Produk....");
        viewModel.isLoading().observe(this, loading -> {
            if(loading){
                dialog.show();
            }
            else{
                dialog.dismiss();
            }
        });

        viewModel.isAllowed().observe(this, allowed -> {
            Intent resultintent = new Intent(AddVariasiActivity.this, ProductActivity.class);
            resultintent.putExtra("nama", viewModel.nama.getValue());
            resultintent.putExtra("harga", viewModel.harga.getValue());
            resultintent.putExtra("stok", viewModel.stok.getValue());
            if(getIntent().hasExtra("id_variasi")){
                resultintent.putExtra("id_variasi", getIntent().getStringExtra("id_variasi"));
            }
            setResult(RESPONSE_VARIANT, resultintent);
            finish();
        });

        TextInputLayout[] tlS = {findViewById(R.id.tlVariasiNama), findViewById(R.id.tlVariasiHarga), findViewById(R.id.tlVariasiStok)};
        viewModel.getFieldErrors().observe(this, errors -> {
            for (int i = 0; i < 3; i++) {
                tlS[i].setError(errors[i]);
            }
        });

        viewModel.getFocusedField().observe(this, focusedNumber -> {
            tlS[focusedNumber].getEditText().requestFocus();
        });

        String[] picOptions = {"Camera", "Gallery"};
        ivPicture = findViewById(R.id.ivVariasiPicture);
        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(AddVariasiActivity.this)
                        .setTitle("Pilih Gambar Dari")
                        .setItems(picOptions, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(i == 0){
                                    dispatchTakePictureIntent();
                                }
                                else{
                                    Intent uploadIntent = new Intent(
                                            Intent.ACTION_PICK,
                                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                                    startActivityForResult(uploadIntent, REQUEST_LOAD_PICTURE);
                                }
                            }
                        })
                        .show();
            }
        });

        viewModel.getPicture().observe(this, bitmap -> {
            ivPicture.setImageBitmap(bitmap);
            varianPicture = bitmap;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_TAKE_PICTURE){
            //pada saat memotret gambar, maka tidak akan mengembalikan data
            //oleh krn itu, supaya tetap masuk if, maka inisialisasi data
            data = new Intent();
        }

        if(data != null && resultCode == RESULT_OK){
            if(requestCode == REQUEST_LOAD_PICTURE){
                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage).start(AddVariasiActivity.this);
            }
            else if(requestCode == REQUEST_TAKE_PICTURE){
                File photoFile = new File(photoPath);
                Uri takenPicUri = Uri.fromFile(photoFile);
                CropImage.activity(takenPicUri).start(AddVariasiActivity.this);
            }
            else{
                //dari library CropImage
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                ivPicture.setImageURI(resultUri);
                getImageViewBitmap();
            }
        }
    }

    private void getImageViewBitmap(){
        ivPicture.setDrawingCacheEnabled(true);
        ivPicture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) ivPicture.getDrawable()).getBitmap();
        varianPicture = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
        viewModel.setPicture(bitmap);

    }

    private void dispatchTakePictureIntent() {
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
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PICTURE);
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
}