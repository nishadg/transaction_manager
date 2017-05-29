/**
 *
 */
public class Transaction {

    final int SUCCESS = 0;
    ILogManager logManager;
    long trID;

    long begin() {
        trID = 0; //TODO get trID from DB
        logManager = new SimpleLogManager();
        return trID;
    }

    int commit() {
        logManager.flush(Long.MAX_VALUE);
        //code to write policy to policy table
        return SUCCESS;
    }

    int abort() {
        return SUCCESS;
    }

    int log(PolicyModel policy) {
        logManager.write(policy, trID);
        return SUCCESS;
    }

}
