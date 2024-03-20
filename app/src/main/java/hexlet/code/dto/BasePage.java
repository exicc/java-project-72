package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasePage {
    private String error;
    private String warning;
    private String success;
}
