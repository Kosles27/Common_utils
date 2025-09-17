package fileUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

import static constantsUtils.CommonConstants.EMPTY_STRING;

/**
 * Class holds methods to work with XML files
 */
public class XmlUtils
{

    /**
     * define an xml file
     * @param XmlPath path of xml file
     * @return doc - xml file
     */
    private static Document xmlParser(String XmlPath) {
        Document doc = null;
        try {
            File fXmlFile = new File(XmlPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return doc;
    }


     /**
     * create data from xml file by sending the node name and node number
     * @param xmlPath xpath of xml file
     * @param nodeName String Node name
     * @param nodeNumber String node number
     * @return String value of relevant data
     */
    public static String getData(String xmlPath,String nodeName, int nodeNumber )
    {
      return xmlParser(xmlPath).getElementsByTagName(nodeName).item(nodeNumber).getTextContent();
    }


    /**
     * return all data from xml according to node name filter
     * @param xmlPath xpath of xml file
     * @param nodeName String Node name
     * @return String of all relevant data
     */
    public static String getDataByFilter(String xmlPath,String nodeName)
    {
       String val=EMPTY_STRING;
        NodeList askedNode=xmlParser(xmlPath).getElementsByTagName(nodeName);
        int length = askedNode.getLength();
        val=askedNode.item(0).getTextContent();
        for(int i=1;i<length;i++) {
            val=val + ";" + askedNode.item(i).getTextContent();
            }
       return val;
    }

    /**
     * Return number of node appearances
     * @param  xmlPath xpath of xml file
     * @param nodeName String Node name
     * @return number of node appearances
     */
    public static int getNodesNumber(String xmlPath, String nodeName)
    {
        return xmlParser(xmlPath).getElementsByTagName(nodeName).getLength();
    }

    /**
     * this function set a value in xmlFile
     * @param  xmlPath xpath of xml file
     * @param nodeName String Node name
     * @param indx numer of node
     * @param value value for item
     */
    public static void setData(String xmlPath,String nodeName,Integer indx,String value)
    {
        xmlParser(xmlPath).getElementsByTagName(nodeName).item(indx).setTextContent(value);
    }

    /**
     * This function return the index of the node name with the relevant attribute value
     * @param xmlPath xpath of xml file
     * @param nodeName String Node name
     * @param att Attribute name of nodeName
     * @param attValue String value of the attribute
     * @return return the index of the node name with the relevant attribute value;
     *         return -1 in case of attribute was not found

     */
    public static int getNodesIndexByAttribute(String xmlPath, String nodeName,String att,String attValue)
    {

        NodeList listOfLang = xmlParser(xmlPath).getElementsByTagName(nodeName);
        int i=0;
        for(i=0; i<listOfLang.getLength() ; i++) {
            Node dataRecordLang = listOfLang.item(i);
            if(dataRecordLang.getNodeType() == Node.ELEMENT_NODE) {
                Element firstElement = (Element) dataRecordLang;
                if(firstElement.getAttribute(att).equals(attValue)){
                    break;
                }
            }
        }
        if(i<listOfLang.getLength()) return i ; else return(-1);
    }




}