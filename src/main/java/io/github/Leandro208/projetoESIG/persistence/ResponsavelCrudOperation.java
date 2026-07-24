package io.github.Leandro208.projetoESIG.persistence;

import javax.persistence.EntityManager;

import io.github.Leandro208.projetoESIG.connection.ConnectionFactory;
import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.UsuarioDAO;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.exception.NegocioException;
import io.github.Leandro208.projetoESIG.util.Criptografar;
import io.github.Leandro208.projetoESIG.validacao.ListaMensagens;

public class ResponsavelCrudOperation extends CadastroCrudOperation {

    @Override
    public void operar(Operacao operacao) throws DAOException, NegocioException {
       validate(operacao);
       if (operacao.getComando().equals(ListaComando.CADASTRAR_USUARIO)){
           cadastrar(operacao);
       }
    }

    private void cadastrar(Operacao operacao) throws DAOException {
        Responsavel responsavel = (Responsavel) operacao.getEntidade();
        Usuario usuario = responsavel.getUsuario();
        usuario.setSenha(Criptografar.encriptografar(usuario.getSenha()));

        EntityManager manager = ConnectionFactory.getEntityManager();
        try {
            manager.getTransaction().begin();
            manager.persist(usuario);
            manager.persist(responsavel);
            manager.getTransaction().commit();
        } catch (Exception e) {
            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            throw new DAOException("Erro ao cadastrar usuário", e);
        } finally {
            manager.close();
        }
    }
    
    @Override
    public void validate(Operacao operacao) throws NegocioException {
        ListaMensagens msg = new ListaMensagens();
        if(operacao.getComando() == ListaComando.CADASTRAR_USUARIO ){
            UsuarioDAO daoUsuario = new UsuarioDAO();
            Usuario usuario = ((Responsavel) operacao.getEntidade()).getUsuario();
            if(daoUsuario.existeUsuarioByEmail(usuario.getEmail())){
                msg.addErro("Já existe um usuário cadastrado com esse email!");
            }
        }

        checkValidation(msg);
    }
}
