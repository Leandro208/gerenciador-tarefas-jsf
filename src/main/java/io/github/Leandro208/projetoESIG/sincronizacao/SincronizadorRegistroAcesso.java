package io.github.Leandro208.projetoESIG.sincronizacao;

import javax.persistence.EntityManager;

import io.github.Leandro208.projetoESIG.connection.ConnectionFactory;
import io.github.Leandro208.projetoESIG.dominio.RegistroAcesso;

/**
 * Sincronizador responsável por registrar o acesso do usuário no sistema.
 * Insere o RegistroAcesso de forma simples e direta.
 */
public class SincronizadorRegistroAcesso {

    private EntityManager em;

    public SincronizadorRegistroAcesso() {
        this.em = ConnectionFactory.getEntityManager();
    }

    /**
     * Registra o acesso do usuário no sistema.
     * Insere apenas o RegistroAcesso na tabela seguranca.registro_acesso.
     * 
     * @param r O entity RegistroAcesso a ser salvo
     */
    public void registrarAcesso(RegistroAcesso r) {
        try {
            em.getTransaction().begin();
            em.persist(r);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    /**
     * Método factory para criar uma instância do sincronizador.
     * 
     * @return Nova instância de SincronizadorRegistroAcesso
     */
    public static SincronizadorRegistroAcesso novoInstancia() {
        return new SincronizadorRegistroAcesso();
    }
}
