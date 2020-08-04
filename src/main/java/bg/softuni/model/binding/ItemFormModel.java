package bg.softuni.model.binding;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class ItemFormModel {
    private String id;
    private String name;
    private String category;
    private String description;
    private List<MultipartFile> images;

    public ItemFormModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Length(min = 2, max = 30, message = "Name should be between 2 and 30 symbols.")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NotBlank(message = "Category can't be empty!")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Length(min = 10, message = "Description should be at least 10 symbols.")
    @NotBlank( message = "Description shouldn't be empty!")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Size(min = 1,max = 3,message = "You need to choose 1 - 3 photos.")
    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
}
