import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by yadhuprakash on 5/26/17.
 */
public class DatabaseConfig {



        static Properties properties;

        public DatabaseConfig()
        {

            properties = new Properties();

            String resourceName = "db.config.properties";

            InputStream inputStream = DatabaseConfig.class.getClassLoader().getResourceAsStream(resourceName);

            try {
                properties.load(inputStream);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }


        }

        public  String getDatabaseName() {
            return properties.getProperty("jdbc.db.databasename");
        }

        public  String getUser() {
            return properties.getProperty("jdbc.db.user");
        }

        public  String getPassword() {
            return properties.getProperty("jdbc.db.password");
        }

        public  String getHost() {return properties.getProperty("jdbc.db.host");}

        public  String getPort() {return properties.getProperty("jdbc.db.port");}


}


