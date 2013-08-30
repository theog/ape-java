package org.theo.depricated;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class visio {

	static XMLReader parser;

    static File aFile = new File("resources/sample.vdx");
	
	public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
		// get parser
		getParser();

		// parser xml file
		parseXMLFile();
	}
  
	public static void getParser() {
	  try {
	    parser = XMLReaderFactory.createXMLReader(
	     "org.apache.xerces.parsers.SAXParser"
	    );
	  }
	  catch (SAXException e) {
	    try {
	      parser = XMLReaderFactory.createXMLReader();
	    }
	    catch (SAXException e1) {
	      throw new NoClassDefFoundError("No SAX parser is available");
	      // or whatever exception your method is declared to throw
	    }
	  }
	}
	  

	
		  /**
		  * Fetch the entire contents of a text file, and return it in a String.
		  * This style of implementation does not throw Exceptions to the caller.
		  *
		  * @param aFile is a file which already exists and can be read.
		  */
	static public String getContents(File aFile) {
		    //...checks on aFile are elided
		    StringBuffer contents = new StringBuffer();

		    //declared here only to make visible to finally clause
		    BufferedReader input = null;
		    try {
		      //use buffering, reading one line at a time
		      //FileReader always assumes default encoding is OK!
		      input = new BufferedReader( new FileReader(aFile) );
		      String line = null; //not declared within while loop
		      /*
		      * readLine is a bit quirky :
		      * it returns the content of a line MINUS the newline.
		      * it returns null only for the END of the stream.
		      * it returns an empty String if two newlines appear in a row.
		      */
		      while (( line = input.readLine()) != null){
		        contents.append(line);
		        contents.append(System.getProperty("line.separator"));
		      }
		    }
		    catch (FileNotFoundException ex) {
		      ex.printStackTrace();
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
		    finally {
		      try {
		        if (input!= null) {
		          //flush and close both "input" and its underlying FileReader
		          input.close();
		        }
		      }
		      catch (IOException ex) {
		        ex.printStackTrace();
		      }
		    }
		    return contents.toString();
	}	
	
	
	public static void parseXMLFile() throws IOException, SAXException, ParserConfigurationException {

		 // read document
		 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
         Document doc = docBuilder.parse(aFile);
         
         // normalize text representation
         doc.getDocumentElement ().normalize ();
         System.out.println ("Root element of the doc is " + 
         doc.getDocumentElement().getNodeName());         
		
         NodeList listOfPersons = doc.getElementsByTagName("person");
         int totalPersons = listOfPersons.getLength();
         System.out.println("Total no of people : " + totalPersons);         
         
         
		
		//String contents = getContents(aFile);
	    //System.out.println(contents);
	    
		//ContentHandler handler = new TextExtractor();
	    //parser.setContentHandler(handler);	    

		//InputSource in = new InputSource();
		//in.setSystemId(aFile);
	    //parser.parse("resources/sample.vdx");
		
		//InputStream raw = u.openStream();
		//InputStream decompressed = new GZIPInputStream(raw);
		//InputSource in = new InputSource(decompressed);
		//InputSource in = new InputSource(raw);
		//in.setSystemId("resources/sample.vdx");
		//parser.parse(in);
	}	  
}