package my.istts.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.ActivityAddAppoItemBinding;

import my.istts.finalproject.viewmodels.AddAppoPetViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class AddAppoItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AddAppoPetViewModel viewModel = new AddAppoPetViewModel();
        ActivityAddAppoItemBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_add_appo_item);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbAddAppo = findViewById(R.id.tbAddAppoItem);
        tbAddAppo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String[] types = {"Anjing", "Kucing", "Kelinci", "Burung", "Ikan", "Hamster", "Reptil", "Ternak", "Lainnya"};
        AutoCompleteTextView autoPetType = findViewById(R.id.autoJenisHewan);
        autoPetType.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types));
        autoPetType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setPetType(i);
            }
        });

        String[] ageTypes = {"Bulan", "Tahun"};
        AutoCompleteTextView autoPetAge = findViewById(R.id.autoJenisUmur);
        autoPetAge.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ageTypes));
        autoPetAge.setText(ageTypes[0], false);
        autoPetAge.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setPetAgeType(ageTypes[i]);
            }
        });

        TextInputLayout[] tlS = {findViewById(R.id.tlAppoPetName), findViewById(R.id.tlAppoPetAge), findViewById(R.id.tlAppoPetType)};

        viewModel.getErrors().observe(this, errors->{
            for (int i = 0; i < errors.length; i++) {
                tlS[i].setError(errors[i]);
            }
        });

        viewModel.getTlNumber().observe(this, tlNumber->{
            tlS[tlNumber].getEditText().requestFocus();
        });

        MaterialButton btnAdd = findViewById(R.id.btnAddAppoPet);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewModel.isValid()){
                    Intent resultintent = new Intent();
                    resultintent.putExtra("nama", viewModel.petName.getValue());
                    resultintent.putExtra("usia", viewModel.petAge.getValue()+" "+viewModel.getPetAgeType());
                    resultintent.putExtra("tipe", viewModel.getTypes().getValue());
                    setResult(2, resultintent);
                    finish();
                }
            }
        });
    }
}