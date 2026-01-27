package io.github.Leandro208.projetoESIG.config;

import org.flywaydb.core.Flyway;

import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class FlywayInitListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(FlywayInitListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            LOGGER.info(">>> INICIANDO FLYWAY <<<");

            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/tarefasDS");

            Flyway flyway = Flyway.configure()
                    .dataSource(ds)
                    .load();

            flyway.migrate();

            LOGGER.info(">>> FLYWAY FINALIZADO <<<");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao executar Flyway", e);
        }
    }
}
