package bg.softuni.model.binding;

import org.hibernate.validator.constraints.Length;

public class AddCommentModel {
    private String content;

    public AddCommentModel() {
    }

    public AddCommentModel(String content) {
        this.content = content;
    }

    @Length(min = 1,max = 150,message = "Comment length should be between 1 and 150 characters!")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
