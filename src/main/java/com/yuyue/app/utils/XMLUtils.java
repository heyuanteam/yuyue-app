package com.yuyue.app.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class XMLUtils {

    public static  String doPost(String url,String str,String charset,String contentType){
//		SSLClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
//			httpClient = new SSLClient();
            CloseableHttpClient httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(url);
            //设置参数
            StringEntity entityParams = new StringEntity(str, charset);
            httpPost.setEntity(entityParams );
            httpPost.setHeader("Content-Type", contentType);

            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    /**
     *   map转xml
     * @param map
     * @param sb
     */
    public static void mapToXMLTest2(Map map, StringBuffer sb) {
        Set set = map.keySet();
        for (Iterator it = set.iterator(); it.hasNext();) {
            String key = (String) it.next();
            Object value = map.get(key);
            if (null == value)
                value = "";
            if (value.getClass().getName().equals("java.util.ArrayList")) {
                ArrayList list = (ArrayList) map.get(key);
                sb.append("<" + key + ">");
                for (int i = 0; i < list.size(); i++) {
                    HashMap hm = (HashMap) list.get(i);
                    mapToXMLTest2(hm, sb);
                }
                sb.append("</" + key + ">");

            } else {
                if (value instanceof HashMap) {
                    sb.append("<" + key + ">");
                    mapToXMLTest2((HashMap) value, sb);
                    sb.append("</" + key + ">");
                } else {
                    sb.append("<" + key + ">" + "<![CDATA["+value+"]]>" + "</" + key + ">");
                }
            }
        }
    }

    /**
     *  XML转map
     * @param xmlStr
     * @return
     */

    public static Map<String,String> xmlString2Map(String xmlStr) throws Exception{
        String FEATURE = null;
        Map<String, String> data = new HashMap<String, String>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //XXE漏洞处理
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbf.setFeature(FEATURE, false);
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            dbf.setFeature(FEATURE, false);
            FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
            dbf.setFeature(FEATURE, false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);//XXE漏洞

            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
