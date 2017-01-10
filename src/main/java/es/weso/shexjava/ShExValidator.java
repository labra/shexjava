package es.weso.shexjava;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

import es.weso.schema.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.rdf.model.Model;

import scala.Option;
import scala.Tuple2;


import es.weso.rdf.PrefixMap;
import es.weso.rdf.RDFReader;
import es.weso.rdf.jena.RDFAsJenaModel;
import es.weso.rdf.nodes.RDFNode;

public class ShExValidator {
	
	Logger log = Logger.getLogger(ShExValidator.class.getName());

	public void validate(Model dataModel, Schema schema) throws Exception {
		Option<String> none = Option.apply(null); // Create a none
		RDFReader rdf = new RDFAsJenaModel(dataModel);
		Result result = schema.validate(rdf,"TARGETDECLS",none,none, rdf.getPrefixMap(), schema.pm());
		if (result.isValid()) {
			log.info("Result is valid");
			System.out.println("Valid. Result: " + result.show());
		} else {
			System.out.println("Not valid");
		}
	} 

	public void validate(String dataFile, String schemaFile, String schemaFormat) throws Exception {
		log.info("Reading data file " + dataFile);
		Model dataModel = RDFDataMgr.loadModel(dataFile);
//		Model dataModel =  RDFDataMgr.loadModel(dataFile);
		log.info("Model read. Size = " + dataModel.size());
		
		
		log.info("Reading shapes file " + schemaFile);
		Schema schema = readSchema(schemaFile,schemaFormat);
		
		log.info("Schema read" + schema.serialize("SHEXC"));

		validate(dataModel,schema);
	}
	
	public Schema readSchema(String schemaFile, String format) throws Exception {
		// Create a none, see: http://stackoverflow.com/questions/1997433/how-to-use-scala-none-from-java-code
		Option<String> none = Option.apply(null); // Create a none
		
		String contents = new String(Files.readAllBytes(Paths.get(schemaFile)));

		// This ugly way to call is because the "fromString" method is
		// declared both in the class and the companion object
		// I will change it soon
        return ShExSchema$.MODULE$.fromString(contents,format,none).get();
	}
}