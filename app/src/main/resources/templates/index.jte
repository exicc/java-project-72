@import hexlet.code.dto.MainPage
@param MainPage page


@template.layout.page(
    page = page,
    content = @`
    <div class="container-fluid p-5">
        <div class="row">
            <div class="col-md-10 col-lg-8 mx-auto text-white">
                <h1 class="display-3 mb-0">Анализатор страниц</h1>
                <p class="lead">Бесплатная проверка сайтов на SEO-пригодность</p>
                <form action="/urls" method="post">
                    <div class="form-floating mb-3 col w-100">
                        <input type="text" id="url" name="url" class="form-control" placeholder="https://example.com">
                        <label for="url">Адрес страницы</label>
                    </div>
                    <div class="col-auto">
                        <button type="submit" class="btn btn-primary btn-lg h-100">Проверить</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
`
)