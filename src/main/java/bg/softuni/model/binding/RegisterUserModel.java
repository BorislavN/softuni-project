package bg.softuni.model.binding;

import bg.softuni.annotation.MatchFields;
import bg.softuni.model.enumeration.Gender;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@MatchFields(first = "password",second = "repeatPassword",message = "Passwords don't match!")
public class RegisterUserModel {
    private String username;
    private String password;
    private String repeatPassword;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;

    public RegisterUserModel() {
    }

    @Length(min = 3, max = 20, message = "Username must be between 3 and 20 characters!")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Length(min = 6, max = 15, message = "Password must be between 6 and 15 characters!")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Length(min = 2, max = 20, message = "First name must be between 2 and 20 characters!")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Length(min = 2, max = 20, message = "Last name must be between 2 and 20 characters!")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @NotBlank(message = "Email can't be empty!")
    @Email(message = "Enter valid email!")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @NotBlank(message = "Gender can't be empty!")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Length(min = 5, max = 15, message = "Repeat password must be between 6 and 15 characters!")
    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
