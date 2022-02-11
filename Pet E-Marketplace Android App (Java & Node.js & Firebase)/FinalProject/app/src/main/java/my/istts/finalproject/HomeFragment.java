package my.istts.finalproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import my.istts.finalproject.R;

import my.istts.finalproject.adapters.ReccHotelAdapter;
import my.istts.finalproject.adapters.ReccProductsAdapter;
import my.istts.finalproject.databinding.FragmentHomeBinding;
import my.istts.finalproject.viewmodels.HomeViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        viewModel = new HomeViewModel(getActivity().getApplication());

        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Set articles
        ImageSlider articleSlider = view.findViewById(R.id.sliderArticles);

        viewModel.getArticleVMs().observe(this, articles->{
            ArrayList<SlideModel> slideModels = new ArrayList<>();
            for (int i=0;i<articles.size();i++){
                slideModels.add(new SlideModel(articles.get(i).getPicture(), articles.get(i).getTitle(), ScaleTypes.FIT));
            }
            articleSlider.setImageList(slideModels);
            articleSlider.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemSelected(int i) {
                    Intent articleIntent = new Intent(getActivity(), ArticleActivity.class);
                    articleIntent.putExtra("id_artikel", articles.get(i).getId());
                    startActivity(articleIntent);
                }
            });
        });


        MaterialButton btnAll = view.findViewById(R.id.btnAllArticles);
        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent articleIntent = new Intent(getActivity(), ArticlesListActivity.class);
                startActivity(articleIntent);
            }
        });

        //Set Reccomendation Rooms
        ReccHotelAdapter reccroomsadapter = new ReccHotelAdapter(new ReccHotelAdapter.hotelClickCallback() {
            @Override
            public void onHotelClick(String id) {
                Intent hotelIntent = new Intent(getActivity(), HotelDetailActivity.class);
                hotelIntent.putExtra("id_kamar", id);
                startActivityForResult(hotelIntent, 1);
            }
        });
        RecyclerView rvHotel = view.findViewById(R.id.rvReccHotel);
        rvHotel.setAdapter(reccroomsadapter);

        viewModel.getHotelVMs().observe(this, vms -> {
            reccroomsadapter.setHotelVMs(vms);
            reccroomsadapter.notifyDataSetChanged();
        });


        //Set Reccomendation Proudcts
        ReccProductsAdapter reccprodadapter = new ReccProductsAdapter(new ReccProductsAdapter.onProductClickCallback() {
            @Override
            public void onProductClick(String id) {
                Intent productIntent = new Intent(getActivity(), PetProductActivity.class);
                productIntent.putExtra("id_produk", id);
                startActivityForResult(productIntent, 2);
            }
        });
        RecyclerView rvProducts = view.findViewById(R.id.rvReccProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(getActivity(), 2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvProducts.setAdapter(reccprodadapter);

        viewModel.getProductsVMs().observe(this, vms -> {
            reccprodadapter.setProductVMs(vms);
            reccprodadapter.notifyDataSetChanged();
        });

        viewModel.getRecommendeds();

        MaterialButton[] btnQuickAccess = {view.findViewById(R.id.shopBtn), view.findViewById(R.id.groomingBtn), view.findViewById(R.id.hotelBtn),
                view.findViewById(R.id.clinicBtn), view.findViewById(R.id.adoptBtn), view.findViewById(R.id.discussBtn)};
        for (int i = 0; i < btnQuickAccess.length; i++) {
            final int index = i;
            btnQuickAccess[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    moveIntent(index);
                }
            });
        }
    }

    private void moveIntent(int idx){
        if(idx == 0){
            //shopping
            startActivity(new Intent(getContext(), ShopCategoryActivity.class));
        }
        else if(idx == 1){
            //grooming
            startActivity(new Intent(getContext(), GroomerListActivity.class));
        }
        else if(idx == 2){
            //hotel
            startActivity(new Intent(getContext(), HotelActivity.class));
        }
        else if(idx == 3){
            //klinik
            startActivity(new Intent(getContext(), ClinicListActivity.class));
        }
        else if(idx == 4){
            //klinik
            startActivity(new Intent(getContext(), PetAdoptActivity.class));
        }
        else{
            startActivity(new Intent(getContext(), DiscussListActivity.class));
        }
    }
}