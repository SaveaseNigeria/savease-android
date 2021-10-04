package saveaseng.ng.savease.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Meta {

    @SerializedName("calls_this_month")
    @Expose
    private Integer callsThisMonth;
    @SerializedName("free_calls_left")
    @Expose
    private Integer freeCallsLeft;

    public Integer getCallsThisMonth() {
        return callsThisMonth;
    }

    public void setCallsThisMonth(Integer callsThisMonth) {
        this.callsThisMonth = callsThisMonth;
    }

    public Integer getFreeCallsLeft() {
        return freeCallsLeft;
    }

    public void setFreeCallsLeft(Integer freeCallsLeft) {
        this.freeCallsLeft = freeCallsLeft;
    }

}