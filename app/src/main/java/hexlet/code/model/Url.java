package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
    public Url(String name, Timestamp createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }

    public String getFormattedTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return dateFormat.format(createdAt);
    }
}
