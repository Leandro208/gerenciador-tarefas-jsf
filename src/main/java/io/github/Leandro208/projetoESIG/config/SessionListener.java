package io.github.Leandro208.projetoESIG.config;

import java.util.Date;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dominio.RegistroEntrada;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;

@WebListener
public class SessionListener implements HttpSessionListener{
	  @Override
	    public void sessionCreated(HttpSessionEvent se) {
	        
	    }

	    @Override
	    public void sessionDestroyed(HttpSessionEvent se) {
	    	Object usuario = se.getSession().getAttribute("usuario");
	    	if (usuario instanceof UsuarioDTO) {
	    	    UsuarioDTO dto = (UsuarioDTO) usuario;
	    	    RegistroEntrada entrada = dto.getEntrada();
	    	    if (entrada != null) {
	    	        entrada.setDataSaida(new Date());
	    	        GenericDAO daoEntrada = new GenericDAOImpl();
	    	        try {
						daoEntrada.update(entrada);
					} catch (DAOException e) {
						e.printStackTrace();
					}
	    	        System.out.println("Saindo: " + entrada);
	    	    }
	    	}
	    }
}
