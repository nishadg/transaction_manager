import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

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
    private Gson gson;

    Transaction() {
        logDB = new LogDBAdapter();
        policyDB = new PolicyDBAdapter();
        ArrayList<LogModel> latest2logs = new ArrayList<>();
        latest2logs = logDB.getLastTwoLogs();
        buffer = new ArrayList<>(bufferSize);
//        System.out.println(latest2logs.get(0));
        gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss.S")
                .create();
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
    }

    int commit() {
        logDB.write(trID, ILogManager.COMMIT);
        flush();
        return SUCCESS;
    }

    int abort() {
        undo();
        logDB.emptyBuffer();
        logDB.write(trID, ILogManager.ABORT);
        flush();
        return SUCCESS;
    }

    int write(String policyJSON) {
        PolicyModel policyModel = gson.fromJson(policyJSON, PolicyModel.class);
        policyModel.entered = new java.sql.Timestamp(new java.util.Date().getTime());
        policyModel.invalidated = null;

        logDB.write(policyModel, trID);
        buffer.add(policyModel);
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
        ADD CHECKPOINT IN THE LOGS
        */

        //STEP 1:
        PolicyModel pm = policyDB.lastPolicy();
        Timestamp lastTimeStamp = pm.entered;

        //STEP 2:
        ArrayList<LogModel> tuplesFromLogtable = logDB.getLogsGreaterThan(lastTimeStamp);

        //STEP 3:
        for (LogModel i : tuplesFromLogtable) {
            if (!i.type.equalsIgnoreCase("update")) continue;
            PolicyModel policyModel = gson.fromJson(i.payload, PolicyModel.class);
            policyDB.write(policyModel);
        }

        //STEP 4:
        flush();


        return 0;
    }

    // tell me (yadhu) if you change the return type
    // else let this comment be here till eternity.
    private int undo() {

        /*
        STEP 1:
        FIND THE TS OF LAST COMMITTED TRANSACTION FROM LOGS
        STEP 2:
        FIND THE POLICIES THAT ARE GREATER THAN THIS TS IN POLICIES TABLE
        STEP 3:
        DELETE THOSE POLICIES
        STEP 4:
        IF PREV VERSION OF THESE POLICIES EXIST, VALIDATE THEM
        */

        // STEP 1
        Timestamp timestamp = logDB.getLastCommitTs();

        // STEP 2
        Set<Integer> policies = policyDB.getPoliciesGreaterThan(timestamp);

        //STEP 3
        policyDB.deletePoliciesGreaterThan(timestamp);

        //STEP4
        for (Integer policy : policies) {
            if (policyDB.exists(policy)) {
                policyDB.validate(policy);
            }
        }
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

    public String timeTraversal(Timestamp timestamp) {
        ArrayList<PolicyModel> policies = policyDB.getPoliciesAt(timestamp);
        ArrayList<String> policyJsons = new ArrayList<>(policies.size());
        for (PolicyModel policy : policies)
            policyJsons.add(gson.toJson(policy, PolicyModel.class));
        return "[ ".concat(String.join(", ", policyJsons)).concat(" ]");
    }
}
