package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.VoucherAdapter;
import com.google.android.material.appbar.MaterialToolbar;

public class VoucherListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_list);

        MaterialToolbar tbVoucher = findViewById(R.id.tbVoucherList);
        tbVoucher.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        VoucherAdapter adapter = new VoucherAdapter(new VoucherAdapter.chooseVoucher() {
            @Override
            public void onChosen(int position) {

            }
        }, new VoucherAdapter.seeVoucherDetail() {
            @Override
            public void onDetail(String id) {
                Intent detailVoucher = new Intent(VoucherListActivity.this, VoucherDetailActivity.class);
                detailVoucher.putExtra("id_promo", id);
                startActivity(detailVoucher);
            }
        }, this);

        RecyclerView rvListVoucher = findViewById(R.id.rvVoucherList);
        rvListVoucher.setAdapter(adapter);
        rvListVoucher.setLayoutManager(new LinearLayoutManager(this));
    }
}