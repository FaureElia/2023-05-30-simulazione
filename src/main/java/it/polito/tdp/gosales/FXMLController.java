package it.polito.tdp.gosales;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.gosales.model.Coppia;
import it.polito.tdp.gosales.model.Model;
import it.polito.tdp.gosales.model.Products;
import it.polito.tdp.gosales.model.Retailers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {
	
	private Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnAnalizzaComponente;

    @FXML
    private Button btnCreaGrafo;

    @FXML
    private Button btnSimula;

    @FXML
    private ComboBox<Integer> cmbAnno;

    @FXML
    private ComboBox<String> cmbNazione;

    @FXML
    private ComboBox<Products> cmbProdotto;

    @FXML
    private ComboBox<Retailers> cmbRivenditore;

    @FXML
    private TextArea txtArchi;

    @FXML
    private TextField txtN;

    @FXML
    private TextField txtNProdotti;

    @FXML
    private TextField txtQ;

    @FXML
    private TextArea txtResult;

    @FXML
    private TextArea txtVertici;

    @FXML
    void doAnalizzaComponente(ActionEvent event) {
    	
    	Retailers r=this.cmbRivenditore.getValue();
    	if(r==null) {
    		this.txtResult.setText("inserire un rivenditore!");
    		return;
    	}
    	
    	Integer dim=this.model.calcolaComponente(r);
    	if(dim==null) {
    		this.txtResult.setText("non è possibile calcolare la componente connesssa");
    		return;
    	}
    	this.txtResult.setText("componente connessa trovata!");
    	this.txtResult.appendText("dimensione: "+ dim);
    	this.txtResult.appendText("sommaPesi: "+this.model.getSommaPesi());

    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	this.txtResult.clear();
    	this.cmbRivenditore.setDisable(true);
		this.btnAnalizzaComponente.setDisable(true);
    	
    	Integer anno=this.cmbAnno.getValue();
    	String nazione=this.cmbNazione.getValue();
    	String mS=this.txtNProdotti.getText();
    	if(anno==null || nazione ==null ||mS=="") {
    		this.txtResult.setText("inserire tutti i valori");
    		return;
    	}
    	try {
    		Integer m=Integer.parseInt(mS);
    		List<Retailers> vertici=this.model.creaGrafo(anno,nazione,m);
    		if(vertici.size()!=0 && vertici!=null) {
    			this.txtResult.setText("grafo creato!\n");
    			this.txtResult.appendText("vertici: "+vertici.size()+"\n");
    			List<Coppia> archi=this.model.getArchi();
    			this.txtResult.appendText("archi: "+archi.size()+"\n");
    			for(Coppia a: archi) {
    				this.txtArchi.appendText(a.toString()+"\n");
    			}
    			for (Retailers r: vertici) {
    				this.txtVertici.appendText(r+"\n");
    			}
    			this.cmbRivenditore.getItems().clear();
    			this.cmbRivenditore.getItems().addAll(vertici);
    			this.cmbRivenditore.setDisable(false);
    			this.btnAnalizzaComponente.setDisable(false);
    		}
    		
    	}catch(NumberFormatException e){
    		this.txtResult.setText("inserire valore numerico");
    		
    		
    	}

    }

    @FXML
    void doSimulazione(ActionEvent event) {
    	Retailers r=this.cmbRivenditore.getValue();
    	Products p=this.cmbProdotto.getValue();
    	String nS=this.txtN.getText();
    	String qS=this.txtQ.getText();
    	Integer anno=this.cmbAnno.getValue();
    	if(r==null || p==null || nS=="" || qS=="" ||anno==null) {
    		this.txtResult.setText("inserire dati corretti");
    		return;
    	}
    	try {
    		int n=Integer.parseInt(nS);
    		int q=Integer.parseInt(qS);
    		if(n<0 ||q<0) {
    			this.txtResult.setText("inserire valori positivi");
    			return;
    		}
    		this.model.simula(r,p,n,q,anno);
    		
    	}catch(NumberFormatException e) {
    		this.txtResult.setText("inserire valori numerici");
    		
    	}

    }
    
    @FXML
    void inserisciProdotti(ActionEvent event) {
    	this.cmbProdotto.setDisable(false);
    	Retailers r=this.cmbRivenditore.getValue();
    	Integer anno=this.cmbAnno.getValue();
    	if(r!=null && anno!=null) {
    		this.cmbProdotto.getItems().addAll(model.getProdotti(r,anno));
    	}
    	this.txtQ.setDisable(false);
    	this.txtN.setDisable(false);
    	this.btnSimula.setDisable(false);

    }

    @FXML
    void initialize() {
        assert btnAnalizzaComponente != null : "fx:id=\"btnAnalizzaComponente\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnSimula != null : "fx:id=\"btnSimula\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbAnno != null : "fx:id=\"cmbAnno\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbNazione != null : "fx:id=\"cmbNazione\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbProdotto != null : "fx:id=\"cmbProdotto\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbRivenditore != null : "fx:id=\"cmbRivenditore\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtArchi != null : "fx:id=\"txtArchi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtN != null : "fx:id=\"txtN\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtNProdotti != null : "fx:id=\"txtNProdotti\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtQ != null : "fx:id=\"txtQ\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtVertici != null : "fx:id=\"txtVertici\" was not injected: check your FXML file 'Scene.fxml'.";

    }
    
    public void setModel(Model model) {
    	this.model = model;
    	this.cmbNazione.getItems().addAll(this.model.getCountries());
    	for(int i=2015; i<=2019; i++) {
    		this.cmbAnno.getItems().add(i);
    	}
    }

}
