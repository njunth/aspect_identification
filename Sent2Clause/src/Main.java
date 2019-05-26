import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Main {
	
	private static void doit(String filePath){
		Document document = null; 
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			document = builder.parse(filePath);
		} catch (SAXException e) {
			e.printStackTrace();
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Element rootElement = document.getDocumentElement();
		
		Sent2Clause sent2clause = new Sent2Clause();
		NodeList sentenceNodeList = rootElement.getElementsByTagName("sentence");
		if(sentenceNodeList != null){
			for(int i = 0; i < sentenceNodeList.getLength(); i++){
				Element sentenceElement = (Element)sentenceNodeList.item(i);
				NodeList sentenceTextNodeList = sentenceElement.getElementsByTagName("text");
				if(sentenceTextNodeList == null || sentenceTextNodeList.getLength() == 0) continue;
				Element textElement = (Element)sentenceTextNodeList.item(0);
				String sentenceText = textElement.getTextContent();
				Collection<String> clauses = sent2clause.cut(sentenceText);
				if (clauses.size()>1){
					System.out.println("Review:   "+sentenceText);
					for(Iterator<String> iter = clauses.iterator(); iter.hasNext();){
						String clause = iter.next();
						System.out.println(clause);
					}
				}					
				
				//add clause element to dom
				for(Iterator<String> iter = clauses.iterator(); iter.hasNext();){
					String clause = iter.next();
					Node clauseElement = document.createElement("clause");
					clauseElement.appendChild(document.createTextNode(clause));
					sentenceElement.appendChild(clauseElement);
				}
				
				//output
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer;
				try {
					transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(document);
					StreamResult result = new StreamResult(new File("clause_" + filePath));
					transformer.transform(source, result);
				} catch (TransformerConfigurationException e) {
					e.printStackTrace();
				} catch (TransformerException e) {
					e.printStackTrace();
				}
			}
		}
				
	}
	
	public static void main(String[] args){
		String[] filePath = {//"ABSA-15_Restaurants_Train_Data.xml",
//				"ABSA15_Restaurants_Test.xml",
				"ABSA-15_Laptops_Train_Data.xml",
//				"ABSA15_Laptops_Test.xml"
		};
		
		for(int i = 0; i < filePath.length; i++){
			System.out.println(filePath[i]);
			doit(filePath[i]);
		}
		
	}

}
