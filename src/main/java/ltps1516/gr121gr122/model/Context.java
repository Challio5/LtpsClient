package ltps1516.gr121gr122.model;

/**
 * Created by rob on 02-12-15.
 */
public class Context {
    // Context
    private static Context ourInstance = new Context();

    //
    private User user;

    public static Context getInstance() {
        return ourInstance;
    }

    private Context() {
    }
}
