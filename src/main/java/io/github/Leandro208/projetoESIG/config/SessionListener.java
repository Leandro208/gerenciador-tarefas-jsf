package io.github.Leandro208.projetoESIG.config;

import java.util.Date;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.entities.RegistroEntrada;
import io.github.Leandro208.projetoESIG.entities.Responsavel;

@WebListener
public class SessionListener implements HttpSessionListener{
	  @Override
	    public void sessionCreated(HttpSessionEvent se) {
	        
	    }

	    @Override
	    public void sessionDestroyed(HttpSessionEvent se) {
	    	Object responsavelObj = se.getSession().getAttribute("responsavel");
	    	if (responsavelObj instanceof Responsavel) {
	    	    Responsavel responsavel = (Responsavel) responsavelObj;
	    	    RegistroEntrada entrada = responsavel.getRegistroEntrada();
	    	    if (entrada != null) {
	    	        entrada.setDataSaida(new Date());
	    	        GenericDAO daoEntrada = new GenericDAOImpl();
	    	        try {
						daoEntrada.create(entrada);
					} catch (DAOException e) {
						e.printStackTrace();
					}
	    	        System.out.println("Saindo: " + entrada);
	    	    }
	    	}
	    }
}
