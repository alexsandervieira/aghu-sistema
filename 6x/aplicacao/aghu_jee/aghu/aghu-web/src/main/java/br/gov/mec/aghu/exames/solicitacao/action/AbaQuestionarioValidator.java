package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.gov.mec.aghu.dominio.DominioMascaraData;
import br.gov.mec.aghu.dominio.DominioTipoDadoQuestionario;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class AbaQuestionarioValidator extends ISECamposObrigatoriosValidator {

   
    private static final Log LOG = LogFactory.getLog(AbaQuestionarioValidator.class);

    public enum AbaQuestionarioValidatorExceptionCode implements BusinessExceptionCode {
        MASCARA_RESPOSTA_INVALIDA;
    }
   
    private int index;
   
    public AbaQuestionarioValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle messages, int index) {
        super(itemSolicitacaoExameVO, messages);
        this.index = index;
    }

    @Override
    boolean validate() {
        LOG.info("Class: " + getClass().getSimpleName());
       
        boolean validou = true;
        boolean validouQuestionario = false;
        if (getItemSolicitacaoExameVO().getRespostasQuestoes() != null && !getItemSolicitacaoExameVO().getRespostasQuestoes().isEmpty()) {
            for (final AelRespostaQuestao aelRespostaQuestao : getItemSolicitacaoExameVO().getRespostasQuestoes()) {
                if (aelRespostaQuestao.getQuestaoQuestionario().getObrigatorio() && StringUtils.isEmpty(aelRespostaQuestao.getResposta())
                        && aelRespostaQuestao.getAghCid() == null && aelRespostaQuestao.getAelValorValidoQuestao() == null
                        && !validouQuestionario) {
                    validou = false;
                    this.statusMessagesAddToControlFromResourceBundle(
                            "_" + aelRespostaQuestao.getQuestionario().getSeq().toString() + "_" + aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getSeq().toString() + "_"
                                    + this.index, Severity.ERROR, "MENSAGEM_RESPOSTA_OBRIGATORIO", getItemSolicitacaoExameVO().getUnfExecutaExame().getDescricaoUsualExame());
                    validouQuestionario = true;
                }
                if (StringUtils.isNotEmpty(aelRespostaQuestao.getResposta()) && DominioTipoDadoQuestionario.D.equals(aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getTipoDados())) {
                    if (aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getMascara() == null) {
                        this.statusMessagesAddToControlFromResourceBundle(
                                "_" + aelRespostaQuestao.getQuestionario().getSeq().toString() + "_" + aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getSeq().toString() + "_"
                                        + this.index, Severity.ERROR, "MASCARA_RESPOSTA_INVALIDA");
                        validou = false;
                    } else {
                        DominioMascaraData mascara = DominioMascaraData.valorPorMascara(aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getMascara());
                        final String pattern = mascara.getExibicao();
                        try {
                            if (pattern != null) {
                                DateTimeFormatter formatador = DateTimeFormat.forPattern(pattern);
                                formatador.parseDateTime(aelRespostaQuestao.getResposta());
                            }
                        } catch (IllegalArgumentException e) {
                            this.statusMessagesAddToControlFromResourceBundle(
                                    "_" + aelRespostaQuestao.getQuestionario().getSeq().toString() + "_" + aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getSeq().toString()
                                            + "_" + this.index, Severity.ERROR, "RESPOSTA_INVALIDA", aelRespostaQuestao.getResposta(),aelRespostaQuestao.getQuestaoQuestionario().getQuestao().getMascara());
                            validou = false;
                        }
                    }
                }
            }
        }
        return validou;
    }

}