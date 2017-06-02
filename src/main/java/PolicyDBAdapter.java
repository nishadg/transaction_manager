import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 */
public class PolicyDBAdapter {

    Connection connection;

    public PolicyDBAdapter() {
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
    protected void finalize() throws Throwable {
        super.finalize();
        connection.close();
    }
}
