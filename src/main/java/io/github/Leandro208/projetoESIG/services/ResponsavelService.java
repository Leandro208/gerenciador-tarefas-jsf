package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import io.github.Leandro208.projetoESIG.dao.GenericDao;
import io.github.Leandro208.projetoESIG.entities.RegistroEntrada;
import io.github.Leandro208.projetoESIG.entities.Responsavel;
import io.github.Leandro208.projetoESIG.util.Criptografar;
import io.github.Leandro208.projetoESIG.util.Message;

public class ResponsavelService implements BaseService<Responsavel>, Serializable{

	private static final long serialVersionUID = 1L;
	
	private GenericDao<Responsavel> dao;
	
	public ResponsavelService() {
		dao = new GenericDao<Responsavel>();
	}
	
	@Override
	public Responsavel buscarPorId(Long id) {
		return dao.buscarPorId(Responsavel.class, id);
	}
	@Override
	public void salvar(Responsavel responsavel) {
		
		List<Responsavel> resp = buscarTodos();
		for(Responsavel r : resp) {
			if(r.getEmail().equalsIgnoreCase(responsavel.getEmail())) {
				Message.warn("Ja existe uma conta cadastrada com esse email!");
				return;
			}
		}
		
		responsavel.setDataCadastro(new Date());;
		responsavel.setSenha(Criptografar.encriptografar(responsavel.getSenha()));
		dao.salvar(responsavel);
	}
	@Override
	public void remover(Responsavel r) {
		dao.remover(Responsavel.class, r.getId());
	}
	
	public List<Responsavel> buscarTodos() {
		List<Responsavel> resultado = new ArrayList<Responsavel>();
		StringBuilder hql = new StringBuilder("select r from Responsavel r order by r.nome");
		resultado = dao.buscarTodos(hql.toString());
		return resultado;
	}
	
	public Responsavel verificarCredenciais(String email, String senha) {
		List<Responsavel> res = new ArrayList<>();
		res = buscarTodos();
		for (Responsavel r : res) {
			if (r.getEmail().equalsIgnoreCase(email) && r.getSenha().equals(Criptografar.encriptografar(senha))) {
				return r;
			}
		}
		return new Responsavel();
	}
	
	public RegistroEntrada registrarEntrada(Responsavel usuario) {
		GenericDao<RegistroEntrada> daoEntrada = new GenericDao<>();
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

}
