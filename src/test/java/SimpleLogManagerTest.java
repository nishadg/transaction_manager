import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.security.Policy;

import static org.junit.Assert.*;

/**
 *
 */
public class SimpleLogManagerTest {

    private SimpleLogManager simpleLogManager;

    @Test
    public void write() throws Exception {
        SimpleLogManager simpleLogManager = new SimpleLogManager();
        PolicyModel policy = new PolicyModel();
        policy.trID = 1L;
        int result = simpleLogManager.write(policy);
        assertEquals(1, result);
    }

    @Test
    public void query() throws Exception {
    }

    @Test
    public void flush() throws Exception {
    }

}