@RegisterPage serve para registrar várias @Page para o mestro Controlador.

@Page serve basicamente para dizer qual controlador será utilizador para a página registrada.

Ex: @Page(name="index", path="index.html")
Name: Um nome qualquer e único, apenas para referencias.
Path= Caminho de onde se encontra o HTML, partir do webcontent.

Outros atributos:
Mobile: Use a anotação @Mobile(path="") para dizer o caminho do site que seja voltado para mobile.
Ex: @Page(name="index",
path="index.html", /* HTML que será utilizado em browsers para DESKTOP */
mobile=@Mobile(path="indexMobile.html") /* HTML que será utilizado em browsers para MOBILE */
)

URLName :Serve para definir um nome único para  URL.
Ex: @Page(name="register",
path="register.html", //  http://localhost/project/register.html
URLName=”register” // http://localhost/project/register
)

Selector : Serve para quando chamar uma página, ir apenas um conteúdo selecionado passando como parâmetro (Query Selector).
Ex:
 <html>
	<body>
		<span>Test</span>
		<div id=”content”>Any thing</div>
	</body>
</html>
@Page(name="register", path="register.html", selector=”#content”)
Return: Any thing

ajaxSelector: Mesma coisa que o de cima, a diferença que é apenas para requisições AJAX.
Rules = Serve para registrar REGRAS DE ACESSO.
Ex: ../samples/userPrincipalRules.md
jsModule: Caso queira manipular o HTML sem ser no java, poderá fazer isso nativamente, apontando o local do  JS.
Ex: ../samples/ javaScriptModuleSystem.md

Parameters: Para registrar parâmetro fixo ao entrar na página, como se tivesse passando por GET(?name=value), porem o parâmetro fica escondido.
Ex: 
@RegisterPage({
	@Page(name="includeUser", path="maintainUser.html", URLName="includeUser", parameters={@PageParameter(name="clean", value="true"), @PageParameter(name="type", value="include")}),
	@Page(name="changeUser", path="maintainUser.html", URLName="changeUser", parameters={@PageParameter(name="clean", value="false"), @PageParameter(name="type", value="change")})
})
public class IndexController extends Window {
	
	@RequestParameter // Or GreenContext.getInstance().getRequest().getParameter("clean")
	private Boolean clean;
	
	@RequestParameter // Or GreenContext.getInstance().getRequest().getParameter("type")
	private String type;
	
    public void init() {
    	System.out.println(type);
    	
    	if(clean) {
    		// Sua lógica
    	}
    }
}
