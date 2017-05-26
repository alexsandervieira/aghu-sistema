# Sistema AGHU

AGHU é a sigla de Aplicativo de Gestão para Hospitais Universitários.
http://www.ebserh.gov.br/web/aghu/sobre/o-que-e



## Configuração do Ambiente de Desenvolvimento

Efetuar clone/download do projeto git
https://github.com/ebserhaghu/aghu-sistema.git	

 ### Código Fonte

 * Na IDE de desenvolvimento criar um projeto a partir de um projeto existente.
 * Selecionar o projeto dentro do diretório aghu-sistema/6x/aplicacao/aghu_jee/aghu-entidades
 * Informar que é um projeto Maven e importar.
 * Importar um novo módulo dentro deste projeto e selecionar o diretório aghu-sistema/6x/aplicacao/aghu_jee/aghu
 * Informar que é um projeto Maven e importar.

 ### Configurar Build
 
 #### Alterar o seuDiretório contidos no command line para o local onde está seus arquivos.

 * Na IDE acessar configurações de run 
 * Configurar o diretório, informar o diretório do projeto: seuDiretório/aghu_jee/aghu
 * Informar os comandos no command line: clean install -DskipTests -Dpmd.skip=true 
 * Acessar configurações de run
 * Configurar o diretório, informar o diretório do projeto: seuDiretório/aghu_jee/aghu-entidades
 * Informar os comandos no command line: clean install
 * Acessar configurações de run
 * Configurar o diretório, informar o diretório do projeto: seuDiretório/aghu_jee/aghu/aghu-ear
 * Informar os comandos no command line: wildfly:deploy -Djboss.home=seuDiretório/6x/aplicacao/wildfly

 #### Depêndencias .m2

 * Para resolução de dependências do projeto.
 * Adicionar os jars do diretório 6x/dependencias/aghu-dependencias-jar no .m2 do projeto

 ### Configuração do Banco de dados 




 ### Deploy da Aplicação

 * 
