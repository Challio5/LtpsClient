package ltps1516.gr121gr122.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import ltps1516.gr121gr122.model.Model;

/**
 * Created by rob on 04-01-16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class NfcCard implements Model {
    private LongProperty cardId;
    private LongProperty userId;

    public NfcCard(
            @JsonProperty("nfcCardId") long cardId,
            @JsonProperty("userId") long userId) {
        this.cardId = new SimpleLongProperty(cardId);
        this.userId = new SimpleLongProperty(userId);
    }

    public NfcCard() {
        this.cardId = new SimpleLongProperty((long) (Math.random() * 100));
        this.userId = new SimpleLongProperty(1);
    }

    @Override
    @JsonProperty("nfcCardId")
    public long getId() {
        return cardId.get();
    }

    public LongProperty cardIdProperty() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId.set(cardId);
    }

    public long getUserId() {
        return userId.get();
    }

    public LongProperty userIdProperty() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId.set(userId);
    }

    @Override
    public String toString() {
        return "NfcCard{" +
                "cardId=" + cardId +
                ", userId=" + userId +
                '}';
    }
}
