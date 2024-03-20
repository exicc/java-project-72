package hexlet.code.repository;

import hexlet.code.model.Url;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {
    public static void save(Url url) throws SQLException {
        var sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
                var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, url.getName());
            preparedStatement.setTimestamp(2, url.getCreatedAt());
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
                System.out.println("New URL ID: " + url.getId());
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }
    public static Optional<Url> findByDomain(String domain) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, domain);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                var name = resultSet.getString("name");
                var timestamp = resultSet.getTimestamp("created_at");
                var url = new Url(name, timestamp);
                url.setName(name);
                return Optional.of(url);
            } else {
                return Optional.empty();
            }
        }
    }
    public static List<Url> getAllUrls() throws SQLException {
        var sql = "SELECT * FROM urls";
        ArrayList<Url> result;
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            result = new ArrayList<>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var createdAt = resultSet.getTimestamp("created_at");
                var url = new Url(name, createdAt);
                url.setId(id);
                result.add(url);
            }
        }
        return result;
    }
    public static Optional<Url> find(long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                Timestamp createdAt = rs.getTimestamp("created_at");
                var url = new Url(name, createdAt);
                url.setId(id);
                return Optional.of(url);
            } else {
                return Optional.empty();
            }
        }
    }
}
