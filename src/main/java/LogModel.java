import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogModel {

    @SerializedName("LSN")
    @Expose
    public Long lsn;
    @SerializedName("TRID")
    @Expose
    public Long trID;
    @SerializedName("prevLSN")
    @Expose
    public Long prevLsn;
    @SerializedName("log_timestamp")
    @Expose
    public String logTimestamp;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("payload")
    @Expose
    public String payload;

}