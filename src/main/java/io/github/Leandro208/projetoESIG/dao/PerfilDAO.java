package io.github.Leandro208.projetoESIG.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import io.github.Leandro208.projetoESIG.dto.FotoPerfilDTO;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Usuario;

public class PerfilDAO extends GenericDAOImpl {
    public Responsavel buscarResponsavelDoUsuario(Long idUsuario) throws DAOException {
        try {
            List<?> resultados = getSession().createQuery(
                    "SELECT r FROM Responsavel r JOIN FETCH r.usuario WHERE r.usuario.id = :id")
                    .setParameter("id", idUsuario).getResultList();
            return resultados.isEmpty() ? null : (Responsavel) resultados.get(0);
        } catch (Exception e) {
            throw new DAOException("Não foi possível carregar o perfil.", e);
        }
    }

    public String buscarHashSenha(Long idUsuario) throws DAOException {
        try {
            Usuario usuario = getSession().find(Usuario.class, idUsuario);
            return usuario == null ? null : usuario.getSenha();
        } catch (Exception e) {
            throw new DAOException("Não foi possível validar a senha atual.", e);
        }
    }

    public void atualizarPerfil(Long idUsuario, Long idResponsavel, String nome, String email,
                                Date dataNascimento, String hashSenha) throws DAOException {
        EntityManager em = getSession();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Usuario usuario = em.find(Usuario.class, idUsuario);
            Responsavel responsavel = em.find(Responsavel.class, idResponsavel);
            if (usuario == null || responsavel == null || responsavel.getUsuario() == null
                    || !idUsuario.equals(responsavel.getUsuario().getId())) {
                throw new DAOException("O perfil informado não pertence ao usuário autenticado.");
            }

            List<?> duplicados = em.createQuery(
                    "SELECT u.id FROM Usuario u WHERE LOWER(u.email) = LOWER(:email) AND u.id <> :id")
                    .setParameter("email", email.trim()).setParameter("id", idUsuario).getResultList();
            if (!duplicados.isEmpty()) {
                throw new DAOException("Já existe uma conta cadastrada com esse e-mail.");
            }

            usuario.setEmail(email.trim());
            responsavel.setNome(nome.trim());
            responsavel.setDataNascimento(dataNascimento);
            if (hashSenha != null) usuario.setSenha(hashSenha);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            if (e instanceof DAOException) throw (DAOException) e;
            throw new DAOException("Não foi possível salvar o perfil.", e);
        }
    }

    public void salvarFoto(Long idUsuario, byte[] conteudo, String tipoMime) throws DAOException {
        executarFoto(idUsuario, conteudo, tipoMime, false);
    }

    public void removerFoto(Long idUsuario) throws DAOException {
        executarFoto(idUsuario, null, null, true);
    }

    private void executarFoto(Long idUsuario, byte[] conteudo, String tipoMime, boolean remover) throws DAOException {
        EntityManager em = getSession();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (remover) {
                em.createNativeQuery("DELETE FROM pessoa.foto_perfil WHERE id_usuario = :id")
                        .setParameter("id", idUsuario).executeUpdate();
            } else {
                em.createNativeQuery("INSERT INTO pessoa.foto_perfil (id_usuario, conteudo, tipo_mime) "
                        + "VALUES (:id, :conteudo, :mime) ON CONFLICT (id_usuario) DO UPDATE "
                        + "SET conteudo = EXCLUDED.conteudo, tipo_mime = EXCLUDED.tipo_mime")
                        .setParameter("id", idUsuario).setParameter("conteudo", conteudo)
                        .setParameter("mime", tipoMime).executeUpdate();
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Não foi possível atualizar a foto de perfil.", e);
        }
    }

    public FotoPerfilDTO buscarFoto(Long idUsuario) throws DAOException {
        try {
            List<?> resultados = getSession().createNativeQuery(
                    "SELECT conteudo, tipo_mime FROM pessoa.foto_perfil WHERE id_usuario = :id")
                    .setParameter("id", idUsuario).getResultList();
            if (resultados.isEmpty()) return null;
            Object[] resultado = (Object[]) resultados.get(0);
            return new FotoPerfilDTO((byte[]) resultado[0], (String) resultado[1]);
        } catch (Exception e) {
            throw new DAOException("Não foi possível carregar a foto de perfil.", e);
        }
    }
}
