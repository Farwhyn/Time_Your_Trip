package com.planmytrip.johan.planmytrip;

/**
 * Created by Eason on 2016-11-19.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class menuAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] menuArray;
    private final Integer[] iconArray;


    public menuAdapter(Activity context, String[] menuArray, Integer[] iconArray) {
        super(context,  R.layout.menu_item ,menuArray);
        this.context = context;
        this.menuArray = menuArray;
        this.iconArray = iconArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater menuInflater = context.getLayoutInflater();
        View menuView = menuInflater.inflate(R.layout.menu_item, null, true);

        TextView menuText = (TextView) menuView.findViewById(R.id.menuText);
        ImageView menuIcon = (ImageView) menuView.findViewById(R.id.menuIcon);

        menuText.setText(menuArray[position]);
        menuIcon.setImageResource(iconArray[position]);
        return menuView;

    }
}
