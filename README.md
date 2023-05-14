### Introdução

A aplicação Java WEB consiste em criar um gerenciador de tarefas onde realiza as seguintes funcionalidades:

- Criar uma Tarefa
- Alterar a tarefa
- Deletar a tarefa
- listar as tarefas
- Concluir Tarefa
- Cadastrar Responsável

Foram criadas 4 views, sendo uma inicial que é onde fica todas as opções na aplicação, uma para cadastrar e editar a tarefa, uma para listar as tarefas, onde nessa fica as funcionalidades de remover, editar(onde redireciona para a pagina de cadastro) e concluir, por ultimo tem a página 
de cadastrar um novo responsável

#### Itens implementados:

- a) Criar uma aplicação Java Web utilizando JavaServer Faces(JSF);
- b) Utilizar persistência em um banco de dados PostgreSQL;
- c) Utilizar JPA;

## Tecnologias utilizadas

- Java8;
- JSF;
- PrimeFaces;
- PostgreSQL;
- JPA;
- Hibernate;
- Maven;

## Diagrama de classes

![gerenciador-tarefas-jsf-2](https://user-images.githubusercontent.com/81039247/210293304-3ca4cae6-fe73-406d-918b-676e51ddcb05.jpg)

## Como executar o projeto localmente


- Clonar repositório
    - git clone https://github.com/Leandro208/gerenciador-tarefas-jsf.git
 
- Abrir o projeto no Eclipse, ou outra IDE que desejar;

- Crie um banco PostgreSQL chamado tarefas, e altere conforme os dados do seu banco o user e password do persistence.xml localizado em src>main>resources>META-INF;

- Adicionar o projeto ao tomcat e iniciar o servidor;

- Abrir no navegador "localhost:8080/{nome_do_projeto}/";

Lembre-se de verificar se esta com uma versão compatível do Java, e também se tem o Banco de dados PostgreSQL 14 instalado.

### Configurando o servidor

Verifique se tem o Tomcat v8.0 instalado e configurado. Caso contrario só seguir os passos no link abaixo:

<a href="https://medium.com/@carlosguilhermeborges/configu%C3%A7%C3%A3o-do-tomcat-no-eclipse-da58064ca497">Como instalar e configurar o Tomcat</a>

