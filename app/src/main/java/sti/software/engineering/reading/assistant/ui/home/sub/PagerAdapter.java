package sti.software.engineering.reading.assistant.ui.home.sub;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import sti.software.engineering.reading.assistant.ui.home.sub.camera.CameraFragment;
import sti.software.engineering.reading.assistant.ui.home.sub.gallery.GalleryFragment;
import sti.software.engineering.reading.assistant.ui.home.sub.read.ReadFragment;

public class PagerAdapter extends FragmentStateAdapter {

    private final int numberOfTabs;

//    public PagerAdapter(@NonNull FragmentManager fm, int behavior, int numberOfTabs) {
//        super(fm, behavior);
//        this.numberOfTabs = numberOfTabs;
//    }

    public PagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int numberOfTabs) {
        super(fragmentManager, lifecycle);
        this.numberOfTabs = numberOfTabs;
    }

    //    @NonNull
//    @Override
//    public Fragment getItem(int position) {
//
//        switch (position) {
//            case 0:
//                return new HomeFragment();
//            case 1:
//                return new CameraFragment();
//            case 2:
//                return new GalleryFragment();
//        }
//
//        return new HomeFragment();
//    }
//
//    @Override
//    public int getCount() {
//        return numberOfTabs;
//    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ReadFragment();
            case 1:
                return new CameraFragment();
            case 2:
                return new GalleryFragment();
        }

        return new ReadFragment();
    }

    @Override
    public int getItemCount() {
        return numberOfTabs;
    }
}
