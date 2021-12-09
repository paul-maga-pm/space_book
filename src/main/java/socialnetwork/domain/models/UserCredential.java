package socialnetwork.domain.models;

import java.util.Objects;

public class UserCredential extends Entity<Long>{
    private String userName;
    private String password;

    public UserCredential(Long userId, String userName, String password) {
        super(userId);
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCredential)) return false;
        if (!super.equals(o)) return false;
        UserCredential that = (UserCredential) o;
        return Objects.equals(userName, that.userName) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userName, password);
    }
}
