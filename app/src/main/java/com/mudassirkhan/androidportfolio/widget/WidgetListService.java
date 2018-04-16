package com.mudassirkhan.androidportfolio.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.mudassirkhan.androidportfolio.R;
import com.mudassirkhan.androidportfolio.application.MyApplication;
import com.mudassirkhan.androidportfolio.models.PortfolioItem;
import com.mudassirkhan.androidportfolio.utils.DataUtils;
import com.mudassirkhan.androidportfolio.utils.ImageUtils;

import java.util.List;

//Service that returns a WidgetListRemoteViewsFactory, which acts as an adapter for our Widget's ListView
public class WidgetListService extends RemoteViewsService {

    //Method that returns a WidgetListRemoteViewsFactory object
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetListRemoteViewsFactory(this.getApplicationContext());
    }


    private class WidgetListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

        //Declaration of WidgetListRemoteViewsFactory's member variables
        private Context mContext;
        private List<PortfolioItem> mPortfolioItems;


        //WidgetListRemoteViewsFactory's constructor
        WidgetListRemoteViewsFactory(Context context) {
            mContext = context;
        }


        //Triggered when the data set changes (when notifyDataSetChanged() is triggered on the remote adapter).
        @Override
        public void onDataSetChanged() {
            mPortfolioItems = DataUtils.getAllPortfolioItems(mContext);

            //We remove the Settings item from the list, since we do not want it to appear in the Widget
            for(int i = 0; i < mPortfolioItems.size(); i++){
                if(mPortfolioItems.get(i).getTitle()
                        .equals(MyApplication.getContext().getString(R.string.portfolio_item_preferences_title))){
                    mPortfolioItems.remove(i);
                };
            }

        }


        //Method that returns the number of item that contains our data source
        @Override
        public int getCount() {
            if (mPortfolioItems == null) {
                return 0;
            }
            return mPortfolioItems.size();
        }


        //Method used to bind data and return RemoveViews
        @Override
        public RemoteViews getViewAt(int position) {

            //We get the current PortfolioItem object
            PortfolioItem portfolioItem = mPortfolioItems.get(position);

            //We get the values out of it
            String title = portfolioItem.getTitle();
            String description = portfolioItem.getDescription();
            int icon = portfolioItem.getIcon();
            int iconBackgroundColor = portfolioItem.getIconBackgroundColor();

            //We change the icon's color
            int colorToUse = ContextCompat.getColor(mContext, iconBackgroundColor);
            Bitmap bitmap = ImageUtils.getCircleBitmap(ImageUtils.getColoredBitmapFromResource(mContext, icon, colorToUse));

            //We create a RemoteViews object and pass in the values that we got right above
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.portfolio_item);
            remoteViews.setTextViewText(R.id.portfolio_item_title, title);
            remoteViews.setTextViewText(R.id.portfolio_item_description, description);
            remoteViews.setImageViewBitmap(R.id.portfolio_item_icon, bitmap);

            //We create a fillInIntent and pass in the information that we will need to open the PortfolioItem Activity, with the desired values
            Intent fillInIntent = new Intent();
            String selectedItemTitle = portfolioItem.getTitle();
            fillInIntent.putExtra(getString(R.string.main_from_detail_activity_title_tag), selectedItemTitle);
            int selectedItemIcon = portfolioItem.getIcon();
            fillInIntent.putExtra(getString(R.string.main_from_detail_activity_icon_tag), selectedItemIcon);
            fillInIntent.putExtra(getString(R.string.main_from_detail_activity_background_color_tag), iconBackgroundColor);

            //We make the RemoteViews clickable by passing in the fillInIntent to it
            remoteViews.setOnClickFillInIntent(R.id.portfolio_item_root_view, fillInIntent);

            //We return the RemoteViews
            return remoteViews;
        }

        //Not useful in our case
        @Override
        public void onCreate() {
        }


        //Not useful in our case
        @Override
        public void onDestroy() {
        }


        //Not useful in our case
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }


        //Not useful in our case
        @Override
        public int getViewTypeCount() {
            return 1;
        }


        //Not useful in our case
        @Override
        public long getItemId(int position) {
            return position;
        }


        //Not useful in our case
        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

}
