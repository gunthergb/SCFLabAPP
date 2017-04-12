package jamie.project1;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public final class MainActivity extends AppCompatActivity {

    private DatabaseReference db;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "<FB>";

    private ArrayList<User> userList = new ArrayList<>();
    private ArrayAdapter<User> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        setupUI();
        setupFB();
        authFB();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void setupUI() {
        UserView.init(this);
        adapter = new UserArrayAdapter(this,getApplicationContext(),R.id.userlist, userList);
        ListView tv = (ListView) findViewById(R.id.userlist);
        tv.setAdapter(adapter);
    }


    //sets the info label to give feedback to the user...
    private void syncUsers(DataSnapshot users) {
        //Lets do 'heavy' work here, and not in the UI thread...
        Log.d("syncUsers", "Refreshing user list(count=" + users.getChildrenCount() + ")");

        List<User> new_users = new ArrayList<>((int) users.getChildrenCount());//temp list to hold users
        for(DataSnapshot userSS : users.getChildren()) {
            new_users.add(User.of(userSS));
        }

        runOnUiThread(() -> {//We can only modify the any UI component in the UI thread...
            {//Sync user count
                TextView v = (TextView) findViewById(R.id.user_count);
                v.setText(users.getChildrenCount() + " users signed in!");
            }
            {//Sync user list
                userList.clear();//clear old users
                userList.addAll(new_users);//populate it with the new users
                adapter.notifyDataSetChanged(); //refresh the list
            }
            Log.d("syncUsers","User info refreshed");
        });
    }

    private void setupFB() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        };
    }

    private void authFB() {
        mAuth.signInWithEmailAndPassword("test@test.com", "password")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        Toast.makeText(getApplicationContext(), R.string.auth_succeed,
                                Toast.LENGTH_SHORT).show();

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                        load();
                    }
                });
    }

    //At this point we are authed and ready to work with the database.
    private void load() {
        //Set it up to sync the list every firebase update...
        db.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                syncUsers(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {/* Empty */}
        });
    }


}
