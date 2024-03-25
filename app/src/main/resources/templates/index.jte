@import hexlet.code.dto.MainPage
@param MainPage page


@template.layout.page(content = @`
    <div class="container mt-4">
        <h1 class="mb-4">Анализатор страниц</h1>
        <form action="/urls" method="post">
            <div class="mb-3">
                <label for="url" class="form-label">Введите URL:</label>
                <input type="text" class="form-control" id="url" name="url" placeholder="Ссылка">
            </div>
            <button type="submit" class="btn btn-primary">Проверить</button>
        </form>
    </div>
`
)