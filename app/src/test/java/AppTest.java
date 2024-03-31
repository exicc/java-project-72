import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;


public class AppTest {

    Javalin app;
    @BeforeEach
    public final void setup() throws IOException, SQLException {
        app = App.getApp();
    }
    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }
    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Сайты");
        });
    }
    @Test
    public void testCreateUrl() {
        String domain = "https://ya.ru";
        Url url = new Url(domain, new Timestamp(System.currentTimeMillis()));
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=https://ya.ru";
            var response = client.post("/urls", requestBody);
            try {
                UrlRepository.save(url);

                var targetUrl = UrlRepository.findByDomain(domain);
                assertThat(targetUrl)
                        .as("Check if URL with domain %s is present in the database", domain)
                        .isPresent();

                assertThat(targetUrl.get().getName())
                        .as("Check if the name of the retrieved URL contains the domain")
                        .contains(domain);

                assertThat(response.code())
                        .isEqualTo(200);

                assertThat(response.body().string())
                        .as("Check if the response body contains the expected URL")
                        .contains(domain);
            } catch (Exception ex) {
                fail("An unexpected exception occurred: " + ex.getMessage());
            }
        });
    }
    @Test
    public void testUrlPage() throws SQLException {
        var url = new Url("http://ya.ru", new Timestamp(System.currentTimeMillis()));
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        });
    }
    @Test
    void testCarNotFound()  {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }
}
