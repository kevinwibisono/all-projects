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
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ActivityAddDiscussBinding;
import my.istts.finalproject.viewmodels.AddDiscussViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddDiscussActivity extends AppCompatActivity {

    private AddDiscussViewModel viewModel;
    private int REQUEST_TAKE_PICTURE = 1;
    private int REQUEST_LOAD_PICTURE = 2;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new AddDiscussViewModel(getApplication());
        ActivityAddDiscussBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_discuss);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbAddDiscuss = findViewById(R.id.tbAddDiscuss);
        tbAddDiscuss.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ScrollView sv = findViewById(R.id.svAddDiscuss);

        String[] species = {"Anjing", "Kucing", "Kelinci", "Burung", "Ikan", "Hamster", "Reptil", "Lainnya"};
        AutoCompleteTextView tvSpecies = findViewById(R.id.autoDiscussPetType);
        tvSpecies.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, species));
        tvSpecies.setText(species[0], false);
        tvSpecies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setJenisHewan(i);
            }
        });

        TextView tvErrorPicture = findViewById(R.id.tvErrorAddDiscuss);
        TextInputLayout[] tlS = {findViewById(R.id.tlAddDiscussTitle), findViewById(R.id.tlAddDiscussDesc)};
        viewModel.getErrors().observe(this, errors->{
            for (int i = 0; i < errors.length; i++) {
                if(i == 0){
                    tvErrorPicture.setText(errors[0]);
                }
                else{
                    tlS[i-1].setError(errors[i]);
                }
            }
        });

        viewModel.getFocusedNum().observe(this, focus->{
            if(focus.equals(0)){
                sv.fullScroll(View.FOCUS_UP);
            }
            else{
                tlS[focus-1].getEditText().requestFocus();
            }
        });

        ProgressDialog loadScreen = new ProgressDialog(this);
        loadScreen.setCancelable(false);
        loadScreen.setTitle("Menambahkan Diskusi...");
        viewModel.isLoading().observe(this, loading -> {
            if(loading){
                loadScreen.show();
            }
            else{
                loadScreen.dismiss();
            }
        });

        viewModel.isDoneAdding().observe(this, added->{
            finish();
        });

        ImageView ivPet = findViewById(R.id.ivPetDiscussImage);
        ivPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] picOptions = {"Camera", "Gallery"};
                new MaterialAlertDialogBuilder(AddDiscussActivity.this)
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
                CropImage.activity(selectedImage).start(AddDiscussActivity.this);
            }
            else if(requestCode == REQUEST_TAKE_PICTURE){
                File photoFile = new File(photoPath);
                Uri takenPicUri = Uri.fromFile(photoFile);
                CropImage.activity(takenPicUri).start(AddDiscussActivity.this);
            }
            else{
                //dari library CropImage
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                ImageView selectedPic = findViewById(R.id.ivPetDiscussImage);
                selectedPic.setImageURI(resultUri);
                getImageViewBitmap(selectedPic);
            }
        }
    }

    private void getImageViewBitmap(ImageView selectedPic){
        selectedPic.setDrawingCacheEnabled(true);
        selectedPic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) selectedPic.getDrawable()).getBitmap();
        viewModel.setPicture(Bitmap.createScaledBitmap(bitmap, 600, 400, false));
    }
}