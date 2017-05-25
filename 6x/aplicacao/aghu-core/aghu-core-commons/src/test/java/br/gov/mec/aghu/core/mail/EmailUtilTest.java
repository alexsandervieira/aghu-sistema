package br.gov.mec.aghu.core.mail;

import org.junit.Assert;
import org.junit.Test;

public class EmailUtilTest {
	
	@Test
	public void testarOfuscacaoEmailGrandePonto() {
		String emailOfuscadoEsperado = "ra***********ao@gmail.com";
		
		String emailOfuscado = EmailUtil.ofuscarEmail("rafael.corvalao@gmail.com");
		
		Assert.assertTrue(emailOfuscadoEsperado.equalsIgnoreCase(emailOfuscado));
	}
	
	@Test
	public void testarOfuscacaoEmailGrande() {
		String emailOfuscadoEsperado = "ra**********ao@gmail.com";
		
		String emailOfuscado = EmailUtil.ofuscarEmail("rafaelcorvalao@gmail.com");
		
		Assert.assertTrue(emailOfuscadoEsperado.equalsIgnoreCase(emailOfuscado));
	}

	@Test
	public void testarOfuscacaoEmailTamanho4() {
		String emailOfuscadoEsperado = "ra**@gmail.com";
		
		String emailOfuscado = EmailUtil.ofuscarEmail("rafa@gmail.com");
		
		Assert.assertTrue(emailOfuscadoEsperado.equalsIgnoreCase(emailOfuscado));
	}

	@Test
	public void testarOfuscacaoEmailTamanho5() {
		String emailOfuscadoEsperado = "ra***@gmail.com";
		
		String emailOfuscado = EmailUtil.ofuscarEmail("rafae@gmail.com");
		
		Assert.assertTrue(emailOfuscadoEsperado.equalsIgnoreCase(emailOfuscado));
	}
	
	@Test
	public void testarOfuscacaoEmailTamanho6() {
		String emailOfuscadoEsperado = "ra****@gmail.com";
		
		String emailOfuscado = EmailUtil.ofuscarEmail("rafael@gmail.com");
		
		Assert.assertTrue(emailOfuscadoEsperado.equalsIgnoreCase(emailOfuscado));
	}

	@Test
	public void testarOfuscacaoEmailTamanho7() {
		String emailOfuscadoEsperado = "ra*****@gmail.com";
		
		String emailOfuscado = EmailUtil.ofuscarEmail("rafaelc@gmail.com");
		
		Assert.assertTrue(emailOfuscadoEsperado.equalsIgnoreCase(emailOfuscado));
	}

	@Test
	public void testarOfuscacaoEmailTamanho8() {
		String emailOfuscadoEsperado = "ra******@gmail.com";
		
		String emailOfuscado = EmailUtil.ofuscarEmail("rafaelco@gmail.com");
		
		Assert.assertTrue(emailOfuscadoEsperado.equalsIgnoreCase(emailOfuscado));
	}
	
	@Test
	public void testarOfuscacaoEmailTamanho9() {
		String emailOfuscadoEsperado = "ra*****or@gmail.com";
		
		String emailOfuscado = EmailUtil.ofuscarEmail("rafaelcor@gmail.com");
		
		Assert.assertTrue(emailOfuscadoEsperado.equalsIgnoreCase(emailOfuscado));
	}


	@Test
	public void testarOfuscacaoEmailComStringVazia() {
		String emailOfuscadoEsperado = null;
		
		String emailOfuscado = EmailUtil.ofuscarEmail(" ");
		
		Assert.assertTrue(emailOfuscadoEsperado == emailOfuscado);
	}

	@Test
	public void testarOfuscacaoEmailComParametroNulo() {
		String emailOfuscadoEsperado = null;
		
		String emailOfuscado = EmailUtil.ofuscarEmail(null);
		
		Assert.assertTrue(emailOfuscadoEsperado == emailOfuscado);
	}
	
	@Test
	public void testarOfuscacaoEmailNaoValidoSemArroba() {
		String emailOfuscadoEsperado = null;
		
		String emailOfuscado = EmailUtil.ofuscarEmail("rafael_corvalaoterra.com.br");
		
		Assert.assertTrue(emailOfuscadoEsperado == emailOfuscado);
	}

	@Test
	public void testarOfuscacaoEmailNaoValidoURL() {
		String emailOfuscadoEsperado = null;
		
		String emailOfuscado = EmailUtil.ofuscarEmail("www.terra.com.br");
		
		Assert.assertTrue(emailOfuscadoEsperado == emailOfuscado);
	}

	@Test
	public void testarOfuscacaoEmailNaoValidoApenasUmDominio() {
		String emailOfuscadoEsperado = null;
		
		String emailOfuscado = EmailUtil.ofuscarEmail("rafael@corvalao");
		
		Assert.assertTrue(emailOfuscadoEsperado == emailOfuscado);
	}

}
