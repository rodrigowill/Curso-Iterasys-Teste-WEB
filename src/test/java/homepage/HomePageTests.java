package homepage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import base.BaseTests;
import pages.CarrinhoPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.PedidoPage;
import pages.ProdutoPage;
import util.Funcoes;

public class HomePageTests extends BaseTests{
	
	@Test
	public void testContarProdutos_oitoProdutosDiferentes() {
		carregarPaginaInicial();
		assertThat(homePage.contarProdutos(), is(8));
	}
	
	@Test
	public void testValidarCarrinhoZerado_ZeroItensNoCarrinho() {
		int produtosNoCarrinho = homePage.obterQuantidadeProdutosNoCarrinho();
		assertThat(produtosNoCarrinho, is(0));
	}

	ProdutoPage produtoPage;
	String nomeProduto_ProdutoPage;
	@Test
	public void testValidarDetalhesDoProduto_DescricaoEValorIguais() {
		
		// Declaração das variáveis usadas neste teste
		int indice = 0;
		
		// Buca informações da página principal
		String nomeProduto_HomePage = homePage.obterNomeProduto(indice);
		String precoProduto_HomePage = homePage.obterPrecoProduto(indice);
		produtoPage = homePage.clicarProduto(indice);
		
		// Busca informações da página do produto
		nomeProduto_ProdutoPage = produtoPage.obterNomeProduto();
		String precoProduto_ProdutoPage = produtoPage.obterPrecoProduto();		
		
		// Realiza as validações do teste
		assertThat(nomeProduto_HomePage.toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
		assertThat(precoProduto_HomePage, is(precoProduto_ProdutoPage));
	}
	
	LoginPage loginPage;
	@Test
	public void testLoginComSucesso_UsuarioLogado() {
		
		String email = "rodrigosoares@teste.com";
		String senha = "123456";
		
		loginPage = homePage.clicarSignIn();

		loginPage.preencherEmail(email);
		loginPage.preencherSenha(senha);
		
		loginPage.clicarSignIn();
		
		assertThat(homePage.estaLogado("Rodrigo Soares"), is(true));  
		
		carregarPaginaInicial();
	}
	
	@ParameterizedTest
	@CsvFileSource(resources = "/massaTeste_Login.csv", numLinesToSkip = 1, delimiter = ';')
	public void testLogin_UsuarioLogadoComDadosValidos(String nomeTeste, String email, String password, String nomeUsuario, String resultado) {
		
		loginPage = homePage.clicarSignIn();

		loginPage.preencherEmail(email);
		loginPage.preencherSenha(password);
		
		loginPage.clicarSignIn();
		
		boolean esperado_LoginOk;
		if (resultado.equals("positivo"))
			esperado_LoginOk = true;
		else
			esperado_LoginOk = false;
		
		assertThat(homePage.estaLogado(nomeUsuario), is(esperado_LoginOk));  
		
		capturarTela(nomeTeste, resultado);
		
		if (esperado_LoginOk)
			homePage.clicarBotaoSignOut();
		
		carregarPaginaInicial();
	}
	
	ModalProdutoPage modalProdutoPage;
	@Test
	public void testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso() {
		
		String tamanhoProduto = "M";
		String corProduto = "Black";
		int qtdProduto = 2;
		
		if (!homePage.estaLogado("Rodrigo Soares")) {
			testLoginComSucesso_UsuarioLogado();
		}
		testValidarDetalhesDoProduto_DescricaoEValorIguais();
		
		produtoPage.selecionarOpcaoDropDrown(tamanhoProduto);
		
		produtoPage.selecionarCorPreta();
		
		produtoPage.alterarQuantidade(qtdProduto);
		
		modalProdutoPage = produtoPage.clicarAdicionarCarrinho();
		
		assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado().endsWith("Product successfully added to your shopping cart"));
		
		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));

		String precoProdutoString = modalProdutoPage.obterPrecoProduto().replace("$", "");
		Double precoProduto = Double.parseDouble(precoProdutoString);
		
		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQtdProduto(), is(Integer.toString(qtdProduto)));
		
		String subTotalString =modalProdutoPage.obterSubTotal().replace("$", "");
		Double subTotal = Double.parseDouble(subTotalString);
		
		Double subTotalCalculado = precoProduto * qtdProduto;
		assertThat(subTotalCalculado, is(subTotal));
		
	}
	
	// Valores esperados
	String esperado_nomeProduto = "Hummingbird printed t-shirt";
	Double esperado_precoProduto = 19.12;
	String esperado_tamanhoProduto = "M";
	String esperado_corProduto = "Black";
	int esperado_qtdProduto = 2;
	Double esperado_subTotalCalculado = esperado_precoProduto * esperado_qtdProduto;
	int esperado_numeroItensTotal = esperado_qtdProduto;
	Double esperado_subtotalTotal = esperado_subTotalCalculado;
	Double esperado_shippingTotal = 7.00;
	Double esperado_totalTaxExclTotal = esperado_subtotalTotal + esperado_shippingTotal;
	Double esperado_totalTaxInclTotal = esperado_totalTaxExclTotal;
	Double esperado_taxasTotal = 0.00;
	
	String esperado_nomeCliente = "Rodrigo Soares";
	
	CarrinhoPage carrinhoPage;
	@Test
	public void testIrParaCarrinho_InformacoesPersistidas() {
		//--Pré-condições--//
		//Produto incluido na tela Modal
		testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso();
		
		//Clicar botão do carrinho
		carrinhoPage = modalProdutoPage.clicarIncluirCarrinho();
		
		//--Validações--//
		System.out.println(carrinhoPage.obter_nomeProduto());
		System.out.println(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_precoProduto()));
		System.out.println(carrinhoPage.obter_tamanhoProduto());
		System.out.println(carrinhoPage.obter_corProduto());
		System.out.println(carrinhoPage.obter_qtdProduto());
		System.out.println(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_subTotalProduto()));
		System.out.println(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()));
		System.out.println(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_subtotalTotal()));
		System.out.println(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_shippingTotal()));
		System.out.println(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_totalTaxExclTotal()));
		System.out.println(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_totalTaxInclTotal()));
		System.out.println(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_taxasTotal()));
		
		//Asserções Hamcrest
		assertThat(carrinhoPage.obter_nomeProduto(), is(esperado_nomeProduto));
		assertThat(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_precoProduto()), is(esperado_precoProduto));
		assertThat(carrinhoPage.obter_tamanhoProduto(), is(esperado_tamanhoProduto));
		assertThat(carrinhoPage.obter_corProduto(), is(esperado_corProduto));
		assertThat(Integer.parseInt(carrinhoPage.obter_qtdProduto()), is(esperado_qtdProduto));
		assertThat(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_subTotalProduto()), is(esperado_subTotalCalculado));
		assertThat(Funcoes.removeTextoItemsDevolveInt(carrinhoPage.obter_numeroItensTotal()), is(esperado_numeroItensTotal));
		assertThat(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_subtotalTotal()), is(esperado_subtotalTotal));
		assertThat(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_shippingTotal()), is(esperado_shippingTotal));
		assertThat(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_totalTaxExclTotal()), is(esperado_totalTaxExclTotal));
		assertThat(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_totalTaxInclTotal()), is(esperado_totalTaxInclTotal));
		assertThat(Funcoes.removeCifraoRetornaDouble(carrinhoPage.obter_taxasTotal()), is(esperado_taxasTotal));
		
	}
	
	CheckoutPage checkoutPage;
	@Test
	public void testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk() {
		//Pré-condições: Produto no carrinho de compras
		testIrParaCarrinho_InformacoesPersistidas();
		
		//CLicar no botão
		checkoutPage = carrinhoPage.clicarBotaoProceedToCheckout();
		
		//Validar informações na tela
		assertThat(Funcoes.removeCifraoRetornaDouble(checkoutPage.obter_totalTaxIncTotal()), is(esperado_totalTaxInclTotal));
	//	assertThat(checkoutPage.obter_nomeCliente(), is(esperado_nomeCliente));
		assertTrue(checkoutPage.obter_nomeCliente().startsWith(esperado_nomeCliente));
		
		checkoutPage.clicarBotaoContinueAddress();
		
		String encontrado_shippingValor = Funcoes.removeTexto(checkoutPage.obter_shippingValor(), " tax excl.");
		assertThat(Funcoes.removeCifraoRetornaDouble(encontrado_shippingValor), is(esperado_shippingTotal));
		
		checkoutPage.clicarBotaoConfirmDeliveryOption();
		
		//Selecionar opção "Pay by Check"
		checkoutPage.selecionarRadioPayByCheck();
		
		//Validar valor total
		String encontrado_amountPayByCheck = Funcoes.removeTexto(checkoutPage.obter_amountPayByCheck(), " (tax incl.)");
		assertThat(Funcoes.removeCifraoRetornaDouble(encontrado_amountPayByCheck), is(esperado_totalTaxInclTotal));
		
		//Clicar na opção "I agree"
		checkoutPage.marcarIAgree();
		assertTrue(checkoutPage.estaSelecionadoIAgree());
	}
	
	@Test
	public void testFinalizarPedido_pedidoFinalizadoComSucesso() {
		//Pré-condição: checkout concluido
		testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOk();
		
		//Clicar no botão para confirmar o pedido
		PedidoPage pedidoPage = checkoutPage.clicarBotaoConfirmaPedido();
		
		//Validar valores
		assertTrue(pedidoPage.obter_textoPedidoConfirmado().endsWith("YOUR ORDER IS CONFIRMED"));
		assertThat(pedidoPage.obter_email(), is("rodrigosoares@teste.com"));
		assertThat(pedidoPage.obter_totalProdutos(), is(esperado_subTotalCalculado));
		assertThat(pedidoPage.obter_totalTaxIncl(), is(esperado_totalTaxInclTotal));
		assertThat(pedidoPage.obter_metodoPagamento(), is("check"));
		
	}
}
