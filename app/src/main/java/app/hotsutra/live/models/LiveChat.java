package app.hotsutra.live.models;

public class LiveChat {
    private String userName;
    private String userImageUrl;
    private String comments;

    public LiveChat() {
    }

    public LiveChat(String userName, String userImageUrl, String comments) {
        this.userName = userName;
        this.userImageUrl = userImageUrl;
        this.comments = comments;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
