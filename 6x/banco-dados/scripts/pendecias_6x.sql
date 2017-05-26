--19/02/2016 #71270 - Novas coluas VIEW v_mam_receitas
DROP VIEW agh.v_mam_receitas;

-- View: agh.v_mam_receitas
CREATE OR REPLACE VIEW agh.v_mam_receitas AS 
SELECT RCT.CON_NUMERO,
RCT.SEQ,
RCT.TIPO,
RCT.ATD_SEQ,
RCT.ASU_APA_ATD_SEQ,
RCT.DTHR_CRIACAO,
RCT.PAC_CODIGO,
IRC.SEQP,
IRC.DESCRICAO,
IRC.QUANTIDADE,
IRC.FORMA_USO,
IRC.IND_USO_CONTINUO,
IRC.IND_INTERNO
FROM agh.mam_item_receituarios irc,
agh.mam_receituarios rct
WHERE irc.rct_seq = rct.seq AND rct.ind_pendente::text <> 'E'::text AND rct.dthr_mvto IS NULL;

ALTER TABLE agh.v_mam_receitas
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mam_receitas TO postgres;
GRANT ALL ON TABLE agh.v_mam_receitas TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_receitas TO acesso_leitura;


--01/03/2016 #71854 - Ajuste VERSION tabelas IMP
UPDATE agh.IMP_CLASSE_IMPRESSAO SET version = 0 WHERE version is null;
ALTER TABLE agh.IMP_CLASSE_IMPRESSAO ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE agh.IMP_CLASSE_IMPRESSAO ALTER COLUMN version SET NOT NULL;

UPDATE agh.imp_computador SET version = 0 WHERE version is null;
ALTER TABLE agh.imp_computador ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE agh.imp_computador ALTER COLUMN version SET NOT NULL;

UPDATE agh.imp_computador_impressora SET version = 0 WHERE version is null;
ALTER TABLE agh.imp_computador_impressora ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE agh.imp_computador_impressora ALTER COLUMN version SET NOT NULL;

UPDATE agh.imp_impressora SET version = 0 WHERE version is null;
ALTER TABLE agh.imp_impressora ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE agh.imp_impressora ALTER COLUMN version SET NOT NULL;

UPDATE agh.imp_servidor_cups SET version = 0 WHERE version is null;
ALTER TABLE agh.imp_servidor_cups ALTER COLUMN version SET DEFAULT 0;
ALTER TABLE agh.imp_servidor_cups ALTER COLUMN version SET NOT NULL;

--10/11/2016 #86153 Script de update comentado para evitar que altere o valor do parametro em todas as atualizações.
/*
--14/04/2016 #74991 - Padronização de campo da unitarizadora.
UPDATE agh.agh_parametros SET vlr_texto = '/opt/aghu/unitarizador/unitarizadora.csv' WHERE nome = 'P_AGHU_CAMINHO_SAIDA_ARQUIVO_UNITARIZADORA';
*/

--25/04/2016 #75322 - Atualizar VIEW v_ael_pesq_pol_exame_unid_hist
CREATE OR REPLACE VIEW agh.v_ael_pesq_pol_exame_unid_hist AS 
SELECT "substring"(unf.descricao::text, 1, 30) AS unf_descricao,
unf.seq AS unf_seq,
CASE
WHEN ise.sit_codigo::text = 'LI'::text THEN ( SELECT max(eis.dthr_evento) AS max
FROM hist.ael_extrato_item_solics eis
WHERE eis.ise_soe_seq = soe.seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = 'AE'::text)
WHEN ise.sit_codigo::text <> 'LI'::text THEN soe.criado_em
ELSE NULL::timestamp without time zone
END AS data,
oem.ordem_nivel1,
oem.ordem_nivel2,
exa.descricao_usual,
man.descricao,
ise.soe_seq,
ise.seqp,
atd.pac_codigo
FROM agh.agh_atendimentos atd
JOIN hist.ael_solicitacao_exames soe ON atd.seq = soe.atd_seq
JOIN hist.ael_item_solicitacao_exames ise ON soe.seq = ise.soe_seq
JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
JOIN agh.agh_unidades_funcionais unf ON unf.seq = ise.ufe_unf_seq
LEFT JOIN agh.ael_ord_exame_mat_analises oem ON oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
WHERE ise.sit_codigo::text <> 'CA'::text
UNION
SELECT "substring"(unf.descricao::text, 1, 30) AS unf_descricao,
unf.seq AS unf_seq,
CASE
WHEN ise.sit_codigo::text = 'LI'::text THEN ( SELECT max(eis.dthr_evento) AS max
FROM hist.ael_extrato_item_solics eis
WHERE eis.ise_soe_seq = soe.seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = 'AE'::text)
WHEN ise.sit_codigo::text <> 'LI'::text THEN soe.criado_em
ELSE NULL::timestamp without time zone
END AS data,
oem.ordem_nivel1,
oem.ordem_nivel2,
exa.descricao_usual,
man.descricao,
ise.soe_seq,
ise.seqp,
atv.pac_codigo
FROM agh.ael_atendimento_diversos atv
JOIN hist.ael_solicitacao_exames soe ON atv.seq = soe.atv_seq
JOIN hist.ael_item_solicitacao_exames ise ON soe.seq = ise.soe_seq
JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
JOIN agh.agh_unidades_funcionais unf ON unf.seq = ise.ufe_unf_seq
LEFT JOIN agh.ael_ord_exame_mat_analises oem ON oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
WHERE soe.atv_seq = atv.seq AND ise.soe_seq = soe.seq AND ise.sit_codigo::text <> 'CA'::text AND exa.sigla::text = ise.ufe_ema_exa_sigla::text AND man.seq = ise.ufe_ema_man_seq AND unf.seq = ise.ufe_unf_seq AND oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
ORDER BY 1, 3 DESC, 4, 5, 6, 7;

--25/04/2016 #75320 - Atualizar VIEW v_ael_pesq_pol_exames_unid
CREATE OR REPLACE VIEW agh.v_ael_pesq_pol_exames_unid AS 
SELECT "substring"(unf.descricao::text, 1, 30) AS unf_descricao,
unf.seq AS unf_seq,
CASE
WHEN ise.sit_codigo::text = 'LI'::text THEN ( SELECT max(eis.dthr_evento) AS max
FROM agh.ael_extrato_item_solics eis
WHERE eis.ise_soe_seq = soe.seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = 'AE'::text)
WHEN ise.sit_codigo::text <> 'LI'::text THEN soe.criado_em
ELSE NULL::timestamp without time zone
END AS data,
oem.ordem_nivel1,
oem.ordem_nivel2,
exa.descricao_usual,
man.descricao,
ise.soe_seq,
ise.seqp,
atd.pac_codigo
FROM agh.agh_atendimentos atd
JOIN agh.ael_solicitacao_exames soe ON atd.seq = soe.atd_seq
JOIN agh.ael_item_solicitacao_exames ise ON soe.seq = ise.soe_seq
JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
JOIN agh.agh_unidades_funcionais unf ON unf.seq = ise.ufe_unf_seq
LEFT JOIN agh.ael_ord_exame_mat_analises oem ON oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
WHERE ise.sit_codigo::text <> 'CA'::text
UNION
SELECT "substring"(unf.descricao::text, 1, 30) AS unf_descricao,
unf.seq AS unf_seq,
CASE
WHEN ise.sit_codigo::text = 'LI'::text THEN ( SELECT max(eis.dthr_evento) AS max
FROM agh.ael_extrato_item_solics eis
WHERE eis.ise_soe_seq = soe.seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = 'AE'::text)
WHEN ise.sit_codigo::text <> 'LI'::text THEN soe.criado_em
ELSE NULL::timestamp without time zone
END AS data,
oem.ordem_nivel1,
oem.ordem_nivel2,
exa.descricao_usual,
man.descricao,
ise.soe_seq,
ise.seqp,
atv.pac_codigo
FROM agh.ael_atendimento_diversos atv
JOIN agh.ael_solicitacao_exames soe ON atv.seq = soe.atv_seq
JOIN agh.ael_item_solicitacao_exames ise ON soe.seq = ise.soe_seq
JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
JOIN agh.agh_unidades_funcionais unf ON unf.seq = ise.ufe_unf_seq
LEFT JOIN agh.ael_ord_exame_mat_analises oem ON oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
WHERE soe.atv_seq = atv.seq AND ise.soe_seq = soe.seq AND ise.sit_codigo::text <> 'CA'::text AND exa.sigla::text = ise.ufe_ema_exa_sigla::text AND man.seq = ise.ufe_ema_man_seq AND unf.seq = ise.ufe_unf_seq AND oem.ema_exa_sigla::text = ise.ufe_ema_exa_sigla::text AND oem.ema_man_seq = ise.ufe_ema_man_seq
ORDER BY 1, 3 DESC, 4, 5, 6, 7;

--27/04/2016 #73435 - Aumento de colunas da tabela de JN de consultas.

ALTER TABLE AGH.AAC_CONSULTAS_JN ALTER COLUMN IND_SIT_CONSULTA TYPE CHARACTER VARYING(2);
ALTER TABLE AGH.AAC_CONSULTAS_JN ALTER COLUMN STC_SITUACAO TYPE CHARACTER VARYING(2);

--28/04/2016 #75869 - Criar parâmetro de sistema P_QTD_MAX_RESULTADOS_EXAMES_NAO_VISUALIZADOS
INSERT INTO agh.agh_parametros( seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, alterado_em, alterado_por, vlr_data, vlr_numerico
, vlr_texto, descricao, rotina_consistencia, version, vlr_data_padrao, vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado) 
VALUES ((select nextval('agh.agh_psi_sq1')), 'MPM', 'P_QTD_MAX_RESULTADOS_EXAMES_NAO_VISUALIZADOS', 'S', now(), 'AGHU', null, null, null, 20, null
, 'Valor numérico que define a quantidade máxima de resultados de exames não visualizados a serem exibidos na prescrição médica', null, 0, null, null
, null, null, 'N');

--09/05/2016 #76018 - Alterar tipo do campo reg_nascimento para String
DROP VIEW IF EXISTS agh.v_ain_internacao_paciente;

ALTER TABLE agh.aip_pacientes
ALTER COLUMN reg_nascimento TYPE character varying(32);

ALTER TABLE agh.aip_pacientes_hist
ALTER COLUMN reg_nascimento TYPE character varying(32);

ALTER TABLE agh.agh_responsaveis
ALTER COLUMN reg_nascimento TYPE character varying(32);

ALTER TABLE agh.agh_responsaveis_jn
ALTER COLUMN reg_nascimento TYPE character varying(32);

CREATE OR REPLACE VIEW agh.v_ain_internacao_paciente AS 
SELECT inte.seq,
inte.pac_codigo AS registro,
pac.nome,
pac.prontuario,
pac.nro_cartao_saude AS cartao_sus,
nac.descricao AS nacionalidade,
pac.naturalidade,
pac.grau_instrucao AS cod_escolaridade,
pac.cor AS cod_cor,
pac.rg,
pac.cpf,
pac.numero_pis,
pac.reg_nascimento,
pac.dt_nascimento,
pac.nome_mae,
pac.nome_pai,
pac.sexo,
pac.estado_civil,
ende.logradouro AS "endereço",
ende.nro_logradouro AS numero,
ende.compl_logradouro AS complemento,
ende.bairro,
cid.uf_sigla AS estado,
cid.nome AS cidade,
cid.cep,
endec.logradouro,
pac.ddd_fone_residencial,
pac.fone_residencial AS telefone,
endec.nro_logradouro,
endec.compl_logradouro AS complementoc,
cidc.uf_sigla AS estadoc,
cidc.nome AS cidadec,
cidc.cep AS cepc,
pac.ddd_fone_recado,
pac.fone_recado,
frh.fator_rh,
peso.peso,
alt.altura
FROM agh.ain_internacoes inte
JOIN agh.aip_pacientes pac ON inte.pac_codigo = pac.codigo
JOIN agh.aip_nacionalidades nac ON pac.nac_codigo = nac.codigo
JOIN agh.aip_enderecos_pacientes ende ON pac.codigo = ende.pac_codigo AND ende.tipo_endereco::text = 'R'::text
LEFT JOIN agh.aip_cidades cid ON cid.codigo = ende.cdd_codigo
LEFT JOIN agh.aip_enderecos_pacientes endec ON pac.codigo = endec.pac_codigo AND endec.tipo_endereco::text <> 'R'::text
LEFT JOIN agh.aip_cidades cidc ON cidc.codigo = endec.cdd_codigo
LEFT JOIN agh.aip_paciente_dado_clinicos frh ON pac.codigo = frh.pac_codigo
LEFT JOIN agh.aip_peso_pacientes peso ON pac.codigo = peso.pac_codigo
LEFT JOIN agh.aip_altura_pacientes alt ON pac.codigo = alt.pac_codigo;

ALTER TABLE agh.v_ain_internacao_paciente
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao_paciente TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao_paciente TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_internacao_paciente TO acesso_leitura;

--09/05/2016 #75709 - Permitir apenas que medicos contratados e professor possam ser responsaveis pela cirurgia.
ALTER TABLE AGH.MBC_AGENDAS DROP CONSTRAINT mbc_agd_ck1;

ALTER TABLE AGH.MBC_AGENDAS ADD CONSTRAINT mbc_agd_ck1 CHECK (puc_ind_funcao_prof::text = ANY(ARRAY['MPF'::character varying::text,'MCO'::character varying::text]));

--09/05/2016 #76640 - Insert parametro APAC impressão
insert into agh.agh_parametros values ((select nextval('agh.agh_psi_sq1')),'MAM','P_AGHU_IMPRIMIR_APAC_DIRETO_AMBULATORIO','S',now(),'AGHU',now()
,'AGHU',NULL,NULL,'S','Habilita impressao direta da Apac no ambulatorio desconsiderando regra de impressao e reimpressao faturamento (otorrino, ofta)',NULL,2,NULL
,NULL,NULL,NULL,'T');


--18/05/2016 #67356	 Criação de tabela de Jn de leitos.

CREATE TABLE agh.ain_leitos_jn
(
  jn_user character varying(30) NOT NULL,
  jn_date_time timestamp without time zone NOT NULL,
  jn_operation character varying(3) NOT NULL,
  lto_id character varying(14) NOT NULL,
  qrt_numero smallint NOT NULL,
  leito character varying(4) NOT NULL,
  ind_cons_clin_unidade character varying(1) NOT NULL,
  ind_bloq_leito_limpeza character varying(1) NOT NULL,
  tml_codigo smallint NOT NULL,
  unf_seq smallint NOT NULL,
  ind_situacao character varying(1) DEFAULT 'A'::character varying,
  esp_seq smallint,
  int_seq integer,
  ind_pertence_refl character varying(1) DEFAULT 'S'::character varying,
  ind_cons_esp character varying(1) NOT NULL,
  ser_matricula integer,
  ser_vin_codigo smallint,
  ind_acompanhamento_ccih character varying(1),
  version integer DEFAULT 0,
  seq_jn bigint NOT NULL,
  CONSTRAINT ain_ltoj_pk PRIMARY KEY (seq_jn)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE agh.ain_leitos_jn
  OWNER TO postgres;
GRANT ALL ON TABLE agh.ain_leitos_jn TO postgres;
GRANT ALL ON TABLE agh.ain_leitos_jn TO acesso_completo;
GRANT SELECT ON TABLE agh.ain_leitos_jn TO acesso_leitura;


CREATE SEQUENCE agh.ain_lto_jn_seq
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 99999999999999
  START 1691
  CACHE 2;
ALTER TABLE agh.ain_lto_jn_seq
  OWNER TO postgres;
GRANT ALL ON SEQUENCE agh.ain_lto_jn_seq TO postgres;
GRANT ALL ON SEQUENCE agh.ain_lto_jn_seq TO acesso_completo;
GRANT SELECT ON SEQUENCE agh.ain_lto_jn_seq TO acesso_leitura;

--31/05/2016 #77964 Criaçao de sequence para tabela de PDT
CREATE SEQUENCE agh.pdt_aps_jn_sq1;

--03/06/2016 #78288 - Novo parametro P_AGHU_PERIODO_DEFAULT_VISUALIZAR_CONTROLES
insert into agh.agh_parametros select
(select nextval('agh.agh_psi_sq1')),'ECP','P_AGHU_PERIODO_DEFAULT_VISUALIZAR_CONTROLES','S',now(),'AGHU',NULL,NULL,NULL,24,NULL,
'Define o valor em horas para o período default na tela de visualizar controles de pacientes. Valores aceitáveis: 1, 6, 12, 24, 48, 168 (7 dias), 360 (15 dias)',
NULL,0,NULL,NULL,NULL,NULL,'N'
WHERE
NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_PERIODO_DEFAULT_VISUALIZAR_CONTROLES' AND sis_sigla = 'ECP');

--08/06/2016 #78468 - Criação da tabela aac_permissao_agendamento_consultas
CREATE TABLE agh.aac_permissao_agendamento_consultas (
    seq integer NOT NULL,
    ser_matricula integer NOT NULL,
    ser_vin_codigo smallint NOT NULL,
    criado_em timestamp without time zone NOT NULL,
    ind_situacao character varying(1) DEFAULT 'A'::character varying,
    grd_seq integer,
    eqp_seq smallint,
    esp_seq smallint,
    unf_seq smallint,
    prof_ser_matricula integer,
    prof_ser_vin_codigo smallint,
    version integer DEFAULT 0 NOT NULL,
    CONSTRAINT aac_grd_ck1 CHECK (((ind_situacao)::text = ANY (ARRAY[('A'::character varying)::text, ('I'::character varying)::text])))
);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_permissao_agendamento_consultas_pkey PRIMARY KEY (seq);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_eqp_fk1 FOREIGN KEY (eqp_seq) REFERENCES agh.agh_equipes(seq);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_esp_fk1 FOREIGN KEY (esp_seq) REFERENCES agh.agh_especialidades(seq);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_ser_fk1 FOREIGN KEY (ser_matricula, ser_vin_codigo) REFERENCES agh.rap_servidores(matricula, vin_codigo);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_prof_ser_fk1 FOREIGN KEY (prof_ser_matricula, prof_ser_vin_codigo) REFERENCES agh.rap_servidores(matricula, vin_codigo);

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_grd_fk1 FOREIGN KEY (grd_seq) REFERENCES agh.aac_grade_agendamen_consultas(seq) ON DELETE CASCADE;

ALTER TABLE ONLY agh.aac_permissao_agendamento_consultas
    ADD CONSTRAINT aac_perm_ag_con_unf_fk1 FOREIGN KEY (unf_seq) REFERENCES agh.agh_unidades_funcionais(seq);

CREATE SEQUENCE agh.aac_perm_agend_cons_sq1
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 99999999999999
    NO MINVALUE
    CACHE 2;

ALTER TABLE agh.aac_permissao_agendamento_consultas OWNER TO postgres;

GRANT ALL ON TABLE agh.aac_permissao_agendamento_consultas TO postgres;
GRANT ALL ON TABLE agh.aac_permissao_agendamento_consultas TO acesso_completo;
GRANT SELECT ON TABLE agh.aac_permissao_agendamento_consultas TO acesso_leitura;


--13/06/2016 #78846 - Nova coluna tabela agh.agh_unidades_funcionais
alter table agh.agh_unidades_funcionais add column setor character varying(2);

--27/10/2016  #86921 Acrescimo de comentario para a coluna setor da table agh_unidades_funcionais
COMMENT ON COLUMN agh.agh_unidades_funcionais.setor IS 'Setor onde se encontra a Unidade Funcional';
	
--14/06/2016 #78966 - Novo parametro P_CENSO_LEITO_UNICO
INSERT INTO agh.agh_parametros( seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, 
alterado_em, alterado_por, vlr_data, vlr_numerico, 
vlr_texto, descricao, rotina_consistencia, version, vlr_data_padrao, 
vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado)
select (select nextval('agh.agh_psi_sq1')),'AIN','P_CENSO_LEITO_UNICO','S',now(),'AGHU',
null,null,null,20,
'N','Novo tipo de censo que unifica os movimentos e mostra apenas um leito além disso verifica leitos com movimento fora da janela', null, 0, null,
null,null,null,'N'
WHERE
NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_CENSO_LEITO_UNICO' AND sis_sigla = 'AIN');

--14/06/2016 #78938 - Remover coluna SEQ_PROC_HOSP
alter table agh.AEL_PROCED_SOLICITACAO_EXAMES drop column if exists seq_proc_hosp;
alter table agh.AEL_PROCED_SECUND_SOLICIT_EXAMES drop column if exists seq_proc_hosp;

--15/06/2016 #79013 - Remover constraint ael_hge_ck4
ALTER TABLE agh.ael_horario_grade_exames DROP CONSTRAINT ael_hge_ck4;

--20/06/2016 #79068 Acrescimo de dados na tabela de aplicações

INSERT INTO agh.agh_aplicacoes (codigo, nome, icone, metodo_chamada, ind_impressao, ind_aplic_disp, ambiente, url_aplicacao_aghu, version) VALUES ('AELP_GRAVA_RESULTADO', 'Grava resultados de exames para chamada do Delphi', NULL, 'P', 'N', 'S', 'A', NULL, 0);
INSERT INTO agh.agh_aplicacoes (codigo, nome, icone, metodo_chamada, ind_impressao, ind_aplic_disp, ambiente, url_aplicacao_aghu, version) VALUES ('IMPLAUDO', 'Visualização/Impressão de Resultados e Laudos de Exames', NULL, 'E', 'N', 'S', 'A', NULL, 0);


--04/07/2016 #80452 Evolução da arquitetura / Lentidão

CREATE OR REPLACE VIEW agh.v_afa_prcr_disp_mdtos AS 
 SELECT vpcr.atd_seq,
    vpcr.seq,
    vpcr.dt_referencia,
    vpcr.dthr_inicio,
    vpcr.dthr_fim,
    vpcr.atd_seq_local,
    atendimento.prontuario,
    atendimento.lto_lto_id,
    atendimento.qrt_numero,
    atendimento.unf_seq,
    atendimento.trp_seq,
    paciente.codigo,
    paciente.nome,
    sumarios.apa_atd_seq,
    sumarios.apa_seq,
    sumarios.seqp,
    count(dispmdtossolic.seq) AS countsolic,
    count(dispmdtosdisp.seq) AS countdisp,
    count(dispmdtosconf.seq) AS countconf,
    count(dispmdtosenv.seq) AS countenv,
    count(dispmdtostriado.seq) AS counttriado,
    count(dispmdtosocorr.seq) AS countocorr
   FROM agh.v_afa_prcr_disp vpcr
     LEFT JOIN agh.agh_atendimentos atendimento ON vpcr.atd_seq = atendimento.seq
     LEFT JOIN agh.aip_pacientes paciente ON atendimento.pac_codigo = paciente.codigo
     LEFT JOIN agh.mpm_alta_sumarios sumarios ON vpcr.seq = sumarios.apa_atd_seq AND sumarios.ind_concluido::text = 'S'::text
     LEFT JOIN agh.mpm_prescricao_medicas prescricao ON prescricao.atd_seq = vpcr.atd_seq AND prescricao.seq = vpcr.seq
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtossolic ON (dispmdtossolic.pme_atd_seq = prescricao.atd_seq AND dispmdtossolic.pme_seq = prescricao.seq OR dispmdtossolic.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtossolic.imo_pmo_pte_seq = prescricao.seq) AND dispmdtossolic.ind_situacao::text = 'S'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosdisp ON (dispmdtosdisp.pme_atd_seq = prescricao.atd_seq AND dispmdtosdisp.pme_seq = prescricao.seq OR dispmdtosdisp.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosdisp.imo_pmo_pte_seq = prescricao.seq) AND dispmdtosdisp.ind_situacao::text = 'D'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosconf ON (dispmdtosconf.pme_atd_seq = prescricao.atd_seq AND dispmdtosconf.pme_seq = prescricao.seq OR dispmdtosconf.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosconf.imo_pmo_pte_seq = prescricao.seq) AND dispmdtosconf.ind_situacao::text = 'C'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosenv ON (dispmdtosenv.pme_atd_seq = prescricao.atd_seq AND dispmdtosenv.pme_seq = prescricao.seq OR dispmdtosenv.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosenv.imo_pmo_pte_seq = prescricao.seq) AND dispmdtosenv.ind_situacao::text = 'E'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtostriado ON (dispmdtostriado.pme_atd_seq = prescricao.atd_seq AND dispmdtostriado.pme_seq = prescricao.seq OR dispmdtostriado.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtostriado.imo_pmo_pte_seq = prescricao.seq) AND dispmdtostriado.ind_situacao::text = 'T'::text AND dispmdtostriado.qtde_dispensada IS NOT NULL AND dispmdtostriado.qtde_dispensada > 0::double precision AND dispmdtostriado.qtde_estornada IS NULL
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosocorr ON (dispmdtosocorr.pme_atd_seq = prescricao.atd_seq AND dispmdtosocorr.pme_seq = prescricao.seq OR dispmdtosocorr.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosocorr.imo_pmo_pte_seq = prescricao.seq) AND dispmdtosocorr.ind_situacao::text = 'T'::text AND (dispmdtosocorr.qtde_estornada IS NOT NULL AND dispmdtosocorr.qtde_estornada > 0::double precision OR dispmdtosocorr.tod_seq IS NOT NULL)
  GROUP BY vpcr.atd_seq, vpcr.seq, vpcr.dt_referencia, vpcr.dthr_inicio, vpcr.dthr_fim, vpcr.atd_seq_local, atendimento.prontuario, atendimento.lto_lto_id, atendimento.qrt_numero, atendimento.unf_seq, atendimento.trp_seq, sumarios.apa_atd_seq, sumarios.apa_seq, sumarios.seqp, paciente.codigo, paciente.nome;

ALTER TABLE agh.v_afa_prcr_disp_mdtos
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_afa_prcr_disp_mdtos TO postgres;
GRANT ALL ON TABLE agh.v_afa_prcr_disp_mdtos TO acesso_completo;
GRANT SELECT ON TABLE agh.v_afa_prcr_disp_mdtos TO acesso_leitura;

--01/07/2016 #80105 Criação de parâmetro para verificar o endereço do paciente no ato da marcação de consultas.

INSERT INTO agh.agh_parametros( seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, alterado_em, alterado_por, vlr_data, vlr_numerico
, vlr_texto, descricao, rotina_consistencia, version, vlr_data_padrao, vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado) 

SELECT
	(select nextval('agh.agh_psi_sq1'))
	,'AAC' -- 'AGENDAMENTO E MARCACAO DE CONSULTAS'
	,'P_VERIFICA_ENDERECO_PACIENTE_MARCACAO_CONSULTA'
	,'S'
	,now()
	,'AGHU'
	,null
	,null
	,null
	,NULL
	,'N'
	,'Parâmetro para verificar o endereço do paciente no ato da marcação de consultas.'
	,null
	,0
	,null
	,null
	,null
	,null
	,'T'
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_VERIFICA_ENDERECO_PACIENTE_MARCACAO_CONSULTA' AND sis_sigla = 'AAC');

--06/07/2016 #78571 Problema de Desabilitar botões de funcionalidades que não estão disponíveis

INSERT INTO agh.agh_parametros( seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, alterado_em, alterado_por, vlr_data, vlr_numerico, 
vlr_texto, descricao, rotina_consistencia, version, vlr_data_padrao, vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado) 
SELECT 
	 (select nextval('agh.agh_psi_sq1'))
	,'MPM'
	,'P_DESABILITAR_BOTOES_EXAME_HEMOTERAPIA'
	,'S'
	,now()
	,'AGHU'
	,null
	,null
	,null
	,null
	,'N'
	,'Parametro criado para desabilitar os botões exame e hemoterapia na lista de pacientes no menu de prescrição médica. Valores válidos para o parametro: S = DESABILITADO E N = HABILITADO.'
	,null
	,0
	,null
	,null
	,null
	,null
	,'N'
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_DESABILITAR_BOTOES_EXAME_HEMOTERAPIA' AND sis_sigla = 'MPM');	

--08/07/2016 #81113 Aumentado o número máximo de caracteres no complemento Prescrição de Enfermagem

ALTER TABLE agh.mpm_prescricao_cuidados ALTER COLUMN descricao TYPE CHARACTER VARYING(240);	
ALTER TABLE agh.epe_prescricoes_cuidados ALTER COLUMN descricao TYPE CHARACTER VARYING(240);

--08/07/2016 #81064 Criação de Parametro  P_EDICAO_DESCRICAO_PHI	

INSERT INTO agh.agh_parametros( seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, alterado_em, alterado_por, vlr_data, vlr_numerico, 
vlr_texto, descricao, rotina_consistencia, version, vlr_data_padrao, vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado) 
SELECT 
	 (select nextval('agh.agh_psi_sq1'))
	,'FAT'
	,'P_EDICAO_DESCRICAO_PHI'
	,'S'
	,now()
	,'AGHU'
	,null
	,null
	,NULL
	,null
	,'N'
	,'Parâmetro que permite edição de PHI. Valor = S ou N'
	,null
	,0
	,null
	,null
	,NULL
	,null
	,'T'
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_EDICAO_DESCRICAO_PHI' AND sis_sigla = 'FAT');
	

--21/07/2016 #81369 Aumentou campo descrição na tabela mam_item_anamneses
--Visões vinculadas ao campo
drop view agh.v_mam_item_ana_ident;
drop view agh.v_mam_emg_item_evo_s;
drop view agh.v_mam_emg_item_evo_i;
drop view agh.v_mam_emg_item_evo_c;

ALTER TABLE agh.mam_item_anamneses ALTER COLUMN descricao TYPE text;
ALTER TABLE agh.mam_item_evolucoes ALTER COLUMN descricao TYPE text;

CREATE OR REPLACE VIEW agh.v_mam_item_ana_ident AS
SELECT mam_item_anamneses.ana_seq,
mam_item_anamneses.tin_seq,
mam_item_anamneses.descricao
FROM agh.mam_item_anamneses;

ALTER TABLE agh.v_mam_item_ana_ident
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mam_item_ana_ident TO postgres;
GRANT ALL ON TABLE agh.v_mam_item_ana_ident TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_item_ana_ident TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_mam_emg_item_evo_s AS
SELECT mam_item_evolucoes.evo_seq,
mam_item_evolucoes.tie_seq,
mam_item_evolucoes.descricao,
1 AS chave
FROM agh.mam_item_evolucoes;

ALTER TABLE agh.v_mam_emg_item_evo_s
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_s TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_s TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_emg_item_evo_s TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_mam_emg_item_evo_i AS
SELECT mam_item_evolucoes.evo_seq,
mam_item_evolucoes.tie_seq,
mam_item_evolucoes.descricao,
1 AS chave
FROM agh.mam_item_evolucoes;

ALTER TABLE agh.v_mam_emg_item_evo_i
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_i TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_i TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_emg_item_evo_i TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_mam_emg_item_evo_c AS
SELECT mam_item_evolucoes.evo_seq,
mam_item_evolucoes.tie_seq,
mam_item_evolucoes.descricao,
1 AS chave
FROM agh.mam_item_evolucoes;

ALTER TABLE agh.v_mam_emg_item_evo_c
OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_c TO postgres;
GRANT ALL ON TABLE agh.v_mam_emg_item_evo_c TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_emg_item_evo_c TO acesso_leitura;	

-- 02/08/2016 Um parametro foi criado com os valores errados. O script a seguir é uma correção 

UPDATE 
	agh.agh_parametros
SET 
	vlr_numerico = NULL
	,vlr_texto = 'N'
	,tipo_dado = 'T'
WHERE
	nome = 'P_VERIFICA_ENDERECO_PACIENTE_MARCACAO_CONSULTA' 
	AND 
	sis_sigla = 'AAC';

-- 02/08/2016 #81105 Modificação na visão criada para integração do HUSM

DROP VIEW agh.v_integracao;
CREATE OR REPLACE VIEW agh.v_integracao AS 
 SELECT atd.pac_codigo AS pac_id,
    pac.nome AS pac_nome,
    pac.dt_nascimento AS pac_nascimento,
    pac.sexo AS pac_sexo,
    pac.nome_mae AS pac_mae,
    pac.fone_residencial AS pac_fone,
    pac.cpf AS pac_cpf,
    pac.rg AS pac_rg,
    pac.nro_cartao_saude AS pac_cns,
        CASE
            WHEN pac.cor::text = 'B'::text THEN 'Branca'::text
            WHEN pac.cor::text = 'P'::text THEN 'Preta'::text
            WHEN pac.cor::text = 'M'::text THEN 'Parda'::text
            WHEN pac.cor::text = 'A'::text THEN 'Amarela'::text
            WHEN pac.cor::text = 'I'::text THEN 'Indigena'::text
            ELSE 'Sem declaracao'::text
        END AS pac_cor,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN concat(logr_pac.nome, ', ', end_pac.nro_logradouro)
            ELSE concat(end_pac.logradouro, ', ', end_pac.nro_logradouro)
        END AS pac_endereco,
    end_pac.compl_logradouro AS pac_end_compl,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN bai_pac.descricao
            ELSE end_pac.bairro
        END AS pac_end_bairro,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN end_pac.bcl_clo_cep
            ELSE end_pac.cep
        END AS pac_end_cep,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN cid_pac.nome
            WHEN end_pac.cdd_codigo IS NOT NULL THEN end_cid_pac.nome
            ELSE end_pac.cidade
        END AS pac_end_cidade,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN cid_pac.uf_sigla
            WHEN end_pac.cdd_codigo IS NOT NULL THEN end_cid_pac.uf_sigla
            ELSE end_pac.uf_sigla
        END AS pac_end_uf,
    cnv.codigo AS exame_conv_id,
    cnv.descricao AS exame_conv_descr,
    solic.unf_seq AS unid_requisitante_id,
    unf_solic.descricao AS unid_requisitante_descr,
    unf_solic.ind_unid_internacao AS und_req_interna,
    unf_exec.unf_seq AS unid_entrega_id,
    unf.descricao AS unid_entrega_descr,
    atd.lto_lto_id AS unid_leito_id,
    qualif.nro_reg_conselho AS solic_reg,
    pessoa.uf_sigla AS solic_uf,
    conselho.sigla AS solic_conselho,
    pessoa.nome AS solic_nome,
    pessoa.dt_nascimento AS solic_nascimento,
    pessoa.sexo AS solic_sexo,
    serv.email AS solic_email,
    item_solic.ufe_ema_exa_sigla AS exame_id,
    exa.descricao AS exame_descr,
    item_solic.ufe_ema_man_seq AS exame_mat_id,
    mat_an.descricao AS exame_descr_mat_analise,
    ex_mat_an.ind_dependente AS exame_dependente,
    tip_am_exa.nro_amostras AS exame_qtde_amostras,
    item_hr.hed_dthr_agenda AS exame_datahora,
    solic.informacoes_clinicas AS exame_info_adic,
    solic.atd_seq AS atendimento_id,
    solic.seq AS solic_exame,
    item_solic.seqp AS solic_seq_item,
    solic.criado_em AS solic_criada_em,
    sit_solic.sit_codigo AS solic_status,
    sit_solic_ex.descricao AS solic_status_descr,
    sit_solic.criado_em AS solic_sit_exame_datahora,
    amo.man_seq AS tipo_am_exa,
    -- >> EXCLUIR
    --amo.seqp AS seq_amostra, 
    -- <<
    item_solic.desc_material_analise,
    -- INCLUIR >>
    atd.prontuario AS pac_same ,
    int_col.descricao AS intervalo_coleta,
    case when int_col.nro_amostras is null then 1 else int_col.nro_amostras end
    -- <<
   FROM agh.ael_solicitacao_exames solic
     JOIN agh.agh_atendimentos atd ON atd.seq = solic.atd_seq
     JOIN agh.ael_item_solicitacao_exames item_solic ON item_solic.soe_seq = solic.seq
     JOIN agh.ael_exames exa ON item_solic.ufe_ema_exa_sigla::text = exa.sigla::text
     JOIN agh.ael_unf_executa_exames unf_exec ON unf_exec.ema_exa_sigla::text = exa.sigla::text AND item_solic.ufe_ema_exa_sigla::text = unf_exec.ema_exa_sigla::text AND item_solic.ufe_ema_man_seq = unf_exec.ema_man_seq AND item_solic.ufe_unf_seq = unf_exec.unf_seq
     JOIN agh.ael_exames_material_analise ex_mat_an ON ex_mat_an.exa_sigla::text = exa.sigla::text AND ex_mat_an.man_seq = item_solic.ufe_ema_man_seq
     JOIN agh.ael_materiais_analises mat_an ON mat_an.seq = ex_mat_an.man_seq
     JOIN agh.agh_unidades_funcionais unf ON unf.seq = unf_exec.unf_seq
     JOIN agh.agh_unidades_funcionais unf_solic ON unf_solic.seq = solic.unf_seq
     LEFT JOIN agh.ael_tipos_amostra_exames tip_am_exa ON mat_an.seq = tip_am_exa.man_seq AND ex_mat_an.exa_sigla::text = tip_am_exa.ema_exa_sigla::text AND ex_mat_an.man_seq = tip_am_exa.ema_man_seq
     LEFT JOIN agh.ael_item_horario_agendados item_hr ON item_hr.ise_soe_seq = item_solic.soe_seq AND item_hr.ise_seqp = item_solic.seqp
     JOIN agh.rap_servidores serv ON solic.ser_matricula = serv.matricula
     JOIN agh.rap_pessoas_fisicas pessoa ON pessoa.codigo = serv.pes_codigo
     LEFT JOIN agh.rap_qualificacoes qualif ON qualif.pes_codigo = pessoa.codigo AND qualif.sequencia = (( SELECT max(q.sequencia) AS max
           FROM agh.rap_qualificacoes q
          WHERE q.pes_codigo = qualif.pes_codigo))
     LEFT JOIN agh.rap_tipos_qualificacao tipo_qualif ON qualif.tql_codigo = tipo_qualif.codigo
     LEFT JOIN agh.rap_conselhos_profissionais conselho ON tipo_qualif.cpr_codigo = conselho.codigo
     JOIN agh.fat_conv_saude_planos cnv_planos ON cnv_planos.seq = solic.csp_seq AND cnv_planos.cnv_codigo = solic.csp_cnv_codigo
     JOIN agh.fat_convenios_saude cnv ON cnv.codigo = cnv_planos.cnv_codigo
     JOIN agh.aip_pacientes pac ON atd.pac_codigo = pac.codigo
     JOIN agh.aip_enderecos_pacientes end_pac ON end_pac.pac_codigo = pac.codigo
     LEFT JOIN agh.aip_cidades end_cid_pac ON end_cid_pac.codigo = end_pac.cdd_codigo
     LEFT JOIN agh.aip_bairros bai_pac ON end_pac.bcl_bai_codigo = bai_pac.codigo
     LEFT JOIN agh.aip_logradouros logr_pac ON end_pac.bcl_clo_lgr_codigo = logr_pac.codigo
     LEFT JOIN agh.aip_cidades cid_pac ON logr_pac.cdd_codigo = cid_pac.codigo
     JOIN agh.ael_sit_item_solicitacoes sit_solic_ex ON sit_solic_ex.codigo::text = item_solic.sit_codigo::text
     JOIN agh.ael_extrato_item_solics sit_solic ON item_solic.soe_seq = sit_solic.ise_soe_seq AND item_solic.seqp = sit_solic.ise_seqp AND sit_solic.seqp = (( SELECT max(es1.seqp) AS max
           FROM agh.ael_extrato_item_solics es1
          WHERE sit_solic.ise_soe_seq = es1.ise_soe_seq AND sit_solic.ise_seqp = es1.ise_seqp))
     -- >> ALTERAR
     LEFT JOIN agh.ael_amostra_item_exames am_item_ex ON item_solic.soe_seq = am_item_ex.ise_soe_seq AND item_solic.seqp = am_item_ex.ise_seqp and am_item_ex.amo_seqp = 1
     -- <<
     LEFT JOIN agh.ael_amostras amo ON am_item_ex.amo_soe_seq = amo.soe_seq AND am_item_ex.amo_seqp = amo.seqp
     -- >> INCLUIR
     LEFT JOIN (select vic.seq, vic.descricao, vic.tipo_substancia, vic.ema_exa_sigla, vic.ema_man_seq, COUNT(vic.SEQ) nro_amostras
                from agh.v_ael_intervalo_coletas vic 
                GROUP BY vic.seq, vic.descricao, vic.tipo_substancia, vic.ema_exa_sigla, vic.ema_man_seq) int_col  on item_solic.ico_seq = int_col.seq
     -- <<
     -- >> EXCLUIR
     -- LEFT JOIN agh.ael_materiais_analises mat_an_am ON mat_an_am.seq = amo.man_seq 
     -- <<
ORDER BY solic.seq DESC, item_solic.seqp;

ALTER TABLE agh.v_integracao
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_integracao TO postgres;
--GRANT SELECT ON TABLE agh.v_integracao TO ugen_integra_exames;	

--08/08/2016 #82724 Criação de view e adição de paramêtros.


INSERT INTO agh.agh_parametros(seq, sis_sigla, nome, mantem_historico, criado_em, criado_por, alterado_em, alterado_por, vlr_data, vlr_numerico, vlr_texto,
descricao, rotina_consistencia, version, vlr_data_padrao, vlr_numerico_padrao, vlr_texto_padrao, exemplo_uso, tipo_dado)
VALUES ((select nextval('agh.agh_psi_sq1')), 'AGH', 'PME_LISTA_PACIENTES_USAR_BUSCA_OTIMIZADA', 'N', now(), 'AGHU', now(), 'AGHU', null, NULL, 'N',
'Parametro para indicar se a tela de lista de pacientes da prescricao medica busca pelo SQL nativo otimizado', null, 0, null, null, 'S', null, 'T');

-- View: agh.v_mpm_lista_pac_internados
DROP VIEW agh.v_mpm_lista_pac_internados;
CREATE OR REPLACE VIEW agh.v_mpm_lista_pac_internados AS 
 SELECT atd.seq AS atd_seq,
    atd.prontuario,
    atd.pac_codigo,
    pac.nome,
    pac.nome_social,
    atd.dthr_inicio AS data_inicio_atendimento,
    atd.dthr_fim AS data_fim_atendimento,
    pac.dt_nascimento AS data_nascimento,
    atd.ser_matricula AS atd_ser_matricula,
    atd.ser_vin_codigo AS atd_ser_vin_codigo,
    atd.ser_matricula,
    atd.ser_vin_codigo,
    atd.ind_sit_sumario_alta,
    atd.origem,
    atd.ESP_SEQ,
    atd.UNF_SEQ,
    atd.ind_pac_cpa,
    atd.IND_PAC_ATENDIMENTO,
    atd.LTO_LTO_ID,
    atd.QRT_NUMERO,
    (case when rapf.nome_usual is null or rapf.nome_usual = '' then rapf.nome else rapf.nome_usual end) as nome_responsavel
    ,   CASE
            WHEN atd.origem::text = 'I'::text AND (( SELECT max(cpa.criado_em) AS max
               FROM agh.mpm_control_prev_altas cpa
              WHERE cpa.atd_seq = atd.seq AND cpa.resposta::text = 'S'::text AND cpa.dt_fim IS NOT NULL AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) >= 0::double precision AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) <= 2::double precision)) IS NOT NULL THEN 'true'::text
            WHEN atd.origem::text = 'N'::text AND (( SELECT max(cpa.criado_em) AS max
               FROM agh.mpm_control_prev_altas cpa
                 JOIN agh.agh_atendimentos atd_mae ON atd_mae.seq = cpa.atd_seq AND atd_mae.origem::text = 'I'::text
              WHERE cpa.atd_seq = atd.atd_seq_mae AND cpa.resposta::text = 'S'::text AND cpa.dt_fim IS NOT NULL AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) >= 0::double precision AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) <= 2::double precision)) IS NOT NULL THEN 'true'::text
            ELSE 'false'::text
        END AS possui_plano_altas,
        CASE
            WHEN atd.lto_lto_id IS NOT NULL THEN concat('L:', atd.lto_lto_id)
            WHEN atd.qrt_numero IS NOT NULL THEN concat('Q:', atd.qrt_numero)
            ELSE ( SELECT (((('U:'::text || unf_1.andar::text) || ' '::text) || unf_1.ind_ala::text) || ' - '::text) || unf_1.descricao::text
               FROM agh.agh_unidades_funcionais unf_1
              WHERE unf_1.seq = atd.unf_seq)
        END AS local,
        CASE
            WHEN
            CASE
                WHEN atd.ind_pac_atendimento::text = 'S'::text AND cuf.unf_seq IS NOT NULL THEN 'false'::text
                ELSE 'true'::text
            END = 'true'::text THEN ''::text
            WHEN NOT (( SELECT count(*) AS count
               FROM agh.mpm_prescricao_medicas pm
              WHERE pm.atd_seq = atd.seq AND pm.ser_matricula_valida IS NOT NULL AND pm.ser_vin_codigo_valida IS NOT NULL AND pm.dthr_fim > now())) > 0 THEN 'PRESCRICAO_NAO_REALIZADA'::text
            WHEN NOT (( SELECT count(*) AS count
               FROM agh.mpm_prescricao_medicas pm
              WHERE pm.atd_seq = atd.seq AND pm.ser_matricula_valida IS NOT NULL AND pm.ser_vin_codigo_valida IS NOT NULL AND pm.dthr_inicio > now() AND to_char(unf.hrio_validade_pme, 'HH24:mi'::text) = to_char(pm.dthr_inicio, 'HH24:mi'::text))) > 0 THEN 'PRESCRICAO_VENCE_NO_DIA'::text
            ELSE 'PRESCRICAO_VENCE_NO_DIA_SEGUINTE'::text
        END AS status_prescricao,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text = 'E'::text THEN 'SUMARIO_ALTA_NAO_ENTREGUE_SAMIS'::text
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_ALTA_NAO_REALIZADO'::text
            ELSE ''::text
        END AS status_sumario_alta,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.ael_item_solicitacao_exames ise
                 JOIN agh.ael_solicitacao_exames soe ON ise.soe_seq = soe.seq
              WHERE soe.atd_seq = atd.seq AND ise.sit_codigo::text = 'LI'::text AND NOT (EXISTS ( SELECT its.ise_seqp,
                        its.ise_soe_seq
                       FROM agh.ael_item_solic_consultados its
                         JOIN agh.ael_item_solicitacao_exames sub_ise ON its.ise_seqp = sub_ise.seqp AND its.ise_soe_seq = sub_ise.soe_seq
                         JOIN agh.ael_solicitacao_exames sub_soe ON sub_ise.soe_seq = sub_soe.seq
                      WHERE its.ise_soe_seq = ise.soe_seq AND its.ise_seqp = ise.seqp AND sub_soe.atd_seq = atd.seq AND sub_ise.sit_codigo::text = 'LI'::text)) AND NOT (EXISTS ( SELECT iri.ise_seqp,
                        iri.ise_soe_seq
                       FROM agh.ael_itens_resul_impressao iri
                      WHERE iri.ise_soe_seq = ise.soe_seq AND iri.ise_seqp = ise.seqp)))) > 0 THEN 'RESULTADOS_NAO_VISUALIZADOS'::text
            WHEN (( SELECT count(*) AS count
               FROM agh.ael_item_solicitacao_exames ise
                 JOIN agh.ael_solicitacao_exames soe ON ise.soe_seq = soe.seq
              WHERE soe.atd_seq = atd.seq AND ise.sit_codigo::text = 'LI'::text AND NOT (EXISTS ( SELECT its.ise_seqp,
                        its.ise_soe_seq
                       FROM agh.ael_item_solic_consultados its
                         JOIN agh.ael_item_solicitacao_exames sub_ise ON its.ise_seqp = sub_ise.seqp AND its.ise_soe_seq = sub_ise.soe_seq
                         JOIN agh.ael_solicitacao_exames sub_soe ON sub_ise.soe_seq = sub_soe.seq
                      WHERE its.ise_soe_seq = ise.soe_seq AND its.ise_seqp = ise.seqp AND sub_soe.atd_seq = atd.seq AND sub_ise.sit_codigo::text = 'LI'::text AND its.ser_matricula = atd.ser_matricula AND its.ser_vin_codigo = atd.ser_vin_codigo)) AND NOT (EXISTS ( SELECT iri.ise_seqp,
                        iri.ise_soe_seq
                       FROM agh.ael_itens_resul_impressao iri
                      WHERE iri.ise_soe_seq = ise.soe_seq AND iri.ise_seqp = ise.seqp)))) > 0 THEN 'RESULTADOS_VISUALIZADOS_OUTRO_MEDICO'::text
            ELSE ''::text
        END AS status_exames_nao_vistos,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_laudos lau
              WHERE lau.atd_seq = atd.seq AND lau.tuo_seq IS NOT NULL AND lau.justificativa IS NULL)) > 0 THEN 'PENDENCIA_LAUDO_UTI'::text
            ELSE ''::text
        END AS status_pendencia_documento,
        CASE
            WHEN (( SELECT count(projetos.pac_codigo) AS count
               FROM agh.ael_projeto_pacientes projetos
              WHERE projetos.pac_codigo = atd.pac_codigo AND (projetos.dt_fim IS NULL OR projetos.dt_fim >= now()) AND (projetos.jex_seq IS NULL OR (EXISTS ( SELECT jex_.seq AS y0_
                       FROM agh.ael_justificativa_exclusoes jex_
                      WHERE jex_.seq = projetos.jex_seq AND jex_.ind_mostra_telas::text = 'S'::text))) AND (EXISTS ( SELECT pjq_.seq AS y0_
                       FROM agh.ael_projeto_pesquisas pjq_
                      WHERE pjq_.seq = projetos.pjq_seq AND (pjq_.dt_fim IS NULL OR pjq_.dt_fim >= now()))))) > 0 THEN 'PACIENTE_PESQUISA'::text
            ELSE ''::text
        END AS status_paciente_pesquisa,
        CASE
            WHEN atd.origem::text = 'I'::text OR atd.origem::text = 'N'::text THEN
            CASE
                WHEN (( SELECT count(*) AS count
                   FROM agh.mam_evolucoes evo
                  WHERE evo.atd_seq = atd.seq AND date_part('day'::text, evo.dthr_valida::timestamp with time zone - now()) = 0::double precision AND evo.dthr_valida_mvto IS NULL)) > 0 THEN
                CASE
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_MEDICO'::text)) THEN 'EVOLUCAO'::text
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_OUTROS'::text)) THEN 'EVOLUCAO'::text
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_ENF'::text)) THEN
                    CASE
                        WHEN (( SELECT count(*) AS count
                           FROM agh.mam_item_evolucoes iev
                          WHERE iev.evo_seq = (( SELECT evo.seq
                                   FROM agh.mam_evolucoes evo
                                  WHERE evo.atd_seq = atd.seq AND date_part('day'::text, evo.dthr_valida::timestamp with time zone - now()) = 0::double precision AND evo.dthr_valida_mvto IS NULL
                                 LIMIT 1)) AND (iev.tie_seq IN ( SELECT mam_tipo_item_evolucoes.seq
                                   FROM agh.mam_tipo_item_evolucoes
                                  WHERE mam_tipo_item_evolucoes.sigla::text = 'C'::text)))) > 0 THEN 'EVOLUCAO'::text
                        ELSE ''::text
                    END
                    ELSE ''::text
                END
                ELSE ''::text
            END
            ELSE ''::text
        END AS status_evolucao,
        CASE
            WHEN (( SELECT count(docs.pac_codigo) AS count
               FROM agh.v_agh_versoes_documentos docs
              WHERE docs.dov_situacao::text = 'P'::text AND docs.pac_codigo = atd.pac_codigo)) > 0 THEN 'PENDENTE'::text
            ELSE ''::text
        END AS status_certificacao_digital,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_sumario_altas sa
              WHERE sa.atd_seq = atd.seq AND sa.mam_seq IS NOT NULL)) > 0 THEN 'true'::text
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_alta_sumarios asu
              WHERE asu.apa_atd_seq = atd.seq AND asu.ind_concluido::text = 'S'::text)) > 0 THEN 'true'::text
            WHEN ((( SELECT agh_parametros.vlr_texto
               FROM agh.agh_parametros
              WHERE agh_parametros.nome::text = 'P_BLOQUEIA_PAC_EMERG'::text))::text) <> 'S'::text AND (( SELECT count(*) AS count
               FROM agh.agh_caract_unid_funcionais cuf2
              WHERE cuf2.unf_seq = atd.unf_seq AND cuf2.caracteristica::text = 'Atend emerg terreo'::text)) > 0 THEN 'true'::text
            ELSE 'false'::text
        END AS disable_button_alta_obito,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'S'::text AND cuf.unf_seq IS NOT NULL THEN 'false'::text
            ELSE 'true'::text
        END AS disable_button_prescrever,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_ALTA'::text
            ELSE 'ALTA'::text
        END AS label_alta,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_OBITO'::text
            ELSE 'OBITO'::text
        END AS label_obito,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mci_notificacao_gmr gmr
              WHERE gmr.pac_codigo = atd.pac_codigo AND gmr.ind_notificacao_ativa::text = 'S'::text)) > 0 THEN 'true'::text
            ELSE 'false'::text
        END AS ind_gmr,
	case 
	   when (select count(*) from agh.AGH_CARACT_UNID_FUNCIONAIS car
		where car.caracteristica = 'Anamnese/Evolução Eletrônica'
		and car.unf_seq = atd.unf_seq) > 0 then 'true'::text
		else 'false'::text
	end as TEM_UNF_CARACT_ANAMNESE_EVOLUCAO,
	case 
	   when (select count(*) 
		from agh.MPM_ANAMNESES ana
		   inner join agh.AGH_ATENDIMENTOS atd1 on atd1.seq = ana.atd_seq
		      inner join agh.RAP_SERVIDORES rap on rap.matricula = atd1.ser_matricula and rap.vin_codigo = atd1.ser_vin_codigo
		         inner join agh.RAP_PESSOAS_FISICAS pes on pes.codigo = rap.pes_codigo
		      inner join agh.AGH_UNIDADES_FUNCIONAIS unf on unf.seq = atd1.UNF_SEQ
		         inner join agh.AGH_CARACT_UNID_FUNCIONAIS car on car.unf_seq = unf.seq
		where ana.atd_seq = atd.seq) > 0 
	    then (
		select distinct case ana.IND_PENDENTE
			when 'R' then 'ANAMNESE_NAO_REALIZADA'
			when 'P' then 'ANAMNESE_PENDENTE'
			when 'V' then
			    case 
			       when (select count(*)
					from agh.MPM_EVOLUCOES evo
					inner join agh.MPM_ANAMNESES ana on ana.seq = evo.ana_seq
					where evo.ANA_SEQ = ana.seq
					and to_char(evo.DTHR_REFERENCIA, 'YYYYMMDD') = to_char(now(), 'YYYYMMDD') 
					and evo.IND_PENDENTE <> 'R') = 0 then 'EVOLUCAO_DO_DIA_NAO_REALIZADA'
			       when (select count(*)
					from agh.MPM_EVOLUCOES evo
					inner join agh.MPM_ANAMNESES ana on ana.seq = evo.ana_seq
					where evo.ANA_SEQ = ana.seq
					and to_char(evo.DTHR_REFERENCIA, 'YYYYMMDD') = to_char(now(), 'YYYYMMDD') 
					and evo.IND_PENDENTE = 'P') > 0 then 'EVOLUCAO_DO_DIA_PENDENTE'
			       when (select count(*)
					from agh.MPM_EVOLUCOES evo
					inner join agh.MPM_ANAMNESES ana on ana.seq = evo.ana_seq
					where evo.ANA_SEQ = ana.seq
					and evo.DTHR_REFERENCIA > date_trunc('day', now()) 
					and evo.IND_PENDENTE = 'V') = 0 then 'EVOLUCAO_VENCE_NO_DIA_SEGUINTE'
				else null
				end
			else null
			end 
		from agh.MPM_ANAMNESES ana
		   inner join agh.AGH_ATENDIMENTOS atd1 on atd1.seq = ana.atd_seq
		      inner join agh.RAP_SERVIDORES rap on rap.matricula = atd1.ser_matricula and rap.vin_codigo = atd1.ser_vin_codigo
			 inner join agh.RAP_PESSOAS_FISICAS pes on pes.codigo = rap.pes_codigo
		      inner join agh.AGH_UNIDADES_FUNCIONAIS unf1 on unf1.seq = atd1.UNF_SEQ
			 inner join agh.AGH_CARACT_UNID_FUNCIONAIS car on car.unf_seq = unf1.seq
		where ana.atd_seq = atd.seq)::text
	    else 'ANAMNESE_NAO_REALIZADA'
	end as STATUS_ANAMNESE_EVOLUCAO
   FROM agh.agh_atendimentos atd
     LEFT JOIN agh.agh_caract_unid_funcionais cuf ON cuf.unf_seq = atd.unf_seq AND cuf.caracteristica::text = 'Pme Informatizada'::text
     LEFT JOIN agh.agh_unidades_funcionais unf ON atd.unf_seq = unf.seq
     LEFT JOIN agh.rap_servidores raps ON raps.matricula = atd.ser_matricula AND raps.vin_codigo = atd.ser_vin_codigo
     LEFT JOIN agh.rap_pessoas_fisicas rapf ON raps.pes_codigo = rapf.codigo
     LEFT JOIN agh.aip_pacientes pac ON pac.codigo = atd.pac_codigo;

ALTER TABLE agh.v_mpm_lista_pac_internados
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mpm_lista_pac_internados TO postgres;
GRANT ALL ON TABLE agh.v_mpm_lista_pac_internados TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mpm_lista_pac_internados TO acesso_leitura;

--17/08/2016 #83265 Alterara Paramêtro PME_LISTA_PACIENTES_USAR_BUSCA_OTIMIZADA

update agh.agh_parametros set vlr_texto = 'S' where nome = 'PME_LISTA_PACIENTES_USAR_BUSCA_OTIMIZADA';

--17/08/2016 #83255 Ajuste de View Internação 

DROP VIEW agh.v_ain_internacao;

ALTER TABLE agh.ain_observacoes_censo ALTER COLUMN descricao TYPE text;

CREATE OR REPLACE VIEW agh.v_ain_internacao AS 
 SELECT inte.seq AS nro_internacao,
    inte.pac_codigo AS registro,
    qua.nro_reg_conselho AS crm,
    esp.nome_especialidade AS especialidade,
        CASE
            WHEN inte.unf_seq IS NOT NULL THEN inte.unf_seq
            ELSE unf2.seq
        END AS unidade_funcional,
    inte.iph_seq AS codigo_procedimento,
    lei.leito,
    lei.qrt_numero,
    cid.codigo AS cid_primario,
    date(inte.dthr_internacao) AS data,
    to_char(inte.dthr_internacao, 'HH:MI:SS'::text) AS hora,
    obs.descricao AS "observação",
    res.nome AS nome_responsavel,
    res.cpf AS documento_responsavel,
    res.ddd_fone::bigint | res.fone AS telefone,
    acp.nome AS acompanhante,
    oev.descricao AS origem,
    cids.codigo AS cid_secundario,
    inte.oev_seq,
    serl.usuario AS login,
    inte.dt_prev_alta,
    inte.doc_obito,
    inte.ind_paciente_internado,
    iph.cod_tabela AS procedimento_unificado
   FROM agh.ain_internacoes inte
     JOIN agh.rap_servidores ser ON inte.ser_matricula_professor = ser.matricula AND inte.ser_vin_codigo_professor = ser.vin_codigo
     JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = ser.pes_codigo
     LEFT JOIN agh.rap_qualificacoes qua ON pes.codigo = qua.pes_codigo
     JOIN agh.agh_especialidades esp ON esp.seq = inte.esp_seq
     LEFT JOIN agh.agh_unidades_funcionais unf ON inte.unf_seq = unf.seq
     LEFT JOIN agh.ain_leitos lei ON inte.lto_lto_id::text = lei.lto_id::text
     LEFT JOIN agh.agh_unidades_funcionais unf2 ON lei.unf_seq = unf2.seq
     LEFT JOIN agh.ain_cids_internacao int_cid ON inte.seq = int_cid.int_seq AND int_cid.ind_prioridade_cid::text = 'P'::text
     LEFT JOIN agh.ain_responsaveis_paciente res ON inte.seq = res.int_seq AND res.tipo_responsabilidade::text = 'C'::text
     LEFT JOIN agh.ain_observacoes_censo obs ON inte.seq = obs.seq
     LEFT JOIN agh.ain_acompanhantes_internacao acp ON inte.seq = acp.int_seq
     JOIN agh.agh_origem_eventos oev ON inte.oev_seq = oev.seq
     LEFT JOIN agh.ain_cids_internacao int_cids ON inte.seq = int_cids.int_seq AND int_cids.ind_prioridade_cid::text = 'S'::text
     JOIN agh.rap_servidores serl ON inte.ser_matricula_digita = serl.matricula AND inte.ser_vin_codigo_digita = serl.vin_codigo
     JOIN agh.fat_itens_proced_hospitalar iph ON inte.iph_seq = iph.seq
     JOIN agh.agh_cids cid ON int_cid.cid_seq = cid.seq
     LEFT JOIN agh.agh_cids cids ON int_cids.cid_seq = cids.seq;

ALTER TABLE agh.v_ain_internacao
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_internacao TO acesso_leitura;


--18/08/2016 #83376 Insere Paramêtro P_AGHU_NOME_ARQUIVO_FINANCIAMENTO

INSERT INTO AGH.AGH_PARAMETROS (ALTERADO_EM, ALTERADO_POR, CRIADO_EM, CRIADO_POR, DESCRICAO, MANTEM_HISTORICO, NOME, SIS_SIGLA, TIPO_DADO, VERSION, VLR_TEXTO, VLR_TEXTO_PADRAO, SEQ) 
	VALUES (now(), 'AGHU', now(), 'AGHU', 'Parâmetro que indica o nome da tabela do sigtap, referente ao tb_financiamento', 'S', 'P_AGHU_NOME_ARQUIVO_FINANCIAMENTO', 'AGH', 'N', 0, 'tb_financiamento.txt', 'tb_financiamento.txt', nextval('agh.agh_psi_sq1'));

-- 19/08/2016 #83461 Modificação da descrição do parametro, por Rafael Teixeira. 

update agh.agh_parametros
set descricao = descricao || '. PARA COBRAR A OBRIGATORIEDADE DO CAMPO N DA AUTORIZAÇÃO É NECESSÁRIO COLOCAR O VALOR DO TEXTO PARA ''S'' E ''N'' PARA NÃO TER QUE VALIDAR.'
,vlr_texto = 'N'
where seq = 7082; 	

-- 23/08/2016 #80556 Modificação no tamanho do campo origem internação;

DROP VIEW agh.v_ain_internacao;
DROP VIEW agh.v_ain_internacao_paciente;
			--altera campo
ALTER TABLE agh.agh_origem_eventos  ALTER COLUMN descricao TYPE CHARACTER VARYING(80);
			--cria novamente as visões vinculadas
CREATE OR REPLACE VIEW agh.v_ain_internacao AS 
 SELECT inte.seq AS nro_internacao,
    inte.pac_codigo AS registro,
    qua.nro_reg_conselho AS crm,
    esp.nome_especialidade AS especialidade,
        CASE
            WHEN inte.unf_seq IS NOT NULL THEN inte.unf_seq
            ELSE unf2.seq
        END AS unidade_funcional,
    inte.iph_seq AS codigo_procedimento,
    lei.leito,
    lei.qrt_numero,
    cid.codigo AS cid_primario,
    date(inte.dthr_internacao) AS data,
    to_char(inte.dthr_internacao, 'HH:MI:SS'::text) AS hora,
    obs.descricao AS "observação",
    res.nome AS nome_responsavel,
    res.cpf AS documento_responsavel,
    res.ddd_fone::bigint | res.fone AS telefone,
    acp.nome AS acompanhante,
    oev.descricao AS origem,
    cids.codigo AS cid_secundario,
    inte.oev_seq,
    serl.usuario AS login,
    inte.dt_prev_alta,
    inte.doc_obito,
    inte.ind_paciente_internado,
    iph.cod_tabela AS procedimento_unificado
   FROM agh.ain_internacoes inte
     JOIN agh.rap_servidores ser ON inte.ser_matricula_professor = ser.matricula AND inte.ser_vin_codigo_professor = ser.vin_codigo
     JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = ser.pes_codigo
     LEFT JOIN agh.rap_qualificacoes qua ON pes.codigo = qua.pes_codigo
     JOIN agh.agh_especialidades esp ON esp.seq = inte.esp_seq
     LEFT JOIN agh.agh_unidades_funcionais unf ON inte.unf_seq = unf.seq
     LEFT JOIN agh.ain_leitos lei ON inte.lto_lto_id::text = lei.lto_id::text
     LEFT JOIN agh.agh_unidades_funcionais unf2 ON lei.unf_seq = unf2.seq
     LEFT JOIN agh.ain_cids_internacao int_cid ON inte.seq = int_cid.int_seq AND int_cid.ind_prioridade_cid::text = 'P'::text
     LEFT JOIN agh.ain_responsaveis_paciente res ON inte.seq = res.int_seq AND res.tipo_responsabilidade::text = 'C'::text
     LEFT JOIN agh.ain_observacoes_censo obs ON inte.seq = obs.seq
     LEFT JOIN agh.ain_acompanhantes_internacao acp ON inte.seq = acp.int_seq
     JOIN agh.agh_origem_eventos oev ON inte.oev_seq = oev.seq
     LEFT JOIN agh.ain_cids_internacao int_cids ON inte.seq = int_cids.int_seq AND int_cids.ind_prioridade_cid::text = 'S'::text
     JOIN agh.rap_servidores serl ON inte.ser_matricula_digita = serl.matricula AND inte.ser_vin_codigo_digita = serl.vin_codigo
     JOIN agh.fat_itens_proced_hospitalar iph ON inte.iph_seq = iph.seq
     JOIN agh.agh_cids cid ON int_cid.cid_seq = cid.seq
     LEFT JOIN agh.agh_cids cids ON int_cids.cid_seq = cids.seq;

ALTER TABLE agh.v_ain_internacao
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_internacao TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_ain_internacao_paciente AS 
 SELECT inte.seq,
    inte.pac_codigo AS registro,
    pac.nome,
    pac.prontuario,
    pac.nro_cartao_saude AS cartao_sus,
    nac.descricao AS nacionalidade,
    pac.naturalidade,
    pac.grau_instrucao AS cod_escolaridade,
    pac.cor AS cod_cor,
    pac.rg,
    pac.cpf,
    pac.numero_pis,
    pac.reg_nascimento,
    pac.dt_nascimento,
    pac.nome_mae,
    pac.nome_pai,
    pac.sexo,
    pac.estado_civil,
    ende.logradouro AS "endereço",
    ende.nro_logradouro AS numero,
    ende.compl_logradouro AS complemento,
    ende.bairro,
    cid.uf_sigla AS estado,
    cid.nome AS cidade,
    cid.cep,
    endec.logradouro,
    pac.ddd_fone_residencial,
    pac.fone_residencial AS telefone,
    endec.nro_logradouro,
    endec.compl_logradouro AS complementoc,
    cidc.uf_sigla AS estadoc,
    cidc.nome AS cidadec,
    cidc.cep AS cepc,
    pac.ddd_fone_recado,
    pac.fone_recado,
    frh.fator_rh,
    peso.peso,
    alt.altura
   FROM agh.ain_internacoes inte
     JOIN agh.aip_pacientes pac ON inte.pac_codigo = pac.codigo
     JOIN agh.aip_nacionalidades nac ON pac.nac_codigo = nac.codigo
     JOIN agh.aip_enderecos_pacientes ende ON pac.codigo = ende.pac_codigo AND ende.tipo_endereco::text = 'R'::text
     LEFT JOIN agh.aip_cidades cid ON cid.codigo = ende.cdd_codigo
     LEFT JOIN agh.aip_enderecos_pacientes endec ON pac.codigo = endec.pac_codigo AND endec.tipo_endereco::text <> 'R'::text
     LEFT JOIN agh.aip_cidades cidc ON cidc.codigo = endec.cdd_codigo
     LEFT JOIN agh.aip_paciente_dado_clinicos frh ON pac.codigo = frh.pac_codigo
     LEFT JOIN agh.aip_peso_pacientes peso ON pac.codigo = peso.pac_codigo
     LEFT JOIN agh.aip_altura_pacientes alt ON pac.codigo = alt.pac_codigo;

ALTER TABLE agh.v_ain_internacao_paciente
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao_paciente TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao_paciente TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_internacao_paciente TO acesso_leitura;

--24/08/2016 #83713 Alterado tamanho da coluna do cuidado de sumarios
ALTER TABLE agh.epe_item_cuidado_sumarios ALTER COLUMN descricao TYPE CHARACTER VARYING(240);	


--26/08/2016 #83640 Inserir permissões para o perfil MULT01

INSERT INTO AGH.CSE_PERFIS VALUES('MULT01','Acesso às telas básicas de OPS04 e OPS14 com atendimento Ambulatório',null,'M',null,'S','N','N','N','N',1,'N',null,'Perfil Multiprofissional com atendimento ambulatórial',0); 
INSERT INTO AGH.CSE_PERFIL_PROCESSOS VALUES((select seq from agh.cse_processos where nome = 'EVOLUCAO'),'MULT01','S','S','S','A',now(),9999999,955,'N','N',0); 
INSERT INTO AGH.CSE_PERFIL_PROCESSOS VALUES((select seq from agh.cse_processos where nome = 'ANAMNESE'),'MULT01','S','S','S','A',now(),9999999,955,'N','N',0); 
INSERT INTO AGH.CSE_PERFIL_PROCESSOS VALUES((select seq from agh.cse_processos where nome = 'PROCEDIMENTOS'),'MULT01','S','S','S','A',now(),9999999,955,'N','N',0); 
INSERT INTO AGH.CSE_PERFIL_PROCESSOS VALUES((select seq from agh.cse_processos where nome = 'ATESTADO'),'MULT01','S','S','S','A',now(),9999999,955,'N','N',0); 
INSERT INTO AGH.CSE_PERFIL_PROCESSOS VALUES((select seq from agh.cse_processos where nome = 'RECEITUARIO'),'MULT01','S','S','S','A',now(),9999999,955,'N','N',0); 
INSERT INTO AGH.CSE_CATEGORIA_PERFIS VALUES('MULT01',(select seq from agh.cse_categoria_profissionais where nome = 'Outro Profissional de Saúde'),'A',now(),9999999,955,0);
INSERT INTO AGH.CSE_CATEGORIA_PERFIS VALUES('MULT01',(select seq from agh.cse_categoria_profissionais where nome = 'Nutricionista'),'A',now(),9999999,955,0) ;

--26/08/2016 #83799 Modificação do tipo da coluna procedimento hospitalar e instrumento de registro
--Limpar tabela de relacionamento entre procedimento hospitalar e instrumento de registro
DELETE FROM agh.FAT_PROCEDIMENTOS_REGISTRO;
ALTER TABLE agh.FAT_PROCEDIMENTOS_REGISTRO ALTER COLUMN cod_procedimento TYPE bigint USING (cod_procedimento::bigint);

--26/08/2016 #83847 Inserindo parametros P_AGHU_NOME_ARQUIVO_REGISTRO e P_AGHU_NOME_ARQUIVO_PROCEDIMENTO_REGISTRO
INSERT INTO AGH.AGH_PARAMETROS 
(
	SEQ,
	SIS_SIGLA,
	NOME,
	DESCRICAO,	
	MANTEM_HISTORICO,
	CRIADO_EM,
	CRIADO_POR,	
	ALTERADO_EM,
	ALTERADO_POR,
	TIPO_DADO,
	VERSION,
	VLR_TEXTO,
	VLR_TEXTO_PADRAO
)
SELECT

	nextval('agh.agh_psi_sq1') AS SEQ,
	'AGH' AS SIS_SIGLA,
	'P_AGHU_NOME_ARQUIVO_REGISTRO' AS NOME,
	'Parâmetro que indica o nome da tabela do sigtap, referente ao tb_registro' AS DESCRICAO,	
	'S' AS MANTEM_HISTORICO,
	NOW() AS CRIADO_EM,
	'AGHU' AS CRIADO_POR,	
	NULL AS ALTERADO_EM,
	NULL AS ALTERADO_POR,
	'N' AS TIPO_DADO,
	0 AS VERSION,
	'tb_registro.txt' AS VLR_TEXTO,
	'tb_registro.txt' AS VLR_TEXTO_PADRAO
	
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_NOME_ARQUIVO_REGISTRO' AND sis_sigla = 'AGH');

INSERT INTO AGH.AGH_PARAMETROS 
(
	SEQ,
	SIS_SIGLA,
	NOME,
	DESCRICAO,	
	MANTEM_HISTORICO,
	CRIADO_EM,
	CRIADO_POR,	
	ALTERADO_EM,
	ALTERADO_POR,
	TIPO_DADO,
	VERSION,
	VLR_TEXTO,
	VLR_TEXTO_PADRAO
)
SELECT
	nextval('agh.agh_psi_sq1') AS SEQ,
	'AGH' AS SIS_SIGLA,
	'P_AGHU_NOME_ARQUIVO_PROCEDIMENTO_REGISTRO' AS NOME,
	'Parâmetro que indica o nome da tabela do sigtap, referente ao tb_procedimento_registro' AS DESCRICAO,
	'S' AS MANTEM_HISTORICO,
	NOW() AS CRIADO_EM,
	'AGHU' AS CRIADO_POR,
	NULL AS ALTERADO_EM,
	NULL AS ALTERADO_POR,	
	'N' AS TIPO_DADO,
	0 AS VERSION,
	'rl_procedimento_registro.txt' AS VLR_TEXTO,
	'rl_procedimento_registro.txt' AS VLR_TEXTO_PADRAO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_NOME_ARQUIVO_PROCEDIMENTO_REGISTRO' AND sis_sigla = 'AGH');		

--14/09/2016 #84756 Alteração do nome	
DROP VIEW AGH.V_AIN_INTERNACAO;

ALTER TABLE AGH.AIN_RESPONSAVEIS_PACIENTE ALTER COLUMN NOME TYPE VARCHAR(100);
ALTER TABLE AGH.AIN_RESPONSAVEIS_PACIENTE_JN ALTER COLUMN NOME TYPE VARCHAR(100);


CREATE OR REPLACE VIEW agh.v_ain_internacao AS 
 SELECT inte.seq AS nro_internacao,
    inte.pac_codigo AS registro,
    qua.nro_reg_conselho AS crm,
    esp.nome_especialidade AS especialidade,
        CASE
            WHEN inte.unf_seq IS NOT NULL THEN inte.unf_seq
            ELSE unf2.seq
        END AS unidade_funcional,
    inte.iph_seq AS codigo_procedimento,
    lei.leito,
    lei.qrt_numero,
    cid.codigo AS cid_primario,
    date(inte.dthr_internacao) AS data,
    to_char(inte.dthr_internacao, 'HH:MI:SS'::text) AS hora,
    obs.descricao AS observacao,
    res.nome AS nome_responsavel,
    res.cpf AS documento_responsavel,
    res.ddd_fone::bigint | res.fone AS telefone,
    acp.nome AS acompanhante,
    oev.descricao AS origem,
    cids.codigo AS cid_secundario,
    inte.oev_seq,
    serl.usuario AS login,
    inte.dt_prev_alta,
    inte.doc_obito,
    inte.ind_paciente_internado,
    iph.cod_tabela AS procedimento_unificado
   FROM agh.ain_internacoes inte
     JOIN agh.rap_servidores ser ON inte.ser_matricula_professor = ser.matricula AND inte.ser_vin_codigo_professor = ser.vin_codigo
     JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = ser.pes_codigo
     LEFT JOIN agh.rap_qualificacoes qua ON pes.codigo = qua.pes_codigo
     JOIN agh.agh_especialidades esp ON esp.seq = inte.esp_seq
     LEFT JOIN agh.agh_unidades_funcionais unf ON inte.unf_seq = unf.seq
     LEFT JOIN agh.ain_leitos lei ON inte.lto_lto_id::text = lei.lto_id::text
     LEFT JOIN agh.agh_unidades_funcionais unf2 ON lei.unf_seq = unf2.seq
     LEFT JOIN agh.ain_cids_internacao int_cid ON inte.seq = int_cid.int_seq AND int_cid.ind_prioridade_cid::text = 'P'::text
     LEFT JOIN agh.ain_responsaveis_paciente res ON inte.seq = res.int_seq AND res.tipo_responsabilidade::text = 'C'::text
     LEFT JOIN agh.ain_observacoes_censo obs ON inte.seq = obs.seq
     LEFT JOIN agh.ain_acompanhantes_internacao acp ON inte.seq = acp.int_seq
     JOIN agh.agh_origem_eventos oev ON inte.oev_seq = oev.seq
     LEFT JOIN agh.ain_cids_internacao int_cids ON inte.seq = int_cids.int_seq AND int_cids.ind_prioridade_cid::text = 'S'::text
     JOIN agh.rap_servidores serl ON inte.ser_matricula_digita = serl.matricula AND inte.ser_vin_codigo_digita = serl.vin_codigo
     JOIN agh.fat_itens_proced_hospitalar iph ON inte.iph_seq = iph.seq
     JOIN agh.agh_cids cid ON int_cid.cid_seq = cid.seq
     LEFT JOIN agh.agh_cids cids ON int_cids.cid_seq = cids.seq;

ALTER TABLE agh.v_ain_internacao
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao TO postgres;
GRANT ALL ON TABLE agh.v_ain_internacao TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_internacao TO acesso_leitura;

		
--20/09/2016 Correção da visão #85043


-- DROP VIEW agh.v_afa_prcr_disp_mdtos;
CREATE OR REPLACE VIEW agh.v_afa_prcr_disp_mdtos AS 
  SELECT vpcr.atd_seq,
    vpcr.seq,
    vpcr.dt_referencia,
    vpcr.dthr_inicio,
    vpcr.dthr_fim,
    vpcr.atd_seq_local,
    atendimento.prontuario,
    atendimento.lto_lto_id,
    atendimento.qrt_numero,
    atendimento.unf_seq,
    atendimento.trp_seq,
    paciente.codigo,
    paciente.nome,
    sumarios.apa_atd_seq,
    sumarios.apa_seq,
    sumarios.seqp,
    count(dispmdtossolic.seq) AS countsolic,
    count(dispmdtosdisp.seq) AS countdisp,
    count(dispmdtosconf.seq) AS countconf,
    count(dispmdtosenv.seq) AS countenv,
    count(dispmdtostriado.seq) AS counttriado,
    count(dispmdtosocorr.seq) AS countocorr
   FROM agh.v_afa_prcr_disp vpcr
     LEFT JOIN agh.agh_atendimentos atendimento ON vpcr.atd_seq = atendimento.seq
     LEFT JOIN agh.aip_pacientes paciente ON atendimento.pac_codigo = paciente.codigo
     LEFT JOIN agh.mpm_alta_sumarios sumarios ON vpcr.atd_seq = sumarios.apa_atd_seq and sumarios.ind_concluido::text = 'S'::text
     LEFT JOIN agh.mpm_prescricao_medicas prescricao ON prescricao.atd_seq = vpcr.atd_seq AND prescricao.seq = vpcr.seq
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtossolic ON ((dispmdtossolic.pme_atd_seq = prescricao.atd_seq AND dispmdtossolic.pme_seq = prescricao.seq) OR (dispmdtossolic.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtossolic.imo_pmo_pte_seq = prescricao.seq)) AND dispmdtossolic.ind_situacao::text = 'S'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosdisp ON ((dispmdtosdisp.pme_atd_seq = prescricao.atd_seq AND dispmdtosdisp.pme_seq = prescricao.seq) OR (dispmdtosdisp.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosdisp.imo_pmo_pte_seq = prescricao.seq)) AND dispmdtosdisp.ind_situacao::text = 'D'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosconf ON ((dispmdtosconf.pme_atd_seq = prescricao.atd_seq AND dispmdtosconf.pme_seq = prescricao.seq) OR (dispmdtosconf.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosconf.imo_pmo_pte_seq = prescricao.seq)) AND dispmdtosconf.ind_situacao::text = 'C'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosenv ON ((dispmdtosenv.pme_atd_seq = prescricao.atd_seq AND dispmdtosenv.pme_seq = prescricao.seq) OR (dispmdtosenv.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosenv.imo_pmo_pte_seq = prescricao.seq)) AND dispmdtosenv.ind_situacao::text = 'E'::text
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtostriado ON ((dispmdtostriado.pme_atd_seq = prescricao.atd_seq AND dispmdtostriado.pme_seq = prescricao.seq) OR (dispmdtostriado.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtostriado.imo_pmo_pte_seq = prescricao.seq)) AND dispmdtostriado.ind_situacao::text = 'T'::text AND dispmdtostriado.qtde_dispensada IS NOT NULL AND dispmdtostriado.qtde_dispensada > 0::double precision AND dispmdtostriado.qtde_estornada IS NULL AND dispmdtostriado.TOD_SEQ IS NULL
     LEFT JOIN agh.afa_dispensacao_mdtos dispmdtosocorr ON ((dispmdtosocorr.pme_atd_seq = prescricao.atd_seq AND dispmdtosocorr.pme_seq = prescricao.seq) OR (dispmdtosocorr.imo_pmo_pte_atd_seq = prescricao.atd_seq AND dispmdtosocorr.imo_pmo_pte_seq = prescricao.seq)) AND dispmdtosocorr.ind_situacao::text = 'T'::text AND (dispmdtosocorr.qtde_estornada IS NOT NULL AND dispmdtosocorr.qtde_estornada > 0::double precision OR dispmdtosocorr.tod_seq IS NOT NULL)     
  GROUP BY vpcr.atd_seq, vpcr.seq, vpcr.dt_referencia, vpcr.dthr_inicio, vpcr.dthr_fim, vpcr.atd_seq_local, atendimento.prontuario, atendimento.lto_lto_id, atendimento.qrt_numero, atendimento.unf_seq, atendimento.trp_seq, sumarios.apa_atd_seq, sumarios.apa_seq, sumarios.seqp, paciente.codigo, paciente.nome;

ALTER TABLE agh.v_afa_prcr_disp_mdtos
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_afa_prcr_disp_mdtos TO postgres;
GRANT ALL ON TABLE agh.v_afa_prcr_disp_mdtos TO acesso_completo;
GRANT SELECT ON TABLE agh.v_afa_prcr_disp_mdtos TO acesso_leitura;


--28/09/2016 #84756 Alteração da constraint mbc_puc_ck1

	ALTER TABLE agh.mbc_prof_atua_unid_cirgs drop CONSTRAINT mbc_puc_ck1;

ALTER TABLE agh.mbc_prof_atua_unid_cirgs
add CONSTRAINT mbc_puc_ck1 CHECK (ind_funcao_prof::text = ANY (ARRAY['MPF'::character varying::text,
'ANP'::character varying::text, 
'ANR'::character varying::text,
'ENF'::character varying::text,
'INS'::character varying::text, 
'MAX'::character varying::text, 
'CIR'::character varying::text, 
'MCO'::character varying::text, 
'ANC'::character varying::text, 
'ESE'::character varying::text, 
'MRE'::character varying::text,
'OPF'::character varying::text,
'ORE'::character varying::text]));


--29/09/2016 #85500 Criação da tabela agh.epe_historico_presc_diagnosticos

CREATE TABLE agh.epe_historico_presc_diagnosticos
(
  pen_atd_seq integer NOT NULL,
  pen_seq integer NOT NULL,
  seq integer NOT NULL,
  dgn_snb_gnb_seq smallint NOT NULL,
  dgn_snb_sequencia smallint NOT NULL,
  dgn_sequencia smallint NOT NULL,
  fre_seq smallint NOT NULL,
  ser_matricula integer NOT NULL,
  ser_vin_codigo smallint NOT NULL,
  criado_em timestamp without time zone NOT NULL,
  descricao character varying(210),
  ind_situacao character varying(1) DEFAULT 'I'::character varying,
  ind_pendente character varying(1) DEFAULT 'S'::character varying,
  version integer NOT NULL DEFAULT 0,
  CONSTRAINT epe_historico_presc_diagnosticos_pkey PRIMARY KEY (seq),
  CONSTRAINT epe_hpd_dgn_fk1 FOREIGN KEY (dgn_snb_gnb_seq, dgn_snb_sequencia, dgn_sequencia)
      REFERENCES agh.epe_diagnosticos (snb_gnb_seq, snb_sequencia, sequencia) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT epe_hpd_pen_fk1 FOREIGN KEY (pen_atd_seq, pen_seq)
      REFERENCES agh.epe_prescricoes_enfermagem (atd_seq, seq) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT epe_hpd_ser_fk1 FOREIGN KEY (ser_matricula, ser_vin_codigo)
      REFERENCES agh.rap_servidores (matricula, vin_codigo) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT epe_hpd_fre_fk1 FOREIGN KEY (fre_seq)
      REFERENCES agh.epe_fat_relacionados (seq) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT epe_hpd_ck1 CHECK (ind_situacao::text = ANY (ARRAY['C'::character varying::text, 'I'::character varying::text, 'E'::character varying::text, 'X'::character varying::text])),            
  CONSTRAINT epe_hpd_ck2 CHECK (ind_pendente::text = ANY (ARRAY['S'::character varying::text, 'N'::character varying::text]))            
)
WITH (
  OIDS=FALSE
);
ALTER TABLE agh.epe_historico_presc_diagnosticos
  OWNER TO postgres;
GRANT ALL ON TABLE agh.epe_historico_presc_diagnosticos TO postgres;
GRANT ALL ON TABLE agh.epe_historico_presc_diagnosticos TO acesso_completo;
GRANT SELECT ON TABLE agh.epe_historico_presc_diagnosticos TO acesso_leitura;

CREATE SEQUENCE agh.epe_hpd_sq1
    START WITH 1
    INCREMENT BY 1
    MAXVALUE 99999999999999
    NO MINVALUE
    CACHE 2;


GRANT ALL ON SEQUENCE agh.epe_hpd_sq1 TO ugen_aghu;
GRANT ALL ON SEQUENCE agh.epe_hpd_sq1 TO acesso_completo;
GRANT SELECT ON SEQUENCE agh.epe_hpd_sq1 TO acesso_leitura;

--03/10/2016 #85689 
ALTER TABLE casca.csc_menu ADD CONSTRAINT csc_menu_uk3 UNIQUE (url);

--05/10/2016 #85813

ALTER TABLE agh.MPM_PRESCRICAO_MDTOS add column ORDEM integer;
ALTER TABLE agh.MPM_PRESCRICAO_CUIDADOS add column ORDEM integer;
ALTER TABLE agh.MPM_PRESCRICAO_PROCEDIMENTOS add column ORDEM integer;
ALTER TABLE agh.MPM_SOLICITACAO_CONSULTORIAS add column ORDEM integer;
ALTER TABLE agh.ABS_SOLICITACOES_HEMOTERAPICAS add column ORDEM integer;
ALTER TABLE agh.MPM_PRESCRICAO_NPTS add column ORDEM integer;
ALTER TABLE agh.MPM_ITEM_PRESCRICAO_MDTOS add column ORDEM integer;

--31/10/2016 #86949 Acrescimo de coméntarios nas tabelas

COMMENT ON COLUMN agh.MPM_PRESCRICAO_MDTOS.ORDEM   IS 'Campo utilizado para armazenar a ordem em que o item deve aparecer';
COMMENT ON COLUMN agh.MPM_PRESCRICAO_CUIDADOS.ORDEM   IS 'Campo utilizado para armazenar a ordem em que o item deve aparecer';
COMMENT ON COLUMN agh.MPM_PRESCRICAO_PROCEDIMENTOS.ORDEM   IS 'Campo utilizado para armazenar a ordem em que o item deve aparecer';
COMMENT ON COLUMN agh.MPM_SOLICITACAO_CONSULTORIAS.ORDEM   IS 'Campo utilizado para armazenar a ordem em que o item deve aparecer';
COMMENT ON COLUMN agh.ABS_SOLICITACOES_HEMOTERAPICAS.ORDEM   IS 'Campo utilizado para armazenar a ordem em que o item deve aparecer';
COMMENT ON COLUMN agh.MPM_PRESCRICAO_NPTS.ORDEM   IS 'Campo utilizado para armazenar a ordem em que o item deve aparecer';
COMMENT ON COLUMN agh.MPM_ITEM_PRESCRICAO_MDTOS.ORDEM   IS 'Campo utilizado para armazenar a ordem em que o item deve aparecer';


--06/10/2016 #85917 Inclusao de Script de Pendencia Parametro de Ticket Ambulatorio

INSERT INTO agh.agh_parametros 
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	 NEXTVAL('AGH.AGH_PSI_SQ1')
	,'MAM'
	,'P_INFORMACAO_SERVIDOR_CADASTRO_TICKET_AMBULATORIO'
	,'S'
	,NOW()
	,'AGHU'
	,NOW()
	,'AGHU'
	,NULL
	,NULL
	,'0'
	,'Parametro responsavel por definir a apresentacao de saida do dados do Servidor que Marcou a Consulta no Ticket do Paciente Ambulatorio. Valor texto deve ser informado: "0" - Apenas primeiro nome (Padrao); "1" - Nome Completo; "2" - Login do Usuario no Sistema.'
	,null
	,4
	,null
	,null
	,null
	,null
	,'T'
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_INFORMACAO_SERVIDOR_CADASTRO_TICKET_AMBULATORIO' AND sis_sigla = 'MAM');	 

--06/10/2016 #85901 Modificação do tamanho da coluna descrição na tabela mpm_unidade_medida_medicas

DROP VIEW agh.v_afa_descr_mdto;
DROP VIEW agh.v_afa_mdto_descricao;
DROP VIEW agh.v_mam_medicamentos;
DROP VIEW agh.v_mpm_dosagem;
DROP VIEW agh.v_mpm_mdtos_descr;
DROP VIEW agh.v_mpm_prescr_mdto_tbs;
DROP VIEW agh.v_mpm_prescr_mdtos;
DROP VIEW agh.v_mpm_prescr_pendente;
DROP VIEW agh.v_aip_pol_mdtos_aghu;
DROP VIEW hist.v_aip_pol_mdtos_aghu;

ALTER TABLE AGH.mpm_unidade_medida_medicas ALTER COLUMN DESCRICAO TYPE VARCHAR(30);
ALTER TABLE AGH.mpm_unidade_medida_medicas_jn ALTER COLUMN DESCRICAO TYPE VARCHAR(30);

--27/10/2016 #86925  Acrescentar comentario a coluna alterada .
COMMENT ON COLUMN agh.mpm_unidade_medida_medicas.descricao IS 'Descrição da unidade de medida médica';
COMMENT ON COLUMN agh.mpm_unidade_medida_medicas_jn.descricao IS 'Descrição da unidade de medida médica';


CREATE OR REPLACE VIEW agh.v_afa_descr_mdto AS 
 SELECT med.mat_codigo,
    med.umm_seq,
    med.tfq_seq,
    med.tum_sigla,
    med.tpr_sigla,
    med.descricao AS descricao_mat,
    med.ind_situacao,
    med.hrio_inicio_adm_sugerido,
    med.frequencia_usual,
    med.ind_padronizacao,
    med.concentracao,
    med.descricao,
    NULL::character varying AS sinonimo,
    med.ind_exige_observacao,
    (((med.descricao || ' '::text) || COALESCE(med.concentracao::text, ''::text)) || ' '::text) || COALESCE(umm.descricao::text, ''::text) AS descricao_editada,
    (((med.descricao || ' '::text) || COALESCE(med.concentracao::text, ''::text)) || ' '::text) || COALESCE(umm.descricao::text, ''::text) AS desc_lov_editada,
    med.ind_diluente,
    med.ind_permite_pma,
    med.ind_permite_presc_enf
   FROM agh.afa_medicamentos med
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON umm.seq = med.umm_seq
UNION
 SELECT med.mat_codigo,
    med.umm_seq,
    med.tfq_seq,
    med.tum_sigla,
    med.tpr_sigla,
    smd.descricao AS descricao_mat,
    med.ind_situacao,
    med.hrio_inicio_adm_sugerido,
    med.frequencia_usual,
    med.ind_padronizacao,
    med.concentracao,
    med.descricao,
    smd.descricao AS sinonimo,
    med.ind_exige_observacao,
    (((med.descricao || ' '::text) || COALESCE(med.concentracao::text, ''::text)) || ' '::text) || COALESCE(umm.descricao::text, ''::text) AS descricao_editada,
    (((smd.descricao::text || ' '::text) || COALESCE(med.concentracao::text, ''::text)) || ' '::text) || COALESCE(umm.descricao::text, ''::text) AS desc_lov_editada,
    med.ind_diluente,
    med.ind_permite_pma,
    med.ind_permite_presc_enf
   FROM agh.afa_medicamentos med
     JOIN agh.afa_sinonimo_mdtos smd ON med.mat_codigo = smd.med_mat_codigo
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON umm.seq = med.umm_seq
  WHERE smd.ind_situacao::text = 'A'::text;

ALTER TABLE agh.v_afa_descr_mdto
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_afa_descr_mdto TO ugen_aghu;
GRANT ALL ON TABLE agh.v_afa_descr_mdto TO acesso_completo;
GRANT SELECT ON TABLE agh.v_afa_descr_mdto TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_afa_mdto_descricao AS 
 SELECT med.mat_codigo AS med_mat_codigo,
    rtrim((med.descricao ||
        CASE
            WHEN med.concentracao IS NOT NULL THEN ' '::text || med.concentracao::character varying::text
            ELSE ''::text
        END) ||
        CASE
            WHEN umm.descricao IS NOT NULL THEN ' '::text || umm.descricao::text
            ELSE ''::text
        END) AS med_descricao,
    rtrim(((med.descricao ||
        CASE
            WHEN med.concentracao IS NOT NULL AND med.concentracao::character varying::text = trunc(med.concentracao)::character varying::text THEN ' '::text || med.concentracao::character varying::text
            ELSE ''::text
        END) ||
        CASE
            WHEN med.concentracao IS NOT NULL AND med.concentracao::character varying::text <> trunc(med.concentracao)::character varying::text THEN ' '::text || to_char(med.concentracao, 'FM999999999990D9999'::text)
            ELSE ''::text
        END) ||
        CASE
            WHEN umm.descricao IS NOT NULL THEN ' '::text || umm.descricao::text
            ELSE ''::text
        END) AS med_descricao_ed,
    rtrim(((med.descricao ||
        CASE
            WHEN med.concentracao IS NOT NULL THEN ' '::text || med.concentracao::character varying::text
            ELSE ''::text
        END) ||
        CASE
            WHEN umm.descricao IS NOT NULL THEN ' '::text || umm.descricao::text
            ELSE ''::text
        END) ||
        CASE
            WHEN med.mat_codigo IS NOT NULL THEN (' (cód: '::text || med.mat_codigo::character varying::text) || ')'::text
            ELSE ''::text
        END) AS med_descricao_codigo,
    rtrim((((med.descricao ||
        CASE
            WHEN med.concentracao IS NOT NULL AND med.concentracao::character varying::text = trunc(med.concentracao)::character varying::text THEN ' '::text || med.concentracao::character varying::text
            ELSE ''::text
        END) ||
        CASE
            WHEN med.concentracao IS NULL AND med.concentracao::character varying::text <> trunc(med.concentracao)::character varying::text THEN ' '::text || med.concentracao::character varying::text
            ELSE ''::text
        END) ||
        CASE
            WHEN umm.descricao IS NOT NULL THEN ' '::text || umm.descricao::text
            ELSE ''::text
        END) ||
        CASE
            WHEN med.mat_codigo IS NOT NULL THEN (' (cód: '::text || med.mat_codigo::character varying::text) || ')'::text
            ELSE ''::text
        END) AS med_descricao_cod_ed
   FROM agh.mpm_unidade_medida_medicas umm
     RIGHT JOIN agh.afa_medicamentos med ON umm.seq = med.umm_seq;

ALTER TABLE agh.v_afa_mdto_descricao
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_afa_mdto_descricao TO ugen_aghu;
GRANT ALL ON TABLE agh.v_afa_mdto_descricao TO acesso_completo;
GRANT SELECT ON TABLE agh.v_afa_mdto_descricao TO acesso_leitura;


CREATE OR REPLACE VIEW agh.v_mam_medicamentos AS 
 SELECT med.mat_codigo,
    med.umm_seq,
    med.tfq_seq,
    med.tum_sigla,
    med.tpr_sigla,
    med.descricao AS descricao_mat,
    med.ind_situacao,
    med.hrio_inicio_adm_sugerido,
    med.frequencia_usual,
    med.ind_padronizacao,
    med.concentracao,
    med.descricao,
    NULL::character varying AS sinonimo,
    med.ind_exige_observacao,
    (((med.descricao || ' '::text) ||
        CASE
            WHEN med.concentracao = trunc(med.concentracao) THEN med.concentracao::text
            ELSE to_char(med.concentracao, 'FM999999999990D9999'::text)
        END) || ' '::text) || umm.descricao::text AS desc_lov_editada,
    umm.descricao AS descricao_umm
   FROM agh.afa_medicamentos med
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON umm.seq = med.umm_seq
UNION
 SELECT med.mat_codigo,
    med.umm_seq,
    med.tfq_seq,
    med.tum_sigla,
    med.tpr_sigla,
    smd.descricao AS descricao_mat,
    med.ind_situacao,
    med.hrio_inicio_adm_sugerido,
    med.frequencia_usual,
    med.ind_padronizacao,
    med.concentracao,
    med.descricao,
    smd.descricao AS sinonimo,
    med.ind_exige_observacao,
    (((smd.descricao::text || ' '::text) ||
        CASE
            WHEN med.concentracao = trunc(med.concentracao) THEN med.concentracao::text
            ELSE to_char(med.concentracao, 'FM999999999990D9999'::text)
        END) || ' '::text) || umm.descricao::text AS desc_lov_editada,
    umm.descricao AS descricao_umm
   FROM agh.afa_medicamentos med
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON umm.seq = med.umm_seq
     JOIN agh.afa_sinonimo_mdtos smd ON med.mat_codigo = smd.med_mat_codigo;

ALTER TABLE agh.v_mam_medicamentos
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mam_medicamentos TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mam_medicamentos TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mam_medicamentos TO acesso_leitura;


CREATE OR REPLACE VIEW agh.v_mpm_dosagem AS 
 SELECT fds.seq AS seq_dosagem,
    med.mat_codigo AS seq_medicamento,
        CASE
            WHEN fds.umm_seq IS NULL THEN tpr.sigla::text
            ELSE substr(umm.descricao::text, 1, 5)
        END AS seq_unidade,
        CASE
            WHEN fds.umm_seq IS NULL THEN tpr.descricao
            ELSE umm.descricao
        END AS descricao_unidade,
    fds.fator_conversao_up AS fator_conversao,
    fds.ind_situacao,
    fds.ind_usual_npt AS fds_ind_usual_npt,
    fds.umm_seq AS fds_umm_seq
   FROM agh.afa_forma_dosagens fds
     JOIN agh.afa_medicamentos med ON med.mat_codigo = fds.med_mat_codigo
     LEFT JOIN agh.afa_tipo_apres_mdtos tpr ON tpr.sigla::text = med.tpr_sigla::text
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON umm.seq = fds.umm_seq;

ALTER TABLE agh.v_mpm_dosagem
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_dosagem TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_dosagem TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mpm_dosagem TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_mpm_mdtos_descr AS 
 SELECT med.mat_codigo,
    med.descricao,
    umm.seq AS umm_seq,
    (((med.descricao || ' '::text) ||
        CASE
            WHEN med.concentracao = trunc(med.concentracao) THEN COALESCE(to_char(med.concentracao, 'fm999999999990'::text), ''::text)
            ELSE COALESCE(to_char(med.concentracao, 'fm999999999990D9999'::text), ''::text)
        END) || ' '::text) || COALESCE(umm.descricao::text, ''::text) AS descricao_edit,
    med.tpr_sigla
   FROM agh.afa_medicamentos med
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON umm.seq = med.umm_seq;

ALTER TABLE agh.v_mpm_mdtos_descr
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_mdtos_descr TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_mdtos_descr TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mpm_mdtos_descr TO acesso_leitura;


CREATE OR REPLACE VIEW agh.v_mpm_prescr_mdto_tbs AS 
 SELECT ime.pmd_atd_seq AS atd_seq,
    ime.pmd_seq,
    ime.seqp AS ime_seqp,
    ime.med_mat_codigo AS mat_codigo,
    pmd.duracao_trat_solicitado,
    pmd.dthr_fim,
    pmd.dthr_inicio,
    pmd.ind_pendente,
    ime.fds_seq,
    ime.dose,
    pmd.tfq_seq,
    pmd.vad_sigla,
    pmd.frequencia,
    tfq.sigla AS tfq_sigla,
    tfq.descricao AS tfq_descricao,
    tfq.sintaxe,
    umm.seq AS umm_seq,
    umm.descricao AS umm_descricao,
    med.concentracao AS med_concentracao,
    med.descricao AS med_descricao,
    (((med.descricao || ' '::text) || to_char(med.concentracao, 'fm999999999990D9999'::text)) || ' '::text) || umm2.descricao::text AS med_descricao_edit,
        CASE
            WHEN umm.descricao::text = NULL::text THEN med.tpr_sigla
            ELSE umm.descricao
        END AS unid_dose,
    (to_char(ime.dose, 'fm999999999990D9999'::text) || ' '::text) ||
        CASE
            WHEN umm.descricao::text = NULL::text THEN med.tpr_sigla
            ELSE umm.descricao
        END::text AS unid_dose_edit,
    COALESCE(replace(tfq.sintaxe::text, '#'::text, to_char(pmd.frequencia::double precision, '99999'::text)), tfq.descricao::text) AS freq_edit,
    ime.ind_origem_justif
   FROM agh.mpm_item_prescricao_mdtos ime
     JOIN agh.mpm_prescricao_mdtos pmd ON ime.pmd_atd_seq = pmd.atd_seq AND ime.pmd_seq = pmd.seq
     JOIN agh.mpm_tipo_freq_aprazamentos tfq ON tfq.seq = pmd.tfq_seq
     JOIN agh.afa_medicamentos med ON med.mat_codigo = ime.med_mat_codigo
     JOIN agh.afa_tipo_uso_mdtos tum ON tum.sigla::text = med.tum_sigla::text
     JOIN agh.afa_forma_dosagens fds ON fds.seq = ime.fds_seq
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON umm.seq = fds.umm_seq
     LEFT JOIN agh.mpm_unidade_medida_medicas umm2 ON umm2.seq = med.umm_seq
  WHERE (tum.gup_seq IN ( SELECT par.vlr_numerico
           FROM agh.agh_parametros par
          WHERE par.nome::text = 'P_GRPO_USO_MDTO_TB'::text));

ALTER TABLE agh.v_mpm_prescr_mdto_tbs
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_prescr_mdto_tbs TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_prescr_mdto_tbs TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mpm_prescr_mdto_tbs TO acesso_leitura;


CREATE OR REPLACE VIEW agh.v_mpm_prescr_mdtos AS 
 SELECT pmd.ind_item_recomendado_alta,
    pmd.atd_seq,
    pmd.seq,
    pmd.tfq_seq AS tp_frequencia,
    pmd.vad_sigla AS via_administracao,
    pmd.tva_seq AS tp_velocid,
    pmd.ind_se_necessario,
    pmd.ind_pendente,
    pmd.frequencia,
    pmd.hora_inicio_administracao AS hr_inicio_adm,
    pmd.dthr_inicio,
    pmd.dthr_fim,
    pmd.gotejo,
    pmd.duracao_trat_solicitado AS duracao_trat,
    ime.duracao_trat_aprovado AS duracao_trat_aprov,
    pmd.dthr_inicio_tratamento,
    pmd.qtde_horas_correr AS qtde_correr,
    pmd.observacao,
    ime.med_mat_codigo AS mat_codigo,
    ime.seqp AS seq_item,
    ime.dose_calculada,
    ime.ind_uso_mdto_antimicrob AS ind_uso_mdto,
    ime.observacao AS descr_complementar,
    ime.fds_seq AS fds_dose,
    ime.dose,
    ime.qtde_calc_sist_24h AS qtde_calc_24h,
    pmd.ind_solucao,
    (((med.descricao || ' '::text) ||
        CASE
            WHEN med.concentracao = trunc(med.concentracao) THEN to_char(med.concentracao, 'fm999999999990'::text)
            ELSE COALESCE(to_char(med.concentracao, 'fm999999999990D9999'::text), ' '::text)
        END) || ' '::text) || COALESCE(umm.descricao::text, ' '::text) AS med_descricao_edit,
        CASE
            WHEN umm2.descricao IS NULL THEN med.tpr_sigla
            ELSE umm2.descricao
        END AS unid_dose,
    (
        CASE
            WHEN ime.dose = trunc(ime.dose) THEN to_char(ime.dose, 'fm999G999G999G990D'::text)
            ELSE to_char(ime.dose, 'fm999G999G999G990D9999'::text)
        END || ' '::text) ||
        CASE
            WHEN umm2.descricao IS NULL THEN med.tpr_sigla
            ELSE umm2.descricao
        END::text AS unid_dose_edit,
    COALESCE(replace(tfq.sintaxe::text, '#'::text, to_char(pmd.frequencia::double precision, '99999'::text)), tfq.descricao::text) AS freq_edit,
    ime.jum_seq,
    ime.ind_mdto_aguarda_entrega,
    ime.ind_uso_mdto_antimicrob,
    ime.qtde_mg_kg,
    ime.qtde_mg_superf_corporal,
    ime.tipo_calculo_dose AS tipo_calc_dose,
    ime.observacao AS observacao_item,
    pmd.criado_em,
    tum.ind_controlado,
    ime.ind_origem_justif,
    pmd.pmd_atd_seq,
    pmd.pmd_seq,
    pmd.alterado_em,
    pmd.ser_matricula_valida,
    pmd.ser_vin_codigo_valida,
    tum.ind_antimicrobiano,
    tum.ind_quimioterapico,
    ((((med.descricao || ' '::text) ||
        CASE
            WHEN med.concentracao = trunc(med.concentracao) THEN to_char(med.concentracao, 'fm999999999990'::text)
            ELSE COALESCE(to_char(med.concentracao, 'fm999999999990D9999'::text), ' '::text)
        END) || ' '::text) || COALESCE(umm.descricao::text, ' '::text)) ||
        CASE
            WHEN ime.observacao IS NULL THEN ' '::text
            ELSE ' : '::text || ime.observacao::text
        END AS med_prcr_desc_completa,
    pmd.unid_horas_correr,
    pmd.volume_diluente_ml,
    pmd.med_mat_codigo AS med_mat_codigo_diluente,
    ime.qtde_param_calculo,
    ime.base_param_calculo,
    ime.umm_seq AS umm_seq_calculo,
    ime.duracao_param_calculo,
    ime.unid_duracao_calculo,
    ime.pca_atd_seq,
    ime.pca_criado_em,
    pmd.ind_bomba_infusao,
    pmd.pme_atd_seq,
    pmd.pme_seq
   FROM agh.mpm_item_prescricao_mdtos ime
     JOIN agh.afa_forma_dosagens fds ON fds.seq = ime.fds_seq
     JOIN agh.mpm_prescricao_mdtos pmd ON ime.pmd_seq = pmd.seq
     JOIN agh.mpm_tipo_freq_aprazamentos tfq ON tfq.seq = pmd.tfq_seq
     JOIN agh.afa_medicamentos med ON med.mat_codigo = ime.med_mat_codigo AND ime.pmd_atd_seq = pmd.atd_seq
     LEFT JOIN agh.mpm_unidade_medida_medicas umm2 ON umm2.seq = fds.umm_seq
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON umm.seq = med.umm_seq
     LEFT JOIN agh.afa_tipo_uso_mdtos tum ON tum.sigla::text = med.tum_sigla::text;

ALTER TABLE agh.v_mpm_prescr_mdtos
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_prescr_mdtos TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_prescr_mdtos TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mpm_prescr_mdtos TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_mpm_prescr_pendente AS 
 SELECT ime.pmd_atd_seq,
    ime.pmd_seq,
    ime.med_mat_codigo,
    ime.seqp,
    tum.ind_antimicrobiano,
    to_number(NULL::text, NULL::text) AS codigo,
    ime.dose,
    pmd.frequencia,
    pmd.vad_sigla,
    pmd.duracao_trat_solicitado,
    ime.ind_uso_mdto_antimicrob,
    NULL::character varying(1) AS ind_faturavel,
    gup.seq AS gup_seq,
    med.descricao,
    ime.jum_seq,
    to_number(NULL::text, NULL::text) AS csp_cnv_codigo,
    to_number(NULL::text, NULL::text) AS csp_seq,
    (((med.descricao || ' '::text) || to_char(med.concentracao, 'fm999999999990D9999'::text)) || ' '::text) || umm2.descricao::text AS descricao_edit,
    (to_char(ime.dose, 'fm999G999G999G990'::text) || ' '::text) ||
        CASE
            WHEN umm.descricao::text = NULL::text THEN med.tpr_sigla
            ELSE umm.descricao
        END::text AS dose_edit,
    COALESCE(replace(tfq.sintaxe::text, '#'::text, to_char(pmd.frequencia::double precision, 'fm999G999G999G990'::text)), tfq.descricao::text) AS freq_edit,
    tum.ind_quimioterapico
   FROM agh.mpm_item_prescricao_mdtos ime
     JOIN agh.mpm_prescricao_mdtos pmd ON ime.pmd_atd_seq = pmd.atd_seq AND ime.pmd_seq = pmd.seq
     JOIN agh.mpm_tipo_freq_aprazamentos tfq ON tfq.seq = pmd.tfq_seq
     JOIN agh.afa_medicamentos med ON ime.med_mat_codigo = med.mat_codigo
     JOIN agh.afa_tipo_uso_mdtos tum ON med.tum_sigla::text = tum.sigla::text
     JOIN agh.afa_grupo_uso_mdtos gup ON tum.gup_seq = gup.seq
     JOIN agh.afa_forma_dosagens fds ON fds.seq = ime.fds_seq
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON umm.seq = fds.umm_seq
     LEFT JOIN agh.mpm_unidade_medida_medicas umm2 ON umm2.seq = med.umm_seq
  WHERE (pmd.ind_pendente::text = ANY (ARRAY['P'::character varying::text, 'B'::character varying::text, 'R'::character varying::text])) AND tum.ind_exige_justificativa::text = 'S'::text AND ime.jum_seq IS NULL AND (pmd.dthr_fim IS NULL OR pmd.dthr_fim >= 'now'::text::date);

ALTER TABLE agh.v_mpm_prescr_pendente
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_prescr_pendente TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_prescr_pendente TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mpm_prescr_pendente TO acesso_leitura;


CREATE OR REPLACE VIEW agh.v_aip_pol_mdtos_aghu AS 
 SELECT DISTINCT atd.pac_codigo,
    ime.pmd_atd_seq AS ime_pmd_atd_seq,
    max(ime.pmd_seq) AS ime_pmd_seq,
    ime.med_mat_codigo AS ime_med_mat_codigo,
    max(ime.seqp) AS ime_seqp,
    min(pmd.dthr_inicio) AS data_inicio,
    max(COALESCE(COALESCE(pmd.dthr_fim, atd.dthr_fim)::timestamp with time zone, now())) AS data_fim,
    NULL::text AS medicamento,
    tum.ind_quimioterapico,
    tum.ind_antimicrobiano,
        CASE
            WHEN gup.seq = 4 THEN 'S'::text
            ELSE 'N'::text
        END AS ind_tuberculostatico,
    ime.observacao AS ime_observacao,
    ime.dose AS ime_dose,
    ime.fds_seq AS me_fds_seq,
    pmd.vad_sigla AS pmd_vad_sigla,
    pmd.frequencia AS pmd_frequencia,
    pmd.tfq_seq AS pmd_tfq_seq,
    pmd.hora_inicio_administracao AS pmd_hora_inicio_adm,
    pmd.qtde_horas_correr AS pmd_qtde_horas_correr,
    pmd.unid_horas_correr AS pmd_unid_horas_correr,
    pmd.gotejo AS pmd_gotejo,
    pmd.tva_seq AS pmd_tva_seq,
    pmd.ind_se_necessario AS pmd_ind_se_necessario,
    0 AS qtde_dias_prcr
   FROM agh.afa_grupo_uso_mdtos gup,
    agh.afa_tipo_uso_mdtos tum,
    agh.mpm_item_prescricao_mdtos ime,
    agh.mpm_prescricao_mdtos pmd,
    agh.agh_atendimentos atd,
    agh.afa_medicamentos med
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON med.umm_seq = umm.seq,
    agh.afa_forma_dosagens fds
     LEFT JOIN agh.mpm_unidade_medida_medicas umm2 ON fds.umm_seq = umm2.seq
  WHERE pmd.atd_seq = atd.seq AND pmd.dthr_inicio <= COALESCE(atd.dthr_fim::timestamp with time zone, now()) AND (pmd.dthr_fim IS NULL OR pmd.dthr_fim > pmd.dthr_inicio) AND
        CASE
            WHEN pmd.ind_pendente::text = 'N'::text THEN 1
            WHEN pmd.ind_pendente::text = 'A'::text THEN 1
            WHEN pmd.ind_pendente::text = 'E'::text THEN 1
            ELSE 0
        END = 1 AND ime.pmd_atd_seq = pmd.atd_seq AND ime.pmd_seq = pmd.seq AND med.mat_codigo = ime.med_mat_codigo AND tum.sigla::text = med.tum_sigla::text AND gup.seq = tum.gup_seq AND med.mat_codigo = ime.med_mat_codigo AND fds.seq = ime.fds_seq
  GROUP BY atd.pac_codigo, ime.pmd_atd_seq, ime.med_mat_codigo, tum.ind_quimioterapico, tum.ind_antimicrobiano,
        CASE
            WHEN gup.seq = 4 THEN 'S'::text
            ELSE 'N'::text
        END, ime.observacao, ime.dose, ime.fds_seq, pmd.vad_sigla, pmd.frequencia, pmd.tfq_seq, pmd.hora_inicio_administracao, pmd.qtde_horas_correr, pmd.unid_horas_correr, pmd.gotejo, pmd.tva_seq, pmd.ind_se_necessario, med.descricao, umm.descricao, med.tpr_sigla, umm2.descricao
UNION
 SELECT DISTINCT atd.pac_codigo,
    0 AS ime_pmd_atd_seq,
    0 AS ime_pmd_seq,
    med.mat_codigo AS ime_med_mat_codigo,
    0 AS ime_seqp,
    min(pte.dt_prev_execucao) AS data_inicio,
    max(pte.dt_prev_execucao) AS data_fim,
    (((med.descricao || ' '::text) || med.concentracao) || ' '::text) || umm.descricao::text AS medicamento,
    tum.ind_quimioterapico,
    tum.ind_antimicrobiano,
        CASE
            WHEN gup.seq = 4 THEN 'S'::text
            ELSE 'N'::text
        END AS ind_tuberculostatico,
    NULL::text AS ime_observacao,
    0 AS ime_dose,
    0 AS me_fds_seq,
    NULL::text AS pmd_vad_sigla,
    0 AS pmd_frequencia,
    0 AS pmd_tfq_seq,
    NULL::timestamp without time zone AS pmd_hora_inicio_adm,
    0 AS pmd_qtde_horas_correr,
    NULL::text AS pmd_unid_horas_correr,
    0 AS pmd_gotejo,
    0 AS pmd_tva_seq,
    NULL::text AS pmd_ind_se_necessario,
    count(DISTINCT pte.seq) AS qtde_dias_prcr
   FROM agh.afa_grupo_uso_mdtos gup,
    agh.afa_tipo_uso_mdtos tum,
    agh.mpm_unidade_medida_medicas umm
     LEFT JOIN agh.afa_medicamentos med ON umm.seq = med.umm_seq,
    agh.mpt_item_prescricao_mdtos imo,
    agh.mpt_prescricao_mdtos pmo,
    agh.mpt_prescricao_pacientes pte,
    agh.agh_atendimentos atd
  WHERE pte.atd_seq = atd.seq AND pte.dthr_valida IS NOT NULL AND pte.dt_prev_execucao <= date_trunc('day'::text, COALESCE(atd.dthr_fim::timestamp with time zone, now())) AND pmo.pte_atd_seq = pte.atd_seq AND pmo.pte_seq = pte.seq AND imo.pmo_pte_atd_seq = pmo.pte_atd_seq AND imo.pmo_pte_seq = pmo.pte_seq AND imo.pmo_seq = pmo.seq AND imo.med_mat_codigo = med.mat_codigo AND med.tum_sigla::text = tum.sigla::text AND tum.gup_seq = gup.seq AND (tum.ind_antimicrobiano::text = 'S'::text OR tum.ind_quimioterapico::text = 'S'::text OR gup.seq = 4)
  GROUP BY atd.pac_codigo, atd.seq, med.mat_codigo, med.descricao, med.concentracao, umm.descricao, tum.ind_quimioterapico, tum.ind_antimicrobiano, gup.seq;

ALTER TABLE agh.v_aip_pol_mdtos_aghu
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_aip_pol_mdtos_aghu TO ugen_aghu;
GRANT ALL ON TABLE agh.v_aip_pol_mdtos_aghu TO acesso_completo;
GRANT SELECT ON TABLE agh.v_aip_pol_mdtos_aghu TO acesso_leitura;


CREATE OR REPLACE VIEW hist.v_aip_pol_mdtos_aghu AS 
 SELECT DISTINCT atd.pac_codigo,
    ime.pmd_atd_seq AS ime_pmd_atd_seq,
    max(ime.pmd_seq) AS ime_pmd_seq,
    ime.med_mat_codigo AS ime_med_mat_codigo,
    max(ime.seqp) AS ime_seqp,
    min(pmd.dthr_inicio) AS data_inicio,
    max(COALESCE(COALESCE(pmd.dthr_fim, atd.dthr_fim)::timestamp with time zone, now())) AS data_fim,
    NULL::text AS medicamento,
    tum.ind_quimioterapico,
    tum.ind_antimicrobiano,
        CASE
            WHEN gup.seq = 4 THEN 'S'::text
            ELSE 'N'::text
        END AS ind_tuberculostatico,
    ime.observacao AS ime_observacao,
    ime.dose AS ime_dose,
    ime.fds_seq AS me_fds_seq,
    pmd.vad_sigla AS pmd_vad_sigla,
    pmd.frequencia AS pmd_frequencia,
    pmd.tfq_seq AS pmd_tfq_seq,
    pmd.hora_inicio_administracao AS pmd_hora_inicio_adm,
    pmd.qtde_horas_correr AS pmd_qtde_horas_correr,
    pmd.unid_horas_correr AS pmd_unid_horas_correr,
    pmd.gotejo AS pmd_gotejo,
    pmd.tva_seq AS pmd_tva_seq,
    pmd.ind_se_necessario AS pmd_ind_se_necessario,
    0 AS qtde_dias_prcr
   FROM agh.afa_grupo_uso_mdtos gup,
    agh.afa_tipo_uso_mdtos tum,
    hist.mpm_item_prescricao_mdtos ime,
    hist.mpm_prescricao_mdtos pmd,
    agh.agh_atendimentos atd,
    agh.afa_medicamentos med
     LEFT JOIN agh.mpm_unidade_medida_medicas umm ON med.umm_seq = umm.seq,
    agh.afa_forma_dosagens fds
     LEFT JOIN agh.mpm_unidade_medida_medicas umm2 ON fds.umm_seq = umm2.seq
  WHERE pmd.atd_seq = atd.seq AND pmd.dthr_inicio <= COALESCE(atd.dthr_fim::timestamp with time zone, now()) AND (pmd.dthr_fim IS NULL OR pmd.dthr_fim > pmd.dthr_inicio) AND
        CASE
            WHEN pmd.ind_pendente::text = 'N'::text THEN 1
            WHEN pmd.ind_pendente::text = 'A'::text THEN 1
            WHEN pmd.ind_pendente::text = 'E'::text THEN 1
            ELSE 0
        END = 1 AND ime.pmd_atd_seq = pmd.atd_seq AND ime.pmd_seq = pmd.seq AND med.mat_codigo = ime.med_mat_codigo AND tum.sigla::text = med.tum_sigla::text AND gup.seq = tum.gup_seq AND med.mat_codigo = ime.med_mat_codigo AND fds.seq = ime.fds_seq
  GROUP BY atd.pac_codigo, ime.pmd_atd_seq, ime.med_mat_codigo, tum.ind_quimioterapico, tum.ind_antimicrobiano,
        CASE
            WHEN gup.seq = 4 THEN 'S'::text
            ELSE 'N'::text
        END, ime.observacao, ime.dose, ime.fds_seq, pmd.vad_sigla, pmd.frequencia, pmd.tfq_seq, pmd.hora_inicio_administracao, pmd.qtde_horas_correr, pmd.unid_horas_correr, pmd.gotejo, pmd.tva_seq, pmd.ind_se_necessario, med.descricao, umm.descricao, med.tpr_sigla, umm2.descricao
UNION
 SELECT DISTINCT atd.pac_codigo,
    0 AS ime_pmd_atd_seq,
    0 AS ime_pmd_seq,
    med.mat_codigo AS ime_med_mat_codigo,
    0 AS ime_seqp,
    min(pte.dt_prev_execucao) AS data_inicio,
    max(pte.dt_prev_execucao) AS data_fim,
    (((med.descricao || ' '::text) || med.concentracao) || ' '::text) || umm.descricao::text AS medicamento,
    tum.ind_quimioterapico,
    tum.ind_antimicrobiano,
        CASE
            WHEN gup.seq = 4 THEN 'S'::text
            ELSE 'N'::text
        END AS ind_tuberculostatico,
    NULL::text AS ime_observacao,
    0 AS ime_dose,
    0 AS me_fds_seq,
    NULL::text AS pmd_vad_sigla,
    0 AS pmd_frequencia,
    0 AS pmd_tfq_seq,
    NULL::timestamp without time zone AS pmd_hora_inicio_adm,
    0 AS pmd_qtde_horas_correr,
    NULL::text AS pmd_unid_horas_correr,
    0 AS pmd_gotejo,
    0 AS pmd_tva_seq,
    NULL::text AS pmd_ind_se_necessario,
    count(DISTINCT pte.seq) AS qtde_dias_prcr
   FROM agh.afa_grupo_uso_mdtos gup,
    agh.afa_tipo_uso_mdtos tum,
    agh.mpm_unidade_medida_medicas umm
     LEFT JOIN agh.afa_medicamentos med ON umm.seq = med.umm_seq,
    agh.mpt_item_prescricao_mdtos imo,
    agh.mpt_prescricao_mdtos pmo,
    agh.mpt_prescricao_pacientes pte,
    agh.agh_atendimentos atd
  WHERE pte.atd_seq = atd.seq AND pte.dthr_valida IS NOT NULL AND pte.dt_prev_execucao <= date_trunc('day'::text, COALESCE(atd.dthr_fim::timestamp with time zone, now())) AND pmo.pte_atd_seq = pte.atd_seq AND pmo.pte_seq = pte.seq AND imo.pmo_pte_atd_seq = pmo.pte_atd_seq AND imo.pmo_pte_seq = pmo.pte_seq AND imo.pmo_seq = pmo.seq AND imo.med_mat_codigo = med.mat_codigo AND med.tum_sigla::text = tum.sigla::text AND tum.gup_seq = gup.seq AND (tum.ind_antimicrobiano::text = 'S'::text OR tum.ind_quimioterapico::text = 'S'::text OR gup.seq = 4)
  GROUP BY atd.pac_codigo, atd.seq, med.mat_codigo, med.descricao, med.concentracao, umm.descricao, tum.ind_quimioterapico, tum.ind_antimicrobiano, gup.seq;

ALTER TABLE hist.v_aip_pol_mdtos_aghu
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE hist.v_aip_pol_mdtos_aghu TO ugen_aghu;
GRANT ALL ON TABLE hist.v_aip_pol_mdtos_aghu TO acesso_completo;
GRANT SELECT ON TABLE hist.v_aip_pol_mdtos_aghu TO acesso_leitura;	

--07/10/2016 #85825 Com objetivo de melhorar o processo de integração entre AGHU e sistema terceiro existente no LAC do HUSM, solicitamos alteração na view agh.v_integracao.

DROP VIEW agh.v_integracao;

CREATE OR REPLACE VIEW agh.v_integracao AS 
 SELECT atd.pac_codigo AS pac_id,
    pac.nome AS pac_nome,
    pac.dt_nascimento AS pac_nascimento,
    pac.sexo AS pac_sexo,
    pac.nome_mae AS pac_mae,
    pac.fone_residencial AS pac_fone,
    pac.cpf AS pac_cpf,
    pac.rg AS pac_rg,
    pac.nro_cartao_saude AS pac_cns,
        CASE
            WHEN pac.cor::text = 'B'::text THEN 'Branca'::text
            WHEN pac.cor::text = 'P'::text THEN 'Preta'::text
            WHEN pac.cor::text = 'M'::text THEN 'Parda'::text
            WHEN pac.cor::text = 'A'::text THEN 'Amarela'::text
            WHEN pac.cor::text = 'I'::text THEN 'Indigena'::text
            ELSE 'Sem declaracao'::text
        END AS pac_cor,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN concat(logr_pac.nome, ', ', end_pac.nro_logradouro)
            ELSE concat(end_pac.logradouro, ', ', end_pac.nro_logradouro)
        END AS pac_endereco,
    end_pac.compl_logradouro AS pac_end_compl,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN bai_pac.descricao
            ELSE end_pac.bairro
        END AS pac_end_bairro,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN end_pac.bcl_clo_cep
            ELSE end_pac.cep
        END AS pac_end_cep,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN cid_pac.nome
            WHEN end_pac.cdd_codigo IS NOT NULL THEN end_cid_pac.nome
            ELSE end_pac.cidade
        END AS pac_end_cidade,
        CASE
            WHEN end_pac.bcl_clo_cep IS NOT NULL THEN cid_pac.uf_sigla
            WHEN end_pac.cdd_codigo IS NOT NULL THEN end_cid_pac.uf_sigla
            ELSE end_pac.uf_sigla
        END AS pac_end_uf,
    cnv.codigo AS exame_conv_id,
    cnv.descricao AS exame_conv_descr,
    solic.unf_seq AS unid_requisitante_id,
    unf_solic.descricao AS unid_requisitante_descr,
    unf_solic.ind_unid_internacao AS und_req_interna,
    unf_exec.unf_seq AS unid_entrega_id,
    unf.descricao AS unid_entrega_descr,
    atd.lto_lto_id AS unid_leito_id,
        CASE
            WHEN qualif.nro_reg_conselho IS NOT NULL THEN qualif.nro_reg_conselho
            ELSE qualif2.nro_reg_conselho
        END AS solic_reg,
        CASE
            WHEN qualif.nro_reg_conselho IS NOT NULL THEN pessoa.uf_sigla
            ELSE pessoa2.uf_sigla
        END AS solic_uf,
        CASE
            WHEN qualif.nro_reg_conselho IS NOT NULL THEN conselho.sigla
            ELSE conselho2.sigla
        END AS solic_conselho,
        CASE
            WHEN qualif.nro_reg_conselho IS NOT NULL THEN pessoa.nome
            ELSE pessoa2.nome
        END AS solic_nome,
        CASE
            WHEN qualif.nro_reg_conselho IS NOT NULL THEN pessoa.dt_nascimento
            ELSE pessoa2.dt_nascimento
        END AS solic_nascimento,
        CASE
            WHEN qualif.nro_reg_conselho IS NOT NULL THEN pessoa.sexo
            ELSE pessoa2.sexo
        END AS solic_sexo,
        CASE
            WHEN qualif.nro_reg_conselho IS NOT NULL THEN serv.email
            ELSE serv2.email
        END AS solic_email,
    item_solic.ufe_ema_exa_sigla AS exame_id,
    exa.descricao AS exame_descr,
    item_solic.ufe_ema_man_seq AS exame_mat_id,
    mat_an.descricao AS exame_descr_mat_analise,
    ex_mat_an.ind_dependente AS exame_dependente,
    tip_am_exa.nro_amostras AS exame_qtde_amostras,
    item_hr.hed_dthr_agenda AS exame_datahora,
    solic.informacoes_clinicas AS exame_info_adic,
    solic.atd_seq AS atendimento_id,
    solic.seq AS solic_exame,
    item_solic.seqp AS solic_seq_item,
    solic.criado_em AS solic_criada_em,
    sit_solic.sit_codigo AS solic_status,
    sit_solic_ex.descricao AS solic_status_descr,
    sit_solic.criado_em AS solic_sit_exame_datahora,
    amo.man_seq AS tipo_am_exa,
    item_solic.desc_material_analise,
    atd.prontuario AS pac_same,
    int_col.descricao AS intervalo_coleta,
        CASE
            WHEN int_col.nro_amostras IS NULL THEN 1::bigint
            ELSE int_col.nro_amostras
        END AS nro_amostras
   FROM agh.ael_solicitacao_exames solic
     JOIN agh.agh_atendimentos atd ON atd.seq = solic.atd_seq
     JOIN agh.ael_item_solicitacao_exames item_solic ON item_solic.soe_seq = solic.seq
     JOIN agh.ael_exames exa ON item_solic.ufe_ema_exa_sigla::text = exa.sigla::text
     JOIN agh.ael_unf_executa_exames unf_exec ON unf_exec.ema_exa_sigla::text = exa.sigla::text AND item_solic.ufe_ema_exa_sigla::text = unf_exec.ema_exa_sigla::text AND item_solic.ufe_ema_man_seq = unf_exec.ema_man_seq AND item_solic.ufe_unf_seq = unf_exec.unf_seq
     JOIN agh.ael_exames_material_analise ex_mat_an ON ex_mat_an.exa_sigla::text = exa.sigla::text AND ex_mat_an.man_seq = item_solic.ufe_ema_man_seq
     JOIN agh.ael_materiais_analises mat_an ON mat_an.seq = ex_mat_an.man_seq
     JOIN agh.agh_unidades_funcionais unf ON unf.seq = unf_exec.unf_seq
     JOIN agh.agh_unidades_funcionais unf_solic ON unf_solic.seq = solic.unf_seq
     LEFT JOIN agh.ael_tipos_amostra_exames tip_am_exa ON mat_an.seq = tip_am_exa.man_seq AND ex_mat_an.exa_sigla::text = tip_am_exa.ema_exa_sigla::text AND ex_mat_an.man_seq = tip_am_exa.ema_man_seq
     LEFT JOIN agh.ael_item_horario_agendados item_hr ON item_hr.ise_soe_seq = item_solic.soe_seq AND item_hr.ise_seqp = item_solic.seqp
     JOIN agh.rap_servidores serv ON solic.ser_matricula = serv.matricula
     JOIN agh.rap_pessoas_fisicas pessoa ON pessoa.codigo = serv.pes_codigo
     LEFT JOIN agh.rap_qualificacoes qualif ON qualif.pes_codigo = pessoa.codigo AND qualif.sequencia = (( SELECT max(q.sequencia) AS max
           FROM agh.rap_qualificacoes q
          WHERE q.pes_codigo = qualif.pes_codigo))
     LEFT JOIN agh.rap_tipos_qualificacao tipo_qualif ON qualif.tql_codigo = tipo_qualif.codigo
     LEFT JOIN agh.rap_conselhos_profissionais conselho ON tipo_qualif.cpr_codigo = conselho.codigo
     JOIN agh.fat_conv_saude_planos cnv_planos ON cnv_planos.seq = solic.csp_seq AND cnv_planos.cnv_codigo = solic.csp_cnv_codigo
     JOIN agh.fat_convenios_saude cnv ON cnv.codigo = cnv_planos.cnv_codigo
     JOIN agh.aip_pacientes pac ON atd.pac_codigo = pac.codigo
     JOIN agh.aip_enderecos_pacientes end_pac ON end_pac.pac_codigo = pac.codigo
     LEFT JOIN agh.aip_cidades end_cid_pac ON end_cid_pac.codigo = end_pac.cdd_codigo
     LEFT JOIN agh.aip_bairros bai_pac ON end_pac.bcl_bai_codigo = bai_pac.codigo
     LEFT JOIN agh.aip_logradouros logr_pac ON end_pac.bcl_clo_lgr_codigo = logr_pac.codigo
     LEFT JOIN agh.aip_cidades cid_pac ON logr_pac.cdd_codigo = cid_pac.codigo
     JOIN agh.ael_sit_item_solicitacoes sit_solic_ex ON sit_solic_ex.codigo::text = item_solic.sit_codigo::text
     JOIN agh.ael_extrato_item_solics sit_solic ON item_solic.soe_seq = sit_solic.ise_soe_seq AND item_solic.seqp = sit_solic.ise_seqp AND sit_solic.seqp = (( SELECT max(es1.seqp) AS max
           FROM agh.ael_extrato_item_solics es1
          WHERE sit_solic.ise_soe_seq = es1.ise_soe_seq AND sit_solic.ise_seqp = es1.ise_seqp))
     LEFT JOIN agh.ael_amostra_item_exames am_item_ex ON item_solic.soe_seq = am_item_ex.ise_soe_seq AND item_solic.seqp = am_item_ex.ise_seqp AND am_item_ex.amo_seqp = 1
     LEFT JOIN agh.ael_amostras amo ON am_item_ex.amo_soe_seq = amo.soe_seq AND am_item_ex.amo_seqp = amo.seqp
     LEFT JOIN (SELECT vic.seq, vic.descricao, vic.tipo_substancia, vic.ema_exa_sigla, vic.ema_man_seq, COUNT(vic.seq) AS nro_amostras
                from agh.v_ael_intervalo_coletas vic 
                GROUP BY vic.seq, vic.descricao, vic.tipo_substancia, vic.ema_exa_sigla, vic.ema_man_seq) AS int_col ON item_solic.ico_seq = int_col.seq
     JOIN agh.rap_servidores serv2 ON solic.ser_matricula_eh_responsabilid = serv2.matricula
     JOIN agh.rap_pessoas_fisicas pessoa2 ON pessoa2.codigo = serv2.pes_codigo
     LEFT JOIN agh.rap_qualificacoes qualif2 ON qualif2.pes_codigo = pessoa2.codigo AND qualif2.sequencia = (( SELECT max(q.sequencia) AS max
           FROM agh.rap_qualificacoes q
          WHERE q.pes_codigo = qualif2.pes_codigo))
     LEFT JOIN agh.rap_tipos_qualificacao tipo_qualif2 ON qualif2.tql_codigo = tipo_qualif2.codigo
     LEFT JOIN agh.rap_conselhos_profissionais conselho2 ON tipo_qualif2.cpr_codigo = conselho2.codigo
ORDER BY solic.seq DESC, item_solic.seqp;

ALTER TABLE agh.v_integracao OWNER TO postgres;

GRANT ALL ON TABLE agh.v_integracao TO ugen_aghu;
GRANT ALL ON TABLE agh.v_integracao TO acesso_completo;
GRANT SELECT ON TABLE agh.v_integracao TO acesso_leitura;


--14/10/2016 #86282 Criação de novas colunas para controle de rotinas do atendimento. O script de update é para preencher as datas anteriores.

ALTER TABLE agh.agh_atendimento_pacientes ADD COLUMN dthr_rotina_medica TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE agh.agh_atendimento_pacientes ADD COLUMN dthr_rotina_enfermagem TIMESTAMP WITHOUT TIME ZONE;

--16/11/2016 #87124 Acrescimo de comentario das colunas  dthr_rotina_medica  e  dthr_rotina_enfermagem.
COMMENT ON COLUMN agh.agh_atendimento_pacientes.dthr_rotina_medica IS 'Coluna que define em qual data e hora foi rodado a rotina de prescrição médica';
COMMENT ON COLUMN agh.agh_atendimento_pacientes.dthr_rotina_enfermagem IS 'Coluna que define em qual data e hora foi rodado a rotina de prescrição de enfermagem';


update agh.agh_parametros set vlr_numerico = 2520 where nome like 'P_AGHU_DIFERENCA_DIAS_ATRAS_GERA_DADOS_SUMARIO_ALTA';

UPDATE agh.agh_atendimento_pacientes 
SET 	
	dthr_rotina_medica = null
from
	(
	SELECT
		ATEN_PACIENTE.seq
	FROM
		agh.agh_atendimentos ATEN
		INNER JOIN agh.agh_atendimento_pacientes ATEN_PACIENTE ON ATEN.seq = ATEN_PACIENTE.atd_seq
	WHERE
		dthr_rotina_medica IS NOT NULL
                AND not exists (select 1 from agh.MPM_ITEM_PRESCRICAO_SUMARIOS ips where apa_atd_seq = aten.seq)
                AND exists (select 1 from agh.MPM_PRESCRICAO_MEDICAS mpm where mpm.atd_seq = aten.seq)
	) X

WHERE
	agh.agh_atendimento_pacientes.seq = X.seq;


UPDATE agh.agh_atendimento_pacientes 
SET 	
	dthr_rotina_medica = agh.agh_atendimento_pacientes.criado_em
from
	(
	SELECT
		ATEN_PACIENTE.seq
		,ATEN_PACIENTE.criado_em
	FROM
		agh.agh_atendimentos ATEN
		INNER JOIN agh.agh_atendimento_pacientes ATEN_PACIENTE ON ATEN.seq = ATEN_PACIENTE.atd_seq
	WHERE
		dthr_rotina_medica IS NULL
                AND exists (select 1 from agh.MPM_ITEM_PRESCRICAO_SUMARIOS ips where apa_atd_seq = aten.seq)
	) X

WHERE
	agh.agh_atendimento_pacientes.seq = X.seq;


UPDATE agh.agh_atendimento_pacientes 
SET 	
	dthr_rotina_enfermagem = null
from
	(
	SELECT
		ATEN_PACIENTE.seq
	FROM
		agh.agh_atendimentos ATEN
		INNER JOIN agh.agh_atendimento_pacientes ATEN_PACIENTE ON ATEN.seq = ATEN_PACIENTE.atd_seq
	WHERE
		dthr_rotina_enfermagem IS NOT NULL
                AND not exists (select 1 from agh.EPE_ITEM_PRESCRICAO_SUMARIOS ips where ips.apa_atd_seq = aten.seq)
                AND exists (select 1 from agh.EPE_PRESCRICOES_ENFERMAGEM epe where epe.atd_seq = aten.seq)
	) X

WHERE
	agh.agh_atendimento_pacientes.seq = X.seq;


UPDATE agh.agh_atendimento_pacientes 
SET 	
	dthr_rotina_enfermagem = agh.agh_atendimento_pacientes.criado_em
from
	(
	SELECT
		ATEN_PACIENTE.seq
		,ATEN_PACIENTE.criado_em
	FROM
		agh.agh_atendimentos ATEN
		INNER JOIN agh.agh_atendimento_pacientes ATEN_PACIENTE ON ATEN.seq = ATEN_PACIENTE.atd_seq
	WHERE
		dthr_rotina_enfermagem IS NULL
                AND exists (select 1 from agh.EPE_ITEM_PRESCRICAO_SUMARIOS ips where apa_atd_seq = aten.seq)
	) X

WHERE
	agh.agh_atendimento_pacientes.seq = X.seq;

INSERT INTO agh.agh_parametros 
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AGH' AS SIS_SIGLA
	,'P_AGHU_DIFERENCA_DIAS_ATRAS_GERA_DADOS_SUMARIO_ALTA' AS NOME
	,'S' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,NULL AS VLR_DATA
	,0 AS VLR_NUMERICO
	,NULL AS VLR_TEXTO
	,'O parâmetro refere-se À quantidade de dias do intervalo, entre a data inicial e final para as rotinas de prescriÇÃo de enfermagem e mÉdica.' AS DESCRICAO
	,NULL AS ROTINA_CONSISTENCIA
	,10 AS VERSION
	,NULL AS VLR_DATA_PADRAO
	,NULL AS VLR_NUMERICO_PADRAO
	,NULL AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'N' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_DIFERENCA_DIAS_ATRAS_GERA_DADOS_SUMARIO_ALTA' AND sis_sigla = 'AGH');	
	
	
--19/10/2016 #86398 Adicionado um novo parametro no sistema	
INSERT INTO AGH.AGH_PARAMETROS 
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	 NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AGH' AS SIS_SIGLA
	,'P_CENSO_DIARIO_EXIBE_OBSERVACAO' AS NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,NULL AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,'S' AS VLR_TEXTO
	,'Define se deve exibir o campo observação no relatório de Censo Diario' AS DESCRICAO
	,NULL AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,NULL AS VLR_DATA_PADRAO
	,NULL AS VLR_NUMERICO_PADRAO
	,'S' AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_CENSO_DIARIO_EXIBE_OBSERVACAO' AND sis_sigla = 'AGH');		

	
--19/10/2016 #83608 - Parametrinho bacana que server pra campo novo do relatorio de posiçao final de estoque
INSERT INTO agh.agh_parametros 
SELECT
  (select nextval('agh.agh_psi_sq1'))
  ,'SCE'
  ,'P_SOMENTE_MATERIAL_ATIVO'
  ,'S'
  ,now()
  ,'AGHU'
  ,now()
  ,'AGHU'
  ,NULL
  ,NULL
  ,'S'
  ,'Campo relacionado aos relatorio de posiçao final de estoque. Atraves desse campo e possivel definir um padrao para o relatorio exibir somente os materiais ativos ou todos os materiais independente se o cadastro do mesmo esta inativos, apresentando todos que tiveram movimentaçao. Recomenda por default definir o valor S mostrando assim so os ativos e deixando o checkbox marcado na tela. Mas com o valor N sera mostrados todos os materiais independentemente deste estar ativo e deixando o campo desmarcado na tela.'
  ,NULL
  ,2
  ,NULL
  ,NULL
  ,NULL
  ,NULL
  ,'T'
WHERE
  NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_SOMENTE_MATERIAL_ATIVO' AND sis_sigla = 'SCE');
  
  
--20/10/2016 #86557 Adicionado novo item no array da constraint 
ALTER TABLE agh.aip_log_pront_onlines
ADD CONSTRAINT aip_lpo_ck1 CHECK (ocorrencia = ANY (ARRAY[1, 2, 3, 4, 5, 6, 7, 8, 51, 52, 53, 54, 99]));	

--21/10/2016 #86615 Adicionado script de modificação da internação. 
ALTER TABLE agh.AIN_SOLICITACOES_INTERNACAO ADD COLUMN MOTIVO_NEGACAO character varying(500);
ALTER TABLE agh.AIN_SOLICITACOES_INTERNACAO ADD COLUMN IND_NEGATIVA_INTERNACAO character varying(1) DEFAULT 'N'::character varying;
ALTER TABLE agh.AIN_SOLICITACOES_INTERNACAO ADD COLUMN TEMPO_PREVISTO smallint;
ALTER TABLE agh.AIN_SOLICITACOES_INTERNACAO ADD COLUMN CID_SEQ integer;
ALTER TABLE agh.AIN_SOLICITACOES_INTERNACAO ADD COLUMN NEGADO_EM timestamp without time zone;

--Modificação na quantidade de caracteres da ALA que precisa alterar visões.
DROP VIEW agh.v_mpm_lista_pac_internados;
DROP VIEW agh.v_mbc_sala_cirg;
DROP VIEW agh.v_lista_prescricao_enfermagem;
DROP VIEW agh.v_lista_mbc_cirurgias;
DROP VIEW agh.v_ain_pesq_leitos;
DROP VIEW agh.v_ain_mvto_internacao;
DROP VIEW agh.v_ain_leitos_limpeza;
DROP VIEW agh.v_ain_disp_vagas;
DROP VIEW agh.v_agh_unid_funcional;
DROP VIEW agh.v_ael_exames_solicitacao_atd_aghu;
DROP VIEW agh.v_ael_exames_solicitacao_atd;
DROP VIEW agh.v_ael_exame_solic_atd_aghu;

ALTER TABLE AGH.AGH_ALAS ALTER COLUMN CODIGO TYPE CHARACTER VARYING(2);
ALTER TABLE AGH.AIN_QUARTOS ALTER COLUMN ALA TYPE CHARACTER VARYING(2);
ALTER TABLE AGH.AGH_UNIDADES_FUNCIONAIS ALTER COLUMN IND_ALA  TYPE CHARACTER VARYING(2);

--31/10/2016 #86947 Acrescimo de coméntarios de tabelas

COMMENT ON COLUMN agh.AIN_SOLICITACOES_INTERNACAO.MOTIVO_NEGACAO  IS 'Ao realizar uma negativa de internação, armazenar neste campo o motivo da negação.';
COMMENT ON COLUMN agh.AIN_SOLICITACOES_INTERNACAO.IND_NEGATIVA_INTERNACAO  IS 'Indica se houve uma negação de internação para a solicitação. S = Sim, N = Não';
COMMENT ON COLUMN agh.AIN_SOLICITACOES_INTERNACAO.TEMPO_PREVISTO  IS 'Campo utilizado para armazenar o tempo previsto de internação baseado no procedimento realizado.';
COMMENT ON COLUMN agh.AIN_SOLICITACOES_INTERNACAO.NEGADO_EM   IS 'Campo utilizado para armazenar o código CID no momento da solicitação de internação.';
COMMENT ON COLUMN agh.AIN_SOLICITACOES_INTERNACAO.CID_SEQ  IS 'Campo utilizado para armazenar a data e hora da negativa de internação.';
COMMENT ON COLUMN agh.AIN_SOLICITACOES_INTERNACAO.CODIGO  IS 'Código da Ala';
COMMENT ON COLUMN agh.AIN_SOLICITACOES_INTERNACAO.ALA  IS 'Código da Ala';
COMMENT ON COLUMN agh.AIN_SOLICITACOES_INTERNACAO.IND_ALA  IS 'Código da Ala';

CREATE OR REPLACE VIEW agh.v_mpm_lista_pac_internados AS 
 SELECT atd.seq AS atd_seq,
    atd.prontuario,
    atd.pac_codigo,
    pac.nome,
    pac.nome_social,
    atd.dthr_inicio AS data_inicio_atendimento,
    atd.dthr_fim AS data_fim_atendimento,
    pac.dt_nascimento AS data_nascimento,
    atd.ser_matricula AS atd_ser_matricula,
    atd.ser_vin_codigo AS atd_ser_vin_codigo,
    atd.ser_matricula,
    atd.ser_vin_codigo,
    atd.ind_sit_sumario_alta,
    atd.origem,
    atd.ESP_SEQ,
    atd.UNF_SEQ,
    atd.ind_pac_cpa,
    atd.IND_PAC_ATENDIMENTO,
    atd.LTO_LTO_ID,
    atd.QRT_NUMERO,
    (case when rapf.nome_usual is null or rapf.nome_usual = '' then rapf.nome else rapf.nome_usual end) as nome_responsavel
    ,   CASE
            WHEN atd.origem::text = 'I'::text AND (( SELECT max(cpa.criado_em) AS max
               FROM agh.mpm_control_prev_altas cpa
              WHERE cpa.atd_seq = atd.seq AND cpa.resposta::text = 'S'::text AND cpa.dt_fim IS NOT NULL AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) >= 0::double precision AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) <= 2::double precision)) IS NOT NULL THEN 'true'::text
            WHEN atd.origem::text = 'N'::text AND (( SELECT max(cpa.criado_em) AS max
               FROM agh.mpm_control_prev_altas cpa
                 JOIN agh.agh_atendimentos atd_mae ON atd_mae.seq = cpa.atd_seq AND atd_mae.origem::text = 'I'::text
              WHERE cpa.atd_seq = atd.atd_seq_mae AND cpa.resposta::text = 'S'::text AND cpa.dt_fim IS NOT NULL AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) >= 0::double precision AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) <= 2::double precision)) IS NOT NULL THEN 'true'::text
            ELSE 'false'::text
        END AS possui_plano_altas,
        CASE
            WHEN atd.lto_lto_id IS NOT NULL THEN concat('L:', atd.lto_lto_id)
            WHEN atd.qrt_numero IS NOT NULL THEN concat('Q:', atd.qrt_numero)
            ELSE ( SELECT (((('U:'::text || unf_1.andar::text) || ' '::text) || unf_1.ind_ala::text) || ' - '::text) || unf_1.descricao::text
               FROM agh.agh_unidades_funcionais unf_1
              WHERE unf_1.seq = atd.unf_seq)
        END AS local,
        CASE
            WHEN
            CASE
                WHEN atd.ind_pac_atendimento::text = 'S'::text AND cuf.unf_seq IS NOT NULL THEN 'false'::text
                ELSE 'true'::text
            END = 'true'::text THEN ''::text
            WHEN NOT (( SELECT count(*) AS count
               FROM agh.mpm_prescricao_medicas pm
              WHERE pm.atd_seq = atd.seq AND pm.ser_matricula_valida IS NOT NULL AND pm.ser_vin_codigo_valida IS NOT NULL AND pm.dthr_fim > now())) > 0 THEN 'PRESCRICAO_NAO_REALIZADA'::text
            WHEN NOT (( SELECT count(*) AS count
               FROM agh.mpm_prescricao_medicas pm
              WHERE pm.atd_seq = atd.seq AND pm.ser_matricula_valida IS NOT NULL AND pm.ser_vin_codigo_valida IS NOT NULL AND pm.dthr_inicio > now() AND to_char(unf.hrio_validade_pme, 'HH24:mi'::text) = to_char(pm.dthr_inicio, 'HH24:mi'::text))) > 0 THEN 'PRESCRICAO_VENCE_NO_DIA'::text
            ELSE 'PRESCRICAO_VENCE_NO_DIA_SEGUINTE'::text
        END AS status_prescricao,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text = 'E'::text THEN 'SUMARIO_ALTA_NAO_ENTREGUE_SAMIS'::text
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_ALTA_NAO_REALIZADO'::text
            ELSE ''::text
        END AS status_sumario_alta,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.ael_item_solicitacao_exames ise
                 JOIN agh.ael_solicitacao_exames soe ON ise.soe_seq = soe.seq
              WHERE soe.atd_seq = atd.seq AND ise.sit_codigo::text = 'LI'::text AND NOT (EXISTS ( SELECT its.ise_seqp,
                        its.ise_soe_seq
                       FROM agh.ael_item_solic_consultados its
                         JOIN agh.ael_item_solicitacao_exames sub_ise ON its.ise_seqp = sub_ise.seqp AND its.ise_soe_seq = sub_ise.soe_seq
                         JOIN agh.ael_solicitacao_exames sub_soe ON sub_ise.soe_seq = sub_soe.seq
                      WHERE its.ise_soe_seq = ise.soe_seq AND its.ise_seqp = ise.seqp AND sub_soe.atd_seq = atd.seq AND sub_ise.sit_codigo::text = 'LI'::text)) AND NOT (EXISTS ( SELECT iri.ise_seqp,
                        iri.ise_soe_seq
                       FROM agh.ael_itens_resul_impressao iri
                      WHERE iri.ise_soe_seq = ise.soe_seq AND iri.ise_seqp = ise.seqp)))) > 0 THEN 'RESULTADOS_NAO_VISUALIZADOS'::text
            WHEN (( SELECT count(*) AS count
               FROM agh.ael_item_solicitacao_exames ise
                 JOIN agh.ael_solicitacao_exames soe ON ise.soe_seq = soe.seq
              WHERE soe.atd_seq = atd.seq AND ise.sit_codigo::text = 'LI'::text AND NOT (EXISTS ( SELECT its.ise_seqp,
                        its.ise_soe_seq
                       FROM agh.ael_item_solic_consultados its
                         JOIN agh.ael_item_solicitacao_exames sub_ise ON its.ise_seqp = sub_ise.seqp AND its.ise_soe_seq = sub_ise.soe_seq
                         JOIN agh.ael_solicitacao_exames sub_soe ON sub_ise.soe_seq = sub_soe.seq
                      WHERE its.ise_soe_seq = ise.soe_seq AND its.ise_seqp = ise.seqp AND sub_soe.atd_seq = atd.seq AND sub_ise.sit_codigo::text = 'LI'::text AND its.ser_matricula = atd.ser_matricula AND its.ser_vin_codigo = atd.ser_vin_codigo)) AND NOT (EXISTS ( SELECT iri.ise_seqp,
                        iri.ise_soe_seq
                       FROM agh.ael_itens_resul_impressao iri
                      WHERE iri.ise_soe_seq = ise.soe_seq AND iri.ise_seqp = ise.seqp)))) > 0 THEN 'RESULTADOS_VISUALIZADOS_OUTRO_MEDICO'::text
            ELSE ''::text
        END AS status_exames_nao_vistos,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_laudos lau
              WHERE lau.atd_seq = atd.seq AND lau.tuo_seq IS NOT NULL AND lau.justificativa IS NULL)) > 0 THEN 'PENDENCIA_LAUDO_UTI'::text
            ELSE ''::text
        END AS status_pendencia_documento,
        CASE
            WHEN (( SELECT count(projetos.pac_codigo) AS count
               FROM agh.ael_projeto_pacientes projetos
              WHERE projetos.pac_codigo = atd.pac_codigo AND (projetos.dt_fim IS NULL OR projetos.dt_fim >= now()) AND (projetos.jex_seq IS NULL OR (EXISTS ( SELECT jex_.seq AS y0_
                       FROM agh.ael_justificativa_exclusoes jex_
                      WHERE jex_.seq = projetos.jex_seq AND jex_.ind_mostra_telas::text = 'S'::text))) AND (EXISTS ( SELECT pjq_.seq AS y0_
                       FROM agh.ael_projeto_pesquisas pjq_
                      WHERE pjq_.seq = projetos.pjq_seq AND (pjq_.dt_fim IS NULL OR pjq_.dt_fim >= now()))))) > 0 THEN 'PACIENTE_PESQUISA'::text
            ELSE ''::text
        END AS status_paciente_pesquisa,
        CASE
            WHEN atd.origem::text = 'I'::text OR atd.origem::text = 'N'::text THEN
            CASE
                WHEN (( SELECT count(*) AS count
                   FROM agh.mam_evolucoes evo
                  WHERE evo.atd_seq = atd.seq AND date_part('day'::text, evo.dthr_valida::timestamp with time zone - now()) = 0::double precision AND evo.dthr_valida_mvto IS NULL)) > 0 THEN
                CASE
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_MEDICO'::text)) THEN 'EVOLUCAO'::text
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_OUTROS'::text)) THEN 'EVOLUCAO'::text
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_ENF'::text)) THEN
                    CASE
                        WHEN (( SELECT count(*) AS count
                           FROM agh.mam_item_evolucoes iev
                          WHERE iev.evo_seq = (( SELECT evo.seq
                                   FROM agh.mam_evolucoes evo
                                  WHERE evo.atd_seq = atd.seq AND date_part('day'::text, evo.dthr_valida::timestamp with time zone - now()) = 0::double precision AND evo.dthr_valida_mvto IS NULL
                                 LIMIT 1)) AND (iev.tie_seq IN ( SELECT mam_tipo_item_evolucoes.seq
                                   FROM agh.mam_tipo_item_evolucoes
                                  WHERE mam_tipo_item_evolucoes.sigla::text = 'C'::text)))) > 0 THEN 'EVOLUCAO'::text
                        ELSE ''::text
                    END
                    ELSE ''::text
                END
                ELSE ''::text
            END
            ELSE ''::text
        END AS status_evolucao,
        CASE
            WHEN (( SELECT count(docs.pac_codigo) AS count
               FROM agh.v_agh_versoes_documentos docs
              WHERE docs.dov_situacao::text = 'P'::text AND docs.pac_codigo = atd.pac_codigo)) > 0 THEN 'PENDENTE'::text
            ELSE ''::text
        END AS status_certificacao_digital,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_sumario_altas sa
              WHERE sa.atd_seq = atd.seq AND sa.mam_seq IS NOT NULL)) > 0 THEN 'true'::text
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_alta_sumarios asu
              WHERE asu.apa_atd_seq = atd.seq AND asu.ind_concluido::text = 'S'::text)) > 0 THEN 'true'::text
            WHEN ((( SELECT agh_parametros.vlr_texto
               FROM agh.agh_parametros
              WHERE agh_parametros.nome::text = 'P_BLOQUEIA_PAC_EMERG'::text))::text) <> 'S'::text AND (( SELECT count(*) AS count
               FROM agh.agh_caract_unid_funcionais cuf2
              WHERE cuf2.unf_seq = atd.unf_seq AND cuf2.caracteristica::text = 'Atend emerg terreo'::text)) > 0 THEN 'true'::text
            ELSE 'false'::text
        END AS disable_button_alta_obito,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'S'::text AND cuf.unf_seq IS NOT NULL THEN 'false'::text
            ELSE 'true'::text
        END AS disable_button_prescrever,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_ALTA'::text
            ELSE 'ALTA'::text
        END AS label_alta,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_OBITO'::text
            ELSE 'OBITO'::text
        END AS label_obito,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mci_notificacao_gmr gmr
              WHERE gmr.pac_codigo = atd.pac_codigo AND gmr.ind_notificacao_ativa::text = 'S'::text)) > 0 THEN 'true'::text
            ELSE 'false'::text
        END AS ind_gmr,
	case 
	   when (select count(*) from agh.AGH_CARACT_UNID_FUNCIONAIS car
		where car.caracteristica = 'Anamnese/Evolução Eletrônica'
		and car.unf_seq = atd.unf_seq) > 0 then 'true'::text
		else 'false'::text
	end as TEM_UNF_CARACT_ANAMNESE_EVOLUCAO,
	case 
	   when (select count(*) 
		from agh.MPM_ANAMNESES ana
		   inner join agh.AGH_ATENDIMENTOS atd1 on atd1.seq = ana.atd_seq
		      inner join agh.RAP_SERVIDORES rap on rap.matricula = atd1.ser_matricula and rap.vin_codigo = atd1.ser_vin_codigo
		         inner join agh.RAP_PESSOAS_FISICAS pes on pes.codigo = rap.pes_codigo
		      inner join agh.AGH_UNIDADES_FUNCIONAIS unf on unf.seq = atd1.UNF_SEQ
		         inner join agh.AGH_CARACT_UNID_FUNCIONAIS car on car.unf_seq = unf.seq
		where ana.atd_seq = atd.seq) > 0 
	    then (
		select distinct case ana.IND_PENDENTE
			when 'R' then 'ANAMNESE_NAO_REALIZADA'
			when 'P' then 'ANAMNESE_PENDENTE'
			when 'V' then
			    case 
			       when (select count(*)
					from agh.MPM_EVOLUCOES evo
					inner join agh.MPM_ANAMNESES ana on ana.seq = evo.ana_seq
					where evo.ANA_SEQ = ana.seq
					and to_char(evo.DTHR_REFERENCIA, 'YYYYMMDD') = to_char(now(), 'YYYYMMDD') 
					and evo.IND_PENDENTE <> 'R') = 0 then 'EVOLUCAO_DO_DIA_NAO_REALIZADA'
			       when (select count(*)
					from agh.MPM_EVOLUCOES evo
					inner join agh.MPM_ANAMNESES ana on ana.seq = evo.ana_seq
					where evo.ANA_SEQ = ana.seq
					and to_char(evo.DTHR_REFERENCIA, 'YYYYMMDD') = to_char(now(), 'YYYYMMDD') 
					and evo.IND_PENDENTE = 'P') > 0 then 'EVOLUCAO_DO_DIA_PENDENTE'
			       when (select count(*)
					from agh.MPM_EVOLUCOES evo
					inner join agh.MPM_ANAMNESES ana on ana.seq = evo.ana_seq
					where evo.ANA_SEQ = ana.seq
					and evo.DTHR_REFERENCIA > date_trunc('day', now()) 
					and evo.IND_PENDENTE = 'V') = 0 then 'EVOLUCAO_VENCE_NO_DIA_SEGUINTE'
				else null
				end
			else null
			end 
		from agh.MPM_ANAMNESES ana
		   inner join agh.AGH_ATENDIMENTOS atd1 on atd1.seq = ana.atd_seq
		      inner join agh.RAP_SERVIDORES rap on rap.matricula = atd1.ser_matricula and rap.vin_codigo = atd1.ser_vin_codigo
			 inner join agh.RAP_PESSOAS_FISICAS pes on pes.codigo = rap.pes_codigo
		      inner join agh.AGH_UNIDADES_FUNCIONAIS unf1 on unf1.seq = atd1.UNF_SEQ
			 inner join agh.AGH_CARACT_UNID_FUNCIONAIS car on car.unf_seq = unf1.seq
		where ana.atd_seq = atd.seq)::text
	    else 'ANAMNESE_NAO_REALIZADA'
	end as STATUS_ANAMNESE_EVOLUCAO
   FROM agh.agh_atendimentos atd
     LEFT JOIN agh.agh_caract_unid_funcionais cuf ON cuf.unf_seq = atd.unf_seq AND cuf.caracteristica::text = 'Pme Informatizada'::text
     LEFT JOIN agh.agh_unidades_funcionais unf ON atd.unf_seq = unf.seq
     LEFT JOIN agh.rap_servidores raps ON raps.matricula = atd.ser_matricula AND raps.vin_codigo = atd.ser_vin_codigo
     LEFT JOIN agh.rap_pessoas_fisicas rapf ON raps.pes_codigo = rapf.codigo
     LEFT JOIN agh.aip_pacientes pac ON pac.codigo = atd.pac_codigo;

ALTER TABLE agh.v_mpm_lista_pac_internados
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mpm_lista_pac_internados TO postgres;
GRANT ALL ON TABLE agh.v_mpm_lista_pac_internados TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mpm_lista_pac_internados TO acesso_leitura;


CREATE OR REPLACE VIEW agh.v_mbc_sala_cirg AS 
 SELECT sci.unf_seq AS sci_unf_seq,
    sci.seqp AS sci_seqp,
    sci.situacao AS sci_situacao,
    sci.ind_especial AS sci_ind_especial,
    sci.nome AS sci_nome,
    sci.motivo_inat AS sci_motivo_inat,
    unf.descricao AS unf_descricao,
    unf.andar AS unf_andar,
    unf.ind_ala AS unf_ind_ala,
    unf.ind_sit_unid_func AS unf_ind_sit_unid_func,
    unf.sigla AS unf_sigla,
    sci.seqp AS sci_sala
   FROM agh.mbc_sala_cirurgicas sci,
    agh.agh_unidades_funcionais unf
  WHERE sci.unf_seq = unf.seq;

ALTER TABLE agh.v_mbc_sala_cirg
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mbc_sala_cirg TO postgres;
GRANT ALL ON TABLE agh.v_mbc_sala_cirg TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mbc_sala_cirg TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_lista_prescricao_enfermagem AS 
 SELECT atendimento.seq AS atd_seq,
    paciente.nome,
    paciente.dt_nascimento,
    atendimento.dthr_inicio AS dt_atendimento,
    atendimento.prontuario,
    paciente.codigo AS pac_codigo,
    atendimento.origem,
    atendimento.unf_seq,
    atendimento.esp_seq,
    atendimento.dthr_fim,
    atendimento.ser_matricula,
    atendimento.ser_vin_codigo,
    atendimento.ind_pac_atendimento,
    atendimento.ind_pac_cpa,
        CASE
            WHEN atendimento.lto_lto_id IS NOT NULL THEN concat('L:', atendimento.lto_lto_id)
            WHEN atendimento.qrt_numero IS NOT NULL THEN concat('Q:', atendimento.qrt_numero)
            ELSE ( SELECT (((('U:'::text || unf.andar::text) || ' '::text) || unf.ind_ala::text) || ' - '::text) || unf.descricao::text
               FROM agh.agh_unidades_funcionais unf
              WHERE unf.seq = atendimento.unf_seq)
        END AS local,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_solicitacao_consultorias solic
              WHERE solic.atd_seq = atendimento.seq AND solic.origem::text = 'E'::text AND solic.dthr_resposta IS NOT NULL AND solic.dthr_conhecimento_resposta IS NULL AND solic.ind_situacao::text = 'A'::text)) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS solic_consultoria_resp,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_solicitacao_consultorias solic
              WHERE solic.atd_seq = atendimento.seq AND solic.origem::text = 'E'::text AND solic.dthr_resposta IS NULL AND solic.dthr_conhecimento_resposta IS NULL AND solic.ind_situacao::text = 'A'::text)) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS solic_consultoria_solic,
    ( SELECT mpm.ind_concluido
           FROM agh.mpm_alta_sumarios mpm
          WHERE mpm.apa_atd_seq = atendimento.seq AND mpm.ind_concluido::text = 'S'::text AND (mpm.ind_tipo::text = ANY (ARRAY['ALT'::character varying::text, 'OBT'::character varying::text]))) AS ind_alta_medica,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mci_notificacao_gmr gmr
              WHERE gmr.pac_codigo = paciente.codigo AND gmr.ind_notificacao_ativa::text = 'S'::text)) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS germ_multi_resistente,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.ecp_controle_pacientes contro
                 JOIN agh.ecp_horario_controles hor ON contro.hct_seq = hor.seq
              WHERE hor.atd_seq = atendimento.seq AND hor.pac_codigo = paciente.codigo)) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS paciente_possui_controle,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.ael_projeto_pacientes proj_pac
              WHERE proj_pac.pac_codigo = paciente.codigo AND (proj_pac.dt_fim IS NULL OR proj_pac.dt_fim >= now()) AND (proj_pac.jex_seq IS NULL OR (EXISTS ( SELECT jex_.seq
                       FROM agh.ael_justificativa_exclusoes jex_
                      WHERE jex_.seq = proj_pac.jex_seq AND jex_.ind_mostra_telas::text = 'S'::text))))) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS existe_projeto_paciente,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mci_notificacao_gmr gmr
              WHERE gmr.pac_codigo = paciente.codigo AND gmr.ind_notificacao_ativa::text = 'S'::text)) > 0 THEN ( SELECT agh_parametros.vlr_texto
               FROM agh.agh_parametros
              WHERE agh_parametros.nome::text = 'P_AGHU_COR_NOTIF_GMR'::text)
            ELSE NULL::character varying
        END AS cor_germe_multiresistente,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.agh_caract_unid_funcionais caract_unidade
              WHERE unidade_funcional.seq = caract_unidade.unf_seq AND caract_unidade.caracteristica::text = 'Pen Informatizada'::text)) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS habilita_prescricao,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio <= now() AND pre.dthr_fim > now())) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS possui_prescricao_vigente,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio <= now() AND pre.dthr_fim > now())) > 0 THEN ( SELECT pre.ser_matricula_valida
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio <= now() AND pre.dthr_fim > now()
             LIMIT 1)
            ELSE NULL::integer
        END AS servidor_prescricao_vigente,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio <= now() AND pre.dthr_fim > now())) > 0 THEN ( SELECT pre.ser_matricula
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio > now()
             LIMIT 1)
            ELSE NULL::integer
        END AS servidor_prescricao_futura,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio <= now() AND pre.dthr_fim > now())) > 0 THEN ( SELECT pre.ser_matricula_valida
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio <= now() AND pre.dthr_fim > now()
             LIMIT 1)
            ELSE NULL::integer
        END AS servidor_presc_vigente_valida,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio <= now() AND pre.dthr_fim > now())) > 0 THEN ( SELECT pre.ser_matricula_valida
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio > now()
             LIMIT 1)
            ELSE NULL::integer
        END AS servidor_presc_futura_valida,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio <= now() AND pre.dthr_fim > now())) > 0 THEN ( SELECT pre.dthr_fim
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio <= now() AND pre.dthr_fim > now()
             LIMIT 1)
            ELSE NULL::timestamp without time zone
        END AS dthr_fim_prescricao_vigente,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.epe_prescricoes_enfermagem pre
              WHERE pre.atd_seq = atendimento.seq AND pre.dthr_inicio > now())) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS prescricao_futura,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.agh_caract_unid_funcionais carac
              WHERE unidade_funcional.seq = carac.unf_seq AND carac.caracteristica::text = 'Anamnese/Evolução Eletrônica'::text)) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS habilita_botao_prescricao,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.epe_notificacoes hora_contr
              WHERE hora_contr.atd_seq = atendimento.seq AND hora_contr.tipo::text = 'U'::text)) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS ulcera_pressao,
    ( SELECT pac_contro.medicao
           FROM agh.ecp_horario_controles hora_contr
             JOIN agh.ecp_controle_pacientes pac_contro ON hora_contr.seq = pac_contro.hct_seq
             JOIN agh.ecp_item_controles item_contro ON pac_contro.ice_seq = item_contro.seq
          WHERE hora_contr.atd_seq = atendimento.seq AND item_contro.seq = (( SELECT agh_parametros.vlr_numerico
                   FROM agh.agh_parametros
                  WHERE agh_parametros.nome::text = 'P_AGHU_CONTROLES_UP'::text)) AND hora_contr.data_hora = (( SELECT max(hora_contr_1.data_hora) AS y0_
                   FROM agh.ecp_horario_controles hora_contr_1
                     JOIN agh.ecp_controle_pacientes pac_contro_1 ON hora_contr_1.seq = pac_contro_1.hct_seq
                     JOIN agh.ecp_item_controles item_contro_1 ON pac_contro_1.ice_seq = item_contro_1.seq
                  WHERE hora_contr_1.atd_seq = atendimento.seq AND item_contro_1.seq = (( SELECT agh_parametros.vlr_numerico
                           FROM agh.agh_parametros
                          WHERE agh_parametros.nome::text = 'P_AGHU_CONTROLES_UP'::text))))) AS ulcera_medicao,
        CASE
            WHEN (( SELECT count(evolucao.seq) AS count
               FROM agh.mam_tipo_item_evolucoes tipo_evolucao
                 CROSS JOIN agh.mam_item_evolucoes item_evolucao
                 CROSS JOIN agh.mam_evolucoes evolucao
                 CROSS JOIN agh.mam_registros registro
              WHERE registro.atd_seq = atendimento.seq AND registro.seq = evolucao.rgt_seq AND evolucao.ind_pendente::text = 'V'::text AND evolucao.dthr_valida_mvto IS NULL AND evolucao.dthr_valida = now() AND evolucao.seq = item_evolucao.evo_seq AND item_evolucao.tie_seq = tipo_evolucao.seq AND tipo_evolucao.cag_seq = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_MEDICO'::text)) AND NOT (evolucao.seq IN ( SELECT mamevoluco4_.evo_seq
                       FROM agh.mam_evolucoes mamevoluco4_
                      WHERE mamevoluco4_.evo_seq = evolucao.seq AND (mamevoluco4_.ind_pendente::text = ANY (ARRAY['P'::character varying::text, 'V'::character varying::text, 'E'::character varying::text, 'A'::character varying::text])))))) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS evolucao_pelo_medico,
        CASE
            WHEN (( SELECT count(evolucao.seq) AS count
               FROM agh.mam_tipo_item_evolucoes tipo_evolucao
                 CROSS JOIN agh.mam_item_evolucoes item_evolucao
                 CROSS JOIN agh.mam_evolucoes evolucao
                 CROSS JOIN agh.mam_registros registro
              WHERE registro.atd_seq = atendimento.seq AND registro.seq = evolucao.rgt_seq AND evolucao.ind_pendente::text = 'V'::text AND evolucao.dthr_valida_mvto IS NULL AND evolucao.dthr_valida = now() AND evolucao.seq = item_evolucao.evo_seq AND item_evolucao.tie_seq = tipo_evolucao.seq AND tipo_evolucao.cag_seq = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_OUTROS'::text)) AND NOT (evolucao.seq IN ( SELECT mamevoluco4_.evo_seq
                       FROM agh.mam_evolucoes mamevoluco4_
                      WHERE mamevoluco4_.evo_seq = evolucao.seq AND (mamevoluco4_.ind_pendente::text = ANY (ARRAY['P'::character varying::text, 'V'::character varying::text, 'E'::character varying::text, 'A'::character varying::text])))))) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS evolucao_pelo_outros,
        CASE
            WHEN (( SELECT count(evolucao.seq) AS count
               FROM agh.mam_tipo_item_evolucoes tipo_evolucao
                 CROSS JOIN agh.mam_item_evolucoes item_evolucao
                 CROSS JOIN agh.mam_evolucoes evolucao
                 CROSS JOIN agh.mam_registros registro
              WHERE registro.atd_seq = atendimento.seq AND registro.seq = evolucao.rgt_seq AND evolucao.ind_pendente::text = 'V'::text AND evolucao.dthr_valida_mvto IS NULL AND evolucao.dthr_valida = now() AND evolucao.seq = item_evolucao.evo_seq AND item_evolucao.tie_seq = tipo_evolucao.seq AND tipo_evolucao.cag_seq = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_ENF'::text)) AND tipo_evolucao.seq <> (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_TIPO_ITEM_EVOLUCAO'::text)) AND NOT (evolucao.seq IN ( SELECT mamevoluco4_.evo_seq
                       FROM agh.mam_evolucoes mamevoluco4_
                      WHERE mamevoluco4_.evo_seq = evolucao.seq AND (mamevoluco4_.ind_pendente::text = ANY (ARRAY['P'::character varying::text, 'V'::character varying::text, 'E'::character varying::text, 'A'::character varying::text])))))) > 0 THEN 'S'::text
            ELSE 'N'::text
        END AS evolucao_pelo_enfermeiro,
    ( SELECT max(crt.criado_em) AS max
           FROM agh.mpm_control_prev_altas crt
          WHERE atendimento.origem::text = 'I'::text AND atendimento.seq = crt.atd_seq) AS dt_prev_alta,
    ( SELECT crt.resposta
           FROM agh.mpm_control_prev_altas crt
          WHERE atendimento.origem::text = 'I'::text AND atendimento.seq = crt.atd_seq AND crt.criado_em = (( SELECT max(crt_1.criado_em) AS max
                   FROM agh.mpm_control_prev_altas crt_1
                  WHERE atendimento.origem::text = 'I'::text AND atendimento.seq = crt_1.atd_seq))) AS resp_prev_alta,
        CASE
            WHEN atendimento.atd_seq_mae IS NOT NULL THEN ( SELECT max(crt.criado_em) AS max
               FROM agh.mpm_control_prev_altas crt
              WHERE atendimento.origem::text = 'I'::text AND atendimento.atd_seq_mae = crt.atd_seq)
            ELSE NULL::timestamp without time zone
        END AS dt_prev_alta_atd_mae,
        CASE
            WHEN atendimento.atd_seq_mae IS NOT NULL THEN ( SELECT crt.resposta
               FROM agh.mpm_control_prev_altas crt
              WHERE atendimento.origem::text = 'I'::text AND atendimento.atd_seq_mae = crt.atd_seq AND crt.criado_em = (( SELECT max(crt_1.criado_em) AS max
                       FROM agh.mpm_control_prev_altas crt_1
                      WHERE atendimento.origem::text = 'I'::text AND atendimento.atd_seq_mae = crt_1.atd_seq)))
            ELSE NULL::character varying
        END AS resp_prev_alta_atd_mae,
        CASE
            WHEN atendimento.atd_seq_mae IS NOT NULL THEN ( SELECT mae.origem
               FROM agh.agh_atendimentos mae
              WHERE atendimento.atd_seq_mae = mae.seq)
            ELSE NULL::character varying
        END AS origem_atd_mae
   FROM agh.agh_atendimentos atendimento
     JOIN agh.aip_pacientes paciente ON atendimento.pac_codigo = paciente.codigo
     JOIN agh.agh_unidades_funcionais unidade_funcional ON atendimento.unf_seq = unidade_funcional.seq
  WHERE atendimento.ind_pac_atendimento::text = 'S'::text AND (atendimento.origem::text = ANY (ARRAY['I'::character varying::text, 'H'::character varying::text, 'U'::character varying::text, 'N'::character varying::text]))
  ORDER BY
        CASE
            WHEN atendimento.lto_lto_id IS NOT NULL THEN concat('L:', atendimento.lto_lto_id)
            WHEN atendimento.qrt_numero IS NOT NULL THEN concat('Q:', atendimento.qrt_numero)
            ELSE ( SELECT (((('U:'::text || unf.andar::text) || ' '::text) || unf.ind_ala::text) || ' - '::text) || unf.descricao::text
               FROM agh.agh_unidades_funcionais unf
              WHERE unf.seq = atendimento.unf_seq)
        END;

ALTER TABLE agh.v_lista_prescricao_enfermagem
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_lista_prescricao_enfermagem TO postgres;
GRANT ALL ON TABLE agh.v_lista_prescricao_enfermagem TO acesso_completo;
GRANT SELECT ON TABLE agh.v_lista_prescricao_enfermagem TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_lista_mbc_cirurgias AS 
 SELECT cir.seq,
    cir.data,
    sl_cir.nome AS sala_nome,
    cir.pac_codigo,
    pac.nome AS nome_paciente,
    pac.prontuario,
    esp.seq AS esp_seq,
    esp.sigla AS esp_sigla,
    esp.nome_especialidade AS esp_nome,
    cir.natureza_agend,
    cir.dthr_fim_cirg,
    cir.origem_pac_cirg,
    cir.csp_cnv_codigo,
    cir.csp_seq,
    cir.dthr_inicio_cirg,
    ( SELECT agenda.lado_cirurgia
           FROM agh.mbc_agendas agenda
          WHERE agenda.seq = cir.agd_seq) AS lado_cirurgia,
    cir.situacao,
    cir.atd_seq,
    cir.qes_mtc_seq,
    cir.qes_seqp,
    cir.vvc_qes_mtc_seq,
    cir.vvc_qes_seqp,
    cir.vvc_seqp,
    cir.mtc_seq,
    cir.complemento_canc,
    cir.criado_em,
    cir.ind_digt_nota_sala,
    cir.ser_matricula,
    cir.ser_vin_codigo,
    cir.unf_seq,
    cir.sci_seqp,
    cir.sci_unf_seq,
    cir.dthr_inicio_ordem,
    cir.dthr_entrada_sr,
    cir.dthr_saida_sr,
    ( SELECT tmp.descricao
           FROM ( SELECT ppc_2.crg_seq,
                    pci_2.descricao
                   FROM agh.mbc_procedimento_cirurgicos pci_2,
                    agh.mbc_proc_esp_por_cirurgias ppc_2
                  WHERE ppc_2.situacao::text = 'A'::text AND pci_2.seq = ppc_2.epr_pci_seq AND ppc_2.ind_principal::text = 'S'::text
                  ORDER BY
                        CASE
                            WHEN ppc_2.ind_resp_proc::text = 'DESC'::text THEN 1
                            ELSE
                            CASE
                                WHEN ppc_2.ind_resp_proc::text = 'NOTA'::text THEN 2
                                ELSE
                                CASE
                                    WHEN ppc_2.ind_resp_proc::text = 'AGND'::text THEN 3
                                    ELSE 0
                                END
                            END
                        END) tmp
          WHERE tmp.crg_seq = cir.seq
         LIMIT 1) AS proc_descr,
    ( SELECT min(
                CASE COALESCE(pes_fis.nome_usual, ''::character varying)
                    WHEN ''::text THEN pes_fis.nome
                    ELSE pes_fis.nome_usual
                END::text) AS min
           FROM agh.mbc_prof_cirurgias prof_cir,
            agh.rap_servidores serv_puc,
            agh.rap_pessoas_fisicas pes_fis
          WHERE prof_cir.crg_seq = cir.seq AND prof_cir.puc_ser_vin_codigo = serv_puc.vin_codigo AND prof_cir.puc_ser_matricula = serv_puc.matricula AND serv_puc.pes_codigo = pes_fis.codigo AND prof_cir.ind_responsavel::text = 'S'::text
          GROUP BY prof_cir.crg_seq) AS nome_equipe,
    ( SELECT
                CASE
                    WHEN min(proj_pac.pjq_seq) > 0 THEN 'S'::text
                    ELSE 'N'::text
                END AS "case"
           FROM agh.ael_projeto_pacientes proj_pac
          WHERE cir.pac_codigo = proj_pac.pac_codigo AND (proj_pac.dt_fim IS NULL OR date_trunc('DAY'::text, proj_pac.dt_fim) >= now()) AND (proj_pac.jex_seq IS NULL OR (EXISTS ( SELECT 'X'
                   FROM agh.ael_justificativa_exclusoes just_exclu
                  WHERE just_exclu.seq = proj_pac.jex_seq AND just_exclu.ind_mostra_telas::text = 'S'::text)))) AS projeto,
    ( SELECT count(*) AS count
           FROM agh.mbc_descricao_cirurgicas descr_cir
          WHERE cir.seq = descr_cir.crg_seq AND descr_cir.situacao::text <> 'CON'::text) AS temp_desc_ciru_pendente,
    ( SELECT count(*) AS count
           FROM agh.mbc_descricao_cirurgicas descr_cir
          WHERE cir.seq = descr_cir.crg_seq) AS temp_desc_ciru,
    ( SELECT count(*) AS count
           FROM agh.pdt_descricoes descr_pdt
          WHERE cir.seq = descr_pdt.crg_seq AND descr_pdt.situacao::text <> 'DEF'::text) AS temp_desc_pdt_pendente,
    ( SELECT count(*) AS count
           FROM agh.pdt_descricoes descr_pdt
          WHERE cir.seq = descr_pdt.crg_seq) AS temp_desc_pdt_simples,
    ( SELECT min(fichas2.seq) AS min
           FROM agh.mbc_ficha_anestesias fichas2
          WHERE fichas2.crg_seq = cir.seq AND fichas2.dthr_mvto IS NULL AND (fichas2.pendente::text = ANY (ARRAY['R'::character varying::text, 'P'::character varying::text, 'V'::character varying::text]))) AS ficha_seq,
    ( SELECT fichas.pendente
           FROM agh.mbc_ficha_anestesias fichas
          WHERE cir.seq = fichas.crg_seq AND fichas.seq = (( SELECT min(fichas2.seq) AS min
                   FROM agh.mbc_ficha_anestesias fichas2
                  WHERE fichas2.crg_seq = fichas.crg_seq AND fichas2.dthr_mvto IS NULL AND (fichas2.pendente::text = ANY (ARRAY['R'::character varying::text, 'P'::character varying::text, 'V'::character varying::text]))))) AS ficha_pendente,
    ( SELECT COALESCE(count(*), 0::bigint) AS "coalesce"
           FROM agh.agh_versoes_documentos ver_doc,
            agh.agh_documentos doc
          WHERE ver_doc.dok_seq = doc.seq AND cir.seq = doc.crg_seq AND ver_doc.situacao::text = 'P'::text) AS tem_certificacao,
    ( SELECT COALESCE(count(*), 0::bigint) AS "coalesce"
           FROM agh.mci_notificacao_gmr gmr
          WHERE gmr.ind_notificacao_ativa::text = 'S'::text AND gmr.pac_codigo = cir.pac_codigo) AS tem_germe_multi,
    ( SELECT max(phi.seq) AS max
           FROM agh.mbc_proc_esp_por_cirurgias ppc,
            agh.fat_proced_hosp_internos phi,
            agh.v_fat_associacao_procedimentos v_fat_assoc_proc,
            agh.fat_conv_saude_planos conv_plano,
            agh.fat_convenios_saude conv_saude,
            agh.fat_caract_item_proc_hosp item_caract_item_proc
          WHERE ppc.epr_pci_seq = phi.pci_seq AND ppc.situacao::text = 'A'::text AND ppc.ind_resp_proc::text =
                CASE
                    WHEN cir.situacao::text = 'RZA'::text AND cir.ind_digt_nota_sala::text = 'S'::text THEN 'NOTA'::text
                    ELSE 'AGND'::text
                END AND ppc.crg_seq = cir.seq AND phi.ind_situacao::text = 'A'::text AND conv_saude.codigo = cir.csp_cnv_codigo AND conv_saude.grupo_convenio::text = 'S'::text AND conv_plano.cnv_codigo = conv_saude.codigo AND conv_plano.seq = cir.csp_seq AND v_fat_assoc_proc.phi_seq = phi.seq AND v_fat_assoc_proc.cpg_cph_csp_cnv_codigo = conv_saude.codigo AND v_fat_assoc_proc.cpg_cph_csp_seq = conv_plano.seq AND v_fat_assoc_proc.cpg_grc_seq = (( SELECT param.vlr_numerico
                   FROM agh.agh_parametros param
                  WHERE param.nome::text = 'P_TIPO_GRUPO_CONTA_SUS'::text)) AND item_caract_item_proc.iph_pho_seq = v_fat_assoc_proc.iph_pho_seq AND item_caract_item_proc.iph_seq = v_fat_assoc_proc.iph_seq AND item_caract_item_proc.tct_seq = (( SELECT tct.seq
                   FROM agh.fat_tipo_caract_itens tct
                  WHERE tct.caracteristica::text = ((( SELECT param.vlr_texto
                           FROM agh.agh_parametros param
                          WHERE param.nome::text = 'P_AGHU_CARACT_CERIH'::text))::text)))) AS exige_cerih,
    pac.lto_lto_id,
    pac.unf_seq AS pac_unf_seq,
    pac.dt_ult_internacao,
    pac.dt_ult_alta,
    ( SELECT qrt.descricao
           FROM agh.ain_quartos qrt
          WHERE qrt.numero = pac.qrt_numero) AS qrt_descricao,
    ( SELECT count(*) AS count
           FROM agh.aip_pacientes pac_1
          WHERE pac_1.codigo = cir.pac_codigo AND pac_1.dt_ult_internacao IS NOT NULL AND (pac_1.dt_ult_alta IS NULL OR pac_1.dt_ult_alta IS NOT NULL AND pac_1.dt_ult_alta < pac_1.dt_ult_internacao)) AS tem_pac_internacao,
    ( SELECT unf.ind_ala
           FROM agh.agh_unidades_funcionais unf
          WHERE unf.seq = pac.unf_seq) AS pac_unf_ala,
    ( SELECT unf.andar
           FROM agh.agh_unidades_funcionais unf
          WHERE unf.seq = pac.unf_seq) AS pac_unf_andar
   FROM agh.mbc_cirurgias cir,
    agh.mbc_sala_cirurgicas sl_cir,
    agh.aip_pacientes pac,
    agh.agh_especialidades esp
  WHERE cir.sci_unf_seq = sl_cir.unf_seq AND cir.sci_seqp = sl_cir.seqp AND cir.pac_codigo = pac.codigo AND cir.esp_seq = esp.seq;

ALTER TABLE agh.v_lista_mbc_cirurgias
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_lista_mbc_cirurgias TO postgres;
GRANT ALL ON TABLE agh.v_lista_mbc_cirurgias TO acesso_completo;
GRANT SELECT ON TABLE agh.v_lista_mbc_cirurgias TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_ain_pesq_leitos AS 
 SELECT lto.lto_id AS lto_lto_id,
    lto.int_seq,
    lto.tml_codigo,
        CASE
            WHEN lto.ind_situacao::text = 'I'::text THEN 'IN'::character varying
            ELSE tml.grupo_mvto_leito
        END AS grupo_mvto_leito,
    qrt.acm_seq,
    qrt.clc_codigo,
    qrt.ind_exclusiv_infeccao,
    "int".csp_cnv_codigo AS cnv_codigo,
    cnv.grupo_convenio,
        CASE
            WHEN cnv.grupo_convenio::text = 'C'::text THEN 'CP'::text
            WHEN cnv.grupo_convenio::text = 'P'::text THEN 'CP'::text
            ELSE 'S'::text
        END AS grupo_conv_part,
    unf.andar,
    unf.ind_ala,
    unf.seq AS unf_seq,
        CASE
            WHEN tml.grupo_mvto_leito::text = 'L'::text THEN 'LIVRE'::text
            ELSE NULL::text
        END AS livre,
    exl.justificativa,
        CASE
            WHEN tml.grupo_mvto_leito::text = 'O'::text THEN COALESCE(atd.dthr_inicio, exl.dthr_lancamento)
            ELSE exl.dthr_lancamento
        END AS dthr_lancamento,
    exl.ser_vin_codigo,
    exl.ser_matricula,
    qrt.sexo_determinante,
    COALESCE(qrt.sexo_ocupacao, qrt.sexo_determinante) AS sexo_ocupacao,
    "int".pac_codigo,
    exl.criado_em,
    tcl.descricao AS tcl_descricao
   FROM agh.ain_leitos lto
     JOIN agh.ain_tipos_mvto_leito tml ON tml.codigo = lto.tml_codigo
     JOIN agh.ain_quartos qrt ON qrt.numero = lto.qrt_numero
     JOIN agh.ain_extrato_leitos exl ON exl.lto_lto_id::text = lto.lto_id::text
     JOIN agh.agh_unidades_funcionais unf ON unf.seq = lto.unf_seq
     LEFT JOIN agh.agh_atendimentos atd ON atd.int_seq = lto.int_seq
     LEFT JOIN agh.ain_caracteristicas_leito clt ON clt.lto_lto_id::text = lto.lto_id::text AND clt.ind_caracteristica_principal::text = 'S'::text
     LEFT JOIN agh.ain_internacoes "int" ON "int".seq = lto.int_seq
     LEFT JOIN agh.ain_tipos_caracteristica_leito tcl ON tcl.seq = clt.tcl_seq
     LEFT JOIN agh.fat_convenios_saude cnv ON cnv.codigo = "int".csp_cnv_codigo
  WHERE tml.grupo_mvto_leito::text <> 'R'::text AND exl.lto_lto_id::text = lto.lto_id::text AND exl.criado_em = (( SELECT max(exl1.criado_em) AS max
           FROM agh.ain_extrato_leitos exl1
          WHERE exl1.lto_lto_id::text = lto.lto_id::text))
UNION
 SELECT lto.lto_id AS lto_lto_id,
    lto.int_seq,
    lto.tml_codigo,
    tml.grupo_mvto_leito,
    qrt.acm_seq,
    qrt.clc_codigo,
    qrt.ind_exclusiv_infeccao,
    "int".csp_cnv_codigo AS cnv_codigo,
    cnv.grupo_convenio,
        CASE
            WHEN cnv.grupo_convenio::text = 'C'::text THEN 'CP'::text
            WHEN cnv.grupo_convenio::text = 'P'::text THEN 'CP'::text
            ELSE 'S'::text
        END AS grupo_conv_part,
    unf.andar,
    unf.ind_ala,
    unf.seq AS unf_seq,
    NULL::text AS livre,
        CASE
            WHEN pac.nome::text = NULL::text THEN exl.justificativa
            ELSE pac.nome
        END AS justificativa,
    exl.dthr_lancamento,
    exl.ser_vin_codigo,
    exl.ser_matricula,
    qrt.sexo_determinante,
    COALESCE(qrt.sexo_ocupacao, qrt.sexo_determinante) AS sexo_ocupacao,
    "int".pac_codigo,
    exl.criado_em,
    tcl.descricao AS tcl_descricao
   FROM agh.ain_leitos lto
     JOIN agh.ain_tipos_mvto_leito tml ON tml.codigo = lto.tml_codigo
     JOIN agh.ain_quartos qrt ON qrt.numero = lto.qrt_numero
     JOIN agh.ain_extrato_leitos exl ON exl.lto_lto_id::text = lto.lto_id::text
     JOIN agh.agh_unidades_funcionais unf ON unf.seq = lto.unf_seq
     LEFT JOIN agh.ain_solic_transf_pacientes stp ON stp.lto_lto_id::text = exl.lto_lto_id::text AND stp.int_seq = exl.int_seq AND stp.ind_sit_solic_leito::text = 'A'::text
     LEFT JOIN agh.ain_internacoes "int" ON "int".seq = stp.int_seq
     LEFT JOIN agh.aip_pacientes pac ON pac.codigo = "int".pac_codigo
     LEFT JOIN agh.fat_convenios_saude cnv ON cnv.codigo = "int".csp_cnv_codigo
     LEFT JOIN agh.ain_caracteristicas_leito clt ON clt.lto_lto_id::text = lto.lto_id::text AND clt.ind_caracteristica_principal::text = 'S'::text
     LEFT JOIN agh.ain_tipos_caracteristica_leito tcl ON tcl.seq = clt.tcl_seq
  WHERE exl.criado_em = (( SELECT max(exl1.criado_em) AS max
           FROM agh.ain_extrato_leitos exl1
          WHERE exl1.lto_lto_id::text = lto.lto_id::text)) AND tml.grupo_mvto_leito::text = 'R'::text;

ALTER TABLE agh.v_ain_pesq_leitos
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_pesq_leitos TO postgres;
GRANT ALL ON TABLE agh.v_ain_pesq_leitos TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_pesq_leitos TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_ain_mvto_internacao AS 
 SELECT mvi.int_seq AS mvi_int_seq,
    mvi.tmi_seq AS mvi_tmi_seq,
    mvi.ser_matricula AS mvi_ser_matricula_professor,
    mvi.ser_vin_codigo AS mvi_ser_vin_codigo_professor,
    mvi.esp_seq AS mvi_esp_seq,
    mvi.lto_lto_id AS mvi_lto_lto_id,
    mvi.qrt_numero AS mvi_qrt_numero,
    mvi.unf_seq AS mvi_unf_seq,
    COALESCE(mvi.lto_lto_id, COALESCE(to_char(mvi.qrt_numero::double precision, '0'::text), (lpad(to_char(unf.andar::double precision, '0'::text), 2, '0'::text) || ' '::text) || unf.ind_ala::text)::character varying) AS mvi_localizacao_paciente,
        CASE
            WHEN mvi.lto_lto_id::text = NULL::text THEN
            CASE
                WHEN mvi.qrt_numero = NULL::smallint THEN 'U'::text
                ELSE 'Q'::text
            END
            ELSE 'L'::text
        END AS mvi_ind_local_paciente,
    mvi.ser_matricula_gerado AS mvi_ser_matricula_gerado,
    mvi.ser_vin_codigo_gerado AS mvi_ser_vin_codigo_gerado,
    "int".seq AS int_seq,
    "int".pac_codigo AS int_pac_codigo,
    "int".esp_seq AS int_esp_seq,
    "int".ser_matricula_professor AS int_ser_matricula_professor,
    "int".ser_vin_codigo_professor AS int_ser_vin_codigo_professor,
    "int".dthr_internacao AS int_dthr_internacao,
    "int".tci_seq AS int_tci_seq,
    "int".csp_cnv_codigo AS int_csp_cnv_codigo,
    "int".csp_seq AS int_csp_seq,
    "int".oev_seq AS int_oev_seq,
    "int".ind_dif_classe AS int_ind_dif_classe,
    "int".ind_local_paciente AS int_ind_local_paciente,
    "int".lto_lto_id AS int_lto_lto_id,
    "int".qrt_numero AS int_qrt_numero,
    "int".unf_seq AS int_unf_seq,
    "int".tam_codigo AS int_tam_codigo,
    "int".atu_seq AS int_atu_seq,
    "int".iph_seq AS int_iph_seq,
    "int".iph_pho_seq AS int_iph_pho_seq,
    "int".justificativa_alt_del AS int_justificativa_alt_del,
    "int".csp_cnv_codigo AS cth_csp_cnv_codigo,
    "int".csp_seq AS cth_csp_seq,
    "int".dthr_internacao AS cth_dt_int_administrativa,
    mvi.criado_em AS mvi_criado_em,
    atd.dthr_inicio AS atd_dthr_inicio,
    mvi.dthr_lancamento AS mvi_dthr_lancamento
   FROM agh.agh_atendimentos atd,
    agh.ain_movimentos_internacao mvi,
    agh.agh_unidades_funcionais unf,
    agh.ain_internacoes "int"
  WHERE mvi.int_seq = "int".seq AND atd.int_seq = "int".seq AND unf.seq = mvi.unf_seq;

ALTER TABLE agh.v_ain_mvto_internacao
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_mvto_internacao TO postgres;
GRANT ALL ON TABLE agh.v_ain_mvto_internacao TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_mvto_internacao TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_ain_leitos_limpeza AS 
 SELECT lto.lto_id,
    (((unf.andar::text || ' '::text) || unf.ind_ala::text) || ' - '::text) || unf.descricao::text AS andar_ala_descricao,
    lto.int_seq
   FROM agh.ain_leitos lto,
    agh.agh_unidades_funcionais unf,
    agh.ain_tipos_mvto_leito tml
  WHERE tml.grupo_mvto_leito::text = 'BL'::text AND tml.codigo = lto.tml_codigo AND unf.seq = lto.unf_seq
UNION
 SELECT lto.lto_id,
    (((unf.andar::text || ' '::text) || unf.ind_ala::text) || ' - '::text) || unf.descricao::text AS andar_ala_descricao,
    "int".seq AS int_seq
   FROM agh.ain_internacoes "int",
    agh.ain_leitos lto,
    agh.agh_unidades_funcionais unf,
    agh.ain_tipos_mvto_leito tml
  WHERE tml.grupo_mvto_leito::text = 'O'::text AND lto.ind_bloq_leito_limpeza::text = 'S'::text AND lto.tml_codigo = tml.codigo AND unf.seq = lto.unf_seq AND "int".seq = lto.int_seq AND "int".dthr_alta_medica IS NOT NULL AND "int".dt_saida_paciente IS NULL;

ALTER TABLE agh.v_ain_leitos_limpeza
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_leitos_limpeza TO postgres;
GRANT ALL ON TABLE agh.v_ain_leitos_limpeza TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_leitos_limpeza TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_ain_disp_vagas AS 
 SELECT vuf.seq AS vuf_unf_seq,
    vuf.andar AS vuf_andar,
    vuf.ind_ala AS vuf_ala,
    (((vuf.andar::text || ' '::text) || vuf.ind_ala::text) || ' - '::text) || vuf.descricao::text AS vuf_andar_ala_descricao,
    vuf.clc_codigo,
    vuf.capac_internacao AS vci_capac_internacao,
    vli.leitos_indisp,
    vti.total_int,
    vlr.leitos_reservados
   FROM agh.agh_unidades_funcionais vuf
     LEFT JOIN agh.v_ain_leitos_indisp vli ON vli.vuf_unf_seq = vuf.seq
     LEFT JOIN agh.v_ain_total_int vti ON vti.vpt_unf_seq = vuf.seq
     LEFT JOIN agh.v_ain_leitos_reserva vlr ON vlr.vuf_unf_seq = vuf.seq
  WHERE vuf.ind_unid_internacao::text = 'S'::text OR
        CASE
            WHEN (( SELECT agh_caract_unid_funcionais.unf_seq
               FROM agh.agh_caract_unid_funcionais
              WHERE agh_caract_unid_funcionais.unf_seq = vuf.seq AND agh_caract_unid_funcionais.caracteristica::text = 'Unid Internacao'::text)) <> NULL::smallint THEN 'S'::text
            ELSE 'N'::text
        END = 'S'::text;

ALTER TABLE agh.v_ain_disp_vagas
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ain_disp_vagas TO postgres;
GRANT ALL ON TABLE agh.v_ain_disp_vagas TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_disp_vagas TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_agh_unid_funcional AS 
 SELECT unf.seq,
    unf.andar,
    unf.ind_ala AS ala,
    unf.hrio_validade_pme,
    (((lpad(unf.andar::text, 2, '0'::text) || ' '::text) || unf.ind_ala::text) || ' - '::text) || unf.descricao::text AS andar_ala_descricao,
    unf.ind_sit_unid_func,
    unf.ind_perm_paciente_extra,
    unf.dthr_conf_censo,
    unf.ind_verf_escala_prof_int,
    unf.ind_bloq_lto_isolamento,
    unf.ind_unid_emergencia,
    unf.capac_internacao,
    unf.clc_codigo,
    unf.tuf_seq,
    unf.unf_seq AS vuf_seq,
    unf.ind_cons_clin,
    unf.descricao AS unf_descricao,
    unf.ind_unid_hosp_dia,
    unf.ind_unid_cti,
    unf.ind_unid_internacao,
    unf.nro_vias_pme,
    unf.nro_vias_pen
   FROM agh.agh_unidades_funcionais unf;

ALTER TABLE agh.v_agh_unid_funcional
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_agh_unid_funcional TO postgres;
GRANT ALL ON TABLE agh.v_agh_unid_funcional TO acesso_completo;
GRANT SELECT ON TABLE agh.v_agh_unid_funcional TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_ael_exames_solicitacao_atd_aghu AS 
 SELECT soe.seq AS soe_seq,
    soe.atd_seq,
    soe.criado_em,
    man.descricao AS descricao_material,
    eis.dthr_evento,
    (exa.descricao_usual::text || ' /  '::text) || man.descricao::text AS nome_exame_material,
    pes.nome AS soe_servidor_nome,
    cnv.descricao AS cnv_descricao,
    atd.lto_lto_id,
    atd.qrt_numero,
    qrt.descricao AS qrt_descricao,
    atd.unf_seq,
    ise.dthr_programada,
    cnv.grupo_convenio,
    aie.origem_mapa,
    ise.ufe_ema_exa_sigla,
    ise.sit_codigo,
    ise.ufe_unf_seq,
    ( SELECT
                CASE
                    WHEN soetemp.atd_seq > 0 THEN pac_atd.nome
                    WHEN pac_atv.nome IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pac_atv.nome::text)::character varying
                        ELSE pac_atv.nome
                    END
                    WHEN atv.nome_paciente IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || atv.nome_paciente::text)::character varying
                        ELSE atv.nome_paciente
                    END
                    WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pjq.nome::text)::character varying
                    WHEN ccq.seq IS NOT NULL THEN ccq.material
                    WHEN lae.seq IS NOT NULL THEN lae.nome
                    WHEN ddv.seq IS NOT NULL THEN ddv.nome
                    WHEN cad.seq IS NOT NULL THEN cad.nome
                    ELSE NULL::character varying
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.aip_pacientes pac_atd ON pac_atd.codigo = atd_1.pac_codigo
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac_atv ON pac_atv.codigo = atv.pac_codigo
             LEFT JOIN agh.ael_projeto_pesquisas pjq ON pjq.seq = atv.pjq_seq
             LEFT JOIN agh.ael_cad_ctrl_qualidades ccq ON ccq.seq = atv.ccq_seq
             LEFT JOIN agh.ael_laboratorio_externos lae ON lae.seq = atv.lae_seq
             LEFT JOIN agh.ael_dados_cadaveres ddv ON ddv.seq = atv.ddv_seq
             LEFT JOIN agh.abs_candidatos_doadores cad ON cad.seq = atv.cad_seq
          WHERE soetemp.seq = soe.seq) AS nome_paciente,
    ( SELECT COALESCE(pac.prontuario, atv.prontuario) AS "coalesce"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac ON pac.codigo = COALESCE(atd_1.pac_codigo, atv.pac_codigo)
          WHERE soetemp.seq = soe.seq) AS prontuario,
    ( SELECT
                CASE
                    WHEN atd_1.origem::text = 'N'::text THEN 'I'::character varying
                    WHEN atd_1.origem IS NULL THEN 'D'::character varying
                    ELSE atd_1.origem
                END AS origem
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
          WHERE soetemp.seq = soe.seq) AS origem,
    ( SELECT
                CASE
                    WHEN atd_1.lto_lto_id IS NOT NULL THEN 'L:'::text || atd_1.lto_lto_id::text
                    WHEN qrt_1.descricao IS NOT NULL THEN 'Q:'::text || qrt_1.descricao::text
                    WHEN atd_1.unf_seq IS NOT NULL THEN (((('U:'::text || auf.andar::text) || ' '::text) || auf.ind_ala::text) || ' - '::text) || auf.descricao::text
                    ELSE NULL::text
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ain_quartos qrt_1 ON atd_1.qrt_numero = qrt_1.numero
             LEFT JOIN agh.agh_unidades_funcionais auf ON auf.seq = atd_1.unf_seq
          WHERE soetemp.seq = soe.seq) AS localizacao
   FROM agh.ael_item_solicitacao_exames ise
     JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
     JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
     JOIN agh.ael_extrato_item_solics eis ON eis.ise_soe_seq = ise.soe_seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = ise.sit_codigo::text
     JOIN agh.ael_solicitacao_exames soe ON soe.seq = ise.soe_seq
     JOIN agh.fat_convenios_saude cnv ON cnv.codigo = soe.csp_cnv_codigo
     JOIN agh.rap_servidores ser ON ser.matricula = soe.ser_matricula_eh_responsabilid AND ser.vin_codigo = soe.ser_vin_codigo_eh_responsabili
     JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = ser.pes_codigo
     LEFT JOIN agh.agh_atendimentos atd ON atd.seq = soe.atd_seq
     LEFT JOIN agh.ain_quartos qrt ON atd.qrt_numero = qrt.numero
     LEFT JOIN agh.ael_amostra_item_exames aie ON aie.ise_soe_seq = ise.soe_seq AND aie.ise_seqp = ise.seqp
  WHERE (eis.criado_em IN ( SELECT max(eis1.criado_em) AS max
           FROM agh.ael_extrato_item_solics eis1
          WHERE eis1.ise_soe_seq = eis.ise_soe_seq AND eis1.ise_seqp = eis.ise_seqp))
  GROUP BY soe.seq, soe.atd_seq, soe.criado_em, man.descricao, eis.dthr_evento, (exa.descricao_usual::text || ' /  '::text) || man.descricao::text, pes.nome, cnv.descricao, atd.lto_lto_id, atd.qrt_numero, qrt.descricao, atd.unf_seq, ise.dthr_programada, cnv.grupo_convenio, aie.origem_mapa, ise.ufe_ema_exa_sigla, ise.sit_codigo, ise.ufe_unf_seq, ( SELECT
                CASE
                    WHEN soetemp.atd_seq > 0 THEN pac_atd.nome
                    WHEN pac_atv.nome IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pac_atv.nome::text)::character varying
                        ELSE pac_atv.nome
                    END
                    WHEN atv.nome_paciente IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || atv.nome_paciente::text)::character varying
                        ELSE atv.nome_paciente
                    END
                    WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pjq.nome::text)::character varying
                    WHEN ccq.seq IS NOT NULL THEN ccq.material
                    WHEN lae.seq IS NOT NULL THEN lae.nome
                    WHEN ddv.seq IS NOT NULL THEN ddv.nome
                    WHEN cad.seq IS NOT NULL THEN cad.nome
                    ELSE NULL::character varying
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.aip_pacientes pac_atd ON pac_atd.codigo = atd_1.pac_codigo
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac_atv ON pac_atv.codigo = atv.pac_codigo
             LEFT JOIN agh.ael_projeto_pesquisas pjq ON pjq.seq = atv.pjq_seq
             LEFT JOIN agh.ael_cad_ctrl_qualidades ccq ON ccq.seq = atv.ccq_seq
             LEFT JOIN agh.ael_laboratorio_externos lae ON lae.seq = atv.lae_seq
             LEFT JOIN agh.ael_dados_cadaveres ddv ON ddv.seq = atv.ddv_seq
             LEFT JOIN agh.abs_candidatos_doadores cad ON cad.seq = atv.cad_seq
          WHERE soetemp.seq = soe.seq), ( SELECT COALESCE(pac.prontuario, atv.prontuario) AS "coalesce"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac ON pac.codigo = COALESCE(atd_1.pac_codigo, atv.pac_codigo)
          WHERE soetemp.seq = soe.seq), ( SELECT
                CASE
                    WHEN atd_1.origem::text = 'N'::text THEN 'I'::character varying
                    WHEN atd_1.origem IS NULL THEN 'D'::character varying
                    ELSE atd_1.origem
                END AS origem
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
          WHERE soetemp.seq = soe.seq), ( SELECT
                CASE
                    WHEN atd_1.lto_lto_id IS NOT NULL THEN 'L:'::text || atd_1.lto_lto_id::text
                    WHEN qrt_1.descricao IS NOT NULL THEN 'Q:'::text || qrt_1.descricao::text
                    WHEN atd_1.unf_seq IS NOT NULL THEN (((('U:'::text || auf.andar::text) || ' '::text) || auf.ind_ala::text) || ' - '::text) || auf.descricao::text
                    ELSE NULL::text
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ain_quartos qrt_1 ON atd_1.qrt_numero = qrt_1.numero
             LEFT JOIN agh.agh_unidades_funcionais auf ON auf.seq = atd_1.unf_seq
          WHERE soetemp.seq = soe.seq);

ALTER TABLE agh.v_ael_exames_solicitacao_atd_aghu
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ael_exames_solicitacao_atd_aghu TO postgres;
GRANT ALL ON TABLE agh.v_ael_exames_solicitacao_atd_aghu TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ael_exames_solicitacao_atd_aghu TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_ael_exames_solicitacao_atd AS 
 SELECT soe.seq AS soe_seq,
    soe.atd_seq,
    soe.criado_em,
    man.descricao AS descricao_material,
    eis.dthr_evento,
    (exa.descricao_usual::text || ' /  '::text) || man.descricao::text AS nome_exame_material,
    pes.nome AS soe_servidor_nome,
    cnv.descricao AS cnv_descricao,
    atd.lto_lto_id,
    atd.qrt_numero,
    atd.unf_seq,
    ise.dthr_programada,
    cnv.grupo_convenio,
    aie.origem_mapa,
    ise.ufe_ema_exa_sigla,
    ise.sit_codigo,
    ise.ufe_unf_seq,
    ( SELECT
                CASE
                    WHEN soetemp.atd_seq > 0 THEN pac_atd.nome
                    WHEN pac_atv.nome IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pac_atv.nome::text)::character varying
                        ELSE pac_atv.nome
                    END
                    WHEN atv.nome_paciente IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || atv.nome_paciente::text)::character varying
                        ELSE atv.nome_paciente
                    END
                    WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pjq.nome::text)::character varying
                    WHEN ccq.seq IS NOT NULL THEN ccq.material
                    WHEN lae.seq IS NOT NULL THEN lae.nome
                    WHEN ddv.seq IS NOT NULL THEN ddv.nome
                    WHEN cad.seq IS NOT NULL THEN cad.nome
                    ELSE NULL::character varying
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.aip_pacientes pac_atd ON pac_atd.codigo = atd_1.pac_codigo
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac_atv ON pac_atv.codigo = atv.pac_codigo
             LEFT JOIN agh.ael_projeto_pesquisas pjq ON pjq.seq = atv.pjq_seq
             LEFT JOIN agh.ael_cad_ctrl_qualidades ccq ON ccq.seq = atv.ccq_seq
             LEFT JOIN agh.ael_laboratorio_externos lae ON lae.seq = atv.lae_seq
             LEFT JOIN agh.ael_dados_cadaveres ddv ON ddv.seq = atv.ddv_seq
             LEFT JOIN agh.abs_candidatos_doadores cad ON cad.seq = atv.cad_seq
          WHERE soetemp.seq = soe.seq) AS nome_paciente,
    ( SELECT COALESCE(pac.prontuario, atv.prontuario) AS "coalesce"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac ON pac.codigo = COALESCE(atd_1.pac_codigo, atv.pac_codigo)
          WHERE soetemp.seq = soe.seq) AS prontuario,
    ( SELECT
                CASE
                    WHEN atd_1.origem::text = 'N'::text THEN 'I'::character varying
                    WHEN atd_1.origem IS NULL THEN 'D'::character varying
                    ELSE atd_1.origem
                END AS origem
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
          WHERE soetemp.seq = soe.seq) AS origem,
    ( SELECT
                CASE
                    WHEN atd_1.lto_lto_id IS NOT NULL THEN 'L:'::text || atd_1.lto_lto_id::text
                    WHEN atd_1.qrt_numero IS NOT NULL THEN 'Q:'::text || atd_1.qrt_numero
                    WHEN atd_1.unf_seq IS NOT NULL THEN (((('U:'::text || auf.andar::text) || ' '::text) || auf.ind_ala::text) || ' - '::text) || auf.descricao::text
                    ELSE NULL::text
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.agh_unidades_funcionais auf ON auf.seq = atd_1.unf_seq
          WHERE soetemp.seq = soe.seq) AS localizacao
   FROM agh.ael_item_solicitacao_exames ise
     JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
     JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
     JOIN agh.ael_extrato_item_solics eis ON eis.ise_soe_seq = ise.soe_seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = ise.sit_codigo::text
     JOIN agh.ael_solicitacao_exames soe ON soe.seq = ise.soe_seq
     JOIN agh.fat_convenios_saude cnv ON cnv.codigo = soe.csp_cnv_codigo
     JOIN agh.rap_servidores ser ON ser.matricula = soe.ser_matricula_eh_responsabilid AND ser.vin_codigo = soe.ser_vin_codigo_eh_responsabili
     JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = ser.pes_codigo
     LEFT JOIN agh.agh_atendimentos atd ON atd.seq = soe.atd_seq
     LEFT JOIN agh.ael_amostra_item_exames aie ON aie.ise_soe_seq = ise.soe_seq AND aie.ise_seqp = ise.seqp
  WHERE (eis.criado_em IN ( SELECT max(eis1.criado_em) AS max
           FROM agh.ael_extrato_item_solics eis1
          WHERE eis1.ise_soe_seq = eis.ise_soe_seq AND eis1.ise_seqp = eis.ise_seqp))
  GROUP BY soe.seq, soe.atd_seq, soe.criado_em, man.descricao, eis.dthr_evento, (exa.descricao_usual::text || ' /  '::text) || man.descricao::text, pes.nome, cnv.descricao, atd.lto_lto_id, atd.qrt_numero, atd.unf_seq, ise.dthr_programada, cnv.grupo_convenio, aie.origem_mapa, ise.ufe_ema_exa_sigla, ise.sit_codigo, ise.ufe_unf_seq, ( SELECT
                CASE
                    WHEN soetemp.atd_seq > 0 THEN pac_atd.nome
                    WHEN pac_atv.nome IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pac_atv.nome::text)::character varying
                        ELSE pac_atv.nome
                    END
                    WHEN atv.nome_paciente IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || atv.nome_paciente::text)::character varying
                        ELSE atv.nome_paciente
                    END
                    WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pjq.nome::text)::character varying
                    WHEN ccq.seq IS NOT NULL THEN ccq.material
                    WHEN lae.seq IS NOT NULL THEN lae.nome
                    WHEN ddv.seq IS NOT NULL THEN ddv.nome
                    WHEN cad.seq IS NOT NULL THEN cad.nome
                    ELSE NULL::character varying
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.aip_pacientes pac_atd ON pac_atd.codigo = atd_1.pac_codigo
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac_atv ON pac_atv.codigo = atv.pac_codigo
             LEFT JOIN agh.ael_projeto_pesquisas pjq ON pjq.seq = atv.pjq_seq
             LEFT JOIN agh.ael_cad_ctrl_qualidades ccq ON ccq.seq = atv.ccq_seq
             LEFT JOIN agh.ael_laboratorio_externos lae ON lae.seq = atv.lae_seq
             LEFT JOIN agh.ael_dados_cadaveres ddv ON ddv.seq = atv.ddv_seq
             LEFT JOIN agh.abs_candidatos_doadores cad ON cad.seq = atv.cad_seq
          WHERE soetemp.seq = soe.seq), ( SELECT COALESCE(pac.prontuario, atv.prontuario) AS "coalesce"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac ON pac.codigo = COALESCE(atd_1.pac_codigo, atv.pac_codigo)
          WHERE soetemp.seq = soe.seq), ( SELECT
                CASE
                    WHEN atd_1.origem::text = 'N'::text THEN 'I'::character varying
                    WHEN atd_1.origem IS NULL THEN 'D'::character varying
                    ELSE atd_1.origem
                END AS origem
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
          WHERE soetemp.seq = soe.seq), ( SELECT
                CASE
                    WHEN atd_1.lto_lto_id IS NOT NULL THEN 'L:'::text || atd_1.lto_lto_id::text
                    WHEN atd_1.qrt_numero IS NOT NULL THEN 'Q:'::text || atd_1.qrt_numero
                    WHEN atd_1.unf_seq IS NOT NULL THEN (((('U:'::text || auf.andar::text) || ' '::text) || auf.ind_ala::text) || ' - '::text) || auf.descricao::text
                    ELSE NULL::text
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.agh_unidades_funcionais auf ON auf.seq = atd_1.unf_seq
          WHERE soetemp.seq = soe.seq);

ALTER TABLE agh.v_ael_exames_solicitacao_atd
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ael_exames_solicitacao_atd TO postgres;
GRANT ALL ON TABLE agh.v_ael_exames_solicitacao_atd TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ael_exames_solicitacao_atd TO acesso_leitura;

CREATE OR REPLACE VIEW agh.v_ael_exame_solic_atd_aghu AS 
 SELECT soe.seq AS soe_seq,
    soe.atd_seq,
    soe.criado_em,
    man.descricao AS descricao_material,
    eis.dthr_evento,
    (exa.descricao_usual::text || ' /  '::text) || man.descricao::text AS nome_exame_material,
    pes.nome AS soe_servidor_nome,
    cnv.descricao AS cnv_descricao,
    atd.lto_lto_id,
    atd.qrt_numero,
    qrt.descricao AS qrt_descricao,
    atd.unf_seq,
    ise.dthr_programada,
    cnv.grupo_convenio,
    aie.origem_mapa,
    ise.ufe_ema_exa_sigla,
    ise.sit_codigo,
    ise.ufe_unf_seq,
    ( SELECT
                CASE
                    WHEN soetemp.atd_seq > 0 THEN pac_atd.nome
                    WHEN pac_atv.nome IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pac_atv.nome::text)::character varying
                        ELSE pac_atv.nome
                    END
                    WHEN atv.nome_paciente IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || atv.nome_paciente::text)::character varying
                        ELSE atv.nome_paciente
                    END
                    WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pjq.nome::text)::character varying
                    WHEN ccq.seq IS NOT NULL THEN ccq.material
                    WHEN lae.seq IS NOT NULL THEN lae.nome
                    WHEN ddv.seq IS NOT NULL THEN ddv.nome
                    WHEN cad.seq IS NOT NULL THEN cad.nome
                    ELSE NULL::character varying
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.aip_pacientes pac_atd ON pac_atd.codigo = atd_1.pac_codigo
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac_atv ON pac_atv.codigo = atv.pac_codigo
             LEFT JOIN agh.ael_projeto_pesquisas pjq ON pjq.seq = atv.pjq_seq
             LEFT JOIN agh.ael_cad_ctrl_qualidades ccq ON ccq.seq = atv.ccq_seq
             LEFT JOIN agh.ael_laboratorio_externos lae ON lae.seq = atv.lae_seq
             LEFT JOIN agh.ael_dados_cadaveres ddv ON ddv.seq = atv.ddv_seq
             LEFT JOIN agh.abs_candidatos_doadores cad ON cad.seq = atv.cad_seq
          WHERE soetemp.seq = soe.seq) AS nome_paciente,
    ( SELECT COALESCE(pac.prontuario, atv.prontuario) AS "coalesce"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac ON pac.codigo = COALESCE(atd_1.pac_codigo, atv.pac_codigo)
          WHERE soetemp.seq = soe.seq) AS prontuario,
    ( SELECT
                CASE
                    WHEN atd_1.origem::text = 'N'::text THEN 'I'::character varying
                    WHEN atd_1.origem IS NULL THEN 'D'::character varying
                    ELSE atd_1.origem
                END AS origem
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
          WHERE soetemp.seq = soe.seq) AS origem,
    ( SELECT
                CASE
                    WHEN atd_1.lto_lto_id IS NOT NULL THEN 'L:'::text || atd_1.lto_lto_id::text
                    WHEN qrt_1.descricao IS NOT NULL THEN 'Q:'::text || qrt_1.descricao::text
                    WHEN atd_1.unf_seq IS NOT NULL THEN (((('U:'::text || auf.andar::text) || ' '::text) || auf.ind_ala::text) || ' - '::text) || auf.descricao::text
                    ELSE NULL::text
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ain_quartos qrt_1 ON atd_1.qrt_numero = qrt_1.numero
             LEFT JOIN agh.agh_unidades_funcionais auf ON auf.seq = atd_1.unf_seq
          WHERE soetemp.seq = soe.seq) AS localizacao
   FROM agh.ael_item_solicitacao_exames ise
     JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
     JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
     JOIN agh.ael_extrato_item_solics eis ON eis.ise_soe_seq = ise.soe_seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = ise.sit_codigo::text
     JOIN agh.ael_solicitacao_exames soe ON soe.seq = ise.soe_seq
     JOIN agh.fat_convenios_saude cnv ON cnv.codigo = soe.csp_cnv_codigo
     JOIN agh.rap_servidores ser ON ser.matricula = soe.ser_matricula_eh_responsabilid AND ser.vin_codigo = soe.ser_vin_codigo_eh_responsabili
     JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = ser.pes_codigo
     LEFT JOIN agh.agh_atendimentos atd ON atd.seq = soe.atd_seq
     LEFT JOIN agh.ain_quartos qrt ON atd.qrt_numero = qrt.numero
     LEFT JOIN agh.ael_amostra_item_exames aie ON aie.ise_soe_seq = ise.soe_seq AND aie.ise_seqp = ise.seqp
  WHERE (eis.criado_em IN ( SELECT max(eis1.criado_em) AS max
           FROM agh.ael_extrato_item_solics eis1
          WHERE eis1.ise_soe_seq = eis.ise_soe_seq AND eis1.ise_seqp = eis.ise_seqp))
  GROUP BY soe.seq, soe.atd_seq, soe.criado_em, man.descricao, eis.dthr_evento, (exa.descricao_usual::text || ' /  '::text) || man.descricao::text, pes.nome, cnv.descricao, atd.lto_lto_id, atd.qrt_numero, qrt.descricao, atd.unf_seq, ise.dthr_programada, cnv.grupo_convenio, aie.origem_mapa, ise.ufe_ema_exa_sigla, ise.sit_codigo, ise.ufe_unf_seq, ( SELECT
                CASE
                    WHEN soetemp.atd_seq > 0 THEN pac_atd.nome
                    WHEN pac_atv.nome IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pac_atv.nome::text)::character varying
                        ELSE pac_atv.nome
                    END
                    WHEN atv.nome_paciente IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || atv.nome_paciente::text)::character varying
                        ELSE atv.nome_paciente
                    END
                    WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pjq.nome::text)::character varying
                    WHEN ccq.seq IS NOT NULL THEN ccq.material
                    WHEN lae.seq IS NOT NULL THEN lae.nome
                    WHEN ddv.seq IS NOT NULL THEN ddv.nome
                    WHEN cad.seq IS NOT NULL THEN cad.nome
                    ELSE NULL::character varying
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.aip_pacientes pac_atd ON pac_atd.codigo = atd_1.pac_codigo
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac_atv ON pac_atv.codigo = atv.pac_codigo
             LEFT JOIN agh.ael_projeto_pesquisas pjq ON pjq.seq = atv.pjq_seq
             LEFT JOIN agh.ael_cad_ctrl_qualidades ccq ON ccq.seq = atv.ccq_seq
             LEFT JOIN agh.ael_laboratorio_externos lae ON lae.seq = atv.lae_seq
             LEFT JOIN agh.ael_dados_cadaveres ddv ON ddv.seq = atv.ddv_seq
             LEFT JOIN agh.abs_candidatos_doadores cad ON cad.seq = atv.cad_seq
          WHERE soetemp.seq = soe.seq), ( SELECT COALESCE(pac.prontuario, atv.prontuario) AS "coalesce"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac ON pac.codigo = COALESCE(atd_1.pac_codigo, atv.pac_codigo)
          WHERE soetemp.seq = soe.seq), ( SELECT
                CASE
                    WHEN atd_1.origem::text = 'N'::text THEN 'I'::character varying
                    WHEN atd_1.origem IS NULL THEN 'D'::character varying
                    ELSE atd_1.origem
                END AS origem
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
          WHERE soetemp.seq = soe.seq), ( SELECT
                CASE
                    WHEN atd_1.lto_lto_id IS NOT NULL THEN 'L:'::text || atd_1.lto_lto_id::text
                    WHEN qrt_1.descricao IS NOT NULL THEN 'Q:'::text || qrt_1.descricao::text
                    WHEN atd_1.unf_seq IS NOT NULL THEN (((('U:'::text || auf.andar::text) || ' '::text) || auf.ind_ala::text) || ' - '::text) || auf.descricao::text
                    ELSE NULL::text
                END AS "case"
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ain_quartos qrt_1 ON atd_1.qrt_numero = qrt_1.numero
             LEFT JOIN agh.agh_unidades_funcionais auf ON auf.seq = atd_1.unf_seq
          WHERE soetemp.seq = soe.seq);

ALTER TABLE agh.v_ael_exame_solic_atd_aghu
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ael_exame_solic_atd_aghu TO postgres;
GRANT ALL ON TABLE agh.v_ael_exame_solic_atd_aghu TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ael_exame_solic_atd_aghu TO acesso_leitura;

--25/10/2016 #86835 Adicionado constraint a tabela agh.agh_job_detail

ALTER TABLE agh.agh_job_detail DROP CONSTRAINT agh_jod_ck1;
ALTER TABLE agh.agh_job_detail add CONSTRAINT agh_jod_ck1 CHECK (ind_situacao::text = ANY (ARRAY['A'::character varying::text, 'E'::character varying::text, 'C'::character varying::text, 'F'::character varying::text, 'N'::character varying::text]));

--26/10/2016 #86557 Adicionado novo item no array da constraint referente a issue #86446
ALTER TABLE agh.aip_log_pront_onlines
ADD CONSTRAINT aip_lpo_ck1 CHECK (ocorrencia = ANY (ARRAY[1, 2, 3, 4, 5, 6, 7, 8, 51, 52, 53, 54, 99]));

--27/10/2016 #86905
ALTER TABLE agh.rap_pessoas_fisicas ADD COLUMN cdd_codigo_municipio INTEGER;
ALTER TABLE agh.rap_pessoas_fisicas ADD CONSTRAINT rap_pes_cdd_fk2 FOREIGN KEY (cdd_codigo_municipio) REFERENCES agh.aip_cidades (codigo) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION ; 
COMMENT ON COLUMN agh.rap_pessoas_fisicas.cdd_codigo_municipio IS 'Código da cidade não cadastrada';

ALTER TABLE agh.rap_pessoas_fisicas_jn ADD COLUMN cdd_codigo_municipio INTEGER;
COMMENT ON COLUMN agh.rap_pessoas_fisicas_jn.cdd_codigo_municipio IS 'Código da cidade não cadastrada';

--08/11/2016 #86905 Campo Alfa numérico contendo o código da Ala.
alter table agh.AGH_UNIDADES_FUNCIONAIS_JN alter column IND_ALA TYPE character varying(2);
COMMENT ON COLUMN agh.AGH_UNIDADES_FUNCIONAIS_JN IS 'Código da ala';




--21/11/2016 #87480 Criação de parametros do SISREG


--P_AGHU_SEQ_SISREG_MUNICIPAL

INSERT INTO agh.agh_parametros
(
	seq
	,sis_sigla
	,nome
	,mantem_historico
	,criado_em,criado_por
	,alterado_em,alterado_por
	,vlr_data
	,vlr_numerico
	,vlr_texto
	,descricao
	,rotina_consistencia,version
	,vlr_data_padrao
	,vlr_numerico_padrao
	,vlr_texto_padrao
	,exemplo_uso
	,tipo_dado
)
select 
	nextval('agh.agh_psi_sq1') AS SEQ
	,'AAC' AS SIS_SIGLA
	,'P_AGHU_SEQ_SISREG_MUNICIPAL' AS NOME
	,'N'  AS MANTEM_HISTORICO
	,now() AS CRIADO_EM
	,'AGHU'  AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,null AS VLR_DATA
	,(
		SELECT 
			seq 
		FROM 
			agh.aac_tipo_agendamentos 
		where 
			descricao = 'SECRETARIA MUNICIPAL SAUDE'
	 )  AS VLR_NUMERICO
	,NULL  AS VLR_TEXTO
	,'Parametro deve possuir a Seq da Forma de agendamento da grade Sisreg Municipal. Usado para identificar o processo de importacao (valor numerico).' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null  AS VLR_DATA_PADRAO
	,(
		SELECT 
			seq 
		FROM 
			agh.aac_tipo_agendamentos 
		WHERE 
			descricao = 'SECRETARIA MUNICIPAL SAUDE'
	 ) AS VLR_NUMERICO_PADRAO
	,NULL  AS VLR_TEXTO_PADRAO
	,NULL  AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
		NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_SEQ_SISREG_MUNICIPAL' AND sis_sigla = 'AAC');

--P_AGHU_SEQ_SISREG_ESTADUAL

INSERT INTO agh.agh_parametros
(
	seq
	,sis_sigla
	,nome,mantem_historico
	,criado_em,criado_por
	,alterado_em,alterado_por
	,vlr_data
	,vlr_numerico
	,vlr_texto
	,descricao
	,rotina_consistencia,version
	,vlr_data_padrao
	,vlr_numerico_padrao
	,vlr_texto_padrao
	,exemplo_uso
	,tipo_dado
)
select 
	nextval('agh.agh_psi_sq1') AS SEQ
	,'AAC' AS SIS_SIGLA
	,'P_AGHU_SEQ_SISREG_ESTADUAL' AS NOME
	,'N' AS MANTEM_HISTORICO
	,now() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL ALTERADO_EM
	,NULL AS ALTERADO_POR
	,null AS VLR_DATA
	, ( 
	     CASE 
		 WHEN 
			(select count(*) from agh.aac_tipo_agendamentos where descricao = 'SECRETARIA ESTADUAL DE SAUDE') > 0 
		THEN 
			(select seq from agh.aac_tipo_agendamentos  where descricao = 'SECRETARIA ESTADUAL DE SAUDE')
		else
			0
		END
	 )  AS VLR_NUMERICO
	,NULL AS VLR_TEXTO
	,'Parametro deve possuir a Seq da Forma de agendamento da grade Sisreg Estadual. Usado para identificar o processo de importacao (valor numerico).'  AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	, ( 
	     CASE 
		 WHEN 
			(select count(*) from agh.aac_tipo_agendamentos where descricao = 'SECRETARIA ESTADUAL DE SAUDE') > 0 
		THEN 
			(select seq from agh.aac_tipo_agendamentos  where descricao = 'SECRETARIA ESTADUAL DE SAUDE')
		else
			0
		END
	 ) AS VLR_NUMERICO_PADRAO
	,NULL AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'T' AS TIPO_DADO
	WHERE
		NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_SEQ_SISREG_ESTADUAL' AND sis_sigla = 'AAC');


--P_AGHU_SEQ_SISREG_CONDICAO_ATENDIMENTO_PRIMEIRA_CONSULTA

INSERT INTO agh.agh_parametros
(
	seq
	,sis_sigla
	,nome,mantem_historico
	,criado_em,criado_por
	,alterado_em,alterado_por
	,vlr_data
	,vlr_numerico
	,vlr_texto
	,descricao
	,rotina_consistencia,version
	,vlr_data_padrao
	,vlr_numerico_padrao
	,vlr_texto_padrao
	,exemplo_uso
	,tipo_dado
)
select 
	nextval('agh.agh_psi_sq1') AS SEQ
	,'AAC' AS SIS_SIGLA
	,'P_AGHU_SEQ_SISREG_CONDICAO_ATENDIMENTO_PRIMEIRA_CONSULTA' AS NOME
	,'N' AS MANTEM_HISTORICO
	,now() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,null AS VLR_DATA
	,(
		select 
			seq 
		from 
			agh.aac_condicao_atendimentos  
		where 
			descricao = 'PRIMEIRA CONSULTA'
	 )  AS VLR_NUMERICO
	,NULL AS VLR_TEXTO
	,'Parametro deve possuir a Seq da Condicao de agendamento primeira consulta da grade Sisreg. Usado para identificar o processo de importacao (valor numerico).' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	,(
		select 
			seq 
		from 
			agh.aac_condicao_atendimentos  
		where 
			descricao = 'PRIMEIRA CONSULTA'
	 ) AS VLR_NUMERICO_PADRAO
	,NULL AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_SEQ_SISREG_CONDICAO_ATENDIMENTO_PRIMEIRA_CONSULTA' AND sis_sigla = 'AAC');


	
--P_AGHU_SEQ_SISREG_CONDICAO_ATENDIMENTO_RETORNO


INSERT INTO agh.agh_parametros
(
	seq
	,sis_sigla
	,nome,mantem_historico
	,criado_em,criado_por
	,alterado_em,alterado_por
	,vlr_data
	,vlr_numerico
	,vlr_texto
	,descricao
	,rotina_consistencia
	,version
	,vlr_data_padrao
	,vlr_numerico_padrao
	,vlr_texto_padrao
	,exemplo_uso
	,tipo_dado
)
	select 
	nextval('agh.agh_psi_sq1') AS SEQ
	,'AAC' AS SIS_SIGLA
	,'P_AGHU_SEQ_SISREG_CONDICAO_ATENDIMENTO_RETORNO' AS NOME
	,'N' AS MANTEM_HISTORICO
	,now() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,null AS VLR_DATA
	, ( 
	     CASE 
		 WHEN 
			(select count(*) from agh.aac_condicao_atendimentos  where descricao = 'RETORNO') > 0 
		THEN 
			(select seq from agh.aac_condicao_atendimentos  where descricao = 'RETORNO')
		else
			0
		END
	 ) AS VLR_NUMERICO
	,NULL AS VLR_TEXTO
	,'Parametro deve possuir a Seq da Condicao de agendamento retorno da grade Sisreg. Usado para identificar o processo de importacao (valor numerico).'  AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	, ( 
	     CASE 
		 WHEN 
			(select count(*) from agh.aac_condicao_atendimentos  where descricao = 'RETORNO') > 0 
		THEN 
			(select seq from agh.aac_condicao_atendimentos  where descricao = 'RETORNO')
		else
			0
		END
	 ) AS VLR_NUMERICO_PADRAO
	, NULL AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_SEQ_SISREG_CONDICAO_ATENDIMENTO_RETORNO' AND sis_sigla = 'AAC');


--P_AGHU_SEQ_SISREG_CONDICAO_ATENDIMENTO_RESERVA_TECNICA

INSERT INTO agh.agh_parametros
(
	seq
	,sis_sigla
	,nome,mantem_historico
	,criado_em,criado_por
	,alterado_em,alterado_por
	,vlr_data
	,vlr_numerico
	,vlr_texto
	,descricao
	,rotina_consistencia,version
	,vlr_data_padrao
	,vlr_numerico_padrao
	,vlr_texto_padrao
	,exemplo_uso
	,tipo_dado
)
select 
	nextval('agh.agh_psi_sq1') AS SEQ
	,'AAC' AS SIS_SIGLA
	,'P_AGHU_SEQ_SISREG_CONDICAO_ATENDIMENTO_RESERVA_TECNICA' AS NOME
	,'N'  AS MANTEM_HISTORICO
	,now() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,null  AS VLR_DATA
	, ( 
	     CASE 
		 WHEN 
			(select count(*) from agh.aac_condicao_atendimentos  where descricao = 'RESERVA TECNICA') > 0 
		THEN 
			(select seq from agh.aac_condicao_atendimentos  where descricao = 'RESERVA TECNICA')
		else
			0
		END
	 ) AS VLR_NUMERICO_PADRAO
	,NULL  AS VLR_TEXTO
	,' Parametro deve possuir a Seq da Condicao de agendamento reserva tecnica da grade Sisreg. Usado para identificar o processo de importacao (valor numerico).' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	, ( 
	     CASE 
		 WHEN 
			(select count(*) from agh.aac_condicao_atendimentos  where descricao = 'RESERVA TECNICA') > 0 
		THEN 
			(select seq from agh.aac_condicao_atendimentos  where descricao = 'RESERVA TECNICA')
		else
			0
		END
	 ) AS VLR_NUMERICO_PADRAO
	,NULL AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_SEQ_SISREG_CONDICAO_ATENDIMENTO_RESERVA_TECNICA' AND sis_sigla = 'AAC');


--21/11/2016 #87680 Alteração das colunas  exige_responsavel , separacao_previa e retorno.
	
ALTER TABLE agh.aip_solicitantes_prontuario ALTER COLUMN exige_responsavel SET DEFAULT 'N'::character varying;
COMMENT ON COLUMN agh.aip_solicitantes_prontuario.exige_responsavel IS 'Define se exige um responsável na solicitação de prontuário. Valor default N. Permite os valores S = Sim e N = Não. Obs: Este campo não será gravado pela aplicação.';

ALTER TABLE agh.aip_solicitantes_prontuario ALTER COLUMN separacao_previa SET DEFAULT 'N'::character varying;
COMMENT ON COLUMN agh.aip_solicitantes_prontuario.separacao_previa IS 'Define se deve acontecer uma separação prévia na solicitação de prontuário. Valor default N. Permite os valores S = Sim e N = Não. Obs: Este campo não será gravado pela aplicação.';

ALTER TABLE agh.aip_solicitantes_prontuario ALTER COLUMN retorno SET DEFAULT 'N'::character varying;
COMMENT ON COLUMN agh.aip_solicitantes_prontuario.retorno IS 'Define se deve realizar retorno na solicitação de prontuário. Valor default N. Permite os valores S = Sim e N = Não. Obs: Este campo não será gravado pela aplicação.';

--Acrescimo das colunas permite_alta_adm é permite_permanencia_com_alta.

ALTER TABLE agh.ain_tipos_alta_medica 
	ADD COLUMN permite_alta_adm character varying(1),
	ADD CONSTRAINT chk_ain_tipos_alta_medica_permite_alta_adm CHECK (permite_alta_adm::text = ANY (ARRAY['S'::character varying::text, 'N'::character varying::text]));

COMMENT ON COLUMN agh.ain_tipos_alta_medica.permite_alta_adm IS 'Indica se permite realizar alta administrativa sem alta médica. S = Sim, N = Não';

ALTER TABLE agh.ain_tipos_alta_medica 
	ADD COLUMN permite_permanencia_com_alta character varying(1),
	ADD CONSTRAINT chk_ain_tipos_alta_medica_permite_permanencia_com_alta CHECK (permite_permanencia_com_alta::text = ANY (ARRAY['S'::character varying::text, 'N'::character varying::text]));

COMMENT ON COLUMN agh.ain_tipos_alta_medica.permite_permanencia_com_alta IS 'Indica se permite realizar transferência do paciente meso após realizar alta administrativa. S = Sim, N = Não';




--Criação de paramêtros relacionados ao modulo de prescrição médica 	
--P_RELATORIO_PRESCRICAO_MEDICA_PAISAGEM
INSERT INTO agh.agh_parametros 
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	 NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AGH' AS SIS_SIGLA
	,'P_RELATORIO_PRESCRICAO_MEDICA_PAISAGEM' as NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,null AS ALTERADO_EM
	,null AS ALTERADO_POR
	,null AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,'N' AS VLR_TEXTO
	,'Define se deve exibir o Relatório de Prescrição Médica no formato Paisagem' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	,null AS VLR_NUMERICO_PADRAO
	,'N' AS VLR_TEXTO_PADRAO
	,null AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_RELATORIO_PRESCRICAO_MEDICA_PAISAGEM' AND sis_sigla = 'AGH');	

-- P_REL_PRESC_MEDICA_INFO_PACIENTE_CABECALHO
INSERT INTO agh.agh_parametros 
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	 NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AGH' AS SIS_SIGLA
	,'P_REL_PRESC_MEDICA_INFO_PACIENTE_CABECALHO' as NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,null AS ALTERADO_EM
	,null AS ALTERADO_POR
	,null AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,'N' AS VLR_TEXTO
	,'Define se deve exibir as informações do paciente no cabeçalho do Relatório de Prescrição Médica' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	,null AS VLR_NUMERICO_PADRAO
	,'N' AS VLR_TEXTO_PADRAO
	,null AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_REL_PRESC_MEDICA_INFO_PACIENTE_CABECALHO' AND sis_sigla = 'AGH');		


-- Modificação a pedido do Mannuel remoção de constraint
--Removendo uma regra que estava no banco e realizando o tratamento no código Java
ALTER TABLE agh.ain_internacoes DROP CONSTRAINT ain_int_ck15;	


--Indica se o registro é a origem padrão do hospital. 
ALTER TABLE agh.agh_samis ADD COLUMN origem_padrao character varying(1),
ADD CONSTRAINT chk_agh_samis_origem_padrao CHECK (origem_padrao::text = ANY (ARRAY['S'::character varying::text, 'N'::character varying::text])),
ALTER COLUMN origem_padrao SET DEFAULT 'N'::character varying;

UPDATE 
	agh.agh_samis 
set 
	origem_padrao = 'N' 
WHERE
	origem_padrao is null;

ALTER TABLE agh.agh_samis ALTER COLUMN origem_padrao SET NOT NULL;

COMMENT ON COLUMN agh.agh_samis.origem_padrao IS 'Indica se o registro é a origem padrão do hospital. Permite os valores: S = Sim e N = Não';
	


--24/11/2016 #87799 Criação de Parametro 'P_AGHU_ATIVA_DESATIVA_SOLICITACAO_EXAMES'

INSERT INTO agh.agh_parametros 
(
	SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AEL' AS SIS_SIGLA
	,'P_AGHU_ATIVA_DESATIVA_SOLICITACAO_EXAMES' as NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,null AS ALTERADO_EM
	,null AS ALTERADO_POR
	,null AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,'S' AS VLR_TEXTO
	,'Parametro responsavel por habilitar e desabilitar o botao solicitar exames nas telas de prescricao medica, e ambulatorio (atendimento). (Valor Texto = S para habilitar o botao ou N para desabilitar). O usuario logado tambem deve possuir permissao de Solicitar e listar exames para habilitar o botao de solicitacao nas respectivas telas. (Valor padrao= S)' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	,null AS VLR_NUMERICO_PADRAO
	,'S' AS VLR_TEXTO_PADRAO
	,null AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_ATIVA_DESATIVA_SOLICITACAO_EXAMES' AND sis_sigla = 'AEL');
	
	
--30/11/2016 #88009 Criação de Parametro 'P_FILTRO_VISUALIZA_CODICOES_CONSULTA'

INSERT INTO agh.agh_parametros 
(
	SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AAC' AS SIS_SIGLA
	,'P_FILTRO_VISUALIZA_CODICOES_CONSULTA' as NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,null AS ALTERADO_EM
	,null AS ALTERADO_POR
	,null AS VLR_DATA
	,4 AS VLR_NUMERICO
	,'S' AS VLR_TEXTO
	,'Parametro responsavel por filtrar a pesquisa do usuario na marcação de consulta e disponibilizacao de horario (ambulatorio) caso o mesmo nao possua a permissao de  VisualizarPrimeirasConsultasSMS. Deve ser informado  o valor numerico da SEQ DO TIPO DE CODICAO DE AGENDAMENTO Valor padrao "4" (Reconsulta) Perfil padrao ADM02.07' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	,4 AS VLR_NUMERICO_PADRAO
	,null AS VLR_TEXTO_PADRAO
	,null AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_FILTRO_VISUALIZA_CODICOES_CONSULTA' AND sis_sigla = 'AAC');

--05/12/2016 #88188 Aumento da coluna DESCRICAO  da tabela AGH.MPM_ITEM_CUIDADO_SUMARIOS

ALTER TABLE AGH.MPM_ITEM_CUIDADO_SUMARIOS ALTER COLUMN DESCRICAO TYPE CHARACTER VARYING (240);

--05/12/2016 #88227 Novo parametro

INSERT INTO agh.agh_parametros
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	 NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'ECP' AS SIS_SIGLA
	,'P_RELATORIO_CONTROLE_PACIENTE_PAISAGEM' as NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,null AS ALTERADO_EM
	,null AS ALTERADO_POR
	,null AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,'N' AS VLR_TEXTO
	,'Parametro responsavel por imprimir o relatorio controles do paciente em formato Paisagem. (Valor Texto = S para habilitar impressao paisagem ou N para desabilitar).(Valor padrao= N)' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	,null AS VLR_NUMERICO_PADRAO
	,'N' AS VLR_TEXTO_PADRAO
	,null AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_RELATORIO_CONTROLE_PACIENTE_PAISAGEM' AND sis_sigla = 'ECP');


--08-12-2016 #88377 criação das colunas para orgão, uf e data de registro nacional.
ALTER TABLE agh.rap_pessoas_fisicas ALTER COLUMN orgao_emissor_rg set data type smallint;
ALTER TABLE agh.rap_pessoas_fisicas ADD COLUMN uf_orgao character varying(2);
ALTER TABLE agh.rap_pessoas_fisicas ADD COLUMN dt_emissao_documento timestamp without time zone;

--NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_RELATORIO_CONTROLE_PACIENTE_PAISAGEM' AND sis_sigla = 'ECP');

--25/11/2016 #87898 Define se o campo "Procedimento SSM", na solicitação de internação, será obrigatório ao solicitar uma internação

INSERT INTO agh.agh_parametros
(
     SEQ
    ,SIS_SIGLA
    ,NOME
    ,MANTEM_HISTORICO
    ,CRIADO_EM
    ,CRIADO_POR
    ,ALTERADO_EM
    ,ALTERADO_POR
    ,VLR_DATA
    ,VLR_NUMERICO
    ,VLR_TEXTO
    ,DESCRICAO
    ,ROTINA_CONSISTENCIA
    ,VERSION
    ,VLR_DATA_PADRAO
    ,VLR_NUMERICO_PADRAO
    ,VLR_TEXTO_PADRAO
    ,EXEMPLO_USO
    ,TIPO_DADO
)
SELECT
    NEXTVAL('agh.agh_psi_sq1') AS SEQ
    ,'AIN' AS SIS_SIGLA
    ,'P_PROCEDIMENTO_SSM_OBRIG' AS NOME
    ,'S' AS MANTEM_HISTORICO
    ,NOW() AS CRIADO_EM
    ,'AGHU' AS CRIADO_POR
    ,NULL AS ALTERADO_EM
    ,NULL AS ALTERADO_POR
    ,NULL AS VLR_DATA
    ,NULL AS VLR_NUMERICO
    ,'N' AS VLR_TEXTO
    ,'Define se o campo "Procedimento SSM", na solicitação de internação, será obrigatório ao solicitar uma internação' AS DESCRICAO
    ,NULL AS ROTINA_CONSISTENCIA
    ,0 AS VERSION
    ,NULL AS VLR_DATA_PADRAO
    ,NULL AS VLR_NUMERICO_PADRAO
    ,'N' AS VLR_TEXTO_PADRAO
    ,NULL AS EXEMPLO_USO
    ,'T' AS TIPO_DADO
WHERE
    NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_PROCEDIMENTO_SSM_OBRIG' AND sis_sigla = 'AIN');	
	
--08/12/2016 #88314 Criação de Parametro 'P_TIPO_APRAZAMENTO_CAMPO_OBSERVACAO_OBRIGATORIO'

INSERT INTO agh.agh_parametros 
(
	SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'MPM' AS SIS_SIGLA
	,'P_TIPO_APRAZAMENTO_CAMPO_OBSERVACAO_OBRIGATORIO' as NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,null AS ALTERADO_EM
	,null AS ALTERADO_POR
	,null AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,' ' AS VLR_TEXTO
	,'Parametro feito para o hospital HESC, quando selecionado quando necessario em tipo de Aprazamento, irá deixar o campo Observacao obrigatorio. O valor texto para o HU sera quando necessario conforme o banco' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	,NULL AS VLR_NUMERICO_PADRAO
	,'N' AS VLR_TEXTO_PADRAO
	,null AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_TIPO_APRAZAMENTO_CAMPO_OBSERVACAO_OBRIGATORIO' AND sis_sigla = 'MPM');

	
--08/12/2016 #88364 Criação de Parametro 'P_CONSIDERA_ALINHAMENTO_VERTICAL_MASCARA_EXAMES'

INSERT INTO agh.agh_parametros 
(
	SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AEL' AS SIS_SIGLA
	,'P_CONSIDERA_ALINHAMENTO_VERTICAL_MASCARA_EXAMES' as NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,null AS ALTERADO_EM
	,null AS ALTERADO_POR
	,null AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,'N' AS VLR_TEXTO
	,'Indica se o sistema deve considerar o alinhamento vertical nos relatorios da mascara de exames' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	,NULL AS VLR_NUMERICO_PADRAO
	,NULL AS VLR_TEXTO_PADRAO
	,null AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_CONSIDERA_ALINHAMENTO_VERTICAL_MASCARA_EXAMES' AND sis_sigla = 'AEL');


--08-12-2016 #88377 criação das colunas para orgão, uf e data de registro nacional.
ALTER TABLE agh.rap_pessoas_fisicas ADD COLUMN orgao_emissor_rg smallint;
ALTER TABLE agh.rap_pessoas_fisicas ADD COLUMN uf_orgao character varying(2);
ALTER TABLE agh.rap_pessoas_fisicas ADD COLUMN dt_emissao_documento timestamp without time zone;

--CONSTRAINT agh.aip_uf_orgao
ALTER TABLE agh.rap_pessoas_fisicas
  ADD CONSTRAINT rap_pes_uf_fk2 FOREIGN KEY (uf_orgao)
      REFERENCES agh.aip_ufs (sigla) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

--CONSTRAINT agh.aip_orgaos_emissores
ALTER TABLE agh.rap_pessoas_fisicas
  ADD CONSTRAINT rap_pes_oed_fk1 FOREIGN KEY (orgao_emissor_rg)
      REFERENCES agh.aip_orgaos_emissores (codigo) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION;

	  
	  

--09/12/2016 #88423 Acrescimo de parametro P_PERFIL_ARQUIVO_CLINICO colunas ind_tipo_solicitacao e unf_solicitante_seq .

--Parâmetro utilizado para definir qual o perfil que deve ser notificado na Central de Pendências sobre solicitações de prontuários requeridos ou solicitados
INSERT INTO agh.agh_parametros 
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	 NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AGH' AS SIS_SIGLA
	,'P_PERFIL_ARQUIVO_CLINICO' AS NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,NULL AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,'SAMIS' AS VLR_TEXTO
	,'Define qual o perfil que deve ser notificado sobre solicitações de prontuários requeridos ou solicitados' AS DESCRICAO
	,NULL AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,NULL AS VLR_DATA_PADRAO
	,NULL AS VLR_NUMERICO_PADRAO
	,'SAMIS' AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_PERFIL_ARQUIVO_CLINICO' AND sis_sigla = 'AGH');	

--Campo para determinar o tipo/origem da solicitação de prontuário. Permite os valores: A=Ambulatório, I= Internação, V=Avulsa e C=Cirurgia
ALTER TABLE agh.AIP_MOVIMENTACAO_PRONTUARIOS ADD COLUMN ind_tipo_solicitacao character varying(1);
ALTER TABLE agh.AIP_MOVIMENTACAO_PRONTUARIOS ADD CONSTRAINT aip_mov_pront_ck1 CHECK (ind_tipo_solicitacao::text = ANY (ARRAY['A'::character varying::text, 'I'::character varying::text, 'V'::character varying::text, 'C'::character varying::text]));

--Comentario da coluna
COMMENT ON COLUMN agh.AIP_MOVIMENTACAO_PRONTUARIOS.ind_tipo_solicitacao IS 'Campo para determinar o tipo/origem da solicitação de prontuário. Permite os valores: A=Ambulatório, I= Internação, V=Avulsa e C=Cirurgia';

--Campo númerico com o seq da Unidade Funcional
ALTER TABLE agh.AIP_MOVIMENTACAO_PRONTUARIOS ADD COLUMN unf_solicitante_seq smallint;
ALTER TABLE agh.AIP_MOVIMENTACAO_PRONTUARIOS ADD CONSTRAINT aip_mov_pront_unf_fk1 FOREIGN KEY (unf_solicitante_seq) REFERENCES agh.agh_unidades_funcionais (seq) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

COMMENT ON COLUMN agh.AIP_MOVIMENTACAO_PRONTUARIOS.unf_solicitante_seq IS 'Campo númerico com o seq da Unidade Funcional';

	
--12/12/2016 #88403 Alteração de  View: agh.v_mpm_lista_pac_internados

DROP VIEW agh.v_mpm_lista_pac_internados;

CREATE OR REPLACE VIEW agh.v_mpm_lista_pac_internados AS 
 SELECT atd.seq AS atd_seq,
    atd.prontuario,
    atd.pac_codigo,
    pac.nome,
    pac.nome_social,
    atd.dthr_inicio AS data_inicio_atendimento,
    atd.dthr_fim AS data_fim_atendimento,
    pac.dt_nascimento AS data_nascimento,
    atd.ser_matricula AS atd_ser_matricula,
    atd.ser_vin_codigo AS atd_ser_vin_codigo,
    atd.ser_matricula,
    atd.ser_vin_codigo,
    atd.ind_sit_sumario_alta,
    atd.origem,
    atd.esp_seq,
    atd.unf_seq,
    atd.ind_pac_cpa,
    atd.ind_pac_atendimento,
    atd.lto_lto_id,
    atd.qrt_numero,
        CASE
            WHEN rapf.nome_usual IS NULL OR rapf.nome_usual::text = ''::text THEN rapf.nome
            ELSE rapf.nome_usual
        END AS nome_responsavel,
        CASE
            WHEN atd.origem::text = 'I'::text AND (( SELECT max(cpa.criado_em) AS max
               FROM agh.mpm_control_prev_altas cpa
              WHERE cpa.atd_seq = atd.seq AND cpa.resposta::text = 'S'::text AND cpa.dt_fim IS NOT NULL AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) >= 0::double precision AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) <= 2::double precision)) IS NOT NULL THEN 'true'::text
            WHEN atd.origem::text = 'N'::text AND (( SELECT max(cpa.criado_em) AS max
               FROM agh.mpm_control_prev_altas cpa
                 JOIN agh.agh_atendimentos atd_mae ON atd_mae.seq = cpa.atd_seq AND atd_mae.origem::text = 'I'::text
              WHERE cpa.atd_seq = atd.atd_seq_mae AND cpa.resposta::text = 'S'::text AND cpa.dt_fim IS NOT NULL AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) >= 0::double precision AND date_part('day'::text, cpa.dt_fim::timestamp with time zone - now()) <= 2::double precision)) IS NOT NULL THEN 'true'::text
            ELSE 'false'::text
        END AS possui_plano_altas,
        CASE
            WHEN atd.lto_lto_id IS NOT NULL THEN ( SELECT (('L:'::text || q.descricao::text) || l.leito::text)
               FROM agh.ain_leitos l
                 JOIN agh.ain_quartos q ON l.qrt_numero = q.numero
              WHERE l.lto_id::text = atd.lto_lto_id::text)
            WHEN atd.qrt_numero IS NOT NULL THEN concat('Q:', atd.qrt_numero)
            ELSE ( SELECT (((('U:'::text || unf_1.andar::text) || ' '::text) || unf_1.ind_ala::text) || ' - '::text) || unf_1.descricao::text
               FROM agh.agh_unidades_funcionais unf_1
              WHERE unf_1.seq = atd.unf_seq)
        END AS local,
        CASE
            WHEN
            CASE
                WHEN atd.ind_pac_atendimento::text = 'S'::text AND cuf.unf_seq IS NOT NULL THEN 'false'::text
                ELSE 'true'::text
            END = 'true'::text THEN ''::text
            WHEN NOT (( SELECT count(*) AS count
               FROM agh.mpm_prescricao_medicas pm
              WHERE pm.atd_seq = atd.seq AND pm.ser_matricula_valida IS NOT NULL AND pm.ser_vin_codigo_valida IS NOT NULL AND pm.dthr_fim > now())) > 0 THEN 'PRESCRICAO_NAO_REALIZADA'::text
            WHEN NOT (( SELECT count(*) AS count
               FROM agh.mpm_prescricao_medicas pm
              WHERE pm.atd_seq = atd.seq AND pm.ser_matricula_valida IS NOT NULL AND pm.ser_vin_codigo_valida IS NOT NULL AND pm.dthr_inicio > now() AND to_char(unf.hrio_validade_pme, 'HH24:mi'::text) = to_char(pm.dthr_inicio, 'HH24:mi'::text))) > 0 THEN 'PRESCRICAO_VENCE_NO_DIA'::text
            ELSE 'PRESCRICAO_VENCE_NO_DIA_SEGUINTE'::text
        END AS status_prescricao,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text = 'E'::text THEN 'SUMARIO_ALTA_NAO_ENTREGUE_SAMIS'::text
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_ALTA_NAO_REALIZADO'::text
            ELSE ''::text
        END AS status_sumario_alta,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.ael_item_solicitacao_exames ise
                 JOIN agh.ael_solicitacao_exames soe ON ise.soe_seq = soe.seq
              WHERE soe.atd_seq = atd.seq AND ise.sit_codigo::text = 'LI'::text AND NOT (EXISTS ( SELECT its.ise_seqp,
                        its.ise_soe_seq
                       FROM agh.ael_item_solic_consultados its
                         JOIN agh.ael_item_solicitacao_exames sub_ise ON its.ise_seqp = sub_ise.seqp AND its.ise_soe_seq = sub_ise.soe_seq
                         JOIN agh.ael_solicitacao_exames sub_soe ON sub_ise.soe_seq = sub_soe.seq
                      WHERE its.ise_soe_seq = ise.soe_seq AND its.ise_seqp = ise.seqp AND sub_soe.atd_seq = atd.seq AND sub_ise.sit_codigo::text = 'LI'::text)) AND NOT (EXISTS ( SELECT iri.ise_seqp,
                        iri.ise_soe_seq
                       FROM agh.ael_itens_resul_impressao iri
                      WHERE iri.ise_soe_seq = ise.soe_seq AND iri.ise_seqp = ise.seqp)))) > 0 THEN 'RESULTADOS_NAO_VISUALIZADOS'::text
            WHEN (( SELECT count(*) AS count
               FROM agh.ael_item_solicitacao_exames ise
                 JOIN agh.ael_solicitacao_exames soe ON ise.soe_seq = soe.seq
              WHERE soe.atd_seq = atd.seq AND ise.sit_codigo::text = 'LI'::text AND NOT (EXISTS ( SELECT its.ise_seqp,
                        its.ise_soe_seq
                       FROM agh.ael_item_solic_consultados its
                         JOIN agh.ael_item_solicitacao_exames sub_ise ON its.ise_seqp = sub_ise.seqp AND its.ise_soe_seq = sub_ise.soe_seq
                         JOIN agh.ael_solicitacao_exames sub_soe ON sub_ise.soe_seq = sub_soe.seq
                      WHERE its.ise_soe_seq = ise.soe_seq AND its.ise_seqp = ise.seqp AND sub_soe.atd_seq = atd.seq AND sub_ise.sit_codigo::text = 'LI'::text AND its.ser_matricula = atd.ser_matricula AND its.ser_vin_codigo = atd.ser_vin_codigo)) AND NOT (EXISTS ( SELECT iri.ise_seqp,
                        iri.ise_soe_seq
                       FROM agh.ael_itens_resul_impressao iri
                      WHERE iri.ise_soe_seq = ise.soe_seq AND iri.ise_seqp = ise.seqp)))) > 0 THEN 'RESULTADOS_VISUALIZADOS_OUTRO_MEDICO'::text
            ELSE ''::text
        END AS status_exames_nao_vistos,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_laudos lau
              WHERE lau.atd_seq = atd.seq AND lau.tuo_seq IS NOT NULL AND lau.justificativa IS NULL)) > 0 THEN 'PENDENCIA_LAUDO_UTI'::text
            ELSE ''::text
        END AS status_pendencia_documento,
        CASE
            WHEN (( SELECT count(projetos.pac_codigo) AS count
               FROM agh.ael_projeto_pacientes projetos
              WHERE projetos.pac_codigo = atd.pac_codigo AND (projetos.dt_fim IS NULL OR projetos.dt_fim >= now()) AND (projetos.jex_seq IS NULL OR (EXISTS ( SELECT jex_.seq AS y0_
                       FROM agh.ael_justificativa_exclusoes jex_
                      WHERE jex_.seq = projetos.jex_seq AND jex_.ind_mostra_telas::text = 'S'::text))) AND (EXISTS ( SELECT pjq_.seq AS y0_
                       FROM agh.ael_projeto_pesquisas pjq_
                      WHERE pjq_.seq = projetos.pjq_seq AND (pjq_.dt_fim IS NULL OR pjq_.dt_fim >= now()))))) > 0 THEN 'PACIENTE_PESQUISA'::text
            ELSE ''::text
        END AS status_paciente_pesquisa,
        CASE
            WHEN atd.origem::text = 'I'::text OR atd.origem::text = 'N'::text THEN
            CASE
                WHEN (( SELECT count(*) AS count
                   FROM agh.mam_evolucoes evo
                  WHERE evo.atd_seq = atd.seq AND date_part('day'::text, evo.dthr_valida::timestamp with time zone - now()) = 0::double precision AND evo.dthr_valida_mvto IS NULL)) > 0 THEN
                CASE
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_MEDICO'::text)) THEN 'EVOLUCAO'::text
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_OUTROS'::text)) THEN 'EVOLUCAO'::text
                    WHEN (( SELECT cprf.cag_seq AS seq
                       FROM agh.cse_categoria_perfis cprf
                         JOIN agh.cse_categoria_profissionais csecategor1_ ON cprf.cag_seq = csecategor1_.seq
                      WHERE csecategor1_.ind_situacao::text = 'A'::text AND cprf.ind_situacao::text = 'A'::text AND (cprf.per_nome::text IN ( SELECT cprf_1.nome AS y0_
                               FROM casca.csc_perfil cprf_1
                                 JOIN casca.csc_perfis_usuarios perfisusua1_ ON cprf_1.id = perfisusua1_.id_perfil
                                 JOIN casca.csc_usuario usuario2_ ON perfisusua1_.id_usuario = usuario2_.id
                                 JOIN agh.rap_servidores srv ON lower(srv.usuario::text) = lower(usuario2_.login::text)
                              WHERE cprf_1.situacao::text = 'A'::text AND (perfisusua1_.dthr_expiracao IS NULL OR perfisusua1_.dthr_expiracao > now()) AND usuario2_.ativo = true AND srv.matricula = atd.ser_matricula AND srv.vin_codigo = atd.ser_vin_codigo))
                     LIMIT 1)) = (( SELECT agh_parametros.vlr_numerico
                       FROM agh.agh_parametros
                      WHERE agh_parametros.nome::text = 'P_CATEG_PROF_ENF'::text)) THEN
                    CASE
                        WHEN (( SELECT count(*) AS count
                           FROM agh.mam_item_evolucoes iev
                          WHERE iev.evo_seq = (( SELECT evo.seq
                                   FROM agh.mam_evolucoes evo
                                  WHERE evo.atd_seq = atd.seq AND date_part('day'::text, evo.dthr_valida::timestamp with time zone - now()) = 0::double precision AND evo.dthr_valida_mvto IS NULL
                                 LIMIT 1)) AND (iev.tie_seq IN ( SELECT mam_tipo_item_evolucoes.seq
                                   FROM agh.mam_tipo_item_evolucoes
                                  WHERE mam_tipo_item_evolucoes.sigla::text = 'C'::text)))) > 0 THEN 'EVOLUCAO'::text
                        ELSE ''::text
                    END
                    ELSE ''::text
                END
                ELSE ''::text
            END
            ELSE ''::text
        END AS status_evolucao,
        CASE
            WHEN (( SELECT count(docs.pac_codigo) AS count
               FROM agh.v_agh_versoes_documentos docs
              WHERE docs.dov_situacao::text = 'P'::text AND docs.pac_codigo = atd.pac_codigo)) > 0 THEN 'PENDENTE'::text
            ELSE ''::text
        END AS status_certificacao_digital,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_sumario_altas sa
              WHERE sa.atd_seq = atd.seq AND sa.mam_seq IS NOT NULL)) > 0 THEN 'true'::text
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_alta_sumarios asu
              WHERE asu.apa_atd_seq = atd.seq AND asu.ind_concluido::text = 'S'::text)) > 0 THEN 'true'::text
            WHEN ((( SELECT agh_parametros.vlr_texto
               FROM agh.agh_parametros
              WHERE agh_parametros.nome::text = 'P_BLOQUEIA_PAC_EMERG'::text))::text) <> 'S'::text AND (( SELECT count(*) AS count
               FROM agh.agh_caract_unid_funcionais cuf2
              WHERE cuf2.unf_seq = atd.unf_seq AND cuf2.caracteristica::text = 'Atend emerg terreo'::text)) > 0 THEN 'true'::text
            ELSE 'false'::text
        END AS disable_button_alta_obito,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'S'::text AND cuf.unf_seq IS NOT NULL THEN 'false'::text
            ELSE 'true'::text
        END AS disable_button_prescrever,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_ALTA'::text
            ELSE 'ALTA'::text
        END AS label_alta,
        CASE
            WHEN atd.ind_pac_atendimento::text = 'N'::text AND atd.ctrl_sumr_alta_pendente::text <> 'E'::text THEN 'SUMARIO_OBITO'::text
            ELSE 'OBITO'::text
        END AS label_obito,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mci_notificacao_gmr gmr
              WHERE gmr.pac_codigo = atd.pac_codigo AND gmr.ind_notificacao_ativa::text = 'S'::text)) > 0 THEN 'true'::text
            ELSE 'false'::text
        END AS ind_gmr,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.agh_caract_unid_funcionais car
              WHERE car.caracteristica::text = 'Anamnese/Evolução Eletrônica'::text AND car.unf_seq = atd.unf_seq)) > 0 THEN 'true'::text
            ELSE 'false'::text
        END AS tem_unf_caract_anamnese_evolucao,
        CASE
            WHEN (( SELECT count(*) AS count
               FROM agh.mpm_anamneses ana
                 JOIN agh.agh_atendimentos atd1 ON atd1.seq = ana.atd_seq
                 JOIN agh.rap_servidores rap ON rap.matricula = atd1.ser_matricula AND rap.vin_codigo = atd1.ser_vin_codigo
                 JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = rap.pes_codigo
                 JOIN agh.agh_unidades_funcionais unf_1 ON unf_1.seq = atd1.unf_seq
                 JOIN agh.agh_caract_unid_funcionais car ON car.unf_seq = unf_1.seq
              WHERE ana.atd_seq = atd.seq)) > 0 THEN ( SELECT DISTINCT
                    CASE ana.ind_pendente
                        WHEN 'R'::text THEN 'ANAMNESE_NAO_REALIZADA'::text
                        WHEN 'P'::text THEN 'ANAMNESE_PENDENTE'::text
                        WHEN 'V'::text THEN
                        CASE
                            WHEN (( SELECT count(*) AS count
                               FROM agh.mpm_evolucoes evo
                                 JOIN agh.mpm_anamneses ana_1 ON ana_1.seq = evo.ana_seq
                              WHERE evo.ana_seq = ana_1.seq AND to_char(evo.dthr_referencia, 'YYYYMMDD'::text) = to_char(now(), 'YYYYMMDD'::text) AND evo.ind_pendente::text <> 'R'::text)) = 0 THEN 'EVOLUCAO_DO_DIA_NAO_REALIZADA'::text
                            WHEN (( SELECT count(*) AS count
                               FROM agh.mpm_evolucoes evo
                                 JOIN agh.mpm_anamneses ana_1 ON ana_1.seq = evo.ana_seq
                              WHERE evo.ana_seq = ana_1.seq AND to_char(evo.dthr_referencia, 'YYYYMMDD'::text) = to_char(now(), 'YYYYMMDD'::text) AND evo.ind_pendente::text = 'P'::text)) > 0 THEN 'EVOLUCAO_DO_DIA_PENDENTE'::text
                            WHEN (( SELECT count(*) AS count
                               FROM agh.mpm_evolucoes evo
                                 JOIN agh.mpm_anamneses ana_1 ON ana_1.seq = evo.ana_seq
                              WHERE evo.ana_seq = ana_1.seq AND evo.dthr_referencia > date_trunc('day'::text, now()) AND evo.ind_pendente::text = 'V'::text)) = 0 THEN 'EVOLUCAO_VENCE_NO_DIA_SEGUINTE'::text
                            ELSE NULL::text
                        END
                        ELSE NULL::text
                    END AS "case"
               FROM agh.mpm_anamneses ana
                 JOIN agh.agh_atendimentos atd1 ON atd1.seq = ana.atd_seq
                 JOIN agh.rap_servidores rap ON rap.matricula = atd1.ser_matricula AND rap.vin_codigo = atd1.ser_vin_codigo
                 JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = rap.pes_codigo
                 JOIN agh.agh_unidades_funcionais unf1 ON unf1.seq = atd1.unf_seq
                 JOIN agh.agh_caract_unid_funcionais car ON car.unf_seq = unf1.seq
              WHERE ana.atd_seq = atd.seq)
            ELSE 'ANAMNESE_NAO_REALIZADA'::text
        END AS status_anamnese_evolucao
   FROM agh.agh_atendimentos atd
     LEFT JOIN agh.agh_caract_unid_funcionais cuf ON cuf.unf_seq = atd.unf_seq AND cuf.caracteristica::text = 'Pme Informatizada'::text
     LEFT JOIN agh.agh_unidades_funcionais unf ON atd.unf_seq = unf.seq
     LEFT JOIN agh.rap_servidores raps ON raps.matricula = atd.ser_matricula AND raps.vin_codigo = atd.ser_vin_codigo
     LEFT JOIN agh.rap_pessoas_fisicas rapf ON raps.pes_codigo = rapf.codigo
     LEFT JOIN agh.aip_pacientes pac ON pac.codigo = atd.pac_codigo;

ALTER TABLE agh.v_mpm_lista_pac_internados
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_mpm_lista_pac_internados TO postgres;
GRANT ALL ON TABLE agh.v_mpm_lista_pac_internados TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mpm_lista_pac_internados TO acesso_leitura;


--14/12/2016 #88581 Criação de um novo parametro
INSERT INTO agh.agh_parametros
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
	SELECT
		  NEXTVAL('agh.agh_psi_sq1') AS SEQ
		,'SCE' AS SIS_SIGLA
		,'P_IMPRIMI_RM_LOCAL_E_REMOTA' as NOME
		,'N' AS MANTEM_HISTORICO
		,NOW() AS CRIADO_EM
		,'AGHU' AS CRIADO_POR
		,null AS ALTERADO_EM
		,null AS ALTERADO_POR
		,null AS VLR_DATA
		,NULL AS VLR_NUMERICO
		,'S' AS VLR_TEXTO
		,'Parametro responsavel pela impressão ao gerar uma nota de requisicao de material. (Valor Texto = S para habilitar impressao ou N para desabilitar).(Valor padrao = S)' AS DESCRICAO
		,null AS ROTINA_CONSISTENCIA
		,0 AS VERSION
		,null AS VLR_DATA_PADRAO
		,null AS VLR_NUMERICO_PADRAO
		,'S' AS VLR_TEXTO_PADRAO
		,null AS EXEMPLO_USO
		,'T' AS TIPO_DADO
	WHERE
		NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_IMPRIMI_RM_LOCAL_E_REMOTA' AND sis_sigla = 'SCE');

--19/12/2016 #88718 Criação do parametro P_AGHU_TELEFONE_PACIENTE_OBRIGATORIO
INSERT INTO agh.agh_parametros
(
     SEQ
    ,SIS_SIGLA
    ,NOME
    ,MANTEM_HISTORICO
    ,CRIADO_EM
    ,CRIADO_POR
    ,ALTERADO_EM
    ,ALTERADO_POR
    ,VLR_DATA
    ,VLR_NUMERICO
    ,VLR_TEXTO
    ,DESCRICAO
    ,ROTINA_CONSISTENCIA
    ,VERSION
    ,VLR_DATA_PADRAO
    ,VLR_NUMERICO_PADRAO
    ,VLR_TEXTO_PADRAO
    ,EXEMPLO_USO
    ,TIPO_DADO
)
SELECT
    NEXTVAL('agh.agh_psi_sq1') AS SEQ
    ,'AIP' AS SIS_SIGLA
    ,'P_AGHU_TELEFONE_PACIENTE_OBRIGATORIO' AS NOME
    ,'S' AS MANTEM_HISTORICO
    ,NOW() AS CRIADO_EM
    ,'AGHU' AS CRIADO_POR
    ,NULL AS ALTERADO_EM
    ,NULL AS ALTERADO_POR
    ,NULL AS VLR_DATA
    ,NULL AS VLR_NUMERICO
    ,'N' AS VLR_TEXTO
    ,'Define se o preenchimento de ao menos um número de telefone será obrigatório ao incluir ou alterar o cadastro de um paciente' AS DESCRICAO
    ,NULL AS ROTINA_CONSISTENCIA
    ,0 AS VERSION
    ,NULL AS VLR_DATA_PADRAO
    ,NULL AS VLR_NUMERICO_PADRAO
    ,'N' AS VLR_TEXTO_PADRAO
    ,NULL AS EXEMPLO_USO
    ,'T' AS TIPO_DADO
WHERE
    NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_TELEFONE_PACIENTE_OBRIGATORIO' AND sis_sigla = 'AIP');		
	
--22/12/2016 #88823 Conceder permissão ao usuario ugen_integra_exames para acessar apenas a view_integracao

GRANT SELECT ON TABLE agh.v_integracao TO ugen_integra_exames;

--16/01/2017 #89307 - Criar parâmetro do sistema P_COD_ATESTADO_MEDICO.

INSERT INTO agh.agh_parametros 
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	 NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'MAM' AS SIS_SIGLA
	,'P_COD_ATESTADO_MEDICO' AS NOME
	,'S' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,NULL AS VLR_DATA
	,2 AS VLR_NUMERICO
	,NULL AS VLR_TEXTO
	,'Parâmetro que informa qual o código do atestado médico' AS DESCRICAO
	,NULL AS ROTINA_CONSISTENCIA
	,1 AS VERSION
	,NULL AS VLR_DATA_PADRAO
	,NULL AS VLR_NUMERICO_PADRAO
	,NULL AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_COD_ATESTADO_MEDICO' AND sis_sigla = 'MAM');	
	
--16/01/2017 #89203 Acrescimo de tipo MCO e OPF a contraint da tabela mbc_agendas.
	
ALTER TABLE agh.mbc_agendas DROP CONSTRAINT mbc_agd_ck1;

ALTER TABLE agh.mbc_agendas
ADD CONSTRAINT mbc_agd_ck1 CHECK (puc_ind_funcao_prof::text = ANY (ARRAY['MPF'::character varying::text, 'MCO'::character varying::text, 'OPF'::character varying::text, 'ORE'::character varying::text]));


	
--17/01/2017 #89515 - Criar parâmetro do sistema P_COD_MVTO_ATUALIZACAO_INTERNACAO e Acrescimo de tipos de movimento de internação "ATUALIZAÇÃO DE INTERNAÇÃO"

INSERT INTO agh.agh_parametros 
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	 NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AIN' AS SIS_SIGLA
	,'P_COD_MVTO_ATUALIZACAO_INTERNACAO' AS NOME
	,'S' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,NULL AS VLR_DATA
	,41 AS VLR_NUMERICO
	,NULL AS VLR_TEXTO
	,'CÓDIGO DO TIPO DE MOVIMENTO DE INTERNAÇÃO QUE INDICA ATUALIZAÇÃO DE INTERNAÇÃO ==> 41' AS DESCRICAO
	,NULL AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,NULL AS VLR_DATA_PADRAO
	,NULL AS VLR_NUMERICO_PADRAO
	,NULL AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'N' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_COD_MVTO_ATUALIZACAO_INTERNACAO' AND sis_sigla = 'AIN');	

INSERT INTO agh.ain_tipos_mvto_internacao(seq,descricao,version,ind_ccih_mvto_local_regras)
SELECT 41
,'ATUALIZAÇÃO DE INTERNAÇÃO'
,0
,'N'
WHERE
NOT EXISTS (SELECT 1 FROM agh.ain_tipos_mvto_internacao WHERE seq =41);

	
--17/01/2017 #89517 - Criar parâmetro do sistema P_COD_MVTO_ESTORNO_ALTA_INTERNACAO e Acrescimo de tipos de movimento de internação "ESTORNO DE ALTA INTERNAÇÃO"

INSERT INTO agh.agh_parametros 
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	 NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AIN' AS SIS_SIGLA
	,'P_COD_MVTO_ESTORNO_ALTA_INTERNACAO' AS NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,NULL AS VLR_DATA
	,40 AS VLR_NUMERICO
	,NULL AS VLR_TEXTO
	,'CÓDIGO DO TIPO DE MOVIMENTO DE INTERNAÇÃO QUE INDICA ESTORNO DE ALTA ==> 40' AS DESCRICAO
	,NULL AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,NULL AS VLR_DATA_PADRAO
	,NULL AS VLR_NUMERICO_PADRAO
	,NULL AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'N' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_COD_MVTO_ESTORNO_ALTA_INTERNACAO' AND sis_sigla = 'AIN');	


INSERT INTO agh.ain_tipos_mvto_internacao(seq,descricao,version,ind_ccih_mvto_local_regras)
SELECT 40
,'ESTORNO DE ALTA INTERNAÇÃO'
,0
,'N'
WHERE
NOT EXISTS (SELECT 1 FROM agh.ain_tipos_mvto_internacao WHERE seq =40);


--17/01/2017 #89557 Casas decimais no campo Velocidade de Infusão
	
DROP VIEW agh.v_mpm_mdtos;

ALTER TABLE agh.mpm_mod_basic_mdtos ALTER COLUMN gotejo SET DATA TYPE double precision;

CREATE OR REPLACE VIEW agh.v_mpm_mdtos AS 
 SELECT mbm.mdb_seq,
    mbm.seq,
    mbm.tfq_seq AS tp_frequencia,
    mbm.vad_sigla AS via_administracao,
    mbm.tva_seq AS tp_velocid,
    mbm.ind_se_necessario,
    mbm.frequencia,
    mbm.hora_inicio_administracao AS hr_inicio_adm,
    mbm.gotejo,
    mbm.qtde_horas_correr AS qtde_correr,
    mbm.observacao,
    imm.med_mat_codigo AS mat_codigo,
    imm.seqp AS seq_item,
    imm.fds_seq AS fds_dose,
    imm.dose
   FROM agh.mpm_item_mod_basico_mdtos imm,
    agh.mpm_mod_basic_mdtos mbm
  WHERE imm.mbm_mdb_seq = mbm.mdb_seq AND imm.mbm_seq = mbm.seq AND mbm.ind_solucao::text = 'N'::text;

ALTER TABLE agh.v_mpm_mdtos
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_mdtos TO ugen_aghu;
GRANT ALL ON TABLE agh.v_mpm_mdtos TO acesso_completo;
GRANT SELECT ON TABLE agh.v_mpm_mdtos TO acesso_leitura;	

--17/01/2017 #89555 Paramentro filtra responsável por definir se a opção de Filtro por Unidade Funcional iniciará habilitada ou nao no carregamento da te de Solicitação de Exames
INSERT INTO agh.agh_parametros 
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	 NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AEL' AS SIS_SIGLA
	,'P_AGHU_DEFINE_VALOR_DEFAULT_FILTRO_UNF_EXAME_SOL' AS NOME
	,'S' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,NULL AS ALTERADO_EM
	,NULL AS ALTERADO_POR
	,NULL AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,'N' AS VLR_TEXTO
	,'Filtro responsável por definir se a opção de Filtro por Unidade Funcional iniciará habilitada ou nao no carregamento da te de Solicitação de Exames. ValorTexto = "N" (desabilitado)(padrao) ou "S" (Habilitado).' AS DESCRICAO
	,NULL AS ROTINA_CONSISTENCIA
	,1 AS VERSION
	,NULL AS VLR_DATA_PADRAO
	,NULL AS VLR_NUMERICO_PADRAO
	,'N' AS VLR_TEXTO_PADRAO
	,NULL AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_DEFINE_VALOR_DEFAULT_FILTRO_UNF_EXAME_SOL' AND sis_sigla = 'AEL');

--18/01/2017 #89612 Parametro responsavel pela impressão do relatorio lista ocorrencia em formato Paisagem
INSERT INTO agh.agh_parametros
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'AFA' AS SIS_SIGLA
	,'P_RELATORIO_LISTA_OCORRENCIA_PAISAGEM' as NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,null AS ALTERADO_EM
	,null AS ALTERADO_POR
	,null AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,'N' AS VLR_TEXTO
	,'Parametro responsavel pela impressão do relatorio lista ocorrencia em formato Paisagem. (Valor Texto = S para habilitar impressao paisagem ou N para desabilitar).(Valor padrao = N)' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	,null AS VLR_NUMERICO_PADRAO
	,'N' AS VLR_TEXTO_PADRAO
	,null AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_RELATORIO_LISTA_OCORRENCIA_PAISAGEM' AND sis_sigla = 'AFA');
	
--09/02/2017 #90209 - Alteração de nome mais significativo
UPDATE agh.agh_parametros SET nome= 'P_AGHU_VALIDA_ARQUIVO_SUS_SIGTAP' WHERE nome = 'P_AGHU_VALIDA_ARQUIVO_SUS';

--14/02/2017 #90673 DROP VIEW agh.v_ael_exame_solic_atd_aghu
CREATE OR REPLACE VIEW agh.v_ael_exame_solic_atd_aghu AS 
 SELECT soe.seq AS soe_seq,
    soe.atd_seq,
    soe.criado_em,
    man.descricao AS descricao_material,
    eis.dthr_evento,
    (exa.descricao_usual::text || ' /  '::text) || man.descricao::text AS nome_exame_material,
    pes.nome AS soe_servidor_nome,
    cnv.descricao AS cnv_descricao,
    atd.lto_lto_id,
    atd.qrt_numero,
    qrt.descricao AS qrt_descricao,
    atd.unf_seq,
    ise.dthr_programada,
    cnv.grupo_convenio,
    aie.origem_mapa,
    ise.ufe_ema_exa_sigla,
    ise.sit_codigo,
    ise.ufe_unf_seq,
    ( SELECT
                CASE
                    WHEN soetemp.atd_seq > 0 THEN pac_atd.nome
                    WHEN pac_atv.nome IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pac_atv.nome::text)::character varying
                        ELSE pac_atv.nome
                    END
                    WHEN atv.nome_paciente IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || atv.nome_paciente::text)::character varying
                        ELSE atv.nome_paciente
                    END
                    WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pjq.nome::text)::character varying
                    WHEN ccq.seq IS NOT NULL THEN ccq.material
                    WHEN lae.seq IS NOT NULL THEN lae.nome
                    WHEN ddv.seq IS NOT NULL THEN ddv.nome
                    WHEN cad.seq IS NOT NULL THEN cad.nome
                    ELSE NULL::character varying
                END AS "case" 
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.aip_pacientes pac_atd ON pac_atd.codigo = atd_1.pac_codigo
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac_atv ON pac_atv.codigo = atv.pac_codigo
             LEFT JOIN agh.ael_projeto_pesquisas pjq ON pjq.seq = atv.pjq_seq
             LEFT JOIN agh.ael_cad_ctrl_qualidades ccq ON ccq.seq = atv.ccq_seq
             LEFT JOIN agh.ael_laboratorio_externos lae ON lae.seq = atv.lae_seq
             LEFT JOIN agh.ael_dados_cadaveres ddv ON ddv.seq = atv.ddv_seq
             LEFT JOIN agh.abs_candidatos_doadores cad ON cad.seq = atv.cad_seq
          WHERE soetemp.seq = soe.seq) AS nome_paciente,
    ( SELECT COALESCE(pac.prontuario, atv.prontuario) AS "coalesce" 
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac ON pac.codigo = COALESCE(atd_1.pac_codigo, atv.pac_codigo)
          WHERE soetemp.seq = soe.seq) AS prontuario,
    ( SELECT
                CASE
                    WHEN atd_1.origem::text = 'N'::text THEN 'I'::character varying
                    WHEN atd_1.origem IS NULL THEN 'D'::character varying
                    ELSE atd_1.origem
                END AS origem
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
          WHERE soetemp.seq = soe.seq) AS origem,
    ( SELECT
                CASE
                    WHEN atd_1.lto_lto_id IS NOT NULL THEN 'L:'::text || atd_1.lto_lto_id::text
                    WHEN qrt_1.descricao IS NOT NULL THEN 'Q:'::text || qrt_1.descricao::text
                    WHEN atd_1.unf_seq IS NOT NULL THEN (((('U:'::text || auf.andar::text) || ' '::text) || auf.ind_ala::text) || ' - '::text) || auf.descricao::text
                    ELSE NULL::text
                END AS "case" 
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ain_quartos qrt_1 ON atd_1.qrt_numero = qrt_1.numero
             LEFT JOIN agh.agh_unidades_funcionais auf ON auf.seq = atd_1.unf_seq
          WHERE soetemp.seq = soe.seq) AS localizacao
   FROM agh.ael_item_solicitacao_exames ise
     JOIN agh.ael_exames exa ON exa.sigla::text = ise.ufe_ema_exa_sigla::text
     JOIN agh.ael_materiais_analises man ON man.seq = ise.ufe_ema_man_seq
     JOIN agh.ael_extrato_item_solics eis ON eis.ise_soe_seq = ise.soe_seq AND eis.ise_seqp = ise.seqp AND eis.sit_codigo::text = ise.sit_codigo::text
     JOIN agh.ael_solicitacao_exames soe ON soe.seq = ise.soe_seq
     JOIN agh.fat_convenios_saude cnv ON cnv.codigo = soe.csp_cnv_codigo
     LEFT JOIN agh.rap_servidores ser ON ser.matricula = soe.ser_matricula_eh_responsabilid AND ser.vin_codigo = soe.ser_vin_codigo_eh_responsabili
     LEFT JOIN agh.rap_pessoas_fisicas pes ON pes.codigo = ser.pes_codigo
     LEFT JOIN agh.agh_atendimentos atd ON atd.seq = soe.atd_seq
     LEFT JOIN agh.ain_quartos qrt ON atd.qrt_numero = qrt.numero
     LEFT JOIN agh.ael_amostra_item_exames aie ON aie.ise_soe_seq = ise.soe_seq AND aie.ise_seqp = ise.seqp
  WHERE (eis.criado_em IN ( SELECT max(eis1.criado_em) AS max
           FROM agh.ael_extrato_item_solics eis1
          WHERE eis1.ise_soe_seq = eis.ise_soe_seq AND eis1.ise_seqp = eis.ise_seqp))
  GROUP BY soe.seq, soe.atd_seq, soe.criado_em, man.descricao, eis.dthr_evento, (exa.descricao_usual::text || ' /  '::text) || man.descricao::text, pes.nome, cnv.descricao, atd.lto_lto_id, atd.qrt_numero, qrt.descricao, atd.unf_seq, ise.dthr_programada, cnv.grupo_convenio, aie.origem_mapa, ise.ufe_ema_exa_sigla, ise.sit_codigo, ise.ufe_unf_seq, ( SELECT
                CASE
                    WHEN soetemp.atd_seq > 0 THEN pac_atd.nome
                    WHEN pac_atv.nome IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pac_atv.nome::text)::character varying
                        ELSE pac_atv.nome
                    END
                    WHEN atv.nome_paciente IS NOT NULL THEN
                    CASE
                        WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || atv.nome_paciente::text)::character varying
                        ELSE atv.nome_paciente
                    END
                    WHEN pjq.numero IS NOT NULL THEN ((('GPP-'::text || pjq.numero::text) || ' - '::text) || pjq.nome::text)::character varying
                    WHEN ccq.seq IS NOT NULL THEN ccq.material
                    WHEN lae.seq IS NOT NULL THEN lae.nome
                    WHEN ddv.seq IS NOT NULL THEN ddv.nome
                    WHEN cad.seq IS NOT NULL THEN cad.nome
                    ELSE NULL::character varying
                END AS "case" 
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.aip_pacientes pac_atd ON pac_atd.codigo = atd_1.pac_codigo
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac_atv ON pac_atv.codigo = atv.pac_codigo
             LEFT JOIN agh.ael_projeto_pesquisas pjq ON pjq.seq = atv.pjq_seq
             LEFT JOIN agh.ael_cad_ctrl_qualidades ccq ON ccq.seq = atv.ccq_seq
             LEFT JOIN agh.ael_laboratorio_externos lae ON lae.seq = atv.lae_seq
             LEFT JOIN agh.ael_dados_cadaveres ddv ON ddv.seq = atv.ddv_seq
             LEFT JOIN agh.abs_candidatos_doadores cad ON cad.seq = atv.cad_seq
          WHERE soetemp.seq = soe.seq), ( SELECT COALESCE(pac.prontuario, atv.prontuario) AS "coalesce" 
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
             LEFT JOIN agh.aip_pacientes pac ON pac.codigo = COALESCE(atd_1.pac_codigo, atv.pac_codigo)
          WHERE soetemp.seq = soe.seq), ( SELECT
                CASE
                    WHEN atd_1.origem::text = 'N'::text THEN 'I'::character varying
                    WHEN atd_1.origem IS NULL THEN 'D'::character varying
                    ELSE atd_1.origem
                END AS origem
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ael_atendimento_diversos atv ON atv.seq = soetemp.atv_seq
          WHERE soetemp.seq = soe.seq), ( SELECT
                CASE
                    WHEN atd_1.lto_lto_id IS NOT NULL THEN 'L:'::text || atd_1.lto_lto_id::text
                    WHEN qrt_1.descricao IS NOT NULL THEN 'Q:'::text || qrt_1.descricao::text
                    WHEN atd_1.unf_seq IS NOT NULL THEN (((('U:'::text || auf.andar::text) || ' '::text) || auf.ind_ala::text) || ' - '::text) || auf.descricao::text
                    ELSE NULL::text
                END AS "case" 
           FROM agh.ael_solicitacao_exames soetemp
             LEFT JOIN agh.agh_atendimentos atd_1 ON atd_1.seq = soetemp.atd_seq
             LEFT JOIN agh.ain_quartos qrt_1 ON atd_1.qrt_numero = qrt_1.numero
             LEFT JOIN agh.agh_unidades_funcionais auf ON auf.seq = atd_1.unf_seq
          WHERE soetemp.seq = soe.seq);

ALTER TABLE agh.v_ael_exame_solic_atd_aghu
  OWNER TO postgres;
GRANT ALL ON TABLE agh.v_ael_exame_solic_atd_aghu TO postgres;
GRANT ALL ON TABLE agh.v_ael_exame_solic_atd_aghu TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ael_exame_solic_atd_aghu TO acesso_leitura;

--21/02/2017 #90669 Correção e otimização da visão V_AIN_SERV_INTERNA

--APAGA AS VISÕES E SUAS DEPENDENCIAS PARA PODER GERAR DE NOVO
DROP VIEW agh.v_ain_serv_transf_tst;
DROP VIEW agh.v_ain_serv_interna;

CREATE OR REPLACE VIEW agh.v_ain_serv_interna AS 

    SELECT 
          servidores.matricula
        , servidores.vin_codigo
        , pessoa_fisica.nome
        , pessoa_fisica.nome_usual
        , qualificacao.nro_reg_conselho
        , especialidade.esp_seq
    FROM
        AGH.rap_servidores servidores
        INNER JOIN AGH.rap_pessoas_fisicas pessoa_fisica ON servidores.pes_codigo = pessoa_fisica.codigo
        INNER JOIN AGH.rap_qualificacoes qualificacao ON pessoa_fisica.codigo = qualificacao.pes_codigo
        INNER JOIN AGH.rap_tipos_qualificacao tipo_qualificacao ON qualificacao.tql_codigo = tipo_qualificacao.codigo
        INNER JOIN AGH.agh_prof_especialidades especialidade ON servidores.matricula = especialidade.ser_matricula AND servidores.vin_codigo = especialidade.ser_vin_codigo
        LEFT JOIN AGH.rap_conselhos_profissionais conselho ON tipo_qualificacao.cpr_codigo = conselho.codigo

    WHERE
        (servidores.dt_fim_vinculo IS NULL OR servidores.dt_fim_vinculo > 'now'::text::date) 
        AND
        qualificacao.nro_reg_conselho IS NOT NULL 
        AND
        conselho.sigla::text = ANY (ARRAY['CREMERS'::character varying::text, 'CRO'::character varying::text, 'COREN'::character varying::text, 'CRM'::character varying::text])
    ORDER BY
          servidores.matricula
        , servidores.vin_codigo
        , pessoa_fisica.nome
        , pessoa_fisica.nome_usual
        , qualificacao.nro_reg_conselho
        , especialidade.esp_seq;

ALTER TABLE agh.v_ain_serv_interna
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_ain_serv_interna TO ugen_aghu;
GRANT ALL ON TABLE agh.v_ain_serv_interna TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_serv_interna TO acesso_leitura;

--View: agh.v_ain_serv_transf_tst

CREATE OR REPLACE VIEW agh.v_ain_serv_transf_tst AS 
( SELECT DISTINCT 'N'::text AS consiste_escala,
    pre.ser_matricula,
    pre.ser_vin_codigo,
    pre.esp_seq,
    0 AS cnv_codigo,
    esp.sigla AS esp_sigla,
    vsc.nome,
    vsc.nro_reg_conselho,
    pre.capac_referencial,
    pre.quant_pac_internados,
    NULL::text AS ind_atua_cti,
    to_date(NULL::text, '0'::text) AS dt_inicio_escala
   FROM agh.agh_prof_especialidades pre,
    agh.agh_especialidades esp,
    agh.v_ain_serv_interna vsc
  WHERE (pre.ser_matricula + 0) = vsc.matricula AND (pre.ser_vin_codigo + 0) = vsc.vin_codigo AND pre.ind_interna::text = 'S'::text AND esp.seq = pre.esp_seq
  ORDER BY 'N'::text, pre.ser_matricula, pre.ser_vin_codigo, pre.esp_seq, 0::integer, esp.sigla, vsc.nome, vsc.nro_reg_conselho, pre.capac_referencial, pre.quant_pac_internados, NULL::text, to_date(NULL::text, '0'::text))
UNION
( SELECT DISTINCT 'S'::text AS consiste_escala,
    epi.pec_pre_ser_matricula AS ser_matricula,
    epi.pec_pre_ser_vin_codigo AS ser_vin_codigo,
    epi.pec_pre_esp_seq AS esp_seq,
    epi.pec_cnv_codigo AS cnv_codigo,
    esp.sigla AS esp_sigla,
    vsc.nome,
    vsc.nro_reg_conselho,
    pre.capac_referencial,
    pre.quant_pac_internados,
    epi.ind_atua_cti,
    epi.dt_inicio AS dt_inicio_escala
   FROM agh.ain_escalas_profissional_int epi,
    agh.agh_profissionais_esp_convenio pec,
    agh.agh_prof_especialidades pre,
    agh.agh_especialidades esp,
    agh.v_ain_serv_interna vsc
  WHERE (pre.ser_matricula + 0) = vsc.matricula AND (pre.ser_vin_codigo + 0) = vsc.vin_codigo AND pre.ind_interna::text = 'S'::text AND pec.pre_ser_matricula = pre.ser_matricula AND pec.pre_ser_vin_codigo = pre.ser_vin_codigo AND pec.pre_esp_seq = pre.esp_seq AND pec.ind_interna::text = 'S'::text AND esp.seq = pre.esp_seq AND epi.pec_pre_ser_matricula = pre.ser_matricula AND epi.pec_pre_ser_vin_codigo = pre.ser_vin_codigo AND epi.pec_pre_esp_seq = pre.esp_seq AND epi.dt_inicio <= 'now'::text::date AND (epi.dt_fim IS NULL OR epi.dt_fim >= 'now'::text::date)
  ORDER BY 'S'::text, epi.pec_pre_ser_matricula, epi.pec_pre_ser_vin_codigo, epi.pec_pre_esp_seq, epi.pec_cnv_codigo, esp.sigla, vsc.nome, vsc.nro_reg_conselho, pre.capac_referencial, pre.quant_pac_internados, epi.ind_atua_cti, epi.dt_inicio);

ALTER TABLE agh.v_ain_serv_transf_tst
  OWNER TO ugen_aghu;
GRANT ALL ON TABLE agh.v_ain_serv_transf_tst TO ugen_aghu;
GRANT ALL ON TABLE agh.v_ain_serv_transf_tst TO acesso_completo;
GRANT SELECT ON TABLE agh.v_ain_serv_transf_tst TO acesso_leitura;


--22/02/2017 #90954 Criação de parametro 'P_AGHU_CIDADE_ASSINATURA_LAUDO_APAC_AMBULATORIO'
INSERT INTO agh.agh_parametros
(
	 SEQ
	,SIS_SIGLA
	,NOME
	,MANTEM_HISTORICO
	,CRIADO_EM
	,CRIADO_POR
	,ALTERADO_EM
	,ALTERADO_POR
	,VLR_DATA
	,VLR_NUMERICO
	,VLR_TEXTO
	,DESCRICAO
	,ROTINA_CONSISTENCIA
	,VERSION
	,VLR_DATA_PADRAO
	,VLR_NUMERICO_PADRAO
	,VLR_TEXTO_PADRAO
	,EXEMPLO_USO
	,TIPO_DADO
)
SELECT
	NEXTVAL('agh.agh_psi_sq1') AS SEQ
	,'MAM' AS SIS_SIGLA
	,'P_AGHU_CIDADE_ASSINATURA_LAUDO_APAC_AMBULATORIO' as NOME
	,'N' AS MANTEM_HISTORICO
	,NOW() AS CRIADO_EM
	,'AGHU' AS CRIADO_POR
	,null AS ALTERADO_EM
	,null AS ALTERADO_POR
	,null AS VLR_DATA
	,NULL AS VLR_NUMERICO
	,'nomeCidadeHU' AS VLR_TEXTO
	,'Parâmetro responsável por definir a cidade da assinatura do laudo APAC Ambulatório.' AS DESCRICAO
	,null AS ROTINA_CONSISTENCIA
	,0 AS VERSION
	,null AS VLR_DATA_PADRAO
	,null AS VLR_NUMERICO_PADRAO
	,'nomeCidadeHU' AS VLR_TEXTO_PADRAO
	,null AS EXEMPLO_USO
	,'T' AS TIPO_DADO
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_AGHU_CIDADE_ASSINATURA_LAUDO_APAC_AMBULATORIO' AND sis_sigla = 'MAM');


--22/03/2017 #90966 Novo parametro

INSERT INTO agh.agh_parametros
( 
	  seq
	, sis_sigla
	, nome
	, mantem_historico
	, criado_em
	, criado_por
	, alterado_em
	, alterado_por
	, vlr_data
	, vlr_numerico
	, vlr_texto
	, descricao
	, rotina_consistencia
	, version
	, vlr_data_padrao
	, vlr_numerico_padrao
	, vlr_texto_padrao
	, exemplo_uso
	, tipo_dado
)

SELECT
	nextval('agh.agh_psi_sq1') AS seq
	,'MAM' AS sis_sigla
	,'P_LIST_COD_RETORNO_NAO_ATENDE' AS nome
	,'S' AS mantem_historico
	,now() AS criado_em
	,'AGHU' AS criado_por
	,null AS alterado_em
	,null AS alterado_por
	,null AS vlr_data
	,NULL AS vlr_numerico
	,' ' AS vlr_texto
	,'Valor de texto que indica as situação de consulta que não devem ser apresentadas para usuários que não atendem consultas. Para configuração correto deve ser informado o códigos da situações de consulta separados por vírgula.' AS descricao
	,null AS rotina_consistencia
	,0 AS version
	,null AS vlr_data_padrao
	,null AS vlr_numerico_padrao
	,null AS vlr_texto_padrao
	,null AS exemplo_uso
	,'T' AS tipo_dado
WHERE
	NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_LIST_COD_RETORNO_NAO_ATENDE' AND sis_sigla = 'MAM');	

--03/04/2017 #91996 Novo parametro	
	
INSERT INTO agh.agh_parametros
(
     SEQ
    ,SIS_SIGLA
    ,NOME
    ,MANTEM_HISTORICO
    ,CRIADO_EM
    ,CRIADO_POR
    ,ALTERADO_EM
    ,ALTERADO_POR
    ,VLR_DATA
    ,VLR_NUMERICO
    ,VLR_TEXTO
    ,DESCRICAO
    ,ROTINA_CONSISTENCIA
    ,VERSION
    ,VLR_DATA_PADRAO
    ,VLR_NUMERICO_PADRAO
    ,VLR_TEXTO_PADRAO
    ,EXEMPLO_USO
    ,TIPO_DADO
)
SELECT
    NEXTVAL('agh.agh_psi_sq1') AS SEQ
    ,'AEL' AS SIS_SIGLA
    ,'P_PRECISAO_RESULT_ANTERIORES_HISTORICO' AS NOME
    ,'S' AS MANTEM_HISTORICO
    ,NOW() AS CRIADO_EM
    ,'AGHU' AS CRIADO_POR
    ,NULL AS ALTERADO_EM
    ,NULL AS ALTERADO_POR
    ,NULL AS VLR_DATA
    ,2 AS VLR_NUMERICO
    ,NULL AS VLR_TEXTO
    ,'Parâmetro para escolher a precisão de casas decimais dos resultados do componente histórico nos laudos dos exames' AS DESCRICAO
    ,NULL AS ROTINA_CONSISTENCIA
    ,0 AS VERSION
    ,NULL AS VLR_DATA_PADRAO
    ,15 AS VLR_NUMERICO_PADRAO
    ,NULL AS VLR_TEXTO_PADRAO
    ,NULL AS EXEMPLO_USO
    ,'N' AS TIPO_DADO
WHERE
    NOT EXISTS (SELECT 1 FROM agh.agh_parametros WHERE nome = 'P_PRECISAO_RESULT_ANTERIORES_HISTORICO' AND sis_sigla = 'AEL');	