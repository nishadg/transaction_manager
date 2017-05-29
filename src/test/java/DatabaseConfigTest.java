import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by yadhuprakash on 5/26/17.
 */
public class DatabaseConfigTest {
    @Test
    public void getDatabaseName() throws Exception {

        DatabaseConfig dc = new DatabaseConfig();
        System.out.println(dc.getDatabaseName());
    }

}