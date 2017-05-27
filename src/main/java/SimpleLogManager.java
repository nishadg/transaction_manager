import com.google.gson.JsonObject;

/**
 *
 */
public class SimpleLogManager implements ILogManager {
    @Override
    public int write(JsonObject policy) {
        return 0;
    }

    @Override
    public int query(long TRID) {
        return 0;
    }

    @Override
    public int flush(long LSN) {
        return 0;
    }
}
