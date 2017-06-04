/**
 * Created by yadhuprakash on 6/2/17.
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class LogDBAdapter extends SimpleLogManager {


    public LogDBAdapter() {
        super();
    }

    void write(LogModel log) {
        String insertSQL = "INSERT INTO Logs (LSN, TRID, prevLSN, log_timestamp, type, payload) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement preparedStmt;
        try {
            preparedStmt = connection.prepareStatement(insertSQL);
            preparedStmt.setLong(1, log.lsn);
            preparedStmt.setLong(2, log.trID);
            preparedStmt.setLong(3, log.prevLsn);
            preparedStmt.setTimestamp(4, log.logTimestamp);
            preparedStmt.setString(5, log.type);
            preparedStmt.setString(6, log.payload);

            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<LogModel> getLastTwoLogs() {
        String last2sql = "SELECT * FROM Logs ORDER BY log_timestamp DESC LIMIT 2";
        ArrayList<LogModel> result = new ArrayList<>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(last2sql);
            while (rs.next()) {
                result.add(createLogModelObject(rs));
            }
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
        }
        return result;
    }

    public ArrayList<LogModel> getLogsGreaterThan(Timestamp ts) {
        ArrayList<LogModel> result = new ArrayList<>();
        String sql = "SELECT * FROM Logs WHERE LOG_TIMESTAMP > ?";
        try {
            PreparedStatement prstmt = connection.prepareStatement(sql);
            prstmt.setTimestamp(1, ts);
            ResultSet rs = prstmt.executeQuery(sql);
            while (rs.next()) {
                result.add(createLogModelObject(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }


    public long getNewTrid() {

        String maxTridSQL = "SELECT MAX(TRID) AS TRID FROM Logs;";
        int result = -1;
        try {

            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(maxTridSQL);

            if (resultSet.next()) {
                return resultSet.getInt("TRID") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }
}
