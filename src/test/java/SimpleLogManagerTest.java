import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import java.security.Policy;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Set;

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
        SimpleLogManager simpleLogManager = new SimpleLogManager();

        PolicyModel policyA = new PolicyModel();
        int resultA = simpleLogManager.write(policyA, 1L);
        assertEquals(1, resultA);

        PolicyModel policyB = new PolicyModel();
        int resultB = simpleLogManager.write(policyB, 2L);
        assertEquals(1, resultB);

        int result = simpleLogManager.flush(2);
       // assertEquals(1, result);

       PolicyDBAdapter adapter = new PolicyDBAdapter();
       adapter.validate(5);
//        java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2017-06-03 21:46:34.54");
//
//        Set<Integer> setRes = adapter.getPoliciesGreaterThan(timestamp);
//        adapter.deletePoliciesGreaterThan(timestamp);
//        System.out.println("Set is" + setRes);


    }

//    @Test
//    public void setPrevLSN() throws Exception {
//        SimpleLogManager simpleLogManager = new SimpleLogManager();
//        long result = simpleLogManager.getMaxLSN();
//        assertEquals(0, result);
//    }

}