@import hexlet.code.dto.urls.UrlPage
@param UrlPage page

@template.layout.page(
content = @`
    <div class="container mt-4">
        <div class="card">
            <div class="card-body">
                <h1 class="card-title">${page.getUrl().getName()}</h1>
            </div>
        </div>
    </div>
`
)