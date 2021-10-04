package saveaseng.ng.savease.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FirstRes {

    @SerializedName("text")
    @Expose
    private List<User> user = null;



    public List<User> getText() {
        return user;
    }

    public void setText(List<User> text) {
        this.user = text;
    }
}
