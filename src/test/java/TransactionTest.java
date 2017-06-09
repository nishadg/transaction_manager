import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.Timestamp;

import static org.junit.Assert.*;

/**
 * Created by yadhuprakash on 6/2/17.
 */
public class TransactionTest {

    Transaction t;

    @Test
    public void testConstructor() throws Exception {
        try {
            Transaction c = TransactionManager.createTransaction();
        }catch (RuntimeException re){
            assertTrue(true);
            System.out.printf("Did not allow 2nd");
        }
    }

    @Before
    public void setup(){
        t = TransactionManager.createTransaction();

    }

//    @Test
//    public void flushTest(){
//        t.begin();
//        for(int i = 0; i < 7; i++){
//            String policyJSON = "{ \"policyID\" : " + i + ", \"user\":\"name\"}";
//            t.write(policyJSON);
//        }
//        assertEquals(2, t.currentBufferSize());
//        t.commit();
//        assertEquals(0, t.currentBufferSize());
//    }

    @Test
    public void flushVersionsTest() {
        t.begin();

        for(int i = 0; i < 6; i++){
            String policyJSON = "{\n" +
                    "  \"policyID\": "+ i +",\n" +
                    "  \"fromTS\": \"2013-08-05 18:19:03.000\",\n" +
                    "  \"toTS\": \"2016-09-28 21:03:30.010\",\n" +
                    "  \"author\": \"Jehanna\",\n" +
                    "  \"querier\": \"Mitch\"\n" +
                    "}";
            t.write(policyJSON);
        }

        t.commit();

    }
    @Test
    public void undo() {

        t.begin();
        for(int i = 10; i < 33; i++){
            String policyJSON = "{ \"policyID\" : " + i+ ", \"author\":\"name\"}";
            t.write(policyJSON);
        }

        t.abort();
    }

    @Test
    public void timeTraversalTest() throws InterruptedException {
        t.begin();
        String policyJSON = "{\n" +
                "  \"policyID\": 100,\n" +
                "  \"fromTS\": \"2013-08-05 18:19:03.000\",\n" +
                "  \"toTS\": \"2016-09-28 21:03:30.010\",\n" +
                "  \"author\": \"Jehanna\",\n" +
                "  \"querier\": \"Mitch\"\n" +
                "}";
        String policyJSON2 = "{\n" +
                "  \"policyID\": 101,\n" +
                "  \"fromTS\": \"2013-08-05 18:19:03.000\",\n" +
                "  \"toTS\": \"2016-09-28 21:03:30.010\",\n" +
                "  \"author\": \"Jason\",\n" +
                "  \"querier\": \"Kandu\"\n" +
                "}";
        t.write(policyJSON);
        t.write(policyJSON2);
        Timestamp ts = new Timestamp(new java.util.Date().getTime());

        Thread.sleep(5000);

        String policyJSON3 = "{\n" +
                "  \"policyID\": 100,\n" +
                "  \"fromTS\": \"2013-08-05 18:19:03.000\",\n" +
                "  \"toTS\": \"2016-09-28 21:03:30.010\",\n" +
                "  \"author\": \"Nishad\",\n" +
                "  \"querier\": \"Yadhu\"\n" +
                "}";
        t.write(policyJSON3);
        t.commit();

        String policies = t.timeTraversal(ts);
        System.out.println("Policies at " + ts.toString() + "\n" + policies);


    }
}