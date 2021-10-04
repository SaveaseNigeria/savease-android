package saveaseng.ng.savease.Model;

public class CardModel {

    private String amount;
    private int cardId;

    public CardModel(String amount,int cardId) {
        this.amount = amount;
        this.cardId = cardId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
}
