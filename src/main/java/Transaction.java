/**
 *
 */
public class Transaction {

    final int SUCCESS = 0;
    ILogManager logManager;

    long begin() {
        long trid = 0; //TODO get trid from DB
        return trid;
    }

    int commit() {
        return SUCCESS;
    }

    int abort() {
        return SUCCESS;
    }

    int log(PolicyModel policy) {
        logManager.write(policy);
        return SUCCESS;
    }

}
