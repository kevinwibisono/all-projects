package my.istts.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import my.istts.finalproject.R;

import my.istts.finalproject.adapters.AppointmentPetAdapter;
import my.istts.finalproject.databinding.ActivityAppointmentBinding;

import my.istts.finalproject.viewmodels.AppointmentViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

public class AppointmentActivity extends AppCompatActivity {

    private int REQUEST_CHOOSE_ADDRESS = 1;
    private int REQUEST_ADD_APPOITEM = 2;
    private AppointmentViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new AppointmentViewModel(getApplication());
        ActivityAppointmentBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_appointment);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        MaterialToolbar tbAppo = findViewById(R.id.tbAddAppo);
        tbAppo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        viewModel.getKlinikDetail(getIntent().getStringExtra("hp_klinik"));

        AppointmentPetAdapter adapter = new AppointmentPetAdapter(new AppointmentPetAdapter.onPetDelete() {
            @Override
            public void onDelete(int position) {
                viewModel.deleteAppoPet(position);
            }
        });

        RecyclerView rvPetList = findViewById(R.id.rvAppoPetList);
        rvPetList.setLayoutManager(new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvPetList.setAdapter(adapter);

        viewModel.getPets().observe(this, vms->{
            adapter.setPetVMs(vms);
            adapter.notifyDataSetChanged();
        });

        MaterialButton btnAddAppoItem = findViewById(R.id.btnAddAppoItem);
        btnAddAppoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addItemIntent = new Intent(AppointmentActivity.this, AddAppoItemActivity.class);
                startActivityForResult(addItemIntent, REQUEST_ADD_APPOITEM);
            }
        });

        AutoCompleteTextView tvJenisAppo = findViewById(R.id.autoJenisAppo);
        viewModel.getClinicAppoTypes().observe(this, types->{
            tvJenisAppo.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, types));
            tvJenisAppo.setText(types.get(0), false);
        });

        tvJenisAppo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setAppointmentType(i);
            }
        });

        long tomorrow = System.currentTimeMillis() + (24 * 3600 * 1000);

        //date picker
        TextInputEditText edDate = findViewById(R.id.edAppoDate);
        edDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog datePicker = new DatePickerDialog(
                        AppointmentActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                edDate.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                                viewModel.setTanggal(myCalendar.getTime());
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));

                datePicker.getDatePicker().setMinDate(tomorrow);
                datePicker.show();
            }
        });

        //time picker
        TextInputEditText edTime = findViewById(R.id.edAppoTime);
        edTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12).setMinute(0)
                        .setTitleText("Tentukan Waktu Janji Temu")
                        .build();
                timePicker.show(AppointmentActivity.this.getSupportFragmentManager(), "");
                timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        edTime.setText(timePicker.getHour()+":"+timePicker.getMinute());
                        viewModel.setJam(timePicker.getHour()+":"+timePicker.getMinute());
                    }
                });
            }
        });

        TextInputEditText edAddress = findViewById(R.id.edAppoAlamat);
        edAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addressIntent = new Intent(AppointmentActivity.this, AddressChooseActivity.class);
                startActivityForResult(addressIntent, REQUEST_CHOOSE_ADDRESS);
            }
        });


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AppointmentActivity.this);
        builder.setTitle("Jadwal Buka-Tutup Klinik");
        builder.setNegativeButton("Ok", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        viewModel.getClinicSchedules().observe(AppointmentActivity.this, builder::setMessage);

        TextView seeClinicSchedule = findViewById(R.id.tvSeeClinicSchedule);
        seeClinicSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.show();
            }
        });

        NestedScrollView svAppo = findViewById(R.id.svAppointment);
        TextInputLayout[] tlS = {findViewById(R.id.tlAppoName), findViewById(R.id.tlAppoHP), null, findViewById(R.id.tlAppoAddr), null, findViewById(R.id.tlAppoKeluhan)};
        TextView[] tvError = {findViewById(R.id.tvAppoErrorPets), findViewById(R.id.tvAppoErrorDate)};
        viewModel.getErrors().observe(this, errors->{
            for (int i = 0; i < errors.length; i++) {
                if(i != 2 && i != 4){
                    tlS[i].setError(errors[i]);
                }
                else{
                    int index = (i/2) - 1;
                    tvError[index].setText(errors[i]);
                }
            }
        });

        viewModel.getTlNumber().observe(this, errorNum->{
            if(errorNum != 2 && errorNum != 4){
                svAppo.scrollTo(0, tlS[errorNum].getTop());
            }
            else{
                int index = (errorNum/2) - 1;
                svAppo.scrollTo(0, tvError[index].getTop());
            }
        });

        ProgressDialog loadingScreen = new ProgressDialog(this);
        loadingScreen.setTitle("Membuat Janji Temu....");
        loadingScreen.setCancelable(false);
        viewModel.isAddLoading().observe(this, loading ->{
            if(loading){
                loadingScreen.show();
            }
            else{
                loadingScreen.dismiss();
                Toast.makeText(this, "Janji Temu Behasil Dijadwalkan", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            if(requestCode == REQUEST_CHOOSE_ADDRESS){
                String id = data.getStringExtra("id_alamat");
                viewModel.setAlamat(id);
            }
            else if(requestCode == REQUEST_ADD_APPOITEM){
                String nama = data.getStringExtra("nama");
                String usia = data.getStringExtra("usia");
                String tipe = data.getStringExtra("tipe");
                viewModel.addNewAppoPet(tipe, nama, usia);
            }
        }
    }
}