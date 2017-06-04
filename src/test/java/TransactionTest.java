import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Created by yadhuprakash on 6/2/17.
 */
public class TransactionTest {

    @Test
    public void testConstructor() throws Exception {

    }

    Transaction t;

    @Before
    public void setup(){
        t = new Transaction();
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

        for(int i = 0; i < 5; i++){
            String policyJSON = "{ \"policyID\" : " + i + ", \"user\":\"name\"}";
            t.write(policyJSON);
        }

        for(int i = 0; i < 5; i++){
            String policyJSON = "{ \"policyID\" : " + i + ", \"user\":\"name\"}";
            t.write(policyJSON);
        }
        t.commit();

    }
    @Test
    public void undo() {

        t.begin();
        for(int i = 10; i < 33; i++){
            String policyJSON = "{ \"policyID\" : " + i+ ", \"user\":\"name\"}";
            t.write(policyJSON);
        }

        t.abort();


    }
}