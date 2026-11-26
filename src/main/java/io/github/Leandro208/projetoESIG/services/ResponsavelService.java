package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dao.UsuarioDAO;
import io.github.Leandro208.projetoESIG.dominio.RegistroAcesso;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.util.Criptografar;

public class ResponsavelService implements BaseService<Responsavel>, Serializable{

	private static final long serialVersionUID = 1L;

	public List<Responsavel> buscarTodos() throws DAOException {
		GenericDAO dao = new GenericDAOImpl();
		try {
			return (List<Responsavel>) dao.findAll(Responsavel.class);
		} finally {
			dao.close();
		}
	}
	
	public UsuarioDTO verificarCredenciais(String email, String senha) throws DAOException {
		UsuarioDAO dao = new UsuarioDAO();
		try{
			UsuarioDTO usuario = dao.findByEmailSenha(email, Criptografar.encriptografar(senha));
			return usuario != null ? usuario : new UsuarioDTO();
		} finally {
			dao.close();
		}

	}
	
	public RegistroAcesso registrarAcesso(Usuario usuario) throws DAOException {
		//TODO: Remover esse metodo do service
		GenericDAO dao = new GenericDAOImpl();
		RegistroAcesso registroAcesso = new RegistroAcesso();
		registroAcesso.setData(new Date());
		registroAcesso.setUsuario(usuario);
		registroAcesso.setIp(getClientIp());
	    
	    dao.create(registroAcesso);
		return registroAcesso;
	}
	
	public String getClientIp() {
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
	    String ip = request.getHeader("X-Forwarded-For");
	    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
	        ip = request.getRemoteAddr();
	    }
	    return ip;
	}

	public Responsavel buscarPorId(Long id) throws DAOException {
       	GenericDAO dao = new GenericDAOImpl();
        try {
            return dao.findById(id,Responsavel.class);
        } finally {
			dao.close();
		}
    }

}
