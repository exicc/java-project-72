package hexlet.code.controller;

import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
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
import java.sql.Timestamp;
import java.util.Optional;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getAllUrls();
        var urlChecks = UrlCheckRepository.getAllUrlChecks();
        String error = ctx.consumeSessionAttribute("error");
        String success = ctx.consumeSessionAttribute("success");
        var page = new UrlsPage(urls, urlChecks);
        page.setError(error);
        page.setSuccess(success);
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var urlOptional = UrlRepository.findUrlByID(id);
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

            var newUrlCheck = new UrlCheck(statusCode, title, h1, description, id);
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

    public static void create(Context ctx) {
        var inputUrl = ctx.formParamAsClass("url", String.class).getOrDefault(null);
        try {
            if (inputUrl.isEmpty() || (!inputUrl.startsWith("http://") && !inputUrl.startsWith("https://"))) {
                throw new IllegalArgumentException("Некорректный URL");
            }
            URI uri = new URI(inputUrl);
            URL url = uri.toURL();
            var domainWithPort = url.getProtocol() + "://" + url.getHost()
                    + (url.getPort() == -1 ? "" : ":" + url.getPort());
            var existingUrl = UrlRepository.getUrlByName(domainWithPort);
            if (existingUrl.isPresent()) {
                ctx.sessionAttribute("error", "Страница уже существует: " + existingUrl.get().getName());
                ctx.redirect("/urls");
                return;
            }
            Url newUrl = new Url(domainWithPort, new Timestamp(System.currentTimeMillis()));
            UrlRepository.save(newUrl);
            ctx.sessionAttribute("success", "Страница успешно добавлена");
            ctx.redirect("/urls");
        } catch (Exception e) {
            ctx.sessionAttribute("error", e.getMessage());
        }
        ctx.redirect("/urls");
    }
}
