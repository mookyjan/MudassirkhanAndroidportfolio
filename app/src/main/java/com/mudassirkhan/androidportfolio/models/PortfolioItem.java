package com.mudassirkhan.androidportfolio.models;

public class PortfolioItem {

    //Declaration of PortfolioItem object's member variables
    private int mIcon;
    private String mTitle;
    private String mDescription;
    private int mIconBackgroundColor;

    //PortfolioItem's constructor
    public PortfolioItem(int icon, String title, String description, int iconBackgroundColor) {
        mIcon = icon;
        mTitle = title;
        mDescription = description;
        mIconBackgroundColor = iconBackgroundColor;
    }

    //PortfolioItem's getters
    public int getIcon() {
        return mIcon;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public int getIconBackgroundColor() {
        return mIconBackgroundColor;
    }

}