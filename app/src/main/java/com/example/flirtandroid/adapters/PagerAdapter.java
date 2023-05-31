package com.example.flirtandroid.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.flirtandroid.R;

public class PagerAdapter extends androidx.viewpager.widget.PagerAdapter {

    private int[] images = {R.drawable.startedimage, R.drawable.startedimage, R.drawable.startedimage};

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        imageView.setImageResource(images[position]);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }
}
