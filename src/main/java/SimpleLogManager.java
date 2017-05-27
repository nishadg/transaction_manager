import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 */
public class SimpleLogManager implements ILogManager {

    private Connection connection;
    private long DB_PORT = 32772;
    private ArrayList<LogModel> logList;

    public SimpleLogManager() {

        logList = new ArrayList<LogModel>();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://localhost:" + DB_PORT + "/policies?" +
                    "user=root&password=password");
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    @Override
    public int write(PolicyModel policy) {

        LogModel log = new LogModel();
        log.trID = policy.trID;
        log.logTimestamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
        log.payload = new Gson().toJson(policy, PolicyModel.class);
        log.type = "update";

        //TODO: logic to get prevLSN. Mostly can be done when flushing ot DB.
        logList.add(log);
        return 1;
    }

    @Override
    public int query(long TRID) {
        return 0;
    }

    @Override
    public int flush(long LSN) {

//  TODO: LSN is a generated auto-increment value in the database. Retrieve the LSN when inserting and use it for prevLSN.

//        String insertSQL = "INSERT INTO Logs (TRID,prevLSN,log_timestamp,type,payload) " +
//                "VALUES (" + valuesString + ")";
//
//        try {
//            Statement insertStatement = connection.createStatement();
//            insertStatement.executeUpdate(insertSQL, Statement.RETURN_GENERATED_KEYS);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return 0;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }
}
