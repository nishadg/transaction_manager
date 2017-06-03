import java.sql.*;
import java.util.Iterator;

/**
 *
 */
public class PolicyDBAdapter {

    Connection connection;

    public PolicyDBAdapter() {
        DatabaseConfig dc = new DatabaseConfig();

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection("jdbc:mysql://localhost:" + dc.getPort() + "/" + dc.getDatabaseName() + "?" +
                    "user=" + dc.getUser() + "&password=" + dc.getPassword() + "");

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    void write(PolicyModel policy) {
        String insertSQL = "INSERT INTO Policies (policyID, payload, entered, invalidated) " +
                "VALUES (?, ?, ?, ?)";

        PreparedStatement preparedStmt;
        try {
            preparedStmt = connection.prepareStatement(insertSQL);
            preparedStmt.setInt(1, policy.policyID);
            preparedStmt.setString(2, policy.payload);
            preparedStmt.setTimestamp(3, policy.entered);
            preparedStmt.setTimestamp(4, policy.invalidated);
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    boolean exists(int policyID) {
        String selectSQL = "SELECT * FROM Policies WHERE policyID = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(selectSQL);
            statement.setInt(1, policyID);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    void invalidate(int policyID, Timestamp invalidateTimestamp) {
        String updateSQL = "UPDATE Policies SET invalidated = ? WHERE policyID = ? AND invalidated IS NULL";
        PreparedStatement preparedStmt;
        try {
            preparedStmt = connection.prepareStatement(updateSQL);
            preparedStmt.setTimestamp(1, invalidateTimestamp);
            preparedStmt.setInt(2, policyID);
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void validate(int policyID) {
        String updateSQL = "UPDATE Policies SET invalidated = NULL WHERE policyID = ?";
        PreparedStatement preparedStmt;
        try {
            preparedStmt = connection.prepareStatement(updateSQL);
            preparedStmt.setInt(1, policyID);
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }
}
