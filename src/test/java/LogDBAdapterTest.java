import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by yadhuprakash on 6/3/17.
 */
public class LogDBAdapterTest {
    @Test
    public void get_last_two_logs() throws Exception {

        LogDBAdapter logDB = new LogDBAdapter();
        ArrayList<LogModel> latest2logs =  new ArrayList<>();
        latest2logs  = logDB.getLastTwoLogs();
        for (LogModel i: latest2logs)
              {
                  System.out.println(i.lsn);

        }

    }

}