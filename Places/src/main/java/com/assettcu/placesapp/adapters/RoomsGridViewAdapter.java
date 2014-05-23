package com.assettcu.placesapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.assettcu.placesapp.R;
import com.assettcu.placesapp.models.Room;
import com.koushikdutta.ion.Ion;

import java.util.List;

/**
 * file: RoomsGridViewAdapter
 * by: Derek Baumgartner
 * created: 5/19/2014.
 */
public class RoomsGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<Room> rooms;
    private LayoutInflater inflater;

    public RoomsGridViewAdapter(Context context, List<Room> rooms) {
        this.context = context;
        this.rooms = rooms;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addRoom(Room room) {
            rooms.add(room);
    }

    public void print(){
        for(int i = 0; i < rooms.size(); i++)
        {
            Log.d("Assett", "Room: " + rooms.get(i));
        }
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Room getItem(int i) {
        return rooms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.adapter_room, null);
        }
        TextView text = (TextView) view.findViewById(R.id.text1);
        text.setText(rooms.get(i).getRoomName());

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);

        Ion.with(imageView)
                .resize(512,512)
                .centerCrop()
                .error(R.drawable.no_image_available)
                .placeholder(R.drawable.no_image_available)
                .load(getItem(i).getRoomImageURL());

        return view;
    }
}
