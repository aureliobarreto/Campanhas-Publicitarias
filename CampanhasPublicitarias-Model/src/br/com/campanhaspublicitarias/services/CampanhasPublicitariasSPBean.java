package br.com.campanhaspublicitarias.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import com.google.gson.JsonObject;

import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.VOProperty;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.jape.wrapper.JapeWrapper;
import br.com.sankhya.jape.wrapper.fluid.FluidCreateVO;
import br.com.sankhya.modelcore.util.BaseSPBean;
import br.com.sankhya.modelcore.util.MGECoreParameter;
import br.com.sankhya.ws.ServiceContext;

/**
 * @author Aurélio
 * @ejb.bean name="CampanhasSP"
 * jndi-name="br/com/campanhaspublicitarias/services/CampanhasSP"
 * type="Stateless" 
 * transaction-type="Container"
 * view-type="remote"
 * 
 * @ejb.transaction type="Supports"
 * 
 * @ejb.util generate="false"
 */

public class CampanhasPublicitariasSPBean extends BaseSPBean implements SessionBean {
	/** * */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @ejb.interface-method tview-tipe="remote"
	 * @ejb.transaction type="Required"
	 */	
	public void inserirAssinatura(ServiceContext sctx) {
		
		
		SessionHandle hnd = null;
		
		try {		
			
			
			hnd = JapeSession.open();
			
		
		
			
			JapeWrapper gerenFreDetDao = JapeFactory.dao("FinanceiroCampanha");
			
			//BigDecimal vlrAssinatura = (BigDecimal) MGECoreParameter.getParameter("arb.campanhaspublicitarias", "br.com.campanhaspublicitarias.param.campercmulta");
			
			JsonObject req = sctx.getJsonRequestBody();
			
			BigDecimal codCampanha = req.get("CODCAMP").getAsBigDecimal();
			
			JapeWrapper campanhaDao = JapeFactory.dao("CampanhasPublicitarias");
			DynamicVO campanhaVo = campanhaDao.findByPK(codCampanha);
			
			BigDecimal vlrAssinatura = campanhaVo.asBigDecimal("VLRASSINATURA");
			
			// Pegando a data de referência
			String dataTexto = req.get("DTREF").getAsString();
			System.out.println("****--------------*********> "+dataTexto);     
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	        Date dataConvertida = sdf.parse(dataTexto);  	 
	        
	    	// Pegando a data de vencimento
			String dataTexto2 = req.get("DTVENC").getAsString();
	      
	        
	        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	        Date dataConvertida2 = sdf2.parse(dataTexto2);  	     
	        
			
			Timestamp dtReferencia = new Timestamp(dataConvertida.getTime());
			Timestamp dtVencimento = new Timestamp(dataConvertida2.getTime());
				
			
			gerenFreDetDao.create()
			.set("CODCAMP", codCampanha)
			.set("VLRTOT", vlrAssinatura)
			.set("DTVENC", dtVencimento)
			.set("DTREFERENCIA", dtReferencia)				
			.save();
			
			JsonObject response = new JsonObject();
			response.addProperty("response", "Assinatura inserida com sucesso.");
			sctx.setJsonResponse(response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JsonObject response = new JsonObject();
			response.addProperty("response", "Erro ao inserir assinatura: \n"+e.getMessage());
			sctx.setJsonResponse(response);
			e.printStackTrace();
		}finally {
			JapeSession.close(hnd);
		}		
	}

	/**
	 * @ejb.interface-method tview-tipe="remote"
	 * @ejb.transaction type="Required"
	 */	
	public void aplicarMulta(ServiceContext sctx) {
		
		
		SessionHandle hnd = null;
		
		try {		
			
			BigDecimal percMulta = (BigDecimal) MGECoreParameter.getParameter("arb.campanhaspublicitarias", "br.com.campanhaspublicitarias.param.campercmulta");
			
			hnd = JapeSession.open();
			
			JapeWrapper financeiroCampanhaDao = JapeFactory.dao("FinanceiroCampanha");
			
			JsonObject req = sctx.getJsonRequestBody();
			
			BigDecimal codCamp= req.get("CODCAMP").getAsBigDecimal();
			BigDecimal id = req.get("ID").getAsBigDecimal();
			BigDecimal vlrTot = req.get("VLRTOT").getAsBigDecimal();
			
			financeiroCampanhaDao.prepareToUpdateByPK(id,codCamp)
			.set("VLRTOT", vlrTot.add(vlrTot.multiply(percMulta.divide(new BigDecimal(100)))))
			.set("VLRMULTA", vlrTot.multiply(percMulta.divide(new BigDecimal(100))))
			.update();
			
			JsonObject response = new JsonObject();
			response.addProperty("response", "O multa de atraso para a assinatura "+id+" foi aplicada com sucesso!");
			sctx.setJsonResponse(response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JsonObject response = new JsonObject();
			response.addProperty("response", "Erro ao aplicar multa \n"+e.getMessage());
			sctx.setJsonResponse(response);
			e.printStackTrace();
		}finally {
			JapeSession.close(hnd);
		}		
	}
	
	/**
	 * @ejb.interface-method tview-tipe="remote"
	 * @ejb.transaction type="Required"
	 */	
	public void duplicarAssinatura (ServiceContext sctx) {
		
		
		SessionHandle hnd = null;
		
		try {		
			
			
			hnd = JapeSession.open();
			
			JapeWrapper financeiroCampanhaDao = JapeFactory.dao("FinanceiroCampanha");
			
			
			
			JsonObject req = sctx.getJsonRequestBody();
			
			BigDecimal codCamp = req.get("CODCAMP").getAsBigDecimal();
			BigDecimal id = req.get("ID").getAsBigDecimal();
			
			DynamicVO vo = financeiroCampanhaDao.findByPK(id,codCamp);
			
			vo.setProperty("ID", null);
			
			DynamicVO newVo = duplicar(vo, "FinanceiroCampanha"); 
			
			
			JsonObject response = new JsonObject();
			response.addProperty("response", "O registro "+id+" foi duplicado para o registro "+ newVo.asBigDecimal("ID") +" com sucesso!");
			sctx.setJsonResponse(response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			JsonObject response = new JsonObject();
			response.addProperty("response", "Erro ao duplicar registro \n"+e.getMessage());
			sctx.setJsonResponse(response);
			e.printStackTrace();
		}finally {
			JapeSession.close(hnd);
		}		
	}
	
	private static DynamicVO duplicar (DynamicVO modeloVO, String dao) throws Exception {
		
		try {
			JapeWrapper japeDao = JapeFactory.dao(dao);
			FluidCreateVO fluidCreateVO = japeDao.create();
			Iterator<VOProperty> iterator = modeloVO.iterator();
			
			while(iterator.hasNext()) {
				VOProperty property = iterator.next();
				fluidCreateVO.set(property.getName(), property.getValue());
			}
			
			DynamicVO saved = fluidCreateVO.save();
			
			return saved;
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e);
		}		
		
	}
	
	
	@Override
	public void ejbActivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ejbPassivate() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ejbRemove() throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSessionContext(SessionContext arg0) throws EJBException, RemoteException {
		// TODO Auto-generated method stub
		
	}
}
