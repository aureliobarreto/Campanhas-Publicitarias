package br.com.campanhaspublicitarias.listeners;

import java.math.BigDecimal;

import com.sankhya.util.TimeUtils;

import br.com.sankhya.jape.event.PersistenceEvent;
import br.com.sankhya.jape.event.PersistenceEventAdapter;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.auth.AuthenticationInfo;
import br.com.sankhya.ws.ServiceContext;

public class CampanhasPublicitariasListener extends PersistenceEventAdapter{
	/** * */
	private static final long serialVersionUID = 1L;

	@Override
	public void beforeInsert(PersistenceEvent event) throws Exception {
		
		DynamicVO newRegistro = (DynamicVO) event.getVo();
		
		newRegistro.setProperty("DHALTER", TimeUtils.getNow());
		newRegistro.setProperty("ATIVO", "S");
		
		BigDecimal usuarioLogado = ((AuthenticationInfo)ServiceContext.getCurrent().getAutentication()).getUserID();
		
		newRegistro.setProperty("CODUSU", usuarioLogado);
		
	}
	
	@Override
	public void beforeUpdate(PersistenceEvent event) throws Exception {
		
		DynamicVO newRegistro = (DynamicVO) event.getVo();
		
		newRegistro.setProperty("DHALTER", TimeUtils.getNow());
		
		BigDecimal usuarioLogado = ((AuthenticationInfo)ServiceContext.getCurrent().getAutentication()).getUserID();
		
		newRegistro.setProperty("CODUSU", usuarioLogado);	
	
		
		
	}
}
