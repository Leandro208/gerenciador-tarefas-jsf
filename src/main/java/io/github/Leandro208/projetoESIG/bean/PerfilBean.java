package io.github.Leandro208.projetoESIG.bean;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.Part;

import io.github.Leandro208.projetoESIG.dao.DAOException;
import io.github.Leandro208.projetoESIG.dominio.Responsavel;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.services.PerfilService;
import io.github.Leandro208.projetoESIG.util.UsuarioUtils;

@ManagedBean
@ViewScoped
public class PerfilBean extends AbstractBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final long TAMANHO_MAXIMO_FOTO = 2L * 1024L * 1024L;

    private String nome;
    private String email;
    private Date dataNascimento;
    private String senhaAtual;
    private String senhaNova;
    private String confirmacaoSenha;
    private transient Part foto;
    private transient PerfilService perfilService;

    @PostConstruct
    public void inicializar() {
        limparSenhas();
        UsuarioDTO logado = UsuarioUtils.getLogado();
        if (logado == null) return;
        try {
            Responsavel perfil = getPerfilService().buscarPerfil(logado.getId());
            if (perfil == null) {
                addMensagemErro("Não foi possível localizar os dados pessoais desta conta.");
                return;
            }
            nome = perfil.getNome();
            email = perfil.getUsuario().getEmail();
            dataNascimento = perfil.getDataNascimento();
        } catch (DAOException e) {
            addMensagemErro("Não foi possível carregar seu perfil.");
        }
    }

    public String salvar() {
        UsuarioDTO logado = UsuarioUtils.getLogado();
        if (logado == null) return navegar("/login");
        byte[] conteudoFoto = null;
        String tipoMime = null;
        try {
            boolean solicitouTrocaDeSenha = senhaInformada(senhaAtual)
                    || senhaInformada(senhaNova) || senhaInformada(confirmacaoSenha);
            String senhaAtualParaValidar = null;
            String senhaNovaParaSalvar = null;
            if (solicitouTrocaDeSenha) {
                if (!senhaInformada(senhaAtual) || !senhaInformada(senhaNova)
                        || !senhaInformada(confirmacaoSenha)) {
                    addMensagemErro("Para alterar a senha, preencha a senha atual, a nova senha e a confirmação.");
                    return null;
                }
                senhaAtualParaValidar = senhaAtual;
                senhaNovaParaSalvar = senhaNova;
                if (!senhaNova.matches("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")) {
                    addMensagemErro("A nova senha deve ter ao menos 6 caracteres, com letras e números.");
                    return null;
                }
                if (!senhaNova.equals(confirmacaoSenha)) {
                    addMensagemErro("A confirmação da nova senha não corresponde.");
                    return null;
                }
            }
            if (foto != null && foto.getSize() > 0) {
                FotoValidada validada = validarFoto(foto);
                conteudoFoto = validada.conteudo;
                tipoMime = validada.tipoMime;
            }

            if (nome == null || nome.trim().length() < 3 || nome.trim().length() > 50) {
                throw new IllegalArgumentException("O nome deve ter entre 3 e 50 caracteres.");
            }
            if (email == null || !email.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                throw new IllegalArgumentException("Informe um e-mail válido.");
            }
            if (dataNascimento == null) throw new IllegalArgumentException("Informe sua data de nascimento.");

            getPerfilService().atualizarPerfil(logado.getId(), logado.getIdResponsavel(), nome,
                    email, dataNascimento, senhaAtualParaValidar, senhaNovaParaSalvar);
            if (conteudoFoto != null) getPerfilService().salvarFoto(logado.getId(), conteudoFoto, tipoMime);

            logado.setNome(nome.trim());
            logado.setEmail(email.trim());
            addMensagem("Perfil atualizado com sucesso.");
            limparSenhas();
            foto = null;
            return null;
        } catch (IllegalArgumentException e) {
            addMensagemErro(e.getMessage());
            return null;
        } catch (DAOException e) {
            addMensagemErro(e.getMessage());
            return null;
        } catch (IOException e) {
            addMensagemErro("Não foi possível ler a imagem enviada.");
            return null;
        }
    }

    public String removerFoto() {
        UsuarioDTO logado = UsuarioUtils.getLogado();
        if (logado == null) return navegar("/login");
        try {
            getPerfilService().removerFoto(logado.getId());
            addMensagem("Foto de perfil removida.");
        } catch (DAOException e) {
            addMensagemErro(e.getMessage());
        }
        return null;
    }

    private FotoValidada validarFoto(Part parte) throws IOException {
        if (parte.getSize() > TAMANHO_MAXIMO_FOTO) {
            throw new IllegalArgumentException("A foto deve ter no máximo 2 MB.");
        }
        byte[] bytes;
        try (InputStream input = parte.getInputStream()) {
            bytes = lerLimitado(input);
        }
        try (ImageInputStream imageInput = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            if (imageInput == null) throw new IllegalArgumentException("Envie uma imagem JPEG ou PNG válida.");
            Iterator<ImageReader> leitores = ImageIO.getImageReaders(imageInput);
            if (!leitores.hasNext()) throw new IllegalArgumentException("Envie uma imagem JPEG ou PNG válida.");
            ImageReader leitor = leitores.next();
            try {
                leitor.setInput(imageInput, true, true);
                String formato = leitor.getFormatName().toLowerCase(Locale.ROOT);
                int largura = leitor.getWidth(0);
                int altura = leitor.getHeight(0);
                if (largura < 1 || altura < 1 || largura > 4096 || altura > 4096) {
                    throw new IllegalArgumentException("A imagem deve ter dimensões de até 4096 × 4096 pixels.");
                }
                String mime = "jpg".equals(formato) || "jpeg".equals(formato) ? "image/jpeg"
                        : "png".equals(formato) ? "image/png" : null;
                if (mime == null) throw new IllegalArgumentException("São aceitas apenas imagens JPEG ou PNG.");
                return new FotoValidada(bytes, mime);
            } finally {
                leitor.dispose();
            }
        }
    }

    private byte[] lerLimitado(InputStream input) throws IOException {
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int total = 0;
        int leitura;
        while ((leitura = input.read(buffer)) != -1) {
            total += leitura;
            if (total > TAMANHO_MAXIMO_FOTO) throw new IllegalArgumentException("A foto deve ter no máximo 2 MB.");
            output.write(buffer, 0, leitura);
        }
        return output.toByteArray();
    }

    private void limparSenhas() {
        senhaAtual = null;
        senhaNova = null;
        confirmacaoSenha = null;
    }

    private boolean senhaInformada(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private PerfilService getPerfilService() {
        if (perfilService == null) perfilService = new PerfilService();
        return perfilService;
    }

    private static final class FotoValidada {
        private final byte[] conteudo;
        private final String tipoMime;
        private FotoValidada(byte[] conteudo, String tipoMime) {
            this.conteudo = conteudo;
            this.tipoMime = tipoMime;
        }
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Date getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(Date dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getSenhaAtual() { return senhaAtual; }
    public void setSenhaAtual(String senhaAtual) { this.senhaAtual = senhaAtual; }
    public String getSenhaNova() { return senhaNova; }
    public void setSenhaNova(String senhaNova) { this.senhaNova = senhaNova; }
    public String getConfirmacaoSenha() { return confirmacaoSenha; }
    public void setConfirmacaoSenha(String confirmacaoSenha) { this.confirmacaoSenha = confirmacaoSenha; }
    public Part getFoto() { return foto; }
    public void setFoto(Part foto) { this.foto = foto; }
}
