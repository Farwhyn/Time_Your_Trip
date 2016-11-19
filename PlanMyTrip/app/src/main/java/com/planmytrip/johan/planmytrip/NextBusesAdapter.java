package com.planmytrip.johan.planmytrip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by johan on 01.11.2016.
 */

public class NextBusesAdapter extends BaseAdapter {


    Context context;
    ArrayList<Bus> data;
    private static LayoutInflater inflater = null;

    public NextBusesAdapter(Context context, ArrayList<Bus> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.next_bus_row, null);
        TextView busNumberTextField = (TextView) vi.findViewById(R.id.busNumber);
        busNumberTextField.setText(data.get(position).getBusNo());
        TextView directionTextField = (TextView) vi.findViewById(R.id.direction);
        directionTextField.setText(data.get(position).getDestination());
        TextView leaveTimeTextField = (TextView) vi.findViewById(R.id.leaveTime);
        leaveTimeTextField.setText(data.get(position).getJustTime());

        return vi;
    }


}
