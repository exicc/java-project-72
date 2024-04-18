package hexlet.code.controller;

import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {

        var urls = UrlRepository.getAllUrls();
        Map<Long, UrlCheck> urlChecks = UrlCheckRepository.findLatestChecks();

        String error = ctx.consumeSessionAttribute("error");
        String success = ctx.consumeSessionAttribute("success");

        var page = new UrlsPage(urls, urlChecks);
        page.setError(error);
        page.setSuccess(success);
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {

        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);

        var urlOptional = Optional.ofNullable(UrlRepository.findUrlByID(id))
                .orElseThrow(() -> new NotFoundResponse("URL с ID " + id + " не найден"));

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
    }

    public static void checkUrl(Context ctx) throws SQLException {

        long id = ctx.pathParamAsClass("id", Long.class).getOrDefault(null);
        var urlOptional = Optional.ofNullable(UrlRepository.findUrlByID(id)
                .orElseThrow(() -> new NotFoundResponse("URL с ID " + id + " не найден")));

        try {
            HttpResponse<String> response = Unirest.get(urlOptional.get().getName()).asString();

            var statusCode = response.getStatus();
            Document doc = Jsoup.parse(response.getBody());
            var title = doc.title();
            Element h1Element = doc.selectFirst("h1");
            var h1 = h1Element == null ? "" : h1Element.text();
            var descriptionElement = doc.selectFirst("meta[name=description]");
            var description = descriptionElement == null ? "" : descriptionElement.attr("content");

            var newUrlCheck = new UrlCheck(statusCode, title, h1, description);
            newUrlCheck.setUrlId(id);
            UrlCheckRepository.save(newUrlCheck);
            ctx.sessionAttribute("success", "Страница успешно проверена");
            ctx.redirect("/urls/" + id);
        } catch (UnirestException e) {
            ctx.sessionAttribute("error", "Некоррентный адрес");
        } catch (Exception e) {
            ctx.sessionAttribute("error", e.getMessage());
        }
        ctx.redirect("/urls/" + urlOptional.get().getId());
    }

    public static void createUrl(Context ctx) throws SQLException {
        var inputUrl = ctx.formParam("url");
        URL parsedUrl;

        try {
            var uri = new URI(inputUrl);
            parsedUrl = uri.toURL();
        } catch (Exception e) {
            ctx.sessionAttribute("error", "Некорректный URL");
            ctx.redirect(NamedRoutes.rootPath());
            return;
        }
        String normalizedUrl = String
                .format(
                        "%s://%s%s",
                        parsedUrl.getProtocol(),
                        parsedUrl.getHost(),
                        parsedUrl.getPort() == -1 ? "" : ":" + parsedUrl.getPort()
                )
                .toLowerCase();

        Url url = UrlRepository.findUrlByName(normalizedUrl).orElse(null);

        if (url != null) {
            ctx.sessionAttribute("error", "Страница уже существует");
        } else {
            Url newUrl = new Url(normalizedUrl);
            UrlRepository.save(newUrl);
            ctx.sessionAttribute("success", "Страница успешно добавлена");
        }
        ctx.redirect("/urls", HttpStatus.forStatus(302));
    }
}
