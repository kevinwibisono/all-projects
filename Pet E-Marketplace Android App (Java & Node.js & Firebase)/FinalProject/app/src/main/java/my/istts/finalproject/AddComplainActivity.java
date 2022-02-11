package my.istts.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ComplainProductAdapter;
import my.istts.finalproject.databinding.ActivityAddComplainBinding;
import my.istts.finalproject.viewmodels.AddComplainViewModel;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddComplainActivity extends AppCompatActivity {

    private int[] REQUEST_LOAD_PICTURES = {100, 101, 102, 103};
    private int[] REQUEST_TAKE_PICTURES = {110, 111, 112, 113};
    private AddComplainViewModel viewModel;
    private String photoPath = "";
    private int currentImageNum = 0;
    private ImageView[] ivBukti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new AddComplainViewModel();
        ActivityAddComplainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_complain);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbAddComplain = findViewById(R.id.tbAddComplain);
        tbAddComplain.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ComplainProductAdapter adapter = new ComplainProductAdapter(new ComplainProductAdapter.onCheckedChanged() {
            @Override
            public void onChecked(int index, boolean checked) {
                viewModel.includeProduct(index, checked);
            }
        }, true);

        RecyclerView rvProduct = findViewById(R.id.rvProductComplain);
        rvProduct.setAdapter(adapter);

        viewModel.getPjListOfItems(getIntent().getStringExtra("id_pj"));

        viewModel.getPjItems().observe(this, pjItems->{
            adapter.setItemPJs(pjItems);
            adapter.notifyDataSetChanged();
        });

        ScrollView svPage = findViewById(R.id.svAddComplain);
        TextInputLayout[] tlS = {findViewById(R.id.tlKomplainKeluhan), findViewById(R.id.tlKomplainJumlah)};
        TextView[] tvErrors = {findViewById(R.id.tvErrorKomplainBukti), findViewById(R.id.tvErrorKomplainProduk)};

        viewModel.getErrors().observe(this, errors->{
            for (int i = 0; i < errors.length; i++) {
                if(i < 1){
                    tlS[i].setError(errors[i]);
                }
                else{
                    tvErrors[i-1].setText(errors[i]);
                }
            }
        });

        viewModel.getFocusedNumber().observe(this, focusedField->{
            if(focusedField < 1){
                svPage.scrollTo(0, tlS[focusedField].getTop());
            }
            else{
                svPage.scrollTo(0, tvErrors[focusedField-1].getTop());
            }
        });

        tlS[1].getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.changePersen(editable.toString());
            }
        });

        MaterialButton btnAddComplain = findViewById(R.id.btnComplainAdd);
        btnAddComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tlS[1].getEditText().setText(viewModel.persen.getValue());
                if(!viewModel.fieldError()){
                    new MaterialAlertDialogBuilder(AddComplainActivity.this)
                            .setTitle("Perhatian!")
                            .setMessage("Apakah anda yakin akan mengajukan komplain senilai Rp "+ThousandSeparator.getTS(viewModel.getJumlahKembali())+" ?")
                            .setNegativeButton("Tidak", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .setPositiveButton("Ya", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                                viewModel.beginUploadImages(getIntent().getStringExtra("id_pj"));
                            })
                            .show();
                }
            }
        });

        ivBukti = new ImageView[]{findViewById(R.id.proof1), findViewById(R.id.proof2), findViewById(R.id.proof3), findViewById(R.id.proof4)};
        for (int i=0;i<4;i++){
            final int index = i;
            ivBukti[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] picOptions = {"Camera", "Gallery"};
                    new MaterialAlertDialogBuilder(AddComplainActivity.this)
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
        
        MaterialButton[] btnDeleteBukti = {findViewById(R.id.btnDeleteBukti1), findViewById(R.id.btnDeleteBukti2),
                findViewById(R.id.btnDeleteBukti3), findViewById(R.id.btnDeleteBukti4)};
        for (int i=0;i<4;i++){
            final int index = i;
            btnDeleteBukti[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ivBukti[index].setImageResource(R.drawable.uploadimg);
                    viewModel.deletePictureBitmap(index);
                }
            });
        }

        ProgressDialog loadDialog = new ProgressDialog(this);
        loadDialog.setCancelable(false);
        loadDialog.setTitle("Mengajukan Komplain...");
        viewModel.getAddLoading().observe(this, loading -> {
            if(loading){
                loadDialog.show();
            }
            else{
                loadDialog.dismiss();
            }
        });

        viewModel.getAdded().observe(this, added->{
            Toast.makeText(this, "Berhasil Mengajukan Komplain", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(data != null){
                if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    Uri resultUri = result.getUri();
                    ivBukti[currentImageNum].setImageURI(resultUri);
                    getImageViewBitmap(ivBukti[currentImageNum], currentImageNum);
                }
                else{
                    String requestNum = String.valueOf(requestCode).substring(2);
                    int reqNumInt = Integer.parseInt(requestNum);
                    Uri selectedImage = data.getData();
                    currentImageNum = reqNumInt;
                    CropImage.activity(selectedImage).start(AddComplainActivity.this);
                }
            }
            else{
                String requestNum = String.valueOf(requestCode).substring(2);
                int reqNumInt = Integer.parseInt(requestNum);
                File f = new File(photoPath);
                Uri photoUri = Uri.fromFile(f);
                currentImageNum = reqNumInt;
                CropImage.activity(photoUri).start(AddComplainActivity.this);
            }
        }
    }

    private void getImageViewBitmap(ImageView selectedPic, int index){
        selectedPic.setDrawingCacheEnabled(true);
        selectedPic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) selectedPic.getDrawable()).getBitmap();
        viewModel.setPictureBitmap(index, Bitmap.createScaledBitmap(bitmap, 500, 500, false));
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
                        "my.istts.finalproject.fileprovider",
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
}