package com.example.sellerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.sellerapp.interfaces.switchToChat;
import com.example.sellerapp.interfaces.switchToOrder;

public class MainActivity extends AppCompatActivity implements switchToChat, switchToOrder{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView mainNav = findViewById(R.id.mainNav);
        mainNav.getMenu().clear();
        int role = getIntent().getIntExtra("Role", -1);
        if(role == 0){
            //shopping
            mainNav.inflateMenu(R.menu.pet_shop_menu);

            int[] menuShopIDs = {R.id.menuShopHome, R.id.menuShopChat, R.id.menuShopProduk, R.id.menuShopPesanan, R.id.menuShopAkun};
            Fragment[] menuShopFragments = {new ShopHomeFragment(), new ConversationsFragment(), new ProductListFragment(),
                    new OrderListFragment(), new ShopAccountFragment()};

            int jump = getIntent().getIntExtra("jumpTo", 0);
            changeFragment(menuShopFragments[jump]);
            mainNav.setSelectedItemId(menuShopIDs[jump]);

            mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int fragNum = findIndex(menuShopIDs, item.getItemId());
                    changeFragment(menuShopFragments[fragNum]);
                    return true;
                }
            });
        }
        else if(role == 1){
            //grooming
            mainNav.inflateMenu(R.menu.pet_grooming_menu);

            int[] menuGroomIDs = {R.id.groomHome, R.id.groomChat, R.id.groomOrder, R.id.groomAkun};
            Fragment[] menuGroomFragments = {new GroomingHomeFragment(), new ConversationsFragment(), new OrderListFragment(),
                    new GroomAccountFragment()};

            int jump = getIntent().getIntExtra("jumpTo", 0);
            changeFragment(menuGroomFragments[jump]);
            mainNav.setSelectedItemId(menuGroomIDs[jump]);

            mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int fragNum = findIndex(menuGroomIDs, item.getItemId());
                    changeFragment(menuGroomFragments[fragNum]);
                    return true;
                }
            });
        }
        else if(role == 2){
            //hotel
            mainNav.inflateMenu(R.menu.pet_hotel_menu);

            int[] menuHotelIDs = {R.id.menuHotelHome, R.id.menuHotelChat, R.id.menuHotelRoom, R.id.menuHotelBook, R.id.menuHotelAkun};
            Fragment[] menuHotelFrags = {new HotelHomeFragment(), new ConversationsFragment(), new HotelListFragment(),
                    new OrderListFragment(), new HotelAccountFragment()};

            int jump = getIntent().getIntExtra("jumpTo", 0);
            changeFragment(menuHotelFrags[jump]);
            mainNav.setSelectedItemId(menuHotelIDs[jump]);

            mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int fragNum = findIndex(menuHotelIDs, item.getItemId());
                    changeFragment(menuHotelFrags[fragNum]);
                    return true;
                }
            });
        }
        else if(role == 3){
            //clinic
            mainNav.inflateMenu(R.menu.pet_clinic_menu);

            int[] menuClinicIDs = {R.id.clinicHome, R.id.clinicChat, R.id.clinicJanji, R.id.clinicAkun};
            Fragment[] menuClinicFragments = {new ClinicHomeFragment(), new ConversationsFragment(), new OrderListFragment(),
                    new ClinicAccountFragment()};

            int jump = getIntent().getIntExtra("jumpTo", 0);
            changeFragment(menuClinicFragments[jump]);
            mainNav.setSelectedItemId(menuClinicIDs[jump]);

            mainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int fragNum = findIndex(menuClinicIDs, item.getItemId());
                    changeFragment(menuClinicFragments[fragNum]);
                    return true;
                }
            });
        }

//        String password = "abcd";
//        Log.d("UUID", UUID.nameUUIDFromBytes(password.getBytes()).toString());
    }

    private void changeFragment(Fragment frag){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrame, frag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .commit();
    }

    @Override
    public void showChats() {
        int role = getIntent().getIntExtra("Role", -1);
        BottomNavigationView mainNav = findViewById(R.id.mainNav);
        int[] idMenus = {R.id.menuShopChat, R.id.groomChat, R.id.menuHotelChat, R.id.clinicChat};
        mainNav.setSelectedItemId(idMenus[role]);
        changeFragment(new ConversationsFragment());
    }

    @Override
    public void showOrders() {
        int role = getIntent().getIntExtra("Role", -1);
        BottomNavigationView mainNav = findViewById(R.id.mainNav);
        int[] idMenus = {R.id.menuShopPesanan, R.id.groomOrder, R.id.menuHotelBook, R.id.clinicJanji};
        mainNav.setSelectedItemId(idMenus[role]);
        changeFragment(new OrderListFragment());
    }

    private int findIndex(int[] array, int find){
        for (int i = 0; i < array.length; i++) {
            if(array[i] == find){
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}