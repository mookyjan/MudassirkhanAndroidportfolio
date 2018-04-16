package com.mudassirkhan.androidportfolio.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.adapters.PortfolioAdapter;
import com.mudassirkhan.androidportfolio.models.PortfolioItem;
import com.mudassirkhan.androidportfolio.ui.uielements.CircleTransform;
import com.mudassirkhan.androidportfolio.ui.uielements.RecyclerViewItemDecoration;
import com.mudassirkhan.androidportfolio.utils.DataUtils;
import com.mudassirkhan.androidportfolio.utils.ImplicitIntentUtils;
import com.mudassirkhan.androidportfolio.widget.AndroidPortfolioWidgetProvider;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PortfolioAdapter.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener,
        Toolbar.OnMenuItemClickListener {

    //Declaration of the Toolbar's member variable
    private Toolbar mToolbar;

    //Declaration of the Floating Action Menu's member variable
    private FloatingActionMenu mFabMenu;

    //Declaration of the Drawer Layout's member variable
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the AppBar, Toolbar, etc.
        setUpAppBar();

        //Setting up the Fab menu and Fab buttons
        setUpFabMenu();

        //Setting up the Navigation Drawer
        setUpNavigationDrawer();

        //Setting up the RecyclerView, its adapter, its Layout Manager, etc.
        setRecyclerView();

        //Saving the time when this activity gets opened
        AndroidPortfolioWidgetProvider.updateLastConnection(this);

        //Setting up the TapTarget Views, for the first time that the Main Activity is launched
        setUpTapTargetViews();

    }



    /* Setting up the UI */


    //Setting up the AppBar, Toolbar, etc.
    private void setUpAppBar() {

        //Getting Appbar's different view references
        AppBarLayout appbarLayout = (AppBarLayout) findViewById(R.id.main_activity_appbar_layout);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_activity_collapsing_toolbar_layout);
        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        ImageView toolbarPictureImageView = (ImageView) findViewById(R.id.main_activity_photo);

        //Setting up the Appbar
        mToolbar.inflateMenu(R.menu.main_activity_options_menu);
        mToolbar.setOnMenuItemClickListener(this);
        appbarLayout.setOnClickListener(this);

        //Binding the profile picture to the AppBar's ImageView
        Picasso.with(this).load(R.drawable.mudassir_khan_pic)
                .transform(new CircleTransform()).into(toolbarPictureImageView);

        //Setting up title and color to the Collapsing Toolbar
        collapsingToolbarLayout.setTitle(getString(R.string.main_activity_collapsed_toolbar_title));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));

    }


    //Setting up the Floating Action Menu/Buttons
    private void setUpFabMenu() {

        //Getting Floating Action Menu/Buttons's views references
        FloatingActionButton phoneFab = (FloatingActionButton) findViewById(R.id.main_activity_phone_fab);
        FloatingActionButton smsFab = (FloatingActionButton) findViewById(R.id.main_activity_sms_fab);
        FloatingActionButton emailFab = (FloatingActionButton) findViewById(R.id.main_activity_email_fab);
        mFabMenu = (FloatingActionMenu) findViewById(R.id.main_activity_fab_menu);

        //Setting Floating Action Buttons' Click Listeners
        phoneFab.setOnClickListener(this);
        smsFab.setOnClickListener(this);
        emailFab.setOnClickListener(this);
    }


    //Setting up the Navigation Drawer
    private void setUpNavigationDrawer() {

        //Getting the Navigation Drawer's views references
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navigationDrawerHeaderView = navigationView.getHeaderView(0);
        ImageView navigationDrawerImageView = (ImageView) navigationDrawerHeaderView.findViewById(R.id.navigation_drawer_imageView);

        //Binding the profile picture to the Navigation Drawer Header's ImageView
        Picasso.with(this).load(R.drawable.mudassir_khan_pic)
                .transform(new CircleTransform()).into(navigationDrawerImageView);

        //Setting up the Navigation Drawer's Toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Setting up the Navigation Drawer Toggle's Click Listener
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNavigationDrawerToggleClickEvent();
            }
        });

        //Setting the Navigation Drawer Item's Click Listeners
        navigationView.setNavigationItemSelectedListener(this);
    }


    //Setting up the RecyclerView
    private void setRecyclerView() {

        //Getting the RecyclerView's reference
        RecyclerView portfolioRecyclerView = (RecyclerView) findViewById(R.id.portfolio_recyclerview);

        //Getting the RecyclerView's data
        List<PortfolioItem> portfolioItems = DataUtils.getAllPortfolioItems(this);

        //Instantiating the PortfolioAdapter
        PortfolioAdapter portfolioAdapter = new PortfolioAdapter(portfolioItems, R.layout.portfolio_item, this, this);

        //Instantiating the LinearLayoutManager
        RecyclerView.LayoutManager portfolioLayoutManager = new LinearLayoutManager(this);

        //Wiring up the RecyclerView
        portfolioRecyclerView.setHasFixedSize(true);
        portfolioRecyclerView.setLayoutManager(portfolioLayoutManager);
        portfolioRecyclerView.setAdapter(portfolioAdapter);
        portfolioRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(this, R.drawable.recyclerviews_line_divider));

        //Setting up a Scroll Listener on the RecyclerView to hide the Floating Action Menu
        portfolioRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                closeFabMenu();
                if (dy > 0)
                    mFabMenu.hideMenu(true);
                else if (dy < 0)
                    mFabMenu.showMenu(true);
            }
        });
    }


    //Setting up the TapTarget Views, for the first time that the Main Activity is launched
    private void setUpTapTargetViews() {

        //Checking if the Main Activity has already been launched
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isMainActivityLaunchedForFirstTime = sharedPreferences.getBoolean(getString(R.string.tap_targets_main_activity_first_time), true);

        //If that is the case, we do not show the Tap Views again
        if (!isMainActivityLaunchedForFirstTime) {
            return;
        }

        //We get the right bottom corner coordinates
        Display display = getWindowManager().getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        int maxX = displaySize.x;
        int maxY = displaySize.y;

        //We create a Rect object with the approximate coordinates of the Floating Action Button
        Rect floatingActionMenuRect = new Rect(maxX - 150, maxY - 150, maxX, maxY);

        //We create our TapTarget sequence
        TapTargetSequence tapTargetSequence = new TapTargetSequence(this)
                .targets(
                        //Navigation Drawer Button
                        TapTarget.forToolbarNavigationIcon(mToolbar, getString(R.string.tap_targets_main_activity_navigation_drawer_button))
                                .cancelable(false),

                        //Share Button
                        TapTarget.forToolbarMenuItem(mToolbar, R.id.menu_item_share, getString(R.string.tap_targets_main_activity_share_button))
                                .cancelable(false),

                        //Floating Action Menu
                        TapTarget.forBounds(floatingActionMenuRect, getString(R.string.tap_targets_main_activity_floating_action_button))
                                .cancelable(false)
                                .transparentTarget(true)
                                .targetRadius(80));

        //We start the sequence
        tapTargetSequence.start();

        //We change the value of the corresponding SharedPreference
        if (isMainActivityLaunchedForFirstTime) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.tap_targets_main_activity_first_time), false);
            editor.apply();
        }

    }



    /* Handling events */


    //Handling Portfolio items clicks events
    @Override
    public void onItemClick(PortfolioItem portfolioItem, int position, View view) {

        //If the Floating Action Menu is closed, we open directly the next activity
        if (!mFabMenu.isOpened()) {
            callPortfolioItemActivity(view, portfolioItem);
        }
        //If the Floating Action Menu is open, we close it, and then we open the next activity after a certain delay
        else {
            closeFabMenu();
            final PortfolioItem delayPortfolioItem = portfolioItem;
            final View delayView = view;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    callPortfolioItemActivity(delayView, delayPortfolioItem);
                }
            }, getResources().getInteger(R.integer.delay_before_launching_intent_when_fab_menu_open));

        }

    }


    //Handling Navigation Drawer's items' click events
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //When a Navigation Item is selected, we open the corresponding link/intent
        int itemId = item.getItemId();
        String url = null;

        switch (itemId) {
            case R.id.nav_my_web_page:
                url = getString(R.string.navigation_drawer_my_website_url);
                ImplicitIntentUtils.openWebPage(this, url);
                break;
            case R.id.nav_linkedin:
                ImplicitIntentUtils.openLinkedin(this);
                break;
            case R.id.nav_github:
                url = getString(R.string.navigation_drawer_github_url);
                ImplicitIntentUtils.openWebPage(this, url);
                break;
            case R.id.nav_settings:
                launchSettingActivity();
                break;
        }

        //And we close the Navigation Drawer
        closeNavigationDrawer();
        return true;
    }


    //Handling different clicks throughout this activity
    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.main_activity_phone_fab:
                ImplicitIntentUtils.dialNumber(this);
                closeFabMenu();
                break;
            case R.id.main_activity_sms_fab:
                ImplicitIntentUtils.sendSms(this);
                closeFabMenu();
                break;
            case R.id.main_activity_email_fab:
                ImplicitIntentUtils.sendEmail(this);
                closeFabMenu();
                break;
            case R.id.main_activity_appbar_layout:
                closeFabMenu();
                break;
        }
    }


    //Handling the Options menu's item click events
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            //When clicked on the Share Icon
            case R.id.menu_item_share:
                closeFabMenu();
                String urlToShare = getString(R.string.intents_share_content_playstore_url) +
                        getApplicationContext().getPackageName();
                ImplicitIntentUtils.shareContent(this,
                        getString(R.string.intents_share_content, urlToShare));
        }

        return true;
    }


    //Handling clicks on the back button
    @Override
    public void onBackPressed() {

        //When clicked on the Back button, if the Navigation Drawer is open, we close it
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeNavigationDrawer();
        }
        //If the Floating Action Menu is open, we close it
        else if (mFabMenu.isOpened()) {
            closeFabMenu();
        }
        //Otherwise, we go to the previous activity
        else {
            super.onBackPressed();
        }
    }


    /* Helper methods */


    //Closing the Floating Action Menu
    private void closeFabMenu() {
        mFabMenu.close(true);
    }


    //Opening the Navigation Drawer
    private void openNavigationDrawer() {
        mDrawerLayout.openDrawer(Gravity.START);
    }


    //Closing the Navigation Drawer
    private void closeNavigationDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    //Handling clicks on the Navigation Drawer Toggle
    private void handleNavigationDrawerToggleClickEvent() {

        //If the Floating Action Menu is open, we close it, and then we open the Navigation Drawer after a certain delay
        if (mFabMenu.isOpened()) {
            closeFabMenu();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDrawerLayout.openDrawer(Gravity.START);
                }
            }, getResources().getInteger(R.integer.delay_before_launching_intent_when_fab_menu_open));
        }

        //If not, we open the Navigation Drawer directly
        else {
            openNavigationDrawer();
        }
    }


    //Handling Portfolio Items' click events, to open the corresponding activity
    private void callPortfolioItemActivity(View view, PortfolioItem portfolioItem) {

        Intent intent = null;

        if (portfolioItem.getTitle().equals(getString(R.string.portfolio_item_preferences_title))) {

            intent = new Intent(this, SettingsActivity.class);

        } else {

            intent = new Intent(MainActivity.this, PortfolioItemsActivity.class);

            //Passing information about the selected item to the receiving activity
            String selectedItemTitle = portfolioItem.getTitle();
            intent.putExtra(getString(R.string.main_from_detail_activity_title_tag), selectedItemTitle);

            int selectedItemIcon = portfolioItem.getIcon();
            intent.putExtra(getString(R.string.main_from_detail_activity_icon_tag), selectedItemIcon);

            int selectedItemIconBackgroundColor = portfolioItem.getIconBackgroundColor();
            intent.putExtra(getString(R.string.main_from_detail_activity_background_color_tag), selectedItemIconBackgroundColor);

        }
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }


    //Method that launches the Settings Activity
    private void launchSettingActivity() {
        Intent settingsActivityIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsActivityIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
