import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;

import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;

public class AppTestOld {

    Javalin app;
    static MockWebServer server;
    static MockResponse response;
    static String domain;
    static String fakePageContent = "<html><head><title>Fake Page</title>"
            + "<meta name=\"description\" content=\"This is a fake page for testing purposes\"></head>"
            + "<body><h1>Welcome to Fake Page</h1><p>This is a fake page for testing purposes</p></body></html>";
    @BeforeAll
    public static void beforeAll() throws IOException {
        server = new MockWebServer();
        response = new MockResponse().setResponseCode(200).setBody(fakePageContent);
        server.enqueue(response);
        server.start();
        domain = "http://" + server.getHostName();
    }
    @AfterAll
    public static void afterAll() throws IOException {
        server.shutdown();
    }
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
    public void testUrlPage() throws SQLException {
        var url = new Url(domain, new Timestamp(System.currentTimeMillis()));
        UrlRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains(domain);
        });
    }
    @Test
    void testUrlNotFound()  {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }
    @Test
    public void testCreateUrl() {
        Url url = new Url(domain, new Timestamp(System.currentTimeMillis()));
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=" + domain;
            var response = client.post("/urls", requestBody);
            try {
                UrlRepository.save(url);

                var targetUrl = UrlRepository.getUrlByName(domain);
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

                var urlId = targetUrl.get().getId();

                var checkResponse = client.post("/urls/" + urlId + "/checks");
                assertThat(checkResponse.code())
                        .as("Check if the response code for checking URL is correct")
                        .isEqualTo(200);
            } catch (Exception ex) {
                fail("An unexpected exception occurred: " + ex.getMessage());
            }
        });
    }
}
