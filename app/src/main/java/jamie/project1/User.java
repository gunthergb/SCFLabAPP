package jamie.project1;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

/**
 * Represents a user in the database
 */
public final class User {

    private DatabaseReference ref;
    private String name;

    private User() {}//prevent public construction

    ////////////////////////////////////////////////////////////////////

    public static User of(DataSnapshot ss) {
        if(ss == null)
            throw new IllegalArgumentException("snapshot == null");

        User user = new User();

        user.name = ss.getKey();
        user.ref = ss.getRef();

        return user;
    }

    public static User of(DatabaseReference ref) {
        if(ref == null)
            throw new IllegalArgumentException("ref == null");

        User user = new User();

        user.name = ref.getKey();
        user.ref = ref;

        return user;
    }

    ////////////////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public Task<Void> delete() {
        return ref.removeValue();
    }
}
