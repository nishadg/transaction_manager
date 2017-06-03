import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 *
 */
public class TransactionManager {



    public static Transaction createTransaction() {
        return new Transaction();
    }
}
