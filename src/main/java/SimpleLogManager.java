import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;


/**
 *
 */
public class SimpleLogManager implements ILogManager {

    private Connection connection;
    private ArrayList<LogModel> logList;


    public SimpleLogManager() {

        logList = new ArrayList<LogModel>();
        DatabaseConfig dc = new DatabaseConfig();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://localhost:" + dc.getPort() + "/"+dc.getDatabaseName()+"?" +
                    "user="+dc.getUser()+"&password="+dc.getPassword()+"");
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    @Override
    public int write(PolicyModel policy, Long trID) {

        LogModel log = new LogModel();
        log.trID = trID;
        //changed the toString for timestamp (mySQL and java both have timestamp datatypes)
        log.logTimestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        log.payload = new Gson().toJson(policy, PolicyModel.class);
        log.type = "update";

        //TODO: logic to get prevLSN. Mostly can be done when flushing ot DB.
        logList.add(log);
        return 1;
    }

    @Override
    public ArrayList query(long TRID) {
        ArrayList<LogModel> result = new ArrayList<>();
        String sql = "SELECT * from LOGS WHERE TRID="+TRID;
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next())
            {
                long lsn  = rs.getInt("LSN");
                long trid = rs.getInt("TRID");
                long prevlsn = rs.getInt("prevLSN");
                String type = rs.getString("type");
                String payload = rs.getString("payload");
                Timestamp log_timestamp = rs.getTimestamp("log_timestamp");
                LogModel lm = new LogModel();
                lm.logTimestamp = log_timestamp;
                lm.lsn = lsn;
                lm.trID = trid;
                lm.prevLsn = prevlsn;
                lm.payload = payload;
                lm.type = type;
                result.add(lm);

            }
        }
        catch (Exception e)
        {

        }

        return result;
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
