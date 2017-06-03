import java.sql.Timestamp;
import java.util.ArrayList;

/**
 *
 */
public class Transaction {

    LogDBAdapter logDB;
    PolicyDBAdapter policyDB;
    final int SUCCESS = 0;
    long trID;

    public Transaction()
    {
        logDB = new LogDBAdapter();
        policyDB  = new PolicyDBAdapter();
        ArrayList<LogModel> latest2logs =  new ArrayList<>();
        latest2logs  = logDB.get_last_two_logs();
        System.out.println(latest2logs.get(0));
        int x = recoveryManager(latest2logs);


    }

    public int recoveryManager(ArrayList<LogModel> latest2logs)
    {
        LogModel lastlog = latest2logs.get(0);
        LogModel secondlastlog = latest2logs.get(1);

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
        if(lastlog.payload == "CHECKPOINT" &&
                (secondlastlog.payload == "COMMIT" || secondlastlog.payload == "ABORT"))
        {
            return 1;
        }

        /*CASE 2:*/
        else if(lastlog.payload == "CHECKPOINT" &&
                (secondlastlog.payload != "COMMIT" || secondlastlog.payload != "ABORT"))
        {
                return undo();
        }


        /*CASE 3:*/
        else if(lastlog.payload == "COMMIT")
        {
            return redo();

        }

        /*CASE 4:*/
        else if(lastlog.payload == "ABORT")
        {
            return undo();
        }

        /*CASE 5:*/
        else
        {
            return undo();
        }

    }


    long begin() {
        trID = 0; //TODO get trID from DB
        return trID;
    }

    int commit() {
        logDB.flush(Long.MAX_VALUE);
        //code to write policy to policy table
        return SUCCESS;
    }

    int abort() {
        return SUCCESS;
    }

    int log(PolicyModel policy) {
        logDB.write(policy, trID);
        return SUCCESS;
    }

    public int redo()
    {
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
        ArrayList<LogModel> tuplesFromLogtable = logDB.get_logs_greater_than(lastTimeStamp);

        //STEP 3:
        for(LogModel i: tuplesFromLogtable) {
            PolicyModel pm2 = new PolicyModel();
            pm2.payload  = i.payload;
            pm2.entered = i.logTimestamp;
            //TODO: find out policyID
            pm2.policyID = 2;
            pm2.invalidated = null;
            policyDB.write(pm2);
        }

        //STEP 4:




        return 0;
    }

    // tell me (yadhu) if you change the return type
    // else let this comment be here till eternity.
    public int undo()
    {

        return 0;
    }

}
