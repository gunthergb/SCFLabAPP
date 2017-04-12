package jamie.project1;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public final class UserArrayAdapter extends ArrayAdapter<User>  {

    private ArrayList<User> list;
    private Activity activity;

    //this custom adapter receives an ArrayList of User objects.
    public UserArrayAdapter(Activity activity,
                            Context context,
                            int textViewResourceId,
                            ArrayList<User> userList) {
        super(context, textViewResourceId, userList);
        this.activity = activity;
        this.list = userList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        User selected_user = list.get(position);
        UserView userView = UserView.create(selected_user);
        //return the row view.
        return userView.getView();
    }
}