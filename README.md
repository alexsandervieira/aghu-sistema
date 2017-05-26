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
 
 * Na IDE acessar configurações de run
 * Configurar o diretório, informar o diretório do projeto Seudiretório/aghu_jee/aghu
 * Informar os comandos no command line: clean install -DskipTests -Dpmd.skip=true 
 * Acessar configurações de run
 * Configurar o diretório, informar o diretório do projetoaghu_jee/aghu-entidades
 * Informar os comandos no command line: clean install

 #### Depêndencias .m2

 