# Gerenciador de Tarefas JSF

Aplicação web Java para gerenciamento de tarefas, equipes, responsáveis e comentários. O projeto usa JSF 2.2, PrimeFaces 7, JPA/Hibernate, PostgreSQL e Maven.

Este guia cobre a execução no IntelliJ IDEA, Eclipse, outros ambientes que importam projetos Maven e Tomcat iniciado manualmente.

## 1. Requisitos

- JDK 8 instalado e configurado. Use um JDK, não apenas um JRE.
- Maven 3 instalado.
- PostgreSQL 14 ou superior.
- Apache Tomcat 8.5.
- Portas 5432 (PostgreSQL) e 8080 (Tomcat) disponíveis, ou ajuste as configurações locais correspondentes.

Confira o Java e o Maven em um terminal:

```text
java -version
mvn -version
```

O Maven deve indicar que está usando o JDK 8.

## 2. Obter e abrir o projeto

Clone o repositório e entre na pasta:

```bash
git clone https://github.com/Leandro208/gerenciador-tarefas-jsf.git
cd gerenciador-tarefas-jsf
```

Abra a pasta que contém `pom.xml` como projeto Maven:

- **IntelliJ IDEA:** escolha *Open* e selecione a pasta do projeto. Confirme a importação Maven e defina o Project SDK como JDK 8.
- **Eclipse:** use *File > Import > Existing Maven Projects*, selecione a pasta e conclua a importação. Configure o JRE do projeto para o JDK 8.
- **Outra IDE:** importe o `pom.xml` como projeto Maven. Também é possível compilar e executar sem IDE, seguindo as seções de build e Tomcat manual.

## 3. Criar o banco PostgreSQL

Crie um banco vazio chamado `tarefas`. Por exemplo, no `psql`:

```sql
CREATE DATABASE tarefas;
```

Use uma conta PostgreSQL com permissão para criar schemas e tabelas nesse banco. Na primeira inicialização, o Flyway executa as migrações do projeto e cria os schemas e as tabelas. Não execute os arquivos de migração manualmente.

Para a execução local descrita neste guia, configure o usuário e a senha do PostgreSQL diretamente no recurso JNDI de `src/main/webapp/META-INF/context.xml`. Ajuste esses valores para seu banco. Não use credenciais reais de produção nem envie segredos para o Git.

## 4. Instalar o driver PostgreSQL no Tomcat

O Tomcat cria o DataSource JNDI e precisa conseguir carregar o driver PostgreSQL. A dependência `postgresql` está com escopo `provided`, portanto o driver deve estar no classloader do servidor.

Depois que o Maven baixar as dependências, localize o arquivo `postgresql-42.2.23.jre7.jar` no repositório local do Maven:

- Linux/macOS: `~/.m2/repository/org/postgresql/postgresql/42.2.23.jre7/`
- Windows: `%USERPROFILE%\.m2\repository\org\postgresql\postgresql\42.2.23.jre7\`

Copie o JAR para a pasta `lib` do Tomcat (`$CATALINA_HOME/lib` no Linux/macOS ou `%CATALINA_HOME%\lib` no Windows). Reinicie o Tomcat após instalar o driver.

## 5. Configurar o DataSource JNDI

A aplicação procura o DataSource pelo nome `java:/comp/env/jdbc/tarefasDS`. O recurso fica em `src/main/webapp/META-INF/context.xml` e contém uma configuração local de exemplo para PostgreSQL em `localhost:5432/tarefas`.

Abra esse arquivo e ajuste `url`, `username` e `password` conforme seu PostgreSQL. O driver `org.postgresql.Driver` também precisa estar disponível no Tomcat, conforme a seção anterior.

O Tomcat pode usar um descritor externo em `CATALINA_BASE/conf/Catalina/localhost/projetoESIG.xml` em preferência ao `META-INF/context.xml` da aplicação. Se esse arquivo existir e não tiver o elemento `<Resource name="jdbc/tarefasDS" ...>`, remova-o da configuração local ou inclua nele o mesmo recurso. No IntelliJ, o caminho exato de `CATALINA_BASE` aparece no console ao iniciar o servidor. [Documentação do contexto no Tomcat 8.5](https://tomcat.apache.org/tomcat-8.5-doc/config/context.html).

## 6. Compilar e empacotar

Na raiz do projeto, execute:

```bash
mvn clean package
```

O WAR gerado estará em:

```text
target/projetoESIG-0.0.1-SNAPSHOT.war
```

## 7. Executar pela IDE

### IntelliJ IDEA

1. Adicione um servidor local Tomcat 8.5 em *Run/Debug Configurations*.
2. Selecione um JDK 8 para o servidor.
3. Na aba *Deployment*, adicione o artefato `projetoESIG:war exploded` ou o WAR empacotado.
4. Defina o contexto da aplicação como `/projetoESIG`.
5. Ajuste as credenciais em `src/main/webapp/META-INF/context.xml` e confira se não há um descritor externo do Tomcat sobrepondo essa configuração.
6. Inicie o servidor e confira o console do Tomcat.

### Eclipse

1. Adicione um servidor Apache Tomcat 8.5 na visão *Servers* e selecione um JDK 8.
2. Adicione o projeto ao servidor.
3. Confirme o contexto `/projetoESIG` nas propriedades de publicação do servidor.
4. Ajuste as credenciais em `src/main/webapp/META-INF/context.xml` e confira se não há um descritor externo sobrepondo essa configuração.
5. Inicie o Tomcat e confira a saída do servidor.

## 8. Executar em Tomcat manualmente

1. Confirme que o JAR do PostgreSQL está em `CATALINA_HOME/lib` e que `jdbc/tarefasDS` está configurado em `META-INF/context.xml` ou no descritor externo do contexto.
2. Copie `target/projetoESIG-0.0.1-SNAPSHOT.war` para `CATALINA_BASE/webapps/` com o nome `projetoESIG.war`.
3. Inicie o Tomcat:

   Linux/macOS:

   ```bash
   "$CATALINA_HOME/bin/startup.sh"
   ```

   Windows:

   ```bat
   "%CATALINA_HOME%\bin\startup.bat"
   ```

4. Acesse [http://localhost:8080/projetoESIG/login.jsf](http://localhost:8080/projetoESIG/login.jsf).

Outras IDEs podem seguir esse mesmo fluxo: importar como Maven, executar `mvn clean package` e publicar o WAR em Tomcat 8.5.

## 9. Primeiro acesso e administrador local

Crie uma conta pela opção de cadastro da tela de login. Contas novas são criadas com o perfil `USER`. Para habilitar localmente as funções administrativas, promova a conta que você acabou de criar no banco `tarefas`:

```sql
UPDATE pessoa.usuario
SET funcao = 'ADM'
WHERE LOWER(email) = LOWER('seu-email@exemplo.com');
```

Use o mesmo e-mail informado no cadastro. Confirme que uma linha foi atualizada e entre novamente na aplicação. Faça esse procedimento apenas no banco local de desenvolvimento.

## 10. Confirmar a inicialização

No log do Tomcat, confirme que o Flyway iniciou e terminou sem erro, com a mensagem `FLYWAY FINALIZADO`, e que o contexto `/projetoESIG` foi publicado. Depois, entre com a conta cadastrada, crie uma equipe e comece a cadastrar tarefas.

Novas senhas são armazenadas com PBKDF2-HMAC-SHA-256 e salt aleatório. Senhas antigas no formato SHA-256 são atualizadas depois do primeiro login válido.

## Solução de problemas

- **`NameNotFoundException` para `jdbc/tarefasDS`:** confira se `META-INF/context.xml` contém `<Resource name="jdbc/tarefasDS" ...>`. Se existir `CATALINA_BASE/conf/Catalina/localhost/projetoESIG.xml`, lembre que ele pode substituir o descritor da aplicação: remova-o ou inclua nele o mesmo `Resource`.
- **`ClassNotFoundException: org.postgresql.Driver`:** confira se `postgresql-42.2.23.jre7.jar` está na pasta `lib` do Tomcat e reinicie o servidor.
- **Conexão recusada:** confirme que o PostgreSQL está ativo, que o banco existe e que host e porta na URL JDBC estão corretos.
- **Falha de autenticação no banco:** confira usuário e senha somente no descritor local do Tomcat.
- **Falha do Flyway ao criar schemas ou tabelas:** conceda ao usuário PostgreSQL permissão para criar esses objetos no banco `tarefas`.
- **Tomcat não inicia ou o IDE publica em outra URL:** confirme o JDK configurado, o contexto `/projetoESIG` e a porta HTTP configurada no servidor.
