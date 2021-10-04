package saveaseng.ng.savease.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Details {
    @SerializedName("authorization_code")
    @Expose
    private Object authorizationCode;
    @SerializedName("account_number")
    @Expose
    private String accountNumber;
    @SerializedName("account_name")
    @Expose
    private Object accountName;
    @SerializedName("bank_code")
    @Expose
    private String bankCode;
    @SerializedName("bank_name")
    @Expose
    private String bankName;

    public Object getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(Object authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Object getAccountName() {
        return accountName;
    }

    public void setAccountName(Object accountName) {
        this.accountName = accountName;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

}
