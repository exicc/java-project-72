@import hexlet.code.dto.BasePage
@import gg.jte.Content
@param Content content
@param BasePage page = null

<!doctype html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Hexlet project</title>
        <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-light bg-light">
            <a class="navbar-brand" href="/">Главная</a>
            <a class="navbar-brand" href="/urls">Список Url'ов</a>
        </nav>
        @if(page != null)
            @if(page.getWarning() != null)
                <div class="alert alert-warning alert-dismissible fade show" role="alert">
                    <p>${page.getWarning()}</p>
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            @endif
            @if(page.getError() != null)
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <p>${page.getError()}</p>
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            @endif
            @if(page.getSuccess() != null)
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <p>${page.getSuccess()}</p>
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            @endif
        @endif


        <div class="container mt-4">
            ${content}
        </div>

        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <script>
            $(document).ready(function() {
                $(".alert").alert();
            });
        </script>
    </body>
</html>