package bg.softuni.model.binding;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Size;
import java.util.List;

public class EditRolesModel {
    private String username;
    private List<String> roles;

    public EditRolesModel() {
    }

    @Length(min = 2, max = 20, message = "Username should be between 2 and 20 symbols!")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Size(min = 1, max = 2, message = "You must choose at least one role!")
    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
