package my.istts.finalproject;

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
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ActivityRegisterBinding;
import my.istts.finalproject.viewmodels.RegisterViewModel;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class RegisterActivity extends AppCompatActivity {

    private int REQUEST_LOAD_PICTURE = 1;
    private int REQUEST_TAKE_PICTURE = 2;
    private RegisterViewModel viewModel;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new RegisterViewModel(getApplication());

        ActivityRegisterBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbRegis = findViewById(R.id.tbRegister);
        tbRegis.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(getIntent().hasExtra("email")){
            viewModel.prepareUpdate();
        }

        ProgressDialog loadScreen = new ProgressDialog(this);
        loadScreen.setCancelable(false);
        viewModel.getLoadingTitle().observe(this, loadScreen::setTitle);
        viewModel.isLoading().observe(this, loading -> {
            if(loading){
                loadScreen.show();
            }
            else{
                loadScreen.dismiss();
            }
        });

        TextInputLayout[] tlS = {findViewById(R.id.tlRegHP), findViewById(R.id.tlRegNama), findViewById(R.id.tlRegPass), findViewById(R.id.tlRegConfirmPass)};
        viewModel.getFieldErrors().observe(this, fieldError -> {
            for (int i=0; i<fieldError.length;i++){
                tlS[i].setError(fieldError[i]);
            }
            if(viewModel.getFocusedNumber() > 0){
                tlS[viewModel.getFocusedNumber()].getEditText().requestFocus();
            }
        });

        ScrollView scroller = findViewById(R.id.svRegister);
        viewModel.getPictureError().observe(this, error -> {
            if(!error.equals("")){
                scroller.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        VerificationBottomDialogFragment verifyDialog = new VerificationBottomDialogFragment();
        verifyDialog.setViewModel(viewModel);

        viewModel.isValid().observe(this, valid -> {
            verifyDialog.show(getSupportFragmentManager(), "");
            verifyDialog.setCancelable(false);
            //start new countdown
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    viewModel.reduceCountDown();
                    handler.postDelayed(this, 1000);
                }
            }, 1000);
        });


        viewModel.isRegistered().observe(this, verified -> {
//            verifyDialog.dismiss();
            Intent main = new Intent(RegisterActivity.this, MainActivity.class);
            if(getIntent().hasExtra("email")){
                main.putExtra("jumpTo", 3);
            }
            startActivity(main);
        });

        ImageView regPicture = findViewById(R.id.ivRegPhoto);
        regPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] picOptions = {"Camera", "Gallery"};
                new MaterialAlertDialogBuilder(RegisterActivity.this)
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

        viewModel.getUploadedImg().observe(this, regPicture::setImageBitmap);
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
                        "my.istts.finalproject.fileprovider",
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
                CropImage.activity(selectedImage).start(RegisterActivity.this);
            }
            else if(requestCode == REQUEST_TAKE_PICTURE){
                File photoFile = new File(photoPath);
                Uri takenPicUri = Uri.fromFile(photoFile);
                CropImage.activity(takenPicUri).start(RegisterActivity.this);
            }
            else{
                //dari library CropImage
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                ImageView selectedPic = findViewById(R.id.ivRegPhoto);
                selectedPic.setImageURI(resultUri);
                getImageViewBitmap(selectedPic);
            }
        }
    }

    private void getImageViewBitmap(ImageView selectedPic){
        selectedPic.setDrawingCacheEnabled(true);
        selectedPic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) selectedPic.getDrawable()).getBitmap();
        viewModel.setUploadedImg(Bitmap.createScaledBitmap(bitmap, 500, 500, false));
    }

}