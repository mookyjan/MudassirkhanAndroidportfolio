package com.mudassirkhan.androidportfolio.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.models.Tab;
import com.mudassirkhan.androidportfolio.ui.fragments.BaseDatabaseFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.ContentProviderBisFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.FileFragment;
import com.mudassirkhan.androidportfolio.ui.fragments.PicassoFragment;
import com.mudassirkhan.androidportfolio.utils.DataUtils;
import com.mudassirkhan.androidportfolio.utils.PermissionsUtils;
import com.mudassirkhan.androidportfolio.widget.AndroidPortfolioWidgetProvider;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.util.ArrayList;
import java.util.List;

import static com.mudassirkhan.androidportfolio.ui.fragments.ContentProviderBisFragment.PERMISSION_WRITE_CALENDAR;
import static com.mudassirkhan.androidportfolio.ui.fragments.ContentProviderBisFragment.PERMISSION_WRITE_CONTACTS;
import static com.mudassirkhan.androidportfolio.ui.fragments.FileFragment.PERMISSION_WRITE_EXTERNAL_STORAGE;
import static com.mudassirkhan.androidportfolio.ui.fragments.PicassoFragment.PERMISSION_READ_EXTERNAL_MEMORY;

public class PortfolioItemsActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,
        View.OnClickListener,
        Toolbar.OnMenuItemClickListener {

    //Constant Strings used as flags for getting information about the selected fragment
    public final static String SELECTED_FRAGMENT_DESCRIPTION = "selected_fragment_description";
    public final static String SELECTED_FRAGMENT_POSSIBLE_ACTIONS = "selected_fragment_possible_actions";
    public final static String SELECTED_FRAGMENT_REPEAT = "selected_fragment_repeat";

    //Declaration of the AppBar's member variables
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;

    //Declaration of the ViewPager's member variables
    private ViewPager mViewPager;
    private BasePortfolioItemPagerAdapter mAdapter;
    private Fragment mCurrentlySelectedFragment;

    //Declaration of the SnackBar's member variables
    private Snackbar mSnack;
    private boolean isSnackBarShowing;
    private TextView mSnackBarDescriptionTextView;
    private TextView mSnackBarPossibleActionsTextView;
    private TextView mSnackBarRepeatingFragmentLabelTextView;
    private TextView mSnackBarRepeatingFragmentTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setting up the theme of the Activity, depending on the data passed in the intent. Must be done before the activity's layout is inflated
        setUpTheme();

        //Inflating the activity's layout
        setContentView(R.layout.activity_portfolio_elements);

        //Setting up the AppBar, Toolbar, etc.
        setUpAppbar();

        //Setting up the SnackBar
        setUpSnackBar();

        //Setting up the TabLayout
        setUpTabLayout();

        //Setting up the ViewPager
        setUpViewPager();

        //Binding titles to TabLayout and Fragments to ViewPager
        setUpViewPagerAndTabsData();

        //Correcting offset bug on Floating Action Button
        addingOffsetToFabs();

        //Saving the time when this activity gets opened
        AndroidPortfolioWidgetProvider.updateLastConnection(this);

        //Setting up the TapTarget Views, for the first time that the PortfolioItem Activity is launched
        setUpTapTargetViews();

    }


    /* Setting up the UI */


    //Setting up the theme of the Activity, depending on the data passed in the intent.
    private void setUpTheme() {
        //We get the intent sent from Main Activity
        Intent intent = getIntent();
        if (intent != null) {
            //We get the theme corresponding to the title, and we set it to the activity
            String activityTitle = intent.getStringExtra(getString(R.string.main_from_detail_activity_title_tag));
            int activityStyle = DataUtils.getActivityStyle(this, activityTitle);
            setTheme(activityStyle);
        }
    }


    //Setting up the AppBar
    private void setUpAppbar() {

        //Getting the Appbar's views references
        mAppBarLayout = (AppBarLayout) findViewById(R.id.activity_portfolio_element_appbar);
        mToolbar = (Toolbar) findViewById(R.id.portfolio_element_toolbar);
        ImageView iconImageView = (ImageView) findViewById(R.id.activity_portfolio_element_icon_imageview);
        TextView titleTextView = (TextView) findViewById(R.id.activity_portfolio_element_title_textview);

        //Setting up the Toolbar
        mToolbar.inflateMenu(R.menu.portfolio_elements_activity_options_menu);
        mToolbar.setNavigationIcon(R.drawable.back_arrow);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setOnMenuItemClickListener(this);

        //Retrieving data sent from Main Activity
        Intent intent = getIntent();
        String title = intent.getStringExtra(getString(R.string.main_from_detail_activity_title_tag));
        int icon = intent.getIntExtra(getString(R.string.main_from_detail_activity_icon_tag), 0);
        int iconBackgroundColor = ContextCompat.getColor(this, intent.getIntExtra(getString(R.string.main_from_detail_activity_background_color_tag), 0));

        //Binding the icon and title to the Toolbar
        titleTextView.setText(title);
        iconImageView.setImageResource(icon);
        iconImageView.setColorFilter(iconBackgroundColor, PorterDuff.Mode.MULTIPLY);
        iconImageView.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC);
    }


    //Setting up the SnackBar
    private void setUpSnackBar() {

        //Getting the SnackBar CoordinatorLayout's reference
        CoordinatorLayout snackBarCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.snack_bar_limit);

        //Setting up the SnackBar
        mSnack = Snackbar.make(snackBarCoordinatorLayout, null, Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout snackBarLayout = (Snackbar.SnackbarLayout) mSnack.getView();
        View snackBarRootView = getLayoutInflater().inflate(R.layout.snack_bar, null);
        snackBarLayout.addView(snackBarRootView, 0);

        //Getting the SnackBar Views' references
        mSnackBarDescriptionTextView = (TextView) snackBarRootView.findViewById(R.id.snack_bar_description);
        mSnackBarPossibleActionsTextView = (TextView) snackBarRootView.findViewById(R.id.snack_bar_possible_actions);
        mSnackBarRepeatingFragmentLabelTextView = (TextView) snackBarRootView.findViewById(R.id.snack_bar_repeating_fragment_label);
        mSnackBarRepeatingFragmentTextView = (TextView) snackBarRootView.findViewById(R.id.snack_bar_repeating_fragment);

        //Making the SnackBar fill the whole ViewPager area
        Snackbar.SnackbarLayout.LayoutParams param = new Snackbar.SnackbarLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.BOTTOM
        );
        snackBarLayout.setLayoutParams(param);

        //Disabling the SnackBar's swipe listener
        snackBarLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ((CoordinatorLayout.LayoutParams) mSnack.getView().getLayoutParams()).setBehavior(null);
                return true;
            }
        });

    }


    //Setting up the TabLayout
    private void setUpTabLayout() {

        //Getting the TabLayout's reference
        mTabLayout = (TabLayout) findViewById(R.id.portfolio_element_tablayout);

        //Setting up the TabLayout's Listener
        mTabLayout.addOnTabSelectedListener(this);

        //Making the TabLayout items horizontally scrollable
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }


    //Setting up the ViewPager
    private void setUpViewPager() {

        //Getting the ViewPager's reference
        mViewPager = (ViewPager) findViewById(R.id.portfolio_element_viewpager);

        //Instantiating the ViewPager's adapter and setting it to the ViewPager
        mAdapter = new BasePortfolioItemPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        //Setting up the ViewPager's Listener
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }


    //Binding titles to TabLayout and Fragments to ViewPager
    private void setUpViewPagerAndTabsData() {
        //Getting the title of the Activity
        Intent intent = getIntent();
        String portfolioItemTitle = intent.getStringExtra(getString(R.string.main_from_detail_activity_title_tag));

        //According to this title, we get the corresponding Tabs and set them up
        List<Tab> tabList = DataUtils.getTabList(this, portfolioItemTitle);
        addDataToViewPager(tabList);
    }


    //Correcting offset bug on Floating Action Button
    private void addingOffsetToFabs() {
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mViewPager.getLayoutParams();
                layoutParams.setMargins(0, 0, 0, mToolbar.getMeasuredHeight() + verticalOffset);
                mViewPager.requestLayout();
            }
        });
    }


    //Setting up the TapTarget Views, for the first time that the PortfolioItems Activity is launched
    private void setUpTapTargetViews() {

        //Checking if the PortfolioItems Activity has already been launched
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isPortfolioItemsActivityLaunchedForFirstTime = sharedPreferences.getBoolean(getString(R.string.tap_targets_portfolio_item_activity_first_time), true);

        //If that is the case, we do not show the Tap Views again
        if (!isPortfolioItemsActivityLaunchedForFirstTime) {
            return;
        }

        //We create our TapTarget sequence
        TapTargetSequence tapTargetSequence = new TapTargetSequence(this)
                .targets(
                        //Info button
                        TapTarget.forToolbarMenuItem(mToolbar, R.id.menu_item_info, getString(R.string.tap_targets_portfolio_item_activity_info_button))
                                .outerCircleColor(R.color.colorPrimaryDark)
                                .cancelable(false),

                        //Settings overflow menu
                        TapTarget.forToolbarOverflow(mToolbar, getString(R.string.tap_targets_portfolio_item_activity_settings_overflow))
                                .outerCircleColor(R.color.colorPrimaryLight)
                                .cancelable(false));

        //We start the sequence
        tapTargetSequence.start();

        //We change the value of the corresponding SharedPreference
        if (isPortfolioItemsActivityLaunchedForFirstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.tap_targets_portfolio_item_activity_first_time), false);
            editor.apply();
        }

    }



    /* Handling events */


    //Handling clicks on the back arrow button
    @Override
    public void onClick(View view) {

        //If we click on the back arrow button, we go back to the Main Activity
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
        } else {
            NavUtils.navigateUpTo(this, upIntent);
        }
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }


    //Handling the Options menu's items click events
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            //In case the info button is clicked
            case R.id.menu_item_info:
                //If the SnackBar is not showing, we open it
                if (!isSnackBarShowing) {
                    openSnackBar();
                }
                //If the SnackBar is showing, we close it
                else {
                    closeSnackBar();
                }
                break;

            //If we click on the settings option, we go to the Settings Activity
            case R.id.menu_item_settings:
                startSettingsActivity();
                break;
        }

        return true;
    }


    //Handling clicks on the back button
    @Override
    public void onBackPressed() {
        //If the SnackBar is showing, we close it
        if (isSnackBarShowing) {
            closeSnackBar();
        }
        //If not, we finish the activity
        else {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


    //Handling when a new Tab is select
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //First of all ,we close the SnackBar, in case it is open
        if (isSnackBarShowing) {
            closeSnackBar();
        }

        //We set the ViewPager to the current position of the TabLayout and determine which is the currently selected fragment
        mViewPager.setCurrentItem(tab.getPosition());
        mCurrentlySelectedFragment = mAdapter.getItem(mViewPager.getCurrentItem());

        //If the currently selected fragment inherits from BaseDatabaseFragment (and is not RealmFragment), then we start its Loader every time it gets selected
        //Indeed, since the Native SQLite fragment and the Content Provider Fragment share the same data, their data must be updated when we land to any one of them
        if (mCurrentlySelectedFragment != null
                && mCurrentlySelectedFragment instanceof BaseDatabaseFragment
                && mCurrentlySelectedFragment.isAdded()
                && !mCurrentlySelectedFragment.getClass().getSimpleName().equals(getString(R.string.realm_fragment_name))) {
            ((BaseDatabaseFragment) mCurrentlySelectedFragment).startLoader();
        }

        //If the currently selected fragment is ContentProviderBis, and that we do not have the WRITE_CALENDAR permission, we ask for it.
        else if (mCurrentlySelectedFragment != null
                && mCurrentlySelectedFragment instanceof ContentProviderBisFragment
                && mCurrentlySelectedFragment.isAdded()
                && ((ContentProviderBisFragment) mCurrentlySelectedFragment).mCalendarLinearLayout.getVisibility() == View.VISIBLE) {

            //If we do not have the permission, we ask for it
            if (!PermissionsUtils.checkIfHasPermission(this, Manifest.permission.WRITE_CALENDAR)) {
                PermissionsUtils.requestPermission(this, Manifest.permission.WRITE_CALENDAR, PERMISSION_WRITE_CALENDAR);
            }
            //If we do have it, we set the calendar stuff on the ContentProviderBisFragment
            else {
                ((ContentProviderBisFragment) mCurrentlySelectedFragment).setUpCalendarRelated();
            }
        }

        //Every time we select a new tab, we change the content of the SnackBar's Views
        Bundle bundle = DataUtils.getFragmentInformation(this, tab.getText().toString());
        if (bundle != null) {
            String fragmentDescription = bundle.getString(SELECTED_FRAGMENT_DESCRIPTION);
            if (fragmentDescription != null) {
                mSnackBarDescriptionTextView.setText(DataUtils.fromHtml(fragmentDescription));
            }
            String fragmentPossibleActions = bundle.getString(SELECTED_FRAGMENT_POSSIBLE_ACTIONS);
            if (fragmentPossibleActions != null) {
                mSnackBarPossibleActionsTextView.setText(DataUtils.fromHtml(fragmentPossibleActions));
            }
            String repeatingFragmentText = bundle.getString(SELECTED_FRAGMENT_REPEAT);
            if (repeatingFragmentText != null) {
                mSnackBarRepeatingFragmentLabelTextView.setVisibility(View.VISIBLE);
                mSnackBarRepeatingFragmentTextView.setVisibility(View.VISIBLE);
                mSnackBarRepeatingFragmentTextView.setText(repeatingFragmentText);
            } else {
                mSnackBarRepeatingFragmentLabelTextView.setVisibility(View.GONE);
                mSnackBarRepeatingFragmentTextView.setVisibility(View.GONE);
            }
        }
    }


    //Handling callbacks for when the user accepts or denies a permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //If the currently selected fragment is an instance of ContentProviderBisFragment, we check the permission result for it
        if (mCurrentlySelectedFragment != null
                && mCurrentlySelectedFragment instanceof ContentProviderBisFragment
                && mCurrentlySelectedFragment.isAdded()) {

            switch (requestCode) {
                //We check if it is a result for the WRITE_CALENDAR permission
                case PERMISSION_WRITE_CALENDAR:
                    //If the user grants us with the permission, we set the calendar stuff on the ContentProviderBisFragment
                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        ((ContentProviderBisFragment) mCurrentlySelectedFragment).setUpCalendarRelated();
                    } else {
                        //If not, we show them a "Permission denied" toast
                        PermissionsUtils.showPermissionDeniedToast(this);
                    }
                    break;

                //We check if it is a result for the WRITE_CONTACTS permission
                case PERMISSION_WRITE_CONTACTS:
                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        //If the user grants us with the permission, we show them a "Permission granted" toast
                        PermissionsUtils.showPermissionGrantedToast(this);
                    } else {
                        //If not, we show them a "Permission denied" toast
                        PermissionsUtils.showPermissionDeniedToast(this);
                    }
                    break;

                default:
                    break;

            }

        }

        //If the currently selected fragment is an instance of FileFragment, we check the permission result for it
        else if (mCurrentlySelectedFragment != null
                && mCurrentlySelectedFragment instanceof FileFragment
                && mCurrentlySelectedFragment.isAdded()) {

            switch (requestCode) {
                //We check that it is a result for the WRITE_EXTERNAL_STORAGE permission
                case PERMISSION_WRITE_EXTERNAL_STORAGE: {
                    //If the user grants us with the permission, we launch the camera Activity
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        ((FileFragment) mCurrentlySelectedFragment).launchCameraActivity();
                    } else {
                        //If not, we show them a "Permission denied" toast
                        PermissionsUtils.showPermissionDeniedToast(this);
                    }
                    break;
                }
            }

        }

        //If the currently selected fragment is an instance of PicassoFragment, we check the permission result for it
        else if (mCurrentlySelectedFragment != null
                && mCurrentlySelectedFragment instanceof PicassoFragment
                && mCurrentlySelectedFragment.isAdded()) {

            switch (requestCode) {
                //We check that it is a result for the READ_EXTERNAL_MEMORY permission
                case PERMISSION_READ_EXTERNAL_MEMORY:
                    //If the user grants us with the permission, we set up the Picasso fragment's RecyclerView with local storage images
                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                        ((PicassoFragment) mCurrentlySelectedFragment).setRecyclerViewWithLocalStorageImages();
                    } else {
                        //If not, we show them a "Permission denied" toast
                        PermissionsUtils.showPermissionDeniedToast(this);
                    }
                    break;
                default:
                    break;
            }

        }

    }


    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //Not useful in our case
    }


    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //Not useful in our case
    }




    /* Helper methods */


    //Opening the SnackBar
    private void openSnackBar() {
        isSnackBarShowing = true;
        mSnack.show();
    }


    //Closing the SnackBar
    private void closeSnackBar() {
        mSnack.dismiss();
        isSnackBarShowing = false;
    }


    //Method that launches the Settings Activity
    private void startSettingsActivity() {
        Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsActivityIntent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    //Method that returns the FragmentStatePagerAdapter's instance to the fragment that calls it
    public FragmentStatePagerAdapter getFragmentStatePagerAdapter() {
        return mAdapter;
    }


    //Method that returns the ViewPager's instance to the fragment that calls it
    public ViewPager getViewPager() {
        return mViewPager;
    }


    //Method used to populate the TabLayout and the ViewPager with corresponding data and fragment
    private void addDataToViewPager(List<Tab> tabList) {
        //We iterate through all the items of the list of Tabs
        for (int i = 0; i < tabList.size(); i++) {
            //We get the current Tab
            Tab currentTab = tabList.get(i);
            //We get its fragment and its title
            Fragment currentFragment = currentTab.getFragment();
            String currentTitle = currentTab.getTabName();
            //We set the fragment to the ViewPager and the title to the TabLayout
            mAdapter.addFragment(currentFragment);
            mTabLayout.addTab(mTabLayout.newTab().setText(currentTitle));
        }
        mViewPager.setOffscreenPageLimit(mTabLayout.getTabCount() - 1);
        mAdapter.notifyDataSetChanged();
    }


    //Inner class defining the BasePortfolioItemPagerAdapter
    private class BasePortfolioItemPagerAdapter extends FragmentStatePagerAdapter {

        //Declaration of BasePortfolioItemPagerAdapter's member variable
        private final List<Fragment> mFragmentList = new ArrayList<>();

        //BasePortfolioItemPagerAdapter's Constructor
        BasePortfolioItemPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //Getting the currently selected Fragment
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        //Getting the count of tabs
        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        //Method used to add a fragment to the list
        void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

}

