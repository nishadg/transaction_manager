import com.google.gson.JsonObject;

/**
 *
 */
public interface ILogManager {

    int log(JsonObject policy);

    int flush();

}
