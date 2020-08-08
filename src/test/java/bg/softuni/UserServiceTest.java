package bg.softuni;

import bg.softuni.model.binding.MoneyTransactionModel;
import bg.softuni.model.binding.RegisterUserModel;
import bg.softuni.model.entity.Item;
import bg.softuni.model.entity.Photo;
import bg.softuni.model.entity.Role;
import bg.softuni.model.entity.User;
import bg.softuni.model.enumeration.Gender;
import bg.softuni.model.service.NotificationServiceModel;
import bg.softuni.model.service.UserServiceModel;
import bg.softuni.model.view.CollectionItem;
import bg.softuni.repository.UserRepository;
import bg.softuni.service.impl.UserServiceImpl;
import bg.softuni.service.interf.NotificationService;
import bg.softuni.service.interf.RoleService;
import bg.softuni.service.interf.UserService;
import bg.softuni.util.core.MultipartFileHandler;
import bg.softuni.util.core.impl.ValidatorUtilImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private final String NONEXISTENT_USERNAME = "NONEXISTENT";
    private final String ADMIN_USERNAME = "ADMIN";

    @Mock
    private NotificationService notificationService;
    @Mock
    private MultipartFileHandler fileHandler;
    @Mock
    private UserRepository repository;
    @Mock
    private RoleService roleService;
    private UserService userService;
    private User admin;
    private Role userRole;
    private PasswordEncoder encoder;

    @BeforeEach
    public void init() {
        this.encoder = new BCryptPasswordEncoder();

        this.userService = new UserServiceImpl(roleService, fileHandler, notificationService,
                repository, new ModelMapper(), new ValidatorUtilImpl(), encoder);

        this.admin = new User();
        this.admin.setUsername("ADMIN");
        this.admin.setPassword(encoder.encode("PASS"));
        this.admin.setEmail("admin@abv.bg");
        this.admin.setBalance(BigDecimal.ZERO);

        Role adminRole = new Role("ROLE_ADMIN");
        adminRole.setId("1");
        this.userRole = new Role("ROLE_USER");
        this.userRole.setId("2");

        this.admin.setRoles(Set.of(adminRole, userRole));
    }

    @Test
    public void testLoadUserByUsernameWithInvalidUsername() {
        when(repository.getByUsername(NONEXISTENT_USERNAME)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> this.userService.loadUserByUsername(NONEXISTENT_USERNAME));
    }

    @Test
    public void testLoadUserByUsername() {
        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));
        UserDetails details = this.userService.loadUserByUsername(ADMIN_USERNAME);

        assertEquals(ADMIN_USERNAME, details.getUsername());
        assertEquals(this.admin.getPassword(), details.getPassword());
        assertEquals(this.admin.getRoles().size(), details.getAuthorities().size());
        assertTrue(details.getAuthorities().containsAll(this.admin.getRoles()));
    }

    @Test
    public void testGetDefaultUsers() {
        User user = new User();
        user.setUsername("USER");

        when(repository.getAllByUsernameOrUsername(ADMIN_USERNAME.toLowerCase(), "user")).thenReturn(List.of(this.admin, user));
        List<String> result = this.userService.getDefaultUsers();

        assertEquals(ADMIN_USERNAME, result.get(0));
        assertEquals("USER", result.get(1));
    }

    @Test
    public void testGetFullNameWithMissingUser() {
        when(repository.getByUsername(NONEXISTENT_USERNAME)).thenReturn(Optional.empty());
        assertNull(userService.getFullName(NONEXISTENT_USERNAME));
    }

    @Test
    public void testGetFullName() {
        this.admin.setFirstName("Pesho");
        this.admin.setLastName("Tigara");

        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));
        assertEquals("Pesho Tigara", userService.getFullName(ADMIN_USERNAME));
    }

    @Test
    public void testGetIdByUsernameWithWrongUsername() {
        when(repository.getByUsername(NONEXISTENT_USERNAME)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getIdByUsername(NONEXISTENT_USERNAME));
    }

    @Test
    public void testGetIdByUsername() {
        this.admin.setId("TEST");
        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));
        assertEquals("TEST", userService.getIdByUsername(ADMIN_USERNAME));
    }

    @Test
    public void testGetBalanceWithWrongUsername() {
        when(repository.getByUsername(NONEXISTENT_USERNAME)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getBalance(NONEXISTENT_USERNAME));
    }

    @Test
    public void testGetBalance() {
        this.admin.setBalance(BigDecimal.TEN);
        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));
        assertEquals(BigDecimal.TEN, userService.getBalance(ADMIN_USERNAME));
    }

    @Test
    public void testGetCollectionCount() {
        this.admin.setCollection(Set.of(new Item()));
        when(repository.findByUsername(ADMIN_USERNAME)).thenReturn(this.admin);
        assertEquals(1, this.userService.getCollectionCount(ADMIN_USERNAME));
    }

    @Test
    public void testGetAllUsernamesWithEmptyRepo() {
        when(repository.findAll()).thenReturn(new ArrayList<>());
        assertEquals(0, userService.getAllUsernames().size());
    }

    @Test
    public void testGetAllUsernames() {
        when(repository.findAll()).thenReturn(List.of(this.admin));
        List<String> out = userService.getAllUsernames();

        assertEquals(1, out.size());
        assertEquals("ADMIN", out.get(0));
    }

    @Test
    public void testGetUserByUsernameWithInvalidUsername() {
        when(repository.getByUsername(NONEXISTENT_USERNAME)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> this.userService.getUserByUsername(NONEXISTENT_USERNAME));
    }

    @Test
    public void testGetUserByUsername() {
        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));
        User out = this.userService.getUserByUsername(ADMIN_USERNAME);

        assertEquals(ADMIN_USERNAME, out.getUsername());
        assertEquals(this.admin.getPassword(), out.getPassword());
        assertEquals(this.admin.getRoles().size(), out.getRoles().size());
        assertTrue(out.getRoles().containsAll(this.admin.getRoles()));
    }

    @Test
    public void testNewNotification() {
        NotificationServiceModel notification = new NotificationServiceModel();
        notification.setText("TEXT");
        notification.setUserUsername("ADMIN");

        when(notificationService.createNotification(this.admin, "TEXT")).thenReturn(notification);
        NotificationServiceModel out = userService.newNotification(this.admin, "TEXT");

        assertEquals(out.getText(), notification.getText());
        assertEquals(out.getUserUsername(), notification.getUserUsername());
    }

    @Test
    public void testRegisterUserWithBindingErrors() {
        RegisterUserModel model = new RegisterUserModel(
                "t", "t", "x", "t", "t", "e", "");
        assertThrows(IllegalStateException.class, () -> this.userService.registerUser(model));
    }

    @Test
    public void testRegisterUserWithExistingUsername() {
        RegisterUserModel model = new RegisterUserModel(
                ADMIN_USERNAME, "ADMINADMIN", "ADMINADMIN", ADMIN_USERNAME, ADMIN_USERNAME, "ADMIN@dsad.bg", "MALE");

        when(this.repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));
        assertThrows(IllegalStateException.class, () -> this.userService.registerUser(model));
    }

    @Test
    public void testRegisterUserWithExistingEmail() {
        RegisterUserModel model = new RegisterUserModel(
                "USER", "USERUSER", "USERUSER", ADMIN_USERNAME, ADMIN_USERNAME, "admin@abv.bg", "MALE");

        when(this.repository.getByEmail("admin@abv.bg")).thenReturn(Optional.of(this.admin));
        assertThrows(IllegalStateException.class, () -> this.userService.registerUser(model));
    }

    @Test
    public void testRegisterUserWithWrongGender() {
        RegisterUserModel model = new RegisterUserModel(
                "USER", "USERUSER", "USERUSER", ADMIN_USERNAME, ADMIN_USERNAME, "user@abv.bg", "SEXY");
        assertThrows(IllegalStateException.class, () -> this.userService.registerUser(model));
    }

    @Test
    public void testRegisterUser() {
        RegisterUserModel model = new RegisterUserModel("USER", "USERUSER", "USERUSER", ADMIN_USERNAME, ADMIN_USERNAME, "user@abv.bg", "MALE");
        ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(roleService.getRolesByNames(List.of("ROLE_USER"))).thenReturn(List.of(this.userRole));
        when(repository.saveAndFlush(any())).thenAnswer((Answer<User>) mock -> {
            User input = mock.getArgument(0);
            input.setId("USER_ID");
            return input;
        });
        UserServiceModel output = userService.registerUser(model);
        verify(notificationService).createNotification(userCaptor.capture(), stringCaptor.capture());

        assertEquals("USER_ID", output.getId());
        assertEquals("USER", output.getUsername());
        assertEquals("user@abv.bg", output.getEmail());
        assertEquals(Gender.MALE, output.getGender());
        assertTrue(this.encoder.matches("USERUSER", output.getPassword()));
        assertEquals("USER_ID", userCaptor.getValue().getId());
        assertEquals("USER", userCaptor.getValue().getUsername());
        assertEquals("Hello ADMIN ADMIN, welcome to our site!", stringCaptor.getValue());
    }

    @Test
    public void testDepositWithInvalidUser() {
        when(repository.getByUsername(NONEXISTENT_USERNAME)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> this.userService.deposit(NONEXISTENT_USERNAME, new MoneyTransactionModel()));
    }

    @Test
    public void testDepositWithInvalidModel() {
        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));
        assertThrows(IllegalStateException.class, () -> this.userService.deposit(ADMIN_USERNAME, new MoneyTransactionModel(BigDecimal.ZERO)));
        assertThrows(IllegalStateException.class, () -> this.userService.deposit(ADMIN_USERNAME, new MoneyTransactionModel(BigDecimal.valueOf(6000000))));
    }

    @Test
    public void testDeposit() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));

        userService.deposit(ADMIN_USERNAME, new MoneyTransactionModel(BigDecimal.valueOf(500)));
        verify(this.repository).saveAndFlush(captor.capture());

        assertEquals(ADMIN_USERNAME, captor.getValue().getUsername());
        assertEquals(BigDecimal.valueOf(500), captor.getValue().getBalance());
    }

    @Test
    public void testWithdrawWithInvalidUser() {
        when(repository.getByUsername(NONEXISTENT_USERNAME)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> this.userService.withdraw(NONEXISTENT_USERNAME, new MoneyTransactionModel()));
    }

    @Test
    public void testWithdrawWithInvalidModel() {
        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));
        assertThrows(IllegalStateException.class, () -> this.userService.withdraw(ADMIN_USERNAME, new MoneyTransactionModel(BigDecimal.ZERO)));
        assertThrows(IllegalStateException.class, () -> this.userService.withdraw(ADMIN_USERNAME, new MoneyTransactionModel(BigDecimal.valueOf(6000000))));
    }

    @Test
    public void testWithdrawWithTooBigValue() {
        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));
        assertThrows(IllegalStateException.class, () -> this.userService.withdraw(ADMIN_USERNAME, new MoneyTransactionModel(BigDecimal.TEN)));
    }

    @Test
    public void testWithdraw() {
        this.admin.setBalance(BigDecimal.TEN);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));

        userService.withdraw(ADMIN_USERNAME, new MoneyTransactionModel(BigDecimal.valueOf(9)));
        verify(this.repository).saveAndFlush(captor.capture());

        assertEquals(ADMIN_USERNAME, captor.getValue().getUsername());
        assertEquals(BigDecimal.ONE, captor.getValue().getBalance());
    }

    @Test
    public void testGetCollectionWithInvalidUser() {
        when(repository.getByUsername(NONEXISTENT_USERNAME)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> this.userService.getCollection(NONEXISTENT_USERNAME));
    }

    @Test
    public void testGetCollection() {
        Item item1=new Item();
        item1.setForSale(true);
        item1.setId("1");
        item1.setName("ONE");
        item1.setOwner(this.admin);
        item1.setPhotos(Set.of(new Photo("PHOTO_ONE",item1)));

        Item item2=new Item();
        item2.setName("TWO");
        item2.setId("2");
        item2.setOwner(this.admin);
        item2.setPhotos(Set.of(new Photo("PHOTO_TWO",item2)));

        this.admin.setCollection(Set.of(item1,item2));

        when(repository.getByUsername(ADMIN_USERNAME)).thenReturn(Optional.of(this.admin));

        List<CollectionItem> out = this.userService.getCollection(ADMIN_USERNAME);
        assertEquals("TWO",out.get(0).getName());
        assertEquals("ONE",out.get(1).getName());
        assertEquals("PHOTO_TWO",out.get(0).getLocations().get(0));
        assertEquals("PHOTO_ONE",out.get(1).getLocations().get(0));
    }
}