import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

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
    public Timestamp logTimestamp;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("payload")
    @Expose
    public String payload;

}