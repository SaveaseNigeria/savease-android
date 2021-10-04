package saveaseng.ng.savease.Model;

public class VoucherTableModel {

    private String VoucherStatus,Amount,VoucherPin,BatchNo,SerialNumber,usedBy,usedDate;

    public VoucherTableModel() {
    }

    public String getUsedBy() {
        return usedBy;
    }

    public void setUsedBy(String usedBy) {
        this.usedBy = usedBy;
    }

    public String getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(String usedDate) {
        this.usedDate = usedDate;
    }

    public String getVoucherStatus() {
        return VoucherStatus;
    }

    public void setVoucherStatus(String voucherStatus) {
        VoucherStatus = voucherStatus;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getVoucherPin() {
        return VoucherPin;
    }

    public void setVoucherPin(String voucherPin) {
        VoucherPin = voucherPin;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }
}
