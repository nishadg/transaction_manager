import com.google.gson.JsonObject;

/**
 *
 */
public interface ILogManager {

    int write(JsonObject policy);
    int query(long TRID);
    int flush(long LSN);

}
