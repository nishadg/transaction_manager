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

    @SerializedName("fromTS")
    @Expose
    public Timestamp fromTS;

    @SerializedName("toTS")
    @Expose
    public Timestamp toTS;

    @SerializedName("author")
    @Expose
    public String author;

    @SerializedName("querier")
    @Expose
    public String querier;
}
