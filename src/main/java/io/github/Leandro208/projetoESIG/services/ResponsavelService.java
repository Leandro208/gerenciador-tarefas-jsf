package io.github.Leandro208.projetoESIG.services;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dao.UsuarioDAO;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
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
		if (email == null || email.trim().isEmpty() || senha == null) return new UsuarioDTO();
		UsuarioDAO dao = new UsuarioDAO();
		try{
			UsuarioDTO usuario = dao.findByEmail(email);
			if (usuario == null || !Criptografar.verificar(senha, usuario.getHashSenha())) {
				return new UsuarioDTO();
			}
			if (Criptografar.precisaAtualizar(usuario.getHashSenha())) {
				dao.atualizarSenha(usuario.getId(), Criptografar.encriptografar(senha));
			}
			usuario.setHashSenha(null);
			return usuario;
		} finally {
			dao.close();
		}

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
