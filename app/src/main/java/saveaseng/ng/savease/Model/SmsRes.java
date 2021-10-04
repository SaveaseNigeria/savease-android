package saveaseng.ng.savease.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SmsRes {

    @SerializedName("0")
    @Expose
    private Integer _0;
    @SerializedName("data")
    @Expose
    private SmsData data;

    public Integer get0() {
        return _0;
    }

    public void set0(Integer _0) {
        this._0 = _0;
    }

    public SmsData getData() {
        return data;
    }

    public void setData(SmsData data) {
        this.data = data;
    }
}
