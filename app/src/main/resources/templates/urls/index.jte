@import hexlet.code.util.NamedRoutes
@import hexlet.code.dto.urls.UrlsPage
@param UrlsPage page

@template.layout.page(
    page = page,
    content = @`
    <div class="container-lg mt-5">
        <h1>Сайты</h1>
            <table class="table table-bordered">
                <thead>
                <tr>
                    <th class="col-1"><strong>ID</strong></th>
                    <th><strong>Имя</strong></th>
                    <th class="col-2 text-nowrap"><strong>Последняя проверка</strong></th>
                    <th class="col-1 text-nowrap"><strong>Код ответа</strong></th>
                </tr>
                </thead>
                <tbody>
                    @if (page != null)
                        @for (var url : page.getUrls())
                            <tr>
                                <td>${url.getId()}</td>
                                <td><a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a></td>
                                @if(page.getUrlChecks().containsKey(url.getId()))
                                    <td>${page.getUrlChecks().get(url.getId()).getFormattedTimestamp()}</td>
                                    <td>${page.getUrlChecks().get(url.getId()).getStatusCode()}</td>
                                @else
                                    <td></td>
                                    <td></td>
                                @endif
                            </tr>
                        @endfor
                    @endif
                </tbody>
            </table>
    </div>
`
)
