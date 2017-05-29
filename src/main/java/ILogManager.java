import java.util.ArrayList;

/**
 *
 */
public interface ILogManager {

    int write(PolicyModel policy);
    ArrayList query(long TRID);
    int flush(long LSN);

}
