package gg.jte.generated.ondemand;
import hexlet.code.dto.MainPage;
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,1,1,4,4,5,5,16,16,16,16,16,1,1,1,1};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, MainPage page) {
		jteOutput.writeContent("\n\n");
		gg.jte.generated.ondemand.layout.JtepageGenerated.render(jteOutput, jteHtmlInterceptor, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\n    <div class=\"container mt-4\">\n        <h1 class=\"mb-4\">Анализатор страниц</h1>\n        <form action=\"/urls\" method=\"post\">\n            <div class=\"mb-3\">\n                <label for=\"url\" class=\"form-label\">Введите URL:</label>\n                <input type=\"text\" class=\"form-control\" id=\"url\" name=\"url\" placeholder=\"URL\">\n            </div>\n            <button type=\"submit\" class=\"btn btn-primary\">Проверить</button>\n        </form>\n    </div>\n");
			}
		}, null);
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		MainPage page = (MainPage)params.get("page");
		render(jteOutput, jteHtmlInterceptor, page);
	}
}
