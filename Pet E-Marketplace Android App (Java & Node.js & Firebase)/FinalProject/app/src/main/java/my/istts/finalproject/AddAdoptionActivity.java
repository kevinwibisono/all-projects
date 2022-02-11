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
import my.istts.finalproject.databinding.ActivityAddAdoptionBinding;
import my.istts.finalproject.viewmodels.AddAdoptionViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddAdoptionActivity extends AppCompatActivity {

    private AddAdoptionViewModel viewModel;
    private int REQUEST_TAKE_PICTURE = 1;
    private int REQUEST_LOAD_PICTURE = 2;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new AddAdoptionViewModel(getApplication());
        ActivityAddAdoptionBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_adoption);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbAddAdopt = findViewById(R.id.tbAddAdopt);
        tbAddAdopt.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(getIntent().hasExtra("id_pet")){
            viewModel.getUpdatingAdoption(getIntent().getStringExtra("id_pet"));
        }

        String[] options = {"Jantan", "Betina"};
        AutoCompleteTextView tvGenderAdopt = findViewById(R.id.autoPetGender);
        tvGenderAdopt.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
        tvGenderAdopt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setJenis_kelamin(i);
            }
        });
        viewModel.getJenis_kelamin().observe(this, jenisKelamin->{
            tvGenderAdopt.setText(options[jenisKelamin], false);
        });

        String[] ageTypes = {"Bulan", "Tahun"};
        AutoCompleteTextView tvAgeType = findViewById(R.id.autoPetAge);
        tvAgeType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ageTypes));
        tvAgeType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setSatuan_umur(i);
            }
        });
        viewModel.getSatuan_umur().observe(this, satuan->{
            tvAgeType.setText(ageTypes[satuan], false);
        });

        String[] species = {"Anjing", "Kucing", "Kelinci", "Burung", "Ikan", "Hamster", "Reptil", "Lainnya"};
        AutoCompleteTextView tvSpecies = findViewById(R.id.autoPetSpecies);
        tvSpecies.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, species));
        tvSpecies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setJenis_hewan(i);
            }
        });
        viewModel.getJenis_hewan().observe(this, jenisHewan->{
            tvSpecies.setText(species[jenisHewan], false);
        });


        ScrollView sv = findViewById(R.id.svAddAdoption);

        TextView tvErrorPicture = findViewById(R.id.tvErrorAddAdopt);
        TextInputLayout[] tlS = {findViewById(R.id.tlAddAdoptNama), findViewById(R.id.tlAddAdoptUmur),
                findViewById(R.id.tlAddAdoptRas), findViewById(R.id.tlAddAdoptDeskripsi)};
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
        viewModel.getLoadingTitle().observe(this, loadScreen::setTitle);
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

        ImageView ivPet = findViewById(R.id.ivPetAdoptImage);
        ivPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] picOptions = {"Camera", "Gallery"};
                new MaterialAlertDialogBuilder(AddAdoptionActivity.this)
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

        viewModel.getPicture().observe(this, ivPet::setImageBitmap);

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
                CropImage.activity(selectedImage).start(AddAdoptionActivity.this);
            }
            else if(requestCode == REQUEST_TAKE_PICTURE){
                File photoFile = new File(photoPath);
                Uri takenPicUri = Uri.fromFile(photoFile);
                CropImage.activity(takenPicUri).start(AddAdoptionActivity.this);
            }
            else{
                //dari library CropImage
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                ImageView selectedPic = findViewById(R.id.ivPetAdoptImage);
                selectedPic.setImageURI(resultUri);
                getImageViewBitmap(selectedPic);
            }
        }
    }

    private void getImageViewBitmap(ImageView selectedPic){
        selectedPic.setDrawingCacheEnabled(true);
        selectedPic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) selectedPic.getDrawable()).getBitmap();
        viewModel.setPicture(Bitmap.createScaledBitmap(bitmap, 500, 500, false));
    }
}