import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

/**
 *
 */
public class PolicyModel {

    @SerializedName("policyID")
    @Expose
    public Long policyID;

    @SerializedName("entered")
    @Expose
    public Timestamp entered;

    @SerializedName("invalidated")
    @Expose
    public Timestamp invalidated;

    @SerializedName("payload")
    @Expose
    public Timestamp payload;

}
