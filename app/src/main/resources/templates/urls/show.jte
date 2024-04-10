@import hexlet.code.dto.urls.UrlPage
@param UrlPage page

@template.layout.page(
    page = page,
    content = @`
    <section>
        <div class="container-lg mt-5">
            <h1>Сайт: ${page.getUrl().getName()}</h1>
            <table class="table table-bordered table-hover mt-3">
                <tbody>
                    <tr>
                        <td><strong>ID</strong></td>
                        <td>${page.getUrl().getId()}</td>
                    </tr>
                    <tr>
                        <td><strong>Имя</strong></td>
                        <td>${page.getUrl().getName()}</td>
                    </tr>
                    <tr>
                        <td><strong>Дата создания</strong></td>
                        <td>${page.getUrl().getFormattedTimestamp()}</td>
                    </tr>
                </tbody>
            </table>
            <h2 class="mt-5">Проверки</h2>
            <form method="post" action="/urls/${page.getUrl().getId()}/checks">
                <button type="submit" class="btn btn-primary">Запустить проверку</button>
            </form>
            <table class="table table-bordered table-hover mt-3">
                <thead>
                    <tr>
                        <th class="col-1">ID</th>
                        <th class="col-1">Код ответа</th>
                        <th>title</th>
                        <th>h1</th>
                        <th>description</th>
                        <th class="col-2">Дата проверки</th>
                    </tr>
                </thead>
                <tbody>
                    @if(!page.getUrlChecks().isEmpty())
                        @for(var urlCheck : page.getUrlChecks())
                            @if(urlCheck.getUrlId() == page.getUrl().getId())
                                <tr>
                                    <td>${urlCheck.getId()}</td>
                                    <td>${urlCheck.getStatusCode()}</td>
                                    <td>${urlCheck.getTitle()}</td>
                                    <td>${urlCheck.getH1()}</td>
                                    <td>${urlCheck.getDescription()}</td>
                                    <td>${urlCheck.getFormattedTimestamp()}</td>
                                </tr>
                            @endif
                        @endfor
                    @endif
                </tbody>
            </table>
        </div>
    </section>
`
)