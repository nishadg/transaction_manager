import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */
public class Transaction {

    private LogDBAdapter logDB;
    private final int SUCCESS = 0;
    private PolicyDBAdapter policyDB;
    private ArrayList<PolicyModel> buffer;
    private int bufferSize = 5;
    private long trID;

    public Transaction() {
        logDB = new LogDBAdapter();
        policyDB = new PolicyDBAdapter();
        ArrayList<LogModel> latest2logs = new ArrayList<>();
        latest2logs = logDB.getLastTwoLogs();
//        System.out.println(latest2logs.get(0));
        if (latest2logs.size() > 0) {
            int x = recoveryManager(latest2logs);
        }
    }

    private int recoveryManager(ArrayList<LogModel> latest2logs) {
        LogModel lastLog = latest2logs.get(0);
        LogModel secondLastLog = latest2logs.get(1);

        /*
        CASE 1:
        IF LAST LOG IS CHECKPOINT AND SECOND LAST IS COMMIT/ABORT:
        THIS MEANS LOGS AND POLICY TABLE ARE IN SYNC AND NO
        RECOVERY MECHANISM NEEDS TO BE DONE

        CASE 2:
        IF LAST LOG IS CHECKPOINT BUT SECOND LAST IS NOT COMMIT/ABORT:
        THIS MEANS LOGS AND POLICY TABLE ARE IN SYNC BUT TRXN FAILED
        MIDWAY-
        --UNDO OF CURRENT TRXN TILL LAST COMMIT NEEDS TO BE DONE

        CASE 3:
        IF LAST LOG IS COMMIT:
        THIS MEANS THAT LOGS ARE WRITTEN COMPLETELY BUT POLICY TABLE
        HASN'T BEEN COMPLETELY WRITTEN
        REDO OF CURRENT TRXN NEEDS TO BE DONE

        CASE 4:
        IF LAST LOG IS ABORT:
        THIS MEANS LOGS ARE WRITTEN COMPLETELY
        BUT POLICY TABLE HASN'T BEEN COMPLETELY UNDONE
        UNDO OF CURRENT TRXN TILL LAST COMMIT NEEDS TO BE DONE


        CASE 5:
        IF LAST LOG IS RANDOM SHIZZ:
        UNDO OF CURRENT TRXN TILL LAST COMMIT NEEDS TO BE DONE
        */

        /*CASE 1:*/
        if (lastLog.type.equals("CHECKPOINT") &&
                (secondLastLog.type.equals("COMMIT") || secondLastLog.type.equals("ABORT"))) {
            return 1;
        }

        /*CASE 2:*/
        else if (lastLog.type.equals("CHECKPOINT") &&
                (!secondLastLog.type.equals("COMMIT") || !secondLastLog.type.equals("ABORT"))) {
            return undo();
        }


        /*CASE 3:*/
        else if (lastLog.type.equalsIgnoreCase("COMMIT")) {
            return redo();

        }

        /*CASE 4:*/
        else if (lastLog.type.equalsIgnoreCase("ABORT")) {
            return undo();
        }

        /*CASE 5:*/
        else {
            return undo();
        }

    }


    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    int currentBufferSize() {
        return buffer.size();
    }


    void begin() {
        trID = logDB.getNewTrid();
        logDB.write(trID, ILogManager.BEGIN);
        buffer = new ArrayList<>(bufferSize);
    }

    int commit() {
        logDB.write(trID, ILogManager.COMMIT);
        flush();
        return SUCCESS;
    }

    int abort() {
        logDB.write(trID, ILogManager.ABORT);
        flush();
        return SUCCESS;
    }

    int write(String policyJSON) {
        PolicyModel policyModel = new Gson().fromJson(policyJSON, PolicyModel.class);
        PolicyModel policy = new PolicyModel(policyModel.policyID,
                new java.sql.Timestamp(new java.util.Date().getTime()), null, policyJSON);

        logDB.write(policy, trID);
        buffer.add(policy);
        if (buffer.size() == bufferSize) {
            flush();
        }
        return SUCCESS;
    }

    private int redo() {
        /*
        STEP 1:
        FIND THE LAST TS FROM THE POLICY TABLE
        STEP 2:
        GET ALL TUPLES FROM LOG TABLE WHICH HAVE HIGHER TIMESTAMP
        STEP 3:
        ADD THEM TO POLICY TABLE
        STEP 4:
        TODO: ADD CHECKPOINT IN THE LOGS??
        */

        //STEP 1:
        PolicyModel pm = policyDB.lastPolicy();
        Timestamp lastTimeStamp = pm.entered;

        //STEP 2:
        ArrayList<LogModel> tuplesFromLogtable = logDB.getLogsGreaterThan(lastTimeStamp);

        //STEP 3:
        for (LogModel i : tuplesFromLogtable) {
            if (!i.type.equalsIgnoreCase("update")) continue;
            PolicyModel pm2 = new PolicyModel();
            PolicyModel policyModel = new Gson().fromJson(i.payload, PolicyModel.class);
            pm2.policyID = policyModel.policyID;
            pm2.payload = policyModel.payload;
            pm2.entered = policyModel.entered;
            pm2.invalidated = null;
            policyDB.write(pm2);
        }

        //STEP 4:


        return 0;
    }

    // tell me (yadhu) if you change the return type
    // else let this comment be here till eternity.
    private int undo() {

        return 0;
    }

    private void flush() {
        logDB.flush(Long.MAX_VALUE);
        for (Iterator<PolicyModel> iterator = buffer.iterator(); iterator.hasNext(); ) {
            PolicyModel policy = iterator.next();
            if (policyDB.exists(policy.policyID)) {
                policyDB.invalidate(policy.policyID, policy.entered);
            }
            policyDB.write(policy);
            iterator.remove();
        }
        logDB.write(trID, ILogManager.CHECKPOINT);
        logDB.flush(Long.MAX_VALUE);
    }

}
