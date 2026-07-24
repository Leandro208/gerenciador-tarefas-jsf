package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDaoII;
import io.github.Leandro208.projetoESIG.dao.UsuarioDAO;
import io.github.Leandro208.projetoESIG.dominio.RegistroAcesso;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.util.Criptografar;

public class ResponsavelService implements BaseService<Responsavel>, Serializable{

	private static final long serialVersionUID = 1L;
	
	private GenericDaoII<Responsavel> dao;
	
	public ResponsavelService() {
		dao = new GenericDaoII<Responsavel>();
	}
	

	
	public void remover(Responsavel r) {
		dao.remover(Responsavel.class, r.getId());
	}
	
	public List<Responsavel> buscarTodos() {
		StringBuilder hql = new StringBuilder("select r from Responsavel r join fetch r.usuario order by r.nome");
		return dao.buscarTodos(hql.toString());
	}
	
	public UsuarioDTO verificarCredenciais(String email, String senha) throws DAOException {
		UsuarioDAO dao = new UsuarioDAO();
		UsuarioDTO usuario = dao.findByEmailSenha(email, Criptografar.encriptografar(senha));
		return usuario != null ? usuario : new UsuarioDTO();
	}
	
	public RegistroAcesso registrarAcesso(Usuario usuario) {
		//TODO: Remover esse metodo do service
		GenericDaoII<RegistroAcesso> daoAcesso = new GenericDaoII<>();
		RegistroAcesso registroAcesso = new RegistroAcesso();
		registroAcesso.setData(new Date());
		registroAcesso.setUsuario(usuario);
		registroAcesso.setIp(getClientIp());
	    
	    daoAcesso.salvar(registroAcesso);
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

	public Responsavel buscarPorId(Long id) {
		return dao.buscarPorId(Responsavel.class, id);
	}

}
