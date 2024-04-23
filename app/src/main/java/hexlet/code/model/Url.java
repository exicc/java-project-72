package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;

@Setter
@Getter
@AllArgsConstructor
public final class Url {
    Long id;
    String name;
    Timestamp createdAt;
    public Url(String name) {
        this.name = name;
    }
}
