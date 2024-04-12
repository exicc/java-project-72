import hexlet.code.App;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    Javalin app;
    static MockWebServer server;
    static MockResponse response;
    static String domain;
    static Optional<Url> optionalUrl;
    static UrlCheck urlCheck;

    static String fakePageContent = "<html><head><title>Fake Page</title>"
            + "<meta name=\"description\" content=\"This is a fake page for testing purposes\"></head>"
            + "<body><h1>Welcome to Fake Page</h1><p>This is a fake page for testing purposes</p></body></html>";
    @BeforeAll
    public static void beforeAll() throws IOException, SQLException {
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
    public final void setup() throws SQLException, IOException {
        app = App.getApp();
        var newUrl = new Url(domain, new Timestamp(System.currentTimeMillis()));
        UrlRepository.save(newUrl);
        optionalUrl = UrlRepository.getUrlByName(domain);

        var newUrlCheck = new UrlCheck(200,
                "Fake Page",
                "Fake Page",
                "fake page",
                optionalUrl.get().getId());
        UrlCheckRepository.save(newUrlCheck);
        urlCheck = UrlCheckRepository.getLastCreatedUrlCheck();
    }
    @Nested
    class UrlTest {
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
                assertThat(response.body().string())
                        .contains(optionalUrl.get().getName())
                        .contains(optionalUrl.get().getId().toString());
            });
        }

        @Test
        public void testUrlPage() {

            JavalinTest.test(app, (server, client) -> {
                var response = client.get("/urls/" + optionalUrl.get().getId().toString());
                assertThat(response.code()).isEqualTo(200);
                assertThat(response.body().string())
                        .contains(optionalUrl.get().getName())
                        .contains(String.valueOf(urlCheck.getStatusCode()));
            });
        }

        @Test
        void testUrlNotFound() {
            JavalinTest.test(app, (server, client) -> {
                var response = client.get("/urls/999999");
                assertThat(response.code()).isEqualTo(404);
            });
        }
    }

    @Nested
    class UrlCheckTest {
        @Test
        void test() {
            JavalinTest.test(app, (server, client) -> {
                var requestBody = "url=" + domain;
                assertThat(client.post("/urls", requestBody).code()).isEqualTo(200);

                assertThat(optionalUrl.isPresent()).isTrue();
                assertThat(optionalUrl.get().getName()).isEqualTo(domain);

                client.post("/urls/" + optionalUrl.get().getId() + "/checks");

                assertThat(client.get("/urls/" + optionalUrl.get().getId()).code())
                        .isEqualTo(200);
                assertThat(urlCheck).isNotNull();
                assertThat(urlCheck.getTitle()).contains("Fake Page");
                assertThat(urlCheck.getH1()).contains("Fake Page");
                assertThat(urlCheck.getDescription()).contains("fake page");
            });
        }
    }
}
