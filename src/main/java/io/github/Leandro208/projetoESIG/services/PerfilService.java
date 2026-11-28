package io.github.Leandro208.projetoESIG.services;

import java.util.Date;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.PerfilDAO;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dto.FotoPerfilDTO;
import io.github.Leandro208.projetoESIG.util.Criptografar;

public class PerfilService {
    public Responsavel buscarPerfil(Long idUsuario) throws DAOException {
        PerfilDAO dao = new PerfilDAO();
        try {
            return dao.buscarResponsavelDoUsuario(idUsuario);
        } finally {
            dao.close();
        }
    }

    public void atualizarPerfil(Long idUsuario, Long idResponsavel, String nome, String email,
                                Date nascimento, String senhaAtual, String senhaNova) throws DAOException {
        PerfilDAO dao = new PerfilDAO();
        try {
            String hash = null;
            if (senhaNova != null && !senhaNova.isEmpty()) {
                String hashAtual = dao.buscarHashSenha(idUsuario);
                if (!Criptografar.verificar(senhaAtual, hashAtual)) {
                    throw new DAOException("A senha atual informada está incorreta.");
                }
                hash = Criptografar.encriptografar(senhaNova);
            }
            dao.atualizarPerfil(idUsuario, idResponsavel, nome, email, nascimento, hash);
        } finally {
            dao.close();
        }
    }

    public void salvarFoto(Long idUsuario, byte[] conteudo, String tipoMime) throws DAOException {
        PerfilDAO dao = new PerfilDAO();
        try {
            dao.salvarFoto(idUsuario, conteudo, tipoMime);
        } finally {
            dao.close();
        }
    }

    public void removerFoto(Long idUsuario) throws DAOException {
        PerfilDAO dao = new PerfilDAO();
        try {
            dao.removerFoto(idUsuario);
        } finally {
            dao.close();
        }
    }

    public FotoPerfilDTO buscarFoto(Long idUsuario) throws DAOException {
        PerfilDAO dao = new PerfilDAO();
        try {
            return dao.buscarFoto(idUsuario);
        } finally {
            dao.close();
        }
    }
}
