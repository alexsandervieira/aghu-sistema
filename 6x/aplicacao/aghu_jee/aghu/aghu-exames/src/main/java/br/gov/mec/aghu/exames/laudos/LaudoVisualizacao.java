package br.gov.mec.aghu.exames.laudos;

public class LaudoVisualizacao extends LaudoMascara {

	@Override
	public void executar(Boolean consideraAlinhamentoVertical) {
		for (ExameVO exame : this.getExamesLista().getExames()) {
			super.criarCabecalhoExame(exame);
			super.criarMensagemResultadoNaoLiberado(exame);
			super.processaMascaras(exame, consideraAlinhamentoVertical);
			super.criarRecebimentoLiberacao(exame);
			super.criarAssinaturaMedico(exame);
			super.criarInformacoesColeta(exame);
			super.criarInformacoesRespiracao(exame);
			super.criarNotasAdicionais(exame);
			super.criarAssinaturaEletronica(exame);
			super.criarLinha();
		}
	}

}
