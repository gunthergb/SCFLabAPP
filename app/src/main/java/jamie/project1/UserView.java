package jamie.project1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

/**
 * Holds references to significant components in a row.
 */
final class UserView {

    private static final String TAG = "UserView";
    private static LayoutInflater inflator;
    private static Activity activity;

    private final User user; //The data of the user we reflect (view to the user)
    //UI:
    private final View view;//The underlying view that is displayed and holds our name and button components...
    private final TextView name; //The text/label component of this row (holds the name of the user(
    private final ImageButton deleteBtn; //The delete button component

    ///////////////////////////////////////////////////////////////////////////////////////

    private UserView(User user, View view, TextView name, ImageButton db) {
        this.user = user;
        this.view = view;
        this.name = name;
        this.deleteBtn = db;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public static void init(Activity activity) { //Called on app start to setup some global info
        //creating LayoutInflator for inflating the row layout.
        UserView.inflator = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //Hold a reference activity to make toasts (see initUI)
        UserView.activity = activity;
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public static UserView create(User user) {
        if(user == null)
            throw new IllegalArgumentException("user == null");

        //inflating the row layout we defined earlier.
        View view = inflator.inflate(R.layout.row_item_layout, null);

        TextView name = (TextView) view.findViewById(R.id.row_name);
        ImageButton deleteBtn = (ImageButton) view.findViewById(R.id.deleteBtn);

        if(name == null || deleteBtn == null)
            throw new IllegalArgumentException("components missing?");

        UserView uv = new UserView(user,view,name,deleteBtn);

        initUI(uv);

        return uv;

    }

    //Set up inital text and listeners...
    private static void initUI(UserView uv) {
        uv.name.setText(uv.user.getName());
        uv.deleteBtn.setOnClickListener(v -> {
            final String name = uv.user.getName();
            Log.i(TAG,"Deleting user:" + name);
            uv.user.delete().addOnCompleteListener(t -> {
                Log.d(TAG,"delete user task '" + name + "' complete:" + t.isSuccessful());
                Toast.makeText(activity.getApplicationContext(),
                        (t.isSuccessful() ? "Deleted " : "Failed to delete ") + name,
                        Toast.LENGTH_LONG).show();
            });
        });
    }

    ///////////////////////////////////////////////////////////////////////////////////////

    public User getUser() {
        return user;
    }

    public TextView getName() {
        return name;
    }

    public ImageButton getDeleteBtn() {
        return deleteBtn;
    }

    public View getView() {
        return view;
    }
}