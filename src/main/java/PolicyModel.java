import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

/**
 *
 */
public class PolicyModel {

    @SerializedName("policyID")
    @Expose
    public Integer policyID;

    @SerializedName("entered")
    @Expose
    public Timestamp entered;

    @SerializedName("invalidated")
    @Expose
    public Timestamp invalidated;

    @SerializedName("payload")
    @Expose
    public String payload;

    public PolicyModel(Integer policyID, Timestamp entered, Timestamp invalidated, String payload) {
        this.policyID = policyID;
        this.entered = entered;
        this.invalidated = invalidated;
        this.payload = payload;
    }

    public PolicyModel() {
    }
}
