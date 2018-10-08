package common;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.z.moveboxplus.R;

import java.util.List;

public class LevelAdapter extends ArrayAdapter<Level>{
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private int resourceId;

    public LevelAdapter(Context context, int resource,List<Level> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        Level level = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView textView = (TextView)view.findViewById(R.id.tv_choose_level);
        switch (level.getLevelState()){
            case 1:
                textView.setText("通过");
                textView.setBackgroundResource(R.color.levelRed);
                break;
            case 2:
                textView.setText(level.getLevelId());
                textView.setBackgroundResource(R.color.levelBackground);
                break;
            case 3:
                textView.setText(level.getLevelId());
                break;
        }
        return view;
    }
}
