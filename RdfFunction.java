package tw.nccu.edu.dht;
 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
 
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDF;
 
public class RdfFunction {
    static String dataURI = "http://nccu.edu.tw-hdt/";
    static final String sfileName = "data.nt";
 
    public String createRDF(String fileName) throws IOException {
        FileReader dataFile = new FileReader(fileName);
        BufferedReader br = new BufferedReader(dataFile);
        String line;
        StringWriter outputWriter = new StringWriter();
 
        while ((line = br.readLine()) != null) {
            // read data frome file and transfer them to RDF & return RDF data
            // back to main class
 
            int firSpace = line.indexOf(' '), secSpace = line.indexOf(' ',
                    firSpace + 1), thirdSpace = line.indexOf(' ', secSpace + 1), fourSpace = line
                    .indexOf(' ', thirdSpace + 1);
            // Data format 'name place machine behavior id' use space to divide
            String nameSource = line.substring(0, firSpace);
            String placeSource = line.substring(firSpace + 1, secSpace);
            String machineSource = line.substring(secSpace + 1, thirdSpace);
            String behaviorSource = line.substring(thirdSpace + 1, fourSpace);
            String idSource = line.substring(fourSpace + 1);
 
            // Create RDF Type data
            Model model = ModelFactory.createDefaultModel();
            Property setAt = model.createProperty(dataURI + "setAt");
            Property behavior = model.createProperty(dataURI + behaviorSource);
 
            Resource place = model.createResource(dataURI + placeSource);
            place.addProperty(RDF.type, dataURI + "Place");
 
            Resource machine = model.createResource(dataURI + machineSource);
            machine.addProperty(RDF.type, dataURI + "Machine");
 
            Resource name = model.createResource(dataURI + nameSource);
            name.addProperty(RDF.type, dataURI + "Person");
            name.addProperty(behavior, machine);
            name.addProperty(setAt, place);
 
            Resource id = model.createResource(dataURI + idSource);
            id.addProperty(RDF.type, dataURI + "id");
 
            // model.write(System.out, "N-TRIPLE");
            StringWriter tmp = new StringWriter();
            model.write(tmp, "N-TRIPLE"); // make data to be N-TRIPLE
 
            StringWriter oneOfRDF = new StringWriter();
            oneOfRDF.write(name.toString());
            oneOfRDF.append("," + tmp.toString());
            outputWriter.append(";" + oneOfRDF.toString());
        }
        // Write to File
        FileWriter writer = new FileWriter(sfileName, true);
        writer.write(outputWriter.toString());
 
        // close buffer and file
        br.close();
        writer.close();
 
        return outputWriter.toString();
    }
 
    public String readRDF(String key1) {
        InputStream in = FileManager.get().open(sfileName);
        if (in == null) {
            throw new IllegalArgumentException("File: " + sfileName
                    + " not found");
        }
 
        Model model = ModelFactory.createDefaultModel();
        model.read(in, "");
        Resource key = model.getResource(key1);
        Selector selector = new SimpleSelector(key, (Property) null,
                (Resource) null);
        StmtIterator iter = model.listStatements(selector);
        StringWriter outputWriter = new StringWriter();
        if (iter.hasNext()) {
            while (iter.hasNext()) {
                // if (outputWriter == null)
                // outputWriter.write(","
                // + iter.nextStatement().getObject().toString());
                outputWriter.append(","
                        + iter.nextStatement().getObject().toString());
            }
        } else {
            System.out.println("No vcards were found in the database");
        }
        return outputWriter.toString();
        // Model model = ModelFactory.createDefaultModel();
        // model.read("file:./bin/tw/nccu/edu/dht/data.rdf", "N-TRIPLE");
        // Resource rdf = model.getResource(key1);
        // Resource name = (Resource)
        // rdf.getRequiredProperty(RDF.type).getObject();
        // String fullName = rdf.getRequiredProperty(RDF.type).getString();
 
    }
}
