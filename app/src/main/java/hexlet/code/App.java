package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.dto.MainPage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.resolve.ResourceCodeResolver;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.stream.Collectors;

import static io.javalin.rendering.template.TemplateUtil.model;


@Slf4j
public class App {
    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();

        app.start(getPort());
    }
    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }
    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL",
                "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
    }
    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }
    public static Javalin getApp() throws IOException, SQLException {

        var hikariConfig = new HikariConfig();
        var databaseUrl = getDatabaseUrl();
        hikariConfig.setJdbcUrl(databaseUrl);
        var sql = readResourceFile("schema.sql");
        var dataSource = new HikariDataSource(hikariConfig);

        log.info(sql);
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.before(ctx -> {
            ctx.contentType("text/html; charset=utf-8");
        });

        app.get("/", ctx -> {
            var page = new MainPage();
            ctx.render("index.jte", model("page", page));
        });

        app.get("/urls/{id}", ctx -> {
            var id = ctx.pathParamAsClass("id", Long.class).get();
            var urlOptional = UrlRepository.findByID(id);
            var urlChecks = UrlCheckRepository.getAllUrlChecks();
            String error = ctx.consumeSessionAttribute("error");
            String success = ctx.consumeSessionAttribute("success");
            if (urlOptional.isPresent()) {
                var page = new UrlPage(urlOptional.get(), urlChecks);
                page.setError(error);
                page.setSuccess(success);
                ctx.render("urls/show.jte", model("page", page));
            } else {
                ctx.status(404).result("URL not found");
            }
        });

        app.get("/urls", ctx -> {
            var urls = UrlRepository.getAllUrls();
            var urlChecks = UrlCheckRepository.getAllUrlChecks();
            String error = ctx.consumeSessionAttribute("error");
            String success = ctx.consumeSessionAttribute("success");
            var page = new UrlsPage(urls, urlChecks);
            page.setError(error);
            page.setSuccess(success);
            ctx.render("urls/index.jte", model("page", page));
        });

        app.post("/urls", ctx -> {
            var inputUrl = ctx.formParam("url");
            try {
                if (!inputUrl.startsWith("http://") && !inputUrl.startsWith("https://")) {
                    throw new IllegalArgumentException();
                }
                URI uri = new URI(inputUrl);
                URL url = uri.toURL();
                var domainWithPort = url.getProtocol() + "://" + url.getHost()
                        + (url.getPort() == -1 ? "" : ":" + url.getPort());
                var existingUrl = UrlRepository.findByDomain(domainWithPort);
                if (existingUrl.isPresent()) {
                    ctx.sessionAttribute("error", "Страница уже существует: " + existingUrl.get().getName());
                    ctx.redirect("/urls");
                    return;
                }
                Url newUrl = new Url(domainWithPort, new Timestamp(System.currentTimeMillis()));
                UrlRepository.save(newUrl);
                ctx.sessionAttribute("success", "Страница успешно добавлена");
                ctx.redirect("/urls");
            } catch (MalformedURLException e) {
                ctx.sessionAttribute("error", "Некорректный URL");
                ctx.redirect("/urls");
            } catch (IllegalArgumentException e) {
                ctx.sessionAttribute("error", "URL не является абсолютным");
                ctx.redirect("/urls");
            }
        });

        app.post("/urls/{id}/checks", ctx -> {
            long id = ctx.pathParamAsClass("id", Long.class).get();

            var urlOptional = UrlRepository.findByID(id);

            if (urlOptional.isPresent()) {
                var url  = urlOptional.get().getName();
                try {
                    HttpResponse<JsonNode> jsonResponse = Unirest.get(url).asJson();
                    if (jsonResponse.getStatus() == 200) {
                        var statusCode = jsonResponse.getStatus();

                        Document doc = Jsoup.connect(url).get();
                        var title = doc.title();

                        Element h1Element = doc.selectFirst("h1");
                        var h1 = h1Element != null ? h1Element.text() : "";

                        var description = doc.select("meta[name=description]").attr("content");

                        var check = new UrlCheck(statusCode, title, h1, description, id);
                        UrlCheckRepository.save(check);
                        ctx.sessionAttribute("success", "Страница успешно проверена");
                        ctx.redirect("/urls/" + id);
                    } else {
                        ctx.sessionAttribute("error", "Ошибка при выполнении запроса : " + jsonResponse.getStatus());
                        ctx.redirect("/urls/" + id);
                    }
                } catch (HttpStatusException e) {
                    ctx.sessionAttribute("error", "URL недоступен");
                    ctx.redirect("/urls" + id);
                } catch (UnirestException e){
                ctx.sessionAttribute("error", "Время ожидания запроса истекло");
                ctx.redirect("/urls/" + id);
                }
            } else {
                ctx.sessionAttribute("error", "URL с ID " + id + " не найден");
                ctx.redirect("/urls");
            }
        });
        return app;
    }
}
