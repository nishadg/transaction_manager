import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.security.Policy;
import java.util.ArrayList;

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
        int result = simpleLogManager.write(policy, 1L);
        assertEquals(1, result);
    }

    @Test
    public void query() throws Exception {
        SimpleLogManager simpleLogManager = new SimpleLogManager();
        long x = 565378690;
        ArrayList<LogModel> r = new ArrayList<>();
        r = simpleLogManager.query(x);
        System.out.println();

    }

    @Test
    public void flush() throws Exception {
    }

}