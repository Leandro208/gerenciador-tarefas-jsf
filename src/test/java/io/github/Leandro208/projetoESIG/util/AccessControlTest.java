package io.github.Leandro208.projetoESIG.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.github.Leandro208.projetoESIG.dominio.Equipe;
import io.github.Leandro208.projetoESIG.dto.UsuarioDTO;
import io.github.Leandro208.projetoESIG.enums.Funcao;
import io.github.Leandro208.projetoESIG.dto.FormConsultaTarefaDto;

public class AccessControlTest {
	@Test
	public void usuarioAcessaSomenteSuaEquipe() {
		UsuarioDTO usuario = new UsuarioDTO();
		Equipe equipe = new Equipe();
		equipe.setId(7L);
		usuario.setEquipe(equipe);

		assertTrue(AccessControl.podeAcessarEquipe(usuario, 7L));
		assertFalse(AccessControl.podeAcessarEquipe(usuario, 8L));
		assertFalse(AccessControl.podeAcessarEquipe(usuario, null));
	}

	@Test
	public void administradorAcessaQualquerEquipe() {
		UsuarioDTO usuario = new UsuarioDTO();
		usuario.setFuncao(Funcao.ADM);

		assertTrue(AccessControl.podeAcessarEquipe(usuario, 7L));
		assertTrue(AccessControl.podeAcessarEquipe(usuario, null));
	}

	@Test
	public void consultaDeUsuarioNaoPodeTrocarEquipePeloFormulario() {
		UsuarioDTO usuario = new UsuarioDTO();
		Equipe equipePermitida = new Equipe();
		equipePermitida.setId(7L);
		usuario.setEquipe(equipePermitida);
		FormConsultaTarefaDto consulta = new FormConsultaTarefaDto();
		Equipe adulterada = new Equipe();
		adulterada.setId(99L);
		consulta.setEquipe(adulterada);

		AccessControl.limitarConsultaDeTarefas(usuario, consulta);

		assertTrue(consulta.getEquipe().getId().equals(7L));
	}

	@Test
	public void usuarioSemEquipeNaoRecebeTarefasSemEquipeAssociada() {
		FormConsultaTarefaDto consulta = new FormConsultaTarefaDto();
		AccessControl.limitarConsultaDeTarefas(new UsuarioDTO(), consulta);

		assertFalse(AccessControl.podeAcessarEquipe(new UsuarioDTO(), null));
		assertTrue(consulta.getEquipe().getId() < 0L);
	}
}
