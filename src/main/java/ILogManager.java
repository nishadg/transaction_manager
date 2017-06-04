import java.util.ArrayList;

/**
 *
 */
public interface ILogManager {


    int BEGIN = 0;
    int COMMIT = 1;
    int ABORT = 2;
    int CHECKPOINT = 3;

    int write(PolicyModel policy, Long trID);
    ArrayList query(long TRID);
    int flush(long LSN);

}
