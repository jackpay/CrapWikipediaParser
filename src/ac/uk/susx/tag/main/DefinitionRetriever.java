package ac.uk.susx.tag.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.obolibrary.oboformat.parser.OBOFormatParserException;
import org.semanticweb.owlapi.model.OWLClass;
//import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
//import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import owltools.graph.OWLGraphWrapper;
import owltools.io.ParserWrapper;

public class DefinitionRetriever {
	
	private String ontologyLoc;
	private String output;
	private OWLGraphWrapper g;
	private WikiTextConverter wtc;

	public DefinitionRetriever(String ontol,String output) {
		ontologyLoc = ontol;
		this.output = output;
		this.wtc = new WikiTextConverter();
	}

	public static void main(String[] args) {
		//DefinitionRetriever def = new DefinitionRetriever("","");
		//DefinitionRetriever def = new DefinitionRetriever("/Users/jp242/Documents/PhD-LDA/DiseaseOntology/diseaseontology/HumanDO.obo","/Users/jp242/Documents/PhD-LDA/DiseaseOntology/diseaseontology/all-onotology-wikipages");
		//DefinitionRetriever def = new DefinitionRetriever("/Users/jp242/Documents/PhD-LDA/DiseaseOntology/diseaseontology/subtree/acquiredmetabolicdisease-DOID0060158/acquired-metabolic-disease.disease.obo","/Users/jp242/Documents/PhD-LDA/DiseaseOntology/diseaseontology/subtree/acquiredmetabolicdisease-DOID0060158/defFiles");
		//DefinitionRetriever def = new DefinitionRetriever("/Users/jp242/Documents/PhD-LDA/DiseaseOntology/diseaseontology/subtree/malereproductivesystemdisease-DOID48/male-reprodcutive-system.disease.obo","/Users/jp242/Documents/PhD-LDA/DiseaseOntology/diseaseontology/subtree/malereproductivesystemdisease-DOID48/defFiles");
		//DefinitionRetriever def = new DefinitionRetriever("/Users/jp242/Documents/PhD-LDA/DiseaseOntology/diseaseontology/subtree/cardiovascular-system-disease-DOID1287/cardiovascular-disease.obo","/Users/jp242/Documents/PhD-LDA/DiseaseOntology/diseaseontology/subtree/cardiovascular-system-disease-DOID1287/defFiles");
		//DefinitionRetriever def = new DefinitionRetriever("/Users/jp242/Documents/PhD-LDA/DiseaseOntology/diseaseontology/subtree/nervous-system-disease-DOID863/nervous-system-disease.obo","/Users/jp242/Documents/PhD-LDA/DiseaseOntology/diseaseontology/subtree/nervous-system-disease-DOID863/defFiles");
		//DefinitionRetriever def = new DefinitionRetriever("/Users/jp242/Documents/PhD-LDA/Experiments/experiment-1-23-01-2014/datasets/acquired-metabolic-disease-0060158/acquired-metabolic-disease-0060158.obo","/Users/jp242/Documents/PhD-LDA/Experiments/experiment-1-23-01-2014/datasets/acquired-metabolic-disease-0060158/defFiles");
		DefinitionRetriever def = new DefinitionRetriever("/Users/jp242/Documents/PhD-LDA/Experiments/experiment-1-23-01-2014/datasets/cognitive-disorders-DOID1561/cognitive-disorder-1561.obo","/Users/jp242/Documents/PhD-LDA/Experiments/experiment-1-23-01-2014/datasets/cognitive-disorders-DOID1561/defFiles");
		try {
			def.getDefinitions();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OBOFormatParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void getDefinitions() throws OWLOntologyCreationException, OBOFormatParserException, IOException {
        ParserWrapper pw = new ParserWrapper();
        
        g = pw.parseToOWLGraph(ontologyLoc);
        //OWLOntology ont = g.getSourceOntology();

        //SimpleShortFormProvider sfp = new SimpleShortFormProvider();
        
        Set<OWLClass> d = g.getOntologyLeaves();
        Iterator<OWLClass> i = d.iterator();
        while(i.hasNext()){
        	OWLClass c = i.next();
        	createDefFile(c);
        	//System.out.println("-----------------------------" + g.getIdentifier(c) + "----------------------------");
        	//System.out.println(g.getDef(c));
        }
	}

	private void createDefFile(OWLClass c) {
		if(g.getDef(c) != null){
			File f = new File(output + "/" + g.getIdentifier(c).replace(":", "-") + ".txt");
			WikipediaDocumentRetriever wdr = new WikipediaDocumentRetriever();
			if(!f.exists()){
				List<String> defRefs = g.getDefXref(c);
				Iterator<String> defS = defRefs.iterator();
				while(defS.hasNext()){
					String next = defS.next();
					if(next.contains("wikipedia")){
						String[] spl = next.split("/");
						String page = spl[spl.length-1];
						System.out.println(page);
						try {
							f.createNewFile();
							FileWriter fw = new FileWriter(f.getAbsoluteFile());
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(wtc.convertToPlainText(wdr.getDoc(URLBuilder(page))));
							bw.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			try {
				wdr.closeHTTP();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public String URLBuilder(String page){
		return "http://en.wikipedia.org/w/api.php?format=json&action=query&prop=revisions&rvprop=content&titles=" + page;
	}
	
}
