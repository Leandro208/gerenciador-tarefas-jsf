package io.github.Leandro208.projetoESIG.dao;

import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.enums.Funcao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class UsuarioDAO {

    public UsuarioDTO findByEmailSenha(String email, String senhaCriptografada) {
        EntityManager session = new GenericDAOImpl().getSession();
        try {
            String hql =
                    "SELECT u.id, u.email, u.funcao, r.nome, r.id, e.id, e.nome " +
                            "FROM Responsavel r " +
                            "JOIN r.usuario u " +
                            "LEFT JOIN r.equipe e " +
                            "WHERE UPPER(u.email) = :email AND u.senha = :senha";

            Object[] result = (Object[]) session
                    .createQuery(hql)
                    .setParameter("email", email.toUpperCase())
                    .setParameter("senha", senhaCriptografada)
                    .getSingleResult();

            UsuarioDTO dto = new UsuarioDTO();
            dto.setId((Long) result[0]);
            dto.setEmail((String) result[1]);
            dto.setFuncao((Funcao) result[2]);
            dto.setNome((String) result[3]);
            dto.setIdResponsavel((Long) result[4]);

            Long idEquipe = (Long) result[5];
            if (idEquipe != null) {
                dto.setEquipe(new Equipe());
                dto.getEquipe().setId(idEquipe);
                dto.getEquipe().setNome((String) result[6]);
            }
            return dto;

        } catch (NoResultException e) {
            return null;
        } finally {
            session.close();
        }
    }

    public boolean existeUsuarioByEmail(String email) {
        String hql = "FROM Usuario u WHERE UPPER(u.email) = :email";
        EntityManager session = new GenericDAOImpl().getSession();
        try {
            Query query = session.createQuery(hql);
            query.setParameter("email", email.toUpperCase());
            return !query.getResultList().isEmpty();
        } finally {
            session.close();
        }
    }
}
