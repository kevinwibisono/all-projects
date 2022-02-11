package com.example.sellerapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sellerapp.adapters.VariasiAdapter;
import com.example.sellerapp.databinding.ActivityProductBinding;
import com.example.sellerapp.viewmodels.ProductViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductActivity extends AppCompatActivity {

    private int[] REQUEST_LOAD_PICTURES = {101, 102, 103, 104};
    private int[] REQUEST_TAKE_PICTURES = {12341, 12342, 12343, 12344};
    private int REQUEST_ADD_VARIANT = 99;
    private ImageView[] productPictures = new ImageView[4];
    private int currentImageNum;
    private ProductViewModel viewModel;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ProductViewModel(getApplication());
        ActivityProductBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_product);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbProductAdd = findViewById(R.id.tbProductAdd);
        tbProductAdd.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().hasExtra("id_produk")){
                    new MaterialAlertDialogBuilder(ProductActivity.this)
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

        if(getIntent().hasExtra("id_produk")){
            viewModel.prepareUpdate(getIntent().getStringExtra("id_produk"));
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

        TextInputLayout[] tlS = new TextInputLayout[6];
        tlS[0] = findViewById(R.id.tlProductName);
        tlS[1] = findViewById(R.id.tlProductKat);
        tlS[2] = findViewById(R.id.tlProductPrice);
        tlS[3] = findViewById(R.id.tlProductStock);
        tlS[4] = findViewById(R.id.tlProductWeight);
        tlS[5] = findViewById(R.id.tlProductDesc);

        TextView[] tvErrors = {findViewById(R.id.tvProductErrorPicture), findViewById(R.id.tvProductErrorVariant)};
        viewModel.getFieldErrors().observe(this, errors -> {
            for (int i=0;i<6;i++){
                tlS[i].setError(errors[i]);
            }
            tvErrors[0].setVisibility(View.VISIBLE);
            tvErrors[0].setText(errors[6]);
            tvErrors[1].setVisibility(View.VISIBLE);
            tvErrors[1].setText(errors[7]);
        });

        NestedScrollView svProducts = findViewById(R.id.svProducts);
        viewModel.getFocusedFieldNumber().observe(this, number -> {
            if(number < 6){
                tlS[number].getEditText().requestFocus();
            }
            else{
                svProducts.scrollTo(0, tvErrors[number-6].getBottom());
            }
        });

        viewModel.isProductAdded().observe(this, added -> {
            finish();
        });

        productPictures[0] = findViewById(R.id.productPicture1);
        productPictures[1] = findViewById(R.id.productPicture2);
        productPictures[2] = findViewById(R.id.productPicture3);
        productPictures[3] = findViewById(R.id.productPicture4);
        for (int i=0;i<4;i++){
            final int index = i;
            viewModel.getPicturesURL(i).observe(this, url -> {
                Glide.with(this).load(url).into(productPictures[index]);
            });
            productPictures[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] picOptions = {"Camera", "Gallery"};
                    new MaterialAlertDialogBuilder(ProductActivity.this)
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
            });
        }

        MaterialButton[] btnDeletePictures = {findViewById(R.id.btnDeletePicture1), findViewById(R.id.btnDeletePicture2), findViewById(R.id.btnDeletePicture3), findViewById(R.id.btnDeletePicture4)};
        for (int i=0;i<4;i++){
            final int index = i;
            btnDeletePictures[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    productPictures[index].setImageResource(R.drawable.uploadimg);
                    viewModel.deleteProductPictures(index);
                }
            });
        }

        VariasiAdapter adapter = new VariasiAdapter(new VariasiAdapter.deleteVariasi() {
            @Override
            public void onDelete(int index) {
                viewModel.deleteVariant(index);
            }
        }, new VariasiAdapter.updateVariasi() {
            @Override
            public void onUpdate(String id_variasi) {
                Intent updateIntent = new Intent(ProductActivity.this, AddVariasiActivity.class);
                updateIntent.putExtra("id_variasi", id_variasi);
                startActivityForResult(updateIntent, REQUEST_ADD_VARIANT);
            }
        });

        viewModel.getVariants().observe(this, variants -> {
            adapter.setVariants(variants);
            adapter.notifyDataSetChanged();
        });

        RecyclerView rvVariasi = findViewById(R.id.rvVariasi);
        rvVariasi.setAdapter(adapter);
        rvVariasi.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        MaterialButton btnAddVariasi = findViewById(R.id.btnAddVariasi);
        btnAddVariasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ProductActivity.this, AddVariasiActivity.class), REQUEST_ADD_VARIANT);
            }
        });


        String[] options = {"Makanan Anjing", "Makanan Kucing", "Makanan Kelinci", "Makanan Burung", "Makanan Ikan", "Makanan Hamster", "Makanan Reptil", "Pakan Ternak", "Peralatan Grooming", "Leash dan Handler", "Treats/Snack (Kudapan)", "Peralatan Kesehatan", "Mainan/Alat Ketangkasan", "Peralatan Kebersihan", "Kandang/Tempat Tidur"};
        AutoCompleteTextView autoKategori = findViewById(R.id.autoCompleteKategori);
        autoKategori.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
        autoKategori.setText(autoKategori.getAdapter().getItem(viewModel.getKategori()).toString(), false);
        autoKategori.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.inputs.setKategori(i);
            }
        });

        viewModel.inputs.getKategori().observe(this, kategori->{
            autoKategori.setText(options[kategori], false);
        });

        SwitchMaterial switchVariants = findViewById(R.id.variasiSwitch);
        switchVariants.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                viewModel.activateVariants(b);
            }
        });

        viewModel.isProductAdded().observe(this, added -> {
            Toast.makeText(this, "Berhasil "+viewModel.getLoadingTitle().getValue(), Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(ProductActivity.this, MainActivity.class);
            mainIntent.putExtra("Role", 0);
            mainIntent.putExtra("jumpTo", 2);
            startActivity(mainIntent);
        });

        MaterialButton btnDelete = findViewById(R.id.btnProductDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(ProductActivity.this)
                        .setTitle("Perhatian!")
                        .setMessage("Apakah anda yakin akan menghapus produk?")
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
            if(resultCode == RESULT_OK){
                //drpd menggunakan for, ambil index dari substring an request code
                if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    productPictures[currentImageNum].setImageURI(resultUri);
                    getImageViewBitmap(productPictures[currentImageNum], currentImageNum);
                }
                else if(String.valueOf(requestCode).contains("10")){
                    String requestNum = String.valueOf(requestCode).substring(2);
                    int reqNumInt = Integer.parseInt(requestNum) - 1;
                    Uri selectedImage = data.getData();
                    currentImageNum = reqNumInt;
                    CropImage.activity(selectedImage).start(ProductActivity.this);
                }
                else if(String.valueOf(requestCode).contains("1234")){
                    String requestNum = String.valueOf(requestCode).substring(4);
                    int reqNumInt = Integer.parseInt(requestNum) - 1;
                    File f = new File(photoPath);
                    Uri photoUri = Uri.fromFile(f);
                    currentImageNum = reqNumInt;
                    CropImage.activity(photoUri).start(ProductActivity.this);
                }
            }
            else{
                receiveDataFromIntent(data);
            }
        }
    }

    private void receiveDataFromIntent(Intent data){
        String nama = data.getStringExtra("nama");
        String harga = data.getStringExtra("harga");
        String stok = data.getStringExtra("stok");
        Bitmap picture = AddVariasiActivity.varianPicture;
        if(data.hasExtra("id_variasi")){
            viewModel.updateVariant(nama, harga, stok, picture, data.getStringExtra("id_variasi"));
        }
        else{
            viewModel.insertVariant(nama, harga, stok, picture);
        }
    }

    private void getImageViewBitmap(ImageView selectedPic, int index){
        selectedPic.setDrawingCacheEnabled(true);
        selectedPic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) selectedPic.getDrawable()).getBitmap();
        viewModel.uploadProductPicture(index, Bitmap.createScaledBitmap(bitmap, 500, 500, false));
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
        if(getIntent().hasExtra("id_produk")){
            new MaterialAlertDialogBuilder(ProductActivity.this)
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
}