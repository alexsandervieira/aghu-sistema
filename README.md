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

 #### Configurar JAVA_HOME JDK 1.7

 #### Informar o Maven utilizado contido no diretório: seuDiretório/6x/aplicacao/apache-maven/conf/settings.xml

 #### Configurar o arquivo settings contindo no diretório: seuDiretório/6x/aplicacao/apache-maven/conf/settings.xml

 * Na IDE acessar configurações de execução criar novo e nomear aghu-full
 * Configurar o diretório, informar o diretório do projeto: seuDiretório/6x/aplicacao/aghu_jee/aghu
 * Informar os comandos no command line: clean install -DskipTests -Dpmd.skip=true 
 * Acessar configurações de run criar novo e nomear aghu-entidades-full
 * Configurar o diretório, informar o diretório do projeto: seuDiretório/6x/aplicacao/aghu_jee/aghu-entidades
 * Informar os comandos no command line: clean install
 * Acessar configurações de run criar novo e nomear aghu-deploy
 * Configurar o diretório, informar o diretório do projeto: seuDiretório/aghu_jee/aghu/aghu-ear
 * Informar os comandos no command line: wildfly:deploy -Djboss.home=seuDiretório/6x/aplicacao/wildfly
 * Na IDE acessar configurações de execução criar novo e nomear rodar-seguranca
 * Nas configurações informar o diretório  seuDiretório/6x/dependencias/aghu-seguranca/tools
 * Informar os comandos no command line: install
 * Nas variaveis de execução na aba runner colocar os seguintes valores
 * Nome: env Valor: dev
 * Nome: local Valor: bsb
 * Nome: priorizarXML Valor: false
 * Nome: simular Valor: true
 * Nome: aplicacao Valor: AGHU
 * Na IDE acessar configurações de execução criar novo e nomear rodar-seguranca-full
 * Nas configurações informar o diretório seuDiretório/6x/dependencias/aghu-seguranca
 * Informar os comandos no command line: clean compile antrun:run
 * Informar também os comandos em Profiles: atualizar-banco 
 * Nas variaveis de execução na aba runner colocar os seguintes valores
 * Nome: env Valor: dev
 * Nome: local Valor: bsb
 * Nome: priorizarXML Valor: false
 * Nome: simular Valor: false
 * Nome: versao Valor: 69
 * Nome: aplicacao Valor: AGHU


 #### Depêndencias .m2

 * Para resolução de dependências do projeto.
 * Adicionar os jars do diretório seuDiretório/6x/dependencias/aghu-dependencias-jar no .m2 do projeto

 ### Configuração do Banco de dados 

 * Acessar o arquivo dbaghu.backup contido no diretório: seuDiretório/6x/banco-dados/banco
 * Restaurar backup do banco em um banco de dados PostgreSql
 * Executar script pendecias_6x.sql contido no diretório: seuDiretório/6x/banco-dados/scripts no banco restaurado

 ### Deploy da Aplicação

 * Executar aghu-entidades-full
 * Executar aghu-full
 * Acessar diretório: seuDiretório/6x/aplicacao/wildfly/bin 
 * Executar dentro do diretório acima: ./standalone.sh -server-config=standalone-full.xml
 * Executar aghu-deploy

 ### Executar Pacote de Segurança

 * Executar rodar-seguranca
 * Executar rodar-seguranca-full

 #### Caso apresente erro os parâmetros informados para rodar este pacote estão na filter filter-dev-bsb.properties 


 ### Acesso AGHU

 * Para logar no AGHU
 * usuário: aghu
 * senha: qualquerSenha
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

 #### Configurar JAVA_HOME JDK 1.7

 #### Informar o Maven utilizado contido no diretório: seuDiretório/6x/aplicacao/apache-maven/conf/settings.xml

 #### Configurar o arquivo settings contindo no diretório: seuDiretório/6x/aplicacao/apache-maven/conf/settings.xml

 * Na IDE acessar configurações de execução criar novo e nomear aghu-full
 * Configurar o diretório, informar o diretório do projeto: seuDiretório/6x/aplicacao/aghu_jee/aghu
 * Informar os comandos no command line: clean install -DskipTests -Dpmd.skip=true 
 * Acessar configurações de run criar novo e nomear aghu-entidades-full
 * Configurar o diretório, informar o diretório do projeto: seuDiretório/6x/aplicacao/aghu_jee/aghu-entidades
 * Informar os comandos no command line: clean install
 * Acessar configurações de run criar novo e nomear aghu-deploy
 * Configurar o diretório, informar o diretório do projeto: seuDiretório/aghu_jee/aghu/aghu-ear
 * Informar os comandos no command line: wildfly:deploy -Djboss.home=seuDiretório/6x/aplicacao/wildfly
 * Na IDE acessar configurações de execução criar novo e nomear rodar-seguranca
 * Nas configurações informar o diretório  seuDiretório/6x/dependencias/aghu-seguranca/tools
 * Informar os comandos no command line: install
 * Nas variaveis de execução na aba runner colocar os seguintes valores
 * Nome: env Valor: dev
 * Nome: local Valor: bsb
 * Nome: priorizarXML Valor: false
 * Nome: simular Valor: true
 * Nome: aplicacao Valor: AGHU
 * Na IDE acessar configurações de execução criar novo e nomear rodar-seguranca-full
 * Nas configurações informar o diretório seuDiretório/6x/dependencias/aghu-seguranca
 * Informar os comandos no command line: clean compile antrun:run
 * Informar também os comandos em Profiles: atualizar-banco 
 * Nas variaveis de execução na aba runner colocar os seguintes valores
 * Nome: env Valor: dev
 * Nome: local Valor: bsb
 * Nome: priorizarXML Valor: false
 * Nome: simular Valor: false
 * Nome: versao Valor: 69
 * Nome: aplicacao Valor: AGHU


 #### Depêndencias .m2

 * Para resolução de dependências do projeto.
 * Adicionar os jars do diretório seuDiretório/6x/dependencias/aghu-dependencias-jar no .m2 do projeto

 ### Configuração do Banco de dados 

 * Acessar o arquivo dbaghu.backup contido no diretório: seuDiretório/6x/banco-dados/banco
 * Restaurar backup do banco em um banco de dados PostgreSql
 * Executar script pendecias_6x.sql contido no diretório: seuDiretório/6x/banco-dados/scripts no banco restaurado

 ### Deploy da Aplicação

 * Executar aghu-entidades-full
 * Executar aghu-full
 * Acessar diretório: seuDiretório/6x/aplicacao/wildfly/bin 
 * Executar dentro do diretório acima: ./standalone.sh -server-config=standalone-full.xml
 * Executar aghu-deploy

 ### Executar Pacote de Segurança

 * Executar rodar-seguranca
 * Executar rodar-seguranca-full

 #### Caso apresente erro os parâmetros informados para rodar este pacote estão na filter filter-dev-bsb.properties 


 ### Acesso AGHU

 * Para logar no AGHU
 * usuário: aghu
 * senha: qualquerSenha
 
