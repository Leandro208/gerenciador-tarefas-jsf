package io.github.Leandro208.projetoESIG.config;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dominio.RegistroAcesso;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;

public class SessionListener implements HttpSessionListener{
	  private static final Logger LOGGER = Logger.getLogger(SessionListener.class.getName());
	  @Override
	    public void sessionCreated(HttpSessionEvent se) {
	        
	    }

	    @Override
	    public void sessionDestroyed(HttpSessionEvent se) {
	    	Object usuario = se.getSession().getAttribute("usuario");
	    	if (usuario instanceof UsuarioDTO) {
	    	    UsuarioDTO dto = (UsuarioDTO) usuario;
	    	    RegistroAcesso entrada = dto.getRegistroAcesso();
	    	    if (entrada != null) {
	    	        entrada.setDataSaida(new Date());
	    	        GenericDAO daoEntrada = new GenericDAOImpl();
	    	        try {
						daoEntrada.update(entrada);
					} catch (DAOException e) {
						LOGGER.log(Level.SEVERE, "Falha ao registrar encerramento da sessão", e);
					} finally {
						daoEntrada.close();
					}
	    	    }
	    	}
	    }
}
