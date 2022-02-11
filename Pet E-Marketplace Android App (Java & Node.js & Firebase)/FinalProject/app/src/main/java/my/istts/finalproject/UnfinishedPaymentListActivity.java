package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.UnfinishedPaymentAdapter;

import my.istts.finalproject.databinding.ActivityUnfinishedPaymentListBinding;

import my.istts.finalproject.viewmodels.UnfinishedPaymentViewModel;
import com.google.android.material.appbar.MaterialToolbar;

public class UnfinishedPaymentListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UnfinishedPaymentViewModel viewModel = new UnfinishedPaymentViewModel(getApplication());
        ActivityUnfinishedPaymentListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_unfinished_payment_list);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbUnfinished = findViewById(R.id.tbUnfinishedPayments);
        tbUnfinished.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        UnfinishedPaymentAdapter adapter = new UnfinishedPaymentAdapter(new UnfinishedPaymentAdapter.showDetailPayment() {
            @Override
            public void show(String id_payment) {
                Intent detailIntent = new Intent(UnfinishedPaymentListActivity.this, FinishPaymentActivity.class);
                detailIntent.putExtra("id_payment", id_payment);
                startActivity(detailIntent);
            }
        });

        RecyclerView rvPayments = findViewById(R.id.rvUnfinishedPayment);
        rvPayments.setAdapter(adapter);

        viewModel.getPayVMs().observe(this, vms->{
            adapter.setPaymentVMs(vms);
            adapter.notifyDataSetChanged();
        });

        viewModel.getUnfinishedPayments();
    }
}