package schaefer.anatoli.caloriedeficit.ui.stats;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import schaefer.anatoli.caloriedeficit.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_weight, R.string.tab_text_eaten, R.string.tab_text_sport, R.string.tab_text_compare_images};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position){
            case 0:
                return StatsWeightFragment.newInstance();
            case 1:
                return StatsCaloriesEatenFragment.newInstance();
            case 2:
                return StatsSportFragment.newInstance();
            case 3:
                return BeforeAfterCompareFragment.newInstance();
            default:
                return new Fragment(); //einfach leeres Fragment zurückgeben
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 4 total pages.
        return 4;
    }
}