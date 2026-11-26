package io.github.Leandro208.projetoESIG.sincronizacao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import io.github.Leandro208.projetoESIG.connection.ConnectionFactory;
import io.github.Leandro208.projetoESIG.dominio.RegistroAcesso;

/**
 * Sincronizador responsável por registrar o acesso do usuário no sistema.
 * Insere o RegistroAcesso de forma simples e direta.
 */
public class SincronizadorRegistroAcesso {

    private static EntityManagerFactory factory = Persistence.createEntityManagerFactory("tarefas");

    private EntityManager em;

    public SincronizadorRegistroAcesso() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory("tarefas");
        }
        this.em = factory.createEntityManager();
    }

    /**
     * Registra o acesso do usuário no sistema.
     * Insere apenas o RegistroAcesso na tabela seguranca.registro_acesso.
     * 
     * @param r O entity RegistroAcesso a ser salvo
     */
    public void registrarAcesso(RegistroAcesso r) {
        em.getTransaction().begin();
        em.persist(r);
        em.getTransaction().commit();
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
