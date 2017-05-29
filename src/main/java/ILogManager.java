/**
 *
 */
public interface ILogManager {

    int write(PolicyModel policy, Long trID);
    int query(long TRID);
    int flush(long LSN);

}
