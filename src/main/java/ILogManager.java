/**
 *
 */
public interface ILogManager {

    int write(PolicyModel policy);
    int query(long TRID);
    int flush(long LSN);

}
