package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.RekeningAdapter;
import my.istts.finalproject.databinding.ActivityRekeningListBinding;

import my.istts.finalproject.viewmodels.RekeningListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class RekeningListActivity extends AppCompatActivity {

    private RekeningListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekening_list);

        viewModel = new RekeningListViewModel(getApplication());
        ActivityRekeningListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_rekening_list);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbRekeningList = findViewById(R.id.tbRekeningList);
        tbRekeningList.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RekeningAdapter adapter = new RekeningAdapter(new RekeningAdapter.chooseRekening() {
            @Override
            public void onChosen(int rekId) {
                viewModel.chooseRek(rekId);
            }
        });

        RecyclerView rvRekening = findViewById(R.id.rvChooseRekening);
        rvRekening.setAdapter(adapter);
        viewModel.getRekVMs().observe(this, vms -> {
            adapter.setRekVMs(vms);
            adapter.notifyDataSetChanged();
        });

        FloatingActionButton addBtn = findViewById(R.id.btnAddRekening);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(RekeningListActivity.this, AddRekeningActivity.class);
                startActivity(addIntent);
            }
        });

        viewModel.isChooseRekDone().observe(this, done->{
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.getRekeningList();

    }
}