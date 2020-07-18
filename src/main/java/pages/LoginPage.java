package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
	
	private WebDriver driver;
	private By email = By.name("email");
	private By senha = By.name("password");
	private By botaoSingIn = By.id("submit-login");
	
	public LoginPage(WebDriver driver) {
		this.driver = driver;
	}
	
	public void preencherEmail(String emailUsuario) {
		driver.findElement(email).sendKeys(emailUsuario);
	}
	
	public void preencherSenha(String senhaUsuario) {
		driver.findElement(senha).sendKeys(senhaUsuario);
	}
	
	public void clicarSignIn() {
		driver.findElement(botaoSingIn).click();
	}

}
