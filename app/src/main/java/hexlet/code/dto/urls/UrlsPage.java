package hexlet.code.dto.urls;

import hexlet.code.dto.BasePage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Getter
public final class UrlsPage extends BasePage {
    private List<Url> urls;
    private List<UrlCheck> urlChecks;

    public Optional<UrlCheck> getLastCheck(long urlId) {
        List<UrlCheck> checks = new ArrayList<>(urlChecks);
        UrlCheck latestCheck = null;

        for (var check : checks) {
            if (check.getUrlId() == urlId) {
                if (latestCheck == null || check.getCreatedAt().after(latestCheck.getCreatedAt())) {
                    latestCheck = check;
                }
            }
        }
        return Optional.ofNullable(latestCheck);
    }

}
