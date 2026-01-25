package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDaoII;
import io.github.Leandro208.projetoESIG.dao.UsuarioDAO;
import io.github.Leandro208.projetoESIG.dominio.RegistroEntrada;
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
		List<Responsavel> resultado = new ArrayList<Responsavel>();
		StringBuilder hql = new StringBuilder("select r from Responsavel r join fetch r.usuario order by r.nome");
		resultado = dao.buscarTodos(hql.toString());
		return resultado;
	}
	
	public UsuarioDTO verificarCredenciais(String email, String senha) throws DAOException {
		UsuarioDAO dao = new UsuarioDAO();
		UsuarioDTO usuario = dao.findByEmailSenha(email, Criptografar.encriptografar(senha));
		return usuario == null ? new UsuarioDTO() : usuario;
	}
	
	public RegistroEntrada registrarEntrada(Usuario usuario) {
		GenericDaoII<RegistroEntrada> daoEntrada = new GenericDaoII<>();
		RegistroEntrada entrada = new RegistroEntrada();
		entrada.setData(new Date());
		entrada.setUsuario(usuario);
		
	    entrada.setIp(getClientIp());
	    
	    daoEntrada.salvar(entrada);
	    System.out.println("Registro de entrada: " + entrada);
		return entrada;
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
