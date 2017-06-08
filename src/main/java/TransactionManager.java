import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 *
 */
public class TransactionManager {

    static private Transaction t;

    public static Transaction createTransaction() {
        if (t == null){
            t = new Transaction();
            return t;
        }
        else throw new RuntimeException("Cannot have multiple transactions");
    }
}
