@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.urls.UrlsPage
@param UrlsPage page

@template.layout.page(
    page = page,
    content = @`
    <div class="container mt-4">
        <h1 class="mb-4">Список URL</h1>
        <ul class="list-group">
            @if (page != null)
                @for (var url : page.getUrls())
                    <li class="list-group-item">
                        <a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
                        <span class="text-muted"> (Создано: ${String.valueOf(url.getCreatedAt())})</span>
                    </li>
                @endfor
            @endif
        </ul>
    </div>
`
)
