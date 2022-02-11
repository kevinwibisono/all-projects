package my.istts.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import my.istts.finalproject.R;

import my.istts.finalproject.interfaces.showAllMenus;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements showAllMenus {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        viewModel = new MainViewModel(getApplication());

//        WorkManager.getInstance(this)
//                .cancelAllWork();

        PeriodicWorkRequest cancelRequest =
                new PeriodicWorkRequest.Builder(AutoCancelFinishWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager
                .getInstance(this)
                .enqueueUniquePeriodicWork("cancel_request", ExistingPeriodicWorkPolicy.KEEP, cancelRequest);

        PeriodicWorkRequest activateRequest =
                new PeriodicWorkRequest.Builder(AutoStartWorker.class, 15, TimeUnit.MINUTES)
                        .build();

        WorkManager
                .getInstance(this)
                .enqueueUniquePeriodicWork("activate_request", ExistingPeriodicWorkPolicy.KEEP, activateRequest);

//        startService(new Intent(this, TimeService.class));

        BottomNavigationView btmNav = findViewById(R.id.mainNav);

//        viewModel.getOrderCount().observe(this, count->{
//            BadgeDrawable badge = btmNav.getOrCreateBadge(R.id.mainOrders);
//            if(count > 0){
//                badge.setVisible(true);
//            }
//            else{
//                badge.setVisible(false);
//            }
//        });
//
//        viewModel.getCartChatCount().observe(this, count->{
//            BadgeDrawable badge = btmNav.getOrCreateBadge(R.id.mainAccount);
//            if(count > 0){
//                badge.setVisible(true);
//            }
//            else{
//                badge.setVisible(false);
//            }
//        });

        int[] itemIds = {R.id.mainHome, R.id.mainMenus, R.id.mainOrders, R.id.mainAccount};

        Fragment[] fragments = {new HomeFragment(), new MenusFragment(), new OrdersFragment(), new AccountFragment()};
        int jump = getIntent().getIntExtra("jumpTo", 0);
        changeFragment(fragments[jump]);
        btmNav.setSelectedItemId(itemIds[jump]);

        btmNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.mainHome){
                    Fragment home = new HomeFragment();
                    changeFragment(home);
                    return true;
                }
                else if(item.getItemId() == R.id.mainMenus){
                    Fragment menus = new MenusFragment();
                    changeFragment(menus);
                    return true;
                }
                else if(item.getItemId() == R.id.mainOrders){
                    Fragment orders = new OrdersFragment();
                    changeFragment(orders);
                    return true;
                }
                else if(item.getItemId() == R.id.mainAccount){
                    Fragment account = new AccountFragment();
                    changeFragment(account);
                    return true;
                }
                return false;
            }
        });

    }

    private void changeFragment(Fragment frag){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrame, frag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .setReorderingAllowed(true)
                .commit();
    }

    @Override
    public void showAllMenus() {
        BottomNavigationView btmNav = findViewById(R.id.mainNav);
        btmNav.setSelectedItemId(R.id.mainMenus);
        Fragment menusFragment = new MenusFragment();
        changeFragment(menusFragment);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }
}