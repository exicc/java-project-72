@import hexlet.code.dto.MainPage
@param MainPage page


@template.layout.page(content = @`
    <div class="container-fluid bg-dark p-5">
        <div class="row">
            <div class="col-md-10 col-lg-8 mx-auto text-white">
                <h1 class="display-3 mb-0">Анализатор страниц</h1>
                <p class="lead">Бесплатная проверка сайтов на SEO-пригодность</p>
                <form action="/urls" method="post" class="rss-form text-body">
                    <div class="row">
                        <div class="col">
                            <div class="form-floating">
                                <label for="url">Введите URL:</label>
                                <input id="url" autofocus type="text" required name="url" aria-label="url" class="form-control" placeholder="Ссылка" autocomplete="off">
                            </div>
                        </div>
                    </div>
                    <div class="col-auto">
                        <button type="submit" class="btn btn-primary btn-lg px-sm-5 mt-2 mt-md-0">Проверить</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
`
)