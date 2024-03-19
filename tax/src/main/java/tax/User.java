package tax;
import org.mindrot.jbcrypt.BCrypt;

import java.io.Serializable;
import java.util.Date;

/**
 * @description:User
 * @author:pxf
 * @data:2023/04/28
 **/
public class User implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private String email ;
    private String phone ;
    private Date login_time ;
    private Date  registerDate;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", login_time=" + login_time +
                ", registerDate=" + registerDate +
                '}';
    }

    public void setLogin_time(Date login_time) {
        this.login_time = login_time;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getLogin_time() {
        return login_time;
    }
}
