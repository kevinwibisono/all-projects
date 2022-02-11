package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.SearchItemsAdapter;
import my.istts.finalproject.viewmodels.SearchViewModel;

public class SearchActivity extends AppCompatActivity {

    private Intent backIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SearchViewModel viewModel = new SearchViewModel();

        int tipe = getIntent().getIntExtra("tipe", 0);
        if(tipe == 0){
            backIntent = new Intent(SearchActivity.this, ShoppingActivity.class);
        }
        else if(tipe == 1){
            backIntent = new Intent(SearchActivity.this, HotelActivity.class);
        }
        else{
            backIntent = new Intent(SearchActivity.this, ArticlesListActivity.class);
        }

        viewModel.setQueriesList(tipe);

        ImageView ivBack = findViewById(R.id.ivSearchBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        EditText edSearch = findViewById(R.id.edSearch);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.arrangeSearchReccomendations(editable.toString());
            }
        });
        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == 6){
                    //enter
                    backIntent.putExtra("search", textView.getText().toString());
                    startActivity(backIntent);
                }
                return false;
            }
        });

        SearchItemsAdapter siAdapter = new SearchItemsAdapter(new SearchItemsAdapter.onButtonClickCallback() {
            @Override
            public void onButtonClick(int value, int jenis) {
                setBackIntentExtra(value, jenis);
                startActivity(backIntent);
            }
        });


        RecyclerView rvSearchItems = findViewById(R.id.rvSearchItems);
        rvSearchItems.setAdapter(siAdapter);
        rvSearchItems.setLayoutManager(new LinearLayoutManager(this));

        viewModel.getQueriesRecommendation().observe(this, queries->{
            siAdapter.setSearchItems(queries);
            siAdapter.notifyDataSetChanged();
        });
    }

    private void setBackIntentExtra(int value, int jenisSearchItem){
        if(jenisSearchItem == 0){
            backIntent.putExtra("kategori", value);
        }
        else if(jenisSearchItem == 1){
            backIntent.putExtra("fasilitas", value);
        }
        else if(jenisSearchItem == 2){
            backIntent.putExtra("topik_hewan", value);
        }
        else if(jenisSearchItem == 3){
            backIntent.putExtra("kategori", value);
        }
        else{
            backIntent.putExtra("target_pembaca", value);
        }
    }
}