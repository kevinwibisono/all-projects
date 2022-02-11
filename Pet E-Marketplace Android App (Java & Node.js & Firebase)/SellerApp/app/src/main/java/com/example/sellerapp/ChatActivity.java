package com.example.sellerapp;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sellerapp.adapters.ChatAdapter;
import com.example.sellerapp.databinding.ActivityChatBinding;
import com.example.sellerapp.interfaces.chatChooseHotel;
import com.example.sellerapp.interfaces.chatChooseOrder;
import com.example.sellerapp.interfaces.chatChooseProduct;
import com.example.sellerapp.interfaces.chatListItem;
import com.example.sellerapp.interfaces.chatListOrder;
import com.example.sellerapp.interfaces.chatListPictures;
import com.example.sellerapp.viewmodels.ChatViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatActivity extends AppCompatActivity implements chatListPictures, chatListItem, chatListOrder, chatChooseProduct, chatChooseHotel, chatChooseOrder {

    private int REQUEST_TAKE_PICTURE = 1;
    private int REQUEST_LOAD_PICTURE = 2;
    private int role = -1;
    private ChatProductBottomDialogFragment fragmentListProduct;
    private ChatHotelBottomDialogFragment fragmentListHotel;
    private ChatOrderBottomDialogFragment fragmentListOrders;
    private ChatViewModel viewModel;
    private String photoPath;
    private final Handler handler = new Handler();
    private final int delay = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ChatViewModel(getApplication());
        ActivityChatBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        if(getIntent().hasExtra("id_pj")){
            chooseOrder(getIntent().getStringExtra("id_pj"));
        }

        MaterialToolbar tbChat = findViewById(R.id.tbChat);
        tbChat.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacksAndMessages(null);
                finish();
            }
        });

        ChatAdapter adapter = new ChatAdapter(new ChatAdapter.onShowImage() {
            @Override
            public void showImage(String url) {
                Intent productIntent = new Intent(ChatActivity.this, ImageViewerActivity.class);
                productIntent.putExtra("url", url);
                startActivity(productIntent);
            }
        }, new ChatAdapter.onShowProduct() {
            @Override
            public void showProduct(String id) {
                Intent productIntent = new Intent(ChatActivity.this, PreviewProductActivity.class);
                productIntent.putExtra("id_produk", id);
                startActivity(productIntent);
            }
        }, new ChatAdapter.onShowRoom() {
            @Override
            public void showRoom(String id) {
                Intent roomIntent = new Intent(ChatActivity.this, HotelPreviewActivity.class);
                roomIntent.putExtra("id_kamar", id);
                startActivity(roomIntent);
            }
        }, new ChatAdapter.onShowOrder() {
            @Override
            public void showOrder(String id, int jenis) {
                if(jenis < 3){
                    Intent orderIntent = new Intent(ChatActivity.this, OrderDetailActivity.class);
                    orderIntent.putExtra("id_pj", id);
                    startActivity(orderIntent);
                }
                else{
                    Intent orderIntent = new Intent(ChatActivity.this, AppointmentDetailActivity.class);
                    orderIntent.putExtra("id_pj", id);
                    startActivity(orderIntent);
                }
            }
        });

        RecyclerView rvChat = findViewById(R.id.rvChat);
        rvChat.setAdapter(adapter);

        rvChat.setLayoutManager(new LinearLayoutManager(this));

        //1 kali saja dijalankan, untuk mendapatkan seluruh chat di room
        viewModel.getChatVMs().observe(this, vms -> {
            adapter.setCurrent_hp(viewModel.getEmail());
            adapter.setChatVM(vms);
            adapter.notifyDataSetChanged();
        });

        //dijalankan setiap kali ada chat baru, agar tinggal langsung ditambahkan ke adapter
        viewModel.getLatestChat().observe(this, chatVM ->{
            adapter.addChatVM(chatVM);
            adapter.notifyDataSetChanged();
            rvChat.smoothScrollToPosition(adapter.getItemCount()-1);
        });

        if(getIntent().hasExtra("id_room")){
            String convID = getIntent().getStringExtra("id_room");
            viewModel.getAllChatsInCR(convID);
        }
        else{
            viewModel.getChatRoom(getIntent().getStringExtra("lawan_bicara"));
        }

        //setiap 1 detik sekali read/baca setiap message
        handler.postDelayed(new Runnable() {
            public void run() {
                viewModel.readMyChats();
                handler.postDelayed(this, delay);
            }
        }, delay);


        ImageView ivPerson = findViewById(R.id.ivChatProfilePic);
        viewModel.getPersonPic().observe(this, pic -> {
            Glide.with(this).load(pic).into(ivPerson);
        });

        ChatBottomDialogFragment fragment = new ChatBottomDialogFragment();
        viewModel.getRole().observe(ChatActivity.this, role -> {
            fragment.setRole(role);
            this.role = role;
        });

        ShapeableImageView ivAdd = findViewById(R.id.chatAddCustom);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.show(getSupportFragmentManager(), "");
            }
        });

        ProgressDialog sendingLoading = new ProgressDialog(this);
        sendingLoading.setCancelable(false);
        sendingLoading.setTitle("Mengirim pesan");
        viewModel.isSending().observe(this, sending -> {
            if(sending){
                sendingLoading.show();
            }
            else{
                sendingLoading.dismiss();
                LinearLayout mainLayoutChat = findViewById(R.id.mainLayoutChat);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLayoutChat.getWindowToken(), 0);
            }
        });
    }

    @Override
    public void showItems() {
        //tunjukkan bottom dialog untuk pilih produk/kamar
        if(role == 0){
            fragmentListProduct = new ChatProductBottomDialogFragment();
            fragmentListProduct.setViewModel(viewModel);
            viewModel.getSellerProducts();
            fragmentListProduct.show(getSupportFragmentManager(), "");
        }
        else if(role == 2){
            fragmentListHotel = new ChatHotelBottomDialogFragment();
            fragmentListHotel.setViewModel(viewModel);
            viewModel.getHotelRooms();
            fragmentListHotel.show(getSupportFragmentManager(), "");
        }
    }

    @Override
    public void showOrders() {
        //tampilkan bottom dialog untuk pilih order
        fragmentListOrders = new ChatOrderBottomDialogFragment();
        fragmentListOrders.setViewModel(viewModel);
        viewModel.getConnectedOrders();
        fragmentListOrders.show(getSupportFragmentManager(), "");
    }

    @Override
    public void showPictures() {
        //tampilkan image upload
        String[] picOptions = {"Camera", "Gallery"};
        new MaterialAlertDialogBuilder(ChatActivity.this)
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
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(uploadIntent, REQUEST_LOAD_PICTURE);
                        }
                    }
                })
                .show();
    }

    //untuk menerima hasil dari upload atau ambil gambar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_TAKE_PICTURE){
            data = new Intent();
        }

        if(data != null && resultCode == RESULT_OK){
            if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();

                //tampilkan gambar di imageview
                ImageView chatPic = findViewById(R.id.chatSendPicture);
                chatPic.setImageURI(resultUri);

                //munculkan bagian gambar diatas isian chat
                toggleCardSend(0);

                //kirim gambar dr imageview ke viewmodel
                getImageViewBitmap(chatPic);
            }
            else if(requestCode == REQUEST_LOAD_PICTURE){
                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage).start(ChatActivity.this);
            }
            else if(requestCode == REQUEST_TAKE_PICTURE){
                File f = new File(photoPath);
                Uri photoUri = Uri.fromFile(f);
                CropImage.activity(photoUri).start(ChatActivity.this);
            }
        }
    }

    //untuk toggle item yang akan diikutsertakan dalam chat, antara gambar/produk&kamar/pesanan
    private void toggleCardSend(int showNumber){
        ImageView selectedPic = findViewById(R.id.chatSendPicture);
        LinearLayout selectedProd = findViewById(R.id.chatSendProduct);
        LinearLayout selectedRoom = findViewById(R.id.chatSendHotel);
        LinearLayout selectedOrder = findViewById(R.id.chatSendOrder);
        viewModel.setItemShown(true);

        if(showNumber == 0){
            //picture
            selectedPic.setVisibility(View.VISIBLE);
            selectedProd.setVisibility(View.GONE);
            selectedRoom.setVisibility(View.GONE);
            selectedOrder.setVisibility(View.GONE);
        }
        else if(showNumber == 1){
            //product
            selectedPic.setVisibility(View.GONE);
            selectedProd.setVisibility(View.VISIBLE);
            selectedRoom.setVisibility(View.GONE);
            selectedOrder.setVisibility(View.GONE);
        }
        else if(showNumber == 2){
            //order
            selectedPic.setVisibility(View.GONE);
            selectedProd.setVisibility(View.GONE);
            selectedRoom.setVisibility(View.GONE);
            selectedOrder.setVisibility(View.VISIBLE);
        }
        else if(showNumber == 3){
            //room
            selectedPic.setVisibility(View.GONE);
            selectedProd.setVisibility(View.GONE);
            selectedRoom.setVisibility(View.VISIBLE);
            selectedOrder.setVisibility(View.GONE);
        }
    }

    @Override
    public void chooseProduct(String id_product) {
        //tampilkan produk dan dismiss bottom fragment Product
        viewModel.setChosenProduct(id_product);
        ImageView ivProductSent = findViewById(R.id.ivSendProductPic);
        viewModel.getChosenItemPic().observe(this, picUrl -> {
            if(!picUrl.equals("")){
                Glide.with(this).load(picUrl).into(ivProductSent);
            }
        });

        toggleCardSend(1);
        if(fragmentListProduct != null){
            fragmentListProduct.dismiss();
        }
    }

    @Override
    public void chooseHotel(String id_kamar) {
        viewModel.setChosenHotel(id_kamar);
        ImageView ivHotelSent = findViewById(R.id.ivSendHotelPic);
        viewModel.getChosenItemPic().observe(this, picUrl -> {
            if(!picUrl.equals("")){
                Glide.with(this).load(picUrl).into(ivHotelSent);
            }
        });

        toggleCardSend(3);
        if(fragmentListHotel != null){
            fragmentListHotel.dismiss();
        }
    }

    @Override
    public void chooseOrder(String id_pj) {
        //tampilkan order dan dismiss bottom fragment Order
        viewModel.setChosenOrder(id_pj);
        ImageView ivOrder = findViewById(R.id.ivChatOrderPic);
        viewModel.getChosenItemPic().observe(this, picUrl -> {
            if(!picUrl.equals("")){
                Glide.with(this).load(picUrl).into(ivOrder);
            }
        });

        toggleCardSend(2);
        if(fragmentListOrders != null){
            fragmentListOrders.dismiss();
        }
    }

    private void getImageViewBitmap(ImageView sentPic){
        sentPic.setDrawingCacheEnabled(true);
        sentPic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) sentPic.getDrawable()).getBitmap();
        viewModel.setUploadedPic(Bitmap.createScaledBitmap(bitmap, 500, 500, false));
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

    @Override
    public void onBackPressed() {
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }


}