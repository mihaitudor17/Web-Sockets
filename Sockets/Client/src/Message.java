public class Message {
    private Options content;
    private User user;

    public Message(Options content, User user) {

        this.content = content;
        this.user = user;

    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return content+"_"+user.toString();
    }
}
