angular
	.module('CampanhasPublicitariasApp', [ 'snk' ])
	.controller(
			'CampanhasPublicitariasController',
			['SkApplication', 'i18n', 'ObjectUtils', 'MGEParameters',
				'GridConfig', 'AngularUtil', 'StringUtils', 'ServiceProxy', 'MessageUtils',
				'SanPopup', '$scope','PopUpParameter',
				
function(SkApplication, i18n, ObjectUtils,
		MGEParameters, GridConfig, AngularUtil,
		StringUtils, ServiceProxy, MessageUtils,
		SanPopup, $scope, PopUpParameter) {

	var self = this;
	var _dynaformCampanhasPublicitarias;
	var _dsCampanhasPublicitarias;

	var _dynaformFinanceiroCampanha;
	var _dsFinanceiroCampanha;
	
	var parameters = [
	    {
	        description: "Data de vencimento",
	        enabled: true,
	        fieldName: undefined,
	        fieldProp: {
	            autofocus: ""//Essa propriedade provê ao Popup a possibilidade de focus
	        },
	        fieldType: "D",	        
	        label: "Data de vencimento",
	        name: "DTVENC",
	        paramIndex: 0,
	        presentationType: "P",
	        properties: {
	            $: "Nome",
	            entityName: "TGFREC",
	            filterID: 0
	        },
	        required: true,
	        type: "D",
	        value: ""
	    },
		{
	        description: "Data de Referência",
	        enabled: true,
	        fieldName: undefined,
	        fieldProp: {
	            autofocus: ""//Essa propriedade provê ao Popup a possibilidade de focus
	        },
	        fieldType: "D",	        
	        label: "Data de Referência",
	        name: "DTREF",
	        paramIndex: 0,
	        presentationType: "P",
	        properties: {
	            $: "Nome",
	            entityName: "TGFREC",
	            filterID: 0
	        },
	        required: true,
	        type: "D",
	        value: ""
	    }
	];

	
	self.onDynaformLoaded = onDynaformLoaded;
	self.customTabsLoader = customTabsLoader;
	self.interceptNavigator = interceptNavigator;
	self.interceptFieldMetadata = interceptFieldMetadata;
	self.buttonAction = buttonAction;
	self.inserirAssinatura = inserirAssinatura;
	self.aplicarMulta = aplicarMulta;
	self.duplicarAssinatura = duplicarAssinatura;

	
	
	ObjectUtils.implements(self, IDynaformInterceptor);
	 
	function onDynaformLoaded(dynaform, dataset) {
		
		if(dataset.getEntityName() == 'CampanhasPublicitarias'){
			_dynaformCampanhasPublicitarias = dynaform;
			_dsCampanhasPublicitarias = dataset;
			
		}
		
		if(dataset.getEntityName() == 'FinanceiroCampanha'){
			_dynaformFinanceiroCampanha = dynaform;
			_dsFinanceiroCampanha= dataset;
			
			_dynaformFinanceiroCampanha.getNavigatorAPI()
			.showAddButton(false)
			.showCopyButton(false)
			.showSaveButton(false);
			
		}
	}
	
	function customTabsLoader (entityName) {
		var customTabs = [];
		return customTabs;
	}
	
	function interceptNavigator(navigator, dynaform) {
		
	}
	
	function interceptFieldMetadata(fieldMetadata, dataset, dynaform) {
		
	}
	
	function buttonAction(){
			alert("Clicou!!");		
	}
	
	function inserirAssinatura(){
		
		var instance = PopUpParameter.openPopUp(parameters);
		
	  	instance.result
	    .then(function(result) {
				
			var param = {"CODCAMP": _dsCampanhasPublicitarias.getFieldValue('CODCAMP'), "DTVENC": result.DTVENC.toISOString(), "DTREF": result.DTREF.toISOString()};		
			console.log(typeof result.DTREF+ " -- "+ result.DTREF);
			ServiceProxy.callService('campanhaspublicitarias@CampanhasSP.inserirAssinatura', param)
			.then(function(response){				
				var mensagem = ObjectUtils.getProperty (response, 'responseBody.response');					
				MessageUtils.showInfo('Aviso', mensagem);			
				_dsFinanceiroCampanha.refresh();
				
			}) 				
		
		}, function(cancel) {
		console.log(cancel);
		});
		

	}
	
	function aplicarMulta(){
		var param = {"CODCAMP": _dsCampanhasPublicitarias.getFieldValue('CODCAMP'),
			         "ID": _dsFinanceiroCampanha.getFieldValue('ID'),
				     "VLRTOT": _dsFinanceiroCampanha.getFieldValue('VLRTOT') };
					
		ServiceProxy.callService('campanhaspublicitarias@CampanhasSP.aplicarMulta', param)
		.then(function(response){				
			var mensagem = ObjectUtils.getProperty (response, 'responseBody.response');					
			MessageUtils.showInfo('Aviso', mensagem);			
			_dsFinanceiroCampanha.refresh();
			
		})
	}
	
	function duplicarAssinatura(){
		var param = {"CODCAMP": _dsCampanhasPublicitarias.getFieldValue('CODCAMP'), "ID": _dsFinanceiroCampanha.getFieldValue('ID')};			
		ServiceProxy.callService('campanhaspublicitarias@CampanhasSP.duplicarAssinatura', param)
		.then(function(response){				
			var mensagem = ObjectUtils.getProperty (response, 'responseBody.response');					
			MessageUtils.showInfo('Aviso', mensagem);						
			_dsFinanceiroCampanha.refresh();
			
		})
	}
	
	

	
	

} ]);