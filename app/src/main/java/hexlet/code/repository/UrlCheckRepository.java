package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UrlCheckRepository extends BaseRepository {
    public static void save(UrlCheck urlCheck) throws SQLException {
        var sql = "INSERT INTO url_checks "
                + "(status_code, title, h1, description, url_id, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        var statusCode = urlCheck.getStatusCode();
        var title = urlCheck.getTitle();
        var h1 = urlCheck.getH1();
        var description = urlCheck.getDescription();
        var urlId = urlCheck.getUrlId();
        var createdAt = new Timestamp(System.currentTimeMillis());

        try (var conn = dataSource.getConnection();
             var preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, statusCode);
            preparedStatement.setString(2, title);
            preparedStatement.setString(3, h1);
            preparedStatement.setString(4, description);
            preparedStatement.setLong(5, urlId);
            preparedStatement.setTimestamp(6, createdAt);
            preparedStatement.executeUpdate();
            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                urlCheck.setId(generatedKeys.getLong(1));
                urlCheck.setCreatedAt(createdAt);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }
    public static List<UrlCheck> getAllUrlChecks() throws SQLException {
        var sql = "SELECT * FROM url_checks";
        ArrayList<UrlCheck> result;
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            result = new ArrayList<>();
            while (resultSet.next()) {
                var urlCheck = new UrlCheck(
                        resultSet.getInt("status_code"),
                        resultSet.getString("title"),
                        resultSet.getString("h1"),
                        resultSet.getString("description"),
                        resultSet.getLong("url_id"),
                        resultSet.getTimestamp("created_at")
                );
                urlCheck.setId(resultSet.getLong("id"));
                result.add(urlCheck);
            }
        }
        return result;
    }
}
