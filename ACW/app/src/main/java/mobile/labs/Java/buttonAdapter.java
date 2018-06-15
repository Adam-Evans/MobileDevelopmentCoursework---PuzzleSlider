package mobile.labs.acw;

import android.media.Image;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

/**
 * Created by AdamTheSecond on 2018-04-10.
 */

public class buttonAdapter extends BaseAdapter {

    private ArrayList<ImageButton> mButtons = null;
    private int mColumnWidth;
    private int mColumnHeight;

    public buttonAdapter(ArrayList<ImageButton> mButtons, int mColumnWidth, int mColumnHeight) {
        this.mButtons = mButtons;
        this.mColumnWidth = mColumnWidth;
        this.mColumnHeight = mColumnHeight;
    }

    @Override
    public int getCount() {
        return mButtons.size();
    }

    @Override
    public Object getItem(int position) {
        return (Object) mButtons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageButton b;
        if (convertView == null)
            b = mButtons.get(position);
        else
            b = (ImageButton) convertView;

        android.widget.AbsListView.LayoutParams params =
                new android.widget.AbsListView.LayoutParams(mColumnWidth, mColumnHeight);
        b.setLayoutParams(params);

        return b;
    }
}
