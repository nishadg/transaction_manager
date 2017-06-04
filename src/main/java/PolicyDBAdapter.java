import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
            //TODO: Handle null values for invalidated field in case of redo
            //TODO: policy.invalidated = null which may not translate to NULL in sql
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

        //Multiple versions of the same policy can exist, select the latest version
        String selectSQL = "SELECT * FROM Policies WHERE policyID = ? ORDER BY entered DESC LIMIT 1";
        PreparedStatement preparedStmt;
        try {
            preparedStmt = connection.prepareStatement(selectSQL);
            preparedStmt.setInt(1, policyID);
            ResultSet result = preparedStmt.executeQuery();

            if (result.next()){
                validate(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void validate(ResultSet result) {
        String updateSQL = "UPDATE Policies SET invalidated = NULL  WHERE id = ?";
        PreparedStatement preparedStmt;
        try {
            preparedStmt = connection.prepareStatement(updateSQL);
            preparedStmt.setInt(1,  result.getInt("id"));
            preparedStmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public PolicyModel lastPolicy() {
        String selectSQL = "SELECT * FROM Policies ORDER BY entered DESC LIMIT 1";
        PolicyModel result = null;
        try {
            PreparedStatement statement = connection.prepareStatement(selectSQL);
            ResultSet rs = statement.executeQuery();
            if(rs.next())
                result = createPolicyModelObject(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public Set<Integer> getPoliciesGreaterThan(Timestamp ts){

        Set<Integer> result = new HashSet<>();
        String sql = "SELECT policyId as Id FROM Policies WHERE entered > ?";
        try {
            PreparedStatement prstmt = connection.prepareStatement(sql);
            prstmt.setTimestamp(1, ts);
            ResultSet rs = prstmt.executeQuery();

            while (rs.next()) {
                int res = rs.getInt("Id");
                result.add(res);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public void deletePoliciesGreaterThan(Timestamp timestamp) {

        String sql = "Delete FROM Policies WHERE entered > ?";

            try {
                PreparedStatement preparedStmt = connection.prepareStatement(sql);
                preparedStmt.setTimestamp(1, timestamp);
                preparedStmt.execute();
            } catch (SQLException ex) {
                ex.printStackTrace();
        }
    }

    public PolicyModel createPolicyModelObject(ResultSet rs)
    {
        PolicyModel pm = new PolicyModel();
        try {
            int policyID = rs.getInt("policyID");
            String payload = rs.getString("payload");
            Timestamp entered = rs.getTimestamp("entered");
            Timestamp invalidated = rs.getTimestamp("invalidated");

            pm.policyID = policyID;
            pm.entered = entered;
            pm.invalidated = invalidated;
            pm.payload = payload;

        }
        catch(SQLException ex)
        {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        return pm;

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }
}
