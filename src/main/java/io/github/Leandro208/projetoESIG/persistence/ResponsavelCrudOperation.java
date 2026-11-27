package io.github.Leandro208.projetoESIG.persistence;

import java.util.Locale;

import javax.persistence.EntityManager;

import io.github.Leandro208.projetoESIG.connection.ConnectionFactory;
import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dao.GenericDAO;
import io.github.Leandro208.projetoESIG.dao.GenericDAOImpl;
import io.github.Leandro208.projetoESIG.dao.UsuarioDAO;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dominio.Usuario;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.exception.NegocioException;
import io.github.Leandro208.projetoESIG.util.Criptografar;
import io.github.Leandro208.projetoESIG.util.AccessControl;
import io.github.Leandro208.projetoESIG.validacao.ListaMensagens;

public class ResponsavelCrudOperation extends CadastroCrudOperation {

    @Override
    public void operar(Operacao operacao) throws DAOException, NegocioException {
       validate(operacao);
       if (operacao.getComando().equals(ListaComando.CADASTRAR_USUARIO)){
           cadastrar(operacao);
       } else if (operacao.getComando().equals(ListaComando.ALTERAR_FUNCAO_USUARIO)){
           alterarFuncao(operacao, AccessControl.requireAdmin());
       }
    }

    private void alterarFuncao(Operacao operacao, UsuarioDTO atual) {
        Responsavel recebido = (Responsavel) operacao.getEntidade();
        if (recebido == null || recebido.getUsuario() == null || recebido.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Usuário inválido para alteração de perfil.");
        }
        if (atual.getId().equals(recebido.getUsuario().getId())) {
            throw new SecurityException("Não é permitido alterar o próprio perfil.");
        }
        GenericDAOImpl dao = new GenericDAOImpl(operacao);
        try {
            Usuario usuario = dao.findById(recebido.getUsuario().getId(), Usuario.class);
            if (usuario == null) {
                throw new IllegalArgumentException("Usuário não encontrado.");
            }
            usuario.setFuncao(recebido.getUsuario().getFuncao());
            dao.update(usuario);
        } catch (DAOException e) {
            throw new IllegalStateException("Não foi possível alterar o perfil.", e);
        } finally {
            dao.close();
        }
    }

    private void cadastrar(Operacao operacao) throws DAOException {
        Responsavel responsavel = (Responsavel) operacao.getEntidade();
        Usuario usuario = responsavel.getUsuario();
        EntityManager manager = ConnectionFactory.getEntityManager();
        String emailInformado = usuario.getEmail();
        String senhaInformada = usuario.getSenha();
        String senhaHash = Criptografar.encriptografar(senhaInformada);
        usuario.setEmail(emailInformado.trim().toLowerCase(Locale.ROOT));
        usuario.setSenha(senhaHash);

        try {
            manager.getTransaction().begin();
            manager.persist(usuario);
            manager.persist(responsavel);
            manager.getTransaction().commit();
        } catch (Exception e) {
            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            usuario.setEmail(emailInformado);
            usuario.setSenha(senhaInformada);
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
            try {
                Usuario usuario = ((Responsavel) operacao.getEntidade()).getUsuario();
                if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
                    msg.addErro("E-mail é obrigatório.");
                } else if(daoUsuario.existeUsuarioByEmail(usuario.getEmail())){
                    msg.addErro("Já existe um usuário cadastrado com esse email!");
                }
            } finally {
                daoUsuario.close();
            }

        }

        checkValidation(msg);
    }
}
