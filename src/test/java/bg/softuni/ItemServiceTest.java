package bg.softuni;

import bg.softuni.model.binding.ItemFormModel;
import bg.softuni.model.entity.Item;
import bg.softuni.model.entity.User;
import bg.softuni.repository.ItemRepository;
import bg.softuni.service.impl.ItemServiceImpl;
import bg.softuni.service.interf.ItemService;
import bg.softuni.service.interf.PhotoService;
import bg.softuni.service.interf.UserService;
import bg.softuni.util.core.MultipartFileHandler;
import bg.softuni.util.core.impl.ValidatorUtilImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    private ItemService itemService;
    @Mock
    private PhotoService photoService;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository repository;
    @Mock
    private MultipartFileHandler fileHandler;
    private User admin;

    @BeforeEach
    public void init() {
        this.itemService = new ItemServiceImpl(fileHandler, photoService, userService, repository, new ModelMapper(), new ValidatorUtilImpl());
        this.admin = new User();
        this.admin.setUsername("ADMIN");
    }

    @Test
    public void testSaveItemWithInvalidBinding() {
        ItemFormModel item = new ItemFormModel("testId", "t", "", "desc", new ArrayList<>());
        assertThrows(IllegalStateException.class, () -> this.itemService.saveItem("test", item));
    }

    @Test
    public void testSaveItemWithInvalidCategory() {
        ItemFormModel item = new ItemFormModel(null, "test", "demo", "testtesttesttesttest", List.of(new MockMultipartFile("test", new byte[50])));
        assertThrows(IllegalStateException.class, () -> this.itemService.saveItem("test", item));
    }

    @Test
    public void testSaveItemWithInvalidEmptyMultipart() {
        ItemFormModel item = new ItemFormModel(null, "test", "awards", "testtesttesttesttest", List.of(new MockMultipartFile("da", new byte[50])));
        assertThrows(IllegalStateException.class, () -> this.itemService.saveItem("test", item));
    }

    @Test
    public void testSaveItem() throws IOException {
        ItemFormModel item = new ItemFormModel(null, "test", "AWARDS", "testtesttesttesttest", List.of(new MockMultipartFile("testFile.jpg", "testFile.jpg", "image/png", Files.readAllBytes(Path.of("src/test/resources/testFile.jpg")))));
        when(userService.getUserByUsername(anyString())).thenReturn(this.admin);
        when(fileHandler.saveFile(anyString(), any())).thenReturn("saved");
        when(repository.saveAndFlush(any())).thenReturn(new Item());
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);

        itemService.saveItem("admin", item);
        verify(repository).saveAndFlush(captor.capture());

        assertEquals("ADMIN", captor.getValue().getOwner().getUsername());
        assertEquals("test", captor.getValue().getName());
        assertFalse(captor.getValue().isForSale());
        assertEquals(1, captor.getValue().getPhotos().size());
    }

}