package my.istts.finalproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import my.istts.finalproject.R;

import java.util.Objects;

public class ImgSliderAdapter extends PagerAdapter {
    private int[] images;
    private imageClickCallback callback;
    private LayoutInflater mLayoutInflater;

    public ImgSliderAdapter(Context ctx, int[] images, imageClickCallback c) {
        this.images = images;
        this.mLayoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.callback = c;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item_img_slider, container, false);

        // referencing the image view from the item.xml file
        final ImageView imageView = (ImageView) itemView.findViewById(R.id.ivHomeSlider);

        // setting the image in the imageView
        imageView.setImageResource(images[position]);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onImageClick(images[position]);
            }
        });

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }

    public interface imageClickCallback{
        void onImageClick(int a);
    }
}
