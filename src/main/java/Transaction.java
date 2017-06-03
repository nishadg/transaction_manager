import com.google.gson.Gson;

import java.util.ArrayList;

/**
 *
 */
public class Transaction {

    final int SUCCESS = 0;
    ILogManager logManager;
    PolicyDBAdapter policyDB;
    ArrayList<PolicyModel> buffer;
    int bufferSize = 5;
    long trID;

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }



    long begin() {
        trID = 0; //TODO get trID from DB
        //TODO: write begin transaction log
        logManager = new SimpleLogManager();
        policyDB = new PolicyDBAdapter();
        buffer = new ArrayList<>(bufferSize);
        return trID;
    }

    int commit() {
        flush();
        return SUCCESS;
    }

    int abort() {
        return SUCCESS;
    }

    int write(String policyJSON) {
        PolicyModel policyModel = new Gson().fromJson(policyJSON, PolicyModel.class);
        PolicyModel policy = new PolicyModel(policyModel.policyID,
                new java.sql.Timestamp(new java.util.Date().getTime()), null, policyJSON);

        logManager.write(policy, trID);
        buffer.add(policy);
        if (buffer.size() == bufferSize) {
            flush();
        }
        return SUCCESS;
    }

    private void flush() {
        logManager.flush(Long.MAX_VALUE);
        for (PolicyModel policy : buffer) {
            if(policyDB.exists(policy.policyID)){
                policyDB.invalidate(policy.policyID, policy.entered);
            }
            policyDB.write(policy);
            buffer.remove(policy);
        }
        //TODO: write checkpoint log.
    }

}
