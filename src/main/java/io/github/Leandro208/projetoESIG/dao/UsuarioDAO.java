package io.github.Leandro208.projetoESIG.dao;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.enums.Funcao;

public class UsuarioDAO extends GenericDAOImpl {

    public UsuarioDTO findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        String hql =
                "SELECT u.id, u.email, u.funcao, r.nome, r.id, e.id, e.nome, u.senha " +
                        "FROM Responsavel r " +
                        "JOIN r.usuario u " +
                        "LEFT JOIN r.equipe e " +
                        "WHERE UPPER(u.email) = :email";

        List<?> results = getSession().createQuery(hql)
                .setParameter("email", email.trim().toUpperCase(Locale.ROOT))
                .getResultList();
        if (results.isEmpty()) return null;
        Object[] result = (Object[]) results.get(0);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId((Long) result[0]);
        dto.setEmail((String) result[1]);
        dto.setFuncao((Funcao) result[2]);
        dto.setNome((String) result[3]);
        dto.setIdResponsavel((Long) result[4]);
        dto.setHashSenha((String) result[7]);

        Long idEquipe = (Long) result[5];
        if (idEquipe != null) {
            dto.setEquipe(new Equipe());
            dto.getEquipe().setId(idEquipe);
            dto.getEquipe().setNome((String) result[6]);
        }
        return dto;
    }

    public void atualizarSenha(Long idUsuario, String hash) throws DAOException {
        EntityTransaction tx = getSession().getTransaction();
        try {
            tx.begin();
            getSession().createQuery("UPDATE Usuario u SET u.senha = :hash WHERE u.id = :id")
                    .setParameter("hash", hash).setParameter("id", idUsuario).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new DAOException("Não foi possível atualizar o hash da senha.", e);
        }
    }

    public Object[] buscarContextoAutorizacao(Long idUsuario) {
        List<?> resultados = getSession().createQuery(
                "SELECT u.funcao, r.id, e.id, e.nome FROM Responsavel r " +
                "JOIN r.usuario u LEFT JOIN r.equipe e WHERE u.id = :id")
                .setParameter("id", idUsuario).getResultList();
        return resultados.isEmpty() ? null : (Object[]) resultados.get(0);
    }

    public boolean existeUsuarioByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String hql = "FROM Usuario u WHERE UPPER(u.email) = :email";
        Query query = getSession().createQuery(hql);
        query.setParameter("email", email.trim().toUpperCase(Locale.ROOT));
        return !query.getResultList().isEmpty();

    }
}
