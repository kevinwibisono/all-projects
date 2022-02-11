package my.istts.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import my.istts.finalproject.R;
import my.istts.finalproject.databinding.BottomDialogAdoptionBinding;

import my.istts.finalproject.viewmodels.AdoptListViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class AdoptBottomDialogFragment extends BottomSheetDialogFragment {

    private AdoptListViewModel viewModel;

    public void setViewModel(AdoptListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BottomDialogAdoptionBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_dialog_adoption, container,
                false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnApply = view.findViewById(R.id.btnApplyAdoptionFilters);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setFilters();
                AdoptBottomDialogFragment.this.dismiss();
            }
        });

        String[] options = {"Bulan", "Tahun"};
        AutoCompleteTextView tvGenderAdopt = view.findViewById(R.id.autoAdoptAgeFilter);
        tvGenderAdopt.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, options));
        tvGenderAdopt.setText(options[0], false);

        tvGenderAdopt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.setAgeType(i);
            }
        });
    }

    @BindingAdapter("android:checked")
    public static void setChecked(CompoundButton chk, boolean checked){
        chk.setChecked(checked);
    }
}
