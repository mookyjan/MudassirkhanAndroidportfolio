package com.mudassirkhan.androidportfolio.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mudassirkhan.androidportfolio.R;

//There is actually nothing in this fragment file, since it only displays a plain text which is set in the layout
public class WidgetFragment extends Fragment {

    //Empty constructor
    public WidgetFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_widget, container, false);
    }

}
