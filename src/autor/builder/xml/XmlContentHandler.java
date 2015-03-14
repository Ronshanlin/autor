package autor.builder.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import autor.builder.message.ConsoleHelper;
import autor.builder.template.ClassProperty;
import autor.builder.template.SubClass;
import autor.builder.utils.AutorConstants;
import autor.builder.utils.StringUtils;

public class XmlContentHandler extends XMLErrorHandler {
	private static final String SQLMAP = "sqlmap";
	private static final String SQLMAP_NAMESPACE = "namespace";
	private static final String SQL = "sql";
	private static final String SQL_ID = "id";
	
	private SubClass subClass;
	private List<ClassProperty> classProperties;
	private ClassProperty classProperty;
	
	private Map<String, String> idTempMap = new HashMap<String, String>();
	private Map<String, String> tempMapNS = new HashMap<String, String>();
	private String namespce = null;
	private Locator locator;
	
	private IFile file;
	
    public XmlContentHandler(IFile file, Map<String, String> tempMapNS){
    	super(file);
    	this.file = file;
    	this.tempMapNS.putAll(tempMapNS);
    }  
    
    /* 
     * 开始解析xml文档。 
     */  
    @Override  
    public void startDocument() throws SAXException {  
        ConsoleHelper.printInfo(">>> start document "); 
    }  
    
    /* 
     * 开始前缀 URI 名称空间范围映射。 
     * 此事件的信息对于常规的命名空间处理并非必需： 
     * 当 http://xml.org/sax/features/namespaces 功能为 true（默认）时， 
     * SAX XML 读取器将自动替换元素和属性名称的前缀。 
     * 参数意义如下： 
     *    prefix ：前缀 
     *    uri ：命名空间 
     */  
    @Override  
    public void startPrefixMapping(String prefix,String uri)  
            throws SAXException {  
		ConsoleHelper.printInfo(">>> start prefix_mapping : xmlns:" + prefix + " = "
				+ "\"" + uri + "\"");
          
    } 
    /* 
     * 接收元素开始的通知。 
     * 参数意义如下： 
     *    uri ：元素的命名空间 
     *    localName ：元素的本地名称（不带前缀） 
     *    qName ：元素的限定名（带前缀） 
     *    atts ：元素的属性集合 
     */  
    @Override  
    public void startElement(String uri, String localName, String qName,   
            Attributes atts) throws SAXParseException{  
        ConsoleHelper.printInfo(">>> start element, qName:"+qName+", localname:"+localName);
        // 
        if (SQLMAP.equalsIgnoreCase(qName)) {
        	this.namespce = atts.getValue(SQLMAP_NAMESPACE);
		    if (StringUtils.isEmpty(this.namespce)) {
                throw new SAXParseException(file.getName() + " does not have a namepsace",
                        this.locator);
            }else {
				this.namespce = this.namespce.trim();
			}
		    // 缓存中存在相同的namespace，且文件名不同
        	if (tempMapNS.containsKey(this.namespce.toUpperCase()) &&
        			!this.file.getName().equals(tempMapNS.get(this.namespce.toUpperCase()))) {
                throw new SAXParseException(this.file.getName() + " and " + tempMapNS.get(this.namespce.toUpperCase())
                        + " has the same namespace: " +this. namespce + "", this.locator);
            }
			this.subClass = new SubClass();
			this.classProperties = new ArrayList<ClassProperty>();
			
			this.subClass.setComment(this.file.getName());
			this.subClass.setSubClassName(this.namespce.toUpperCase());
			this.subClass.setProperties(this.classProperties);
			
			this.classProperty = new ClassProperty();
			this.classProperty.setPropName(SQLMAP_NAMESPACE.toUpperCase());
	        this.classProperty.setPropValue(this.namespce.concat(AutorConstants.SEPARATOR_DOT));
	        this.classProperties.add(classProperty);
		}else if (SQL.equalsIgnoreCase(qName)) {
			if (StringUtils.isEmpty(atts.getValue(SQL_ID))) {
                throw new SAXParseException(file.getName() + " has a sql that does not has a sqlId", this.locator);
			}
			String key = this.namespce.concat("."+atts.getValue(SQL_ID).trim());
			if (idTempMap.containsKey(key)) {
                throw new SAXParseException(file.getName() + " has exist the same sqlId:"
                        + atts.getValue(SQL_ID), this.locator);
			}else {
				idTempMap.put(key, "");
			}
			this.classProperty = new ClassProperty();
			this.classProperty.setPropName(atts.getValue(SQL_ID).toUpperCase());
			this.classProperty.setPropValue(key);
			this.classProperties.add(classProperty);
		}
    }  
      
    /* 
     * 接收字符数据的通知。 
     * 在DOM中 ch[begin:end] 相当于Text节点的节点值（nodeValue） 
     */  
    @Override  
    public void characters(char[] ch, int begin, int length) throws SAXException {
        //sql = new String(ch, begin, length)
    	ConsoleHelper.printInfo(">>> characters(" + length + ")");
    }  
  
    /* 
     * 接收文档的结尾的通知。 
     * 参数意义如下： 
     *    uri ：元素的命名空间 
     *    localName ：元素的本地名称（不带前缀） 
     *    qName ：元素的限定名（带前缀） 
     *  
     */  
    @Override  
    public void endElement(String uri,String localName,String qName)  
            throws SAXException {  
		ConsoleHelper.printInfo(">>> end element，qName=" + qName + "(" + uri + ")");
    }  
    
    /* 
     * 接收文档的结尾的通知。 
     */  
    @Override  
    public void endDocument() throws SAXException {  
		ConsoleHelper.printInfo(">>> end document"); 
    }  
  
    /* 
     * 结束前缀 URI 范围的映射。 
     */  
    @Override  
    public void endPrefixMapping(String prefix) throws SAXException {  
		ConsoleHelper.printInfo(">>> end prefix_mapping : " + prefix);
    }  
  
    /* 
     * 接收元素内容中可忽略的空白的通知。 
     * 参数意义如下： 
     *     ch : 来自 XML 文档的字符 
     *     start : 数组中的开始位置 
     *     length : 从数组中读取的字符的个数 
     */  
    @Override  
    public void ignorableWhitespace(char[] ch, int begin, int length)  
            throws SAXException {  
        ConsoleHelper.printInfo(">>> ignorable whitespace("+length+"): "+new String(ch, begin, length));  
    }  
      
    /* 
     * 接收处理指令的通知。 
     * 参数意义如下： 
     *     target : 处理指令目标 
     *     data : 处理指令数据，如果未提供，则为 null。 
     */  
    @Override  
    public void processingInstruction(String target,String data)  
            throws SAXException {  
		ConsoleHelper.printInfo(">>> process instruction : (target = \"" + target
				+ "\",data = \"" + data + "\")");
    }  
  
    /* 
     * 接收用来查找 SAX 文档事件起源的对象。 
     * 参数意义如下： 
     *     locator : 可以返回任何 SAX 文档事件位置的对象 
     */  
    @Override  
    public void setDocumentLocator(Locator locator) {  
		this.locator = locator;
    }  
  
    /* 
     * 接收跳过的实体的通知。 
     * 参数意义如下：  
     *     name : 所跳过的实体的名称。如果它是参数实体，则名称将以 '%' 开头， 
     *            如果它是外部 DTD 子集，则将是字符串 "[dtd]" 
     */  
    @Override  
    public void skippedEntity(String name) throws SAXException {  
		ConsoleHelper.printInfo(">>> skipped_entity : " + name); 
    }  
    
    public SubClass getSubClass() {
		return this.subClass;
	}
}  
