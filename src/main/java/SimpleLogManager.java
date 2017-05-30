import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.*;
import java.util.Iterator;


/**
 *
 */
public class SimpleLogManager implements ILogManager {

    private Connection connection;
    private ArrayList<LogModel> logList;
    private long LSN = 0;

    public SimpleLogManager() {

        logList = new ArrayList<LogModel>();
        DatabaseConfig dc = new DatabaseConfig();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://localhost:" + dc.getPort() + "/"+dc.getDatabaseName()+"?" +
                    "user="+dc.getUser()+"&password="+dc.getPassword()+"");

            LSN = getMaxLSN();

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
        log.prevLsn = LSN;
        log.lsn = ++LSN;
        //changed the toString for timestamp (mySQL and java both have timestamp datatypes)
        log.logTimestamp = new java.sql.Timestamp(new java.util.Date().getTime());
        log.payload = new Gson().toJson(policy, PolicyModel.class);
        log.type = "update";

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

        String insertSQL = "INSERT INTO Logs (LSN, TRID, prevLSN, log_timestamp, type, payload) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Iterator<LogModel> iter = logList.iterator();
        while(iter.hasNext()){
            LogModel log = iter.next();
            if (log.lsn <= LSN) {
                try {
                    PreparedStatement preparedStmt = connection.prepareStatement(insertSQL);
                    preparedStmt.setLong(1, log.lsn);
                    preparedStmt.setLong(2, log.trID);
                    preparedStmt.setLong(3, log.prevLsn);
                    preparedStmt.setTimestamp(4, log.logTimestamp);
                    preparedStmt.setString(5, log.type);
                    preparedStmt.setString(6, log.payload);
                    preparedStmt.execute();

                    iter.remove();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        return 1;
    }

    private long getMaxLSN(){

        String getMaxLSN = "SELECT MAX(LSN) as lsn FROM Logs;";
        int result = -1;
        try {

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(getMaxLSN);

            if (resultSet.next()) {
                result = resultSet.getInt("lsn");
            }

            if (result != 0) {
                LSN = result;
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return LSN;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }
}
