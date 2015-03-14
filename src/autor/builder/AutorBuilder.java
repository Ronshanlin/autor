package autor.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import autor.builder.message.ConsoleHelper;
import autor.builder.project.ProjectHelper;
import autor.builder.template.FreemarkerParser;
import autor.builder.template.JavaTemplate;
import autor.builder.template.TemplateSerialization;
import autor.builder.utils.AutorConstants;
import autor.builder.utils.JsonUtil;
import autor.builder.utils.StringUtils;
import autor.builder.utils.TemplateCompare;
import autor.builder.xml.SaxXmlParser;

public class AutorBuilder extends IncrementalProjectBuilder {
	private static Map<String, String> last_cache = new HashMap<String, String>();
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@SuppressWarnings("rawtypes")
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		Map<String, IFile> ifileMap=new HashMap<String,IFile>();
		
		if (kind == FULL_BUILD) {
			getProject().accept(new MyResourceVisitor(ifileMap));
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				getProject().accept(new MyResourceVisitor(ifileMap));
			} else {
				delta.accept(new MyDeltaVisitor(ifileMap));
			}
		}
		
		doParse(ifileMap);
		
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(AutorConstants.MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	private void doParse(Map<String, IFile> ifileMap){
		if (ifileMap == null || ifileMap.isEmpty()) {
			return;
		}
		
		try {
			// 校验和解析xml
			JavaTemplate template = SaxXmlParser.parse(
					ProjectHelper.getPackagePrefix(getProject().getName()),
					new ArrayList<IFile>(ifileMap.values()));
			// 查看是否变化
			if (template==null || this.compare(template)) {
				return;
			}
			
			// 序列化到本地
			template = TemplateSerialization.serialize(getProject().getName(), template);
			// 解析freemaker模板
			String javaCode = FreemarkerParser.parse(template);
			ConsoleHelper.printInfo("java code:" + javaCode);
			
			// 写文件
			ProjectHelper.toJavaFile(getProject(), javaCode);
			ConsoleHelper.printInfo("end...");
		} catch (Exception e) {
			ConsoleHelper.printError("--AuotR plugin error: parse xml error->"+e.getMessage());
		}
	}
	
	void checkXML(IResource resource, Map<String, IFile> iFileMap) {
	    ConsoleHelper.printInfo("**************check xml:"+resource.getLocation());
	    String target = getProject().getName().concat(AutorConstants.SEPARATOR_BACKSLASH).concat(AutorConstants.SRC_FOLDER_TARGET);
	    
        if (!resource.getLocation().toString().contains(target) && (resource instanceof IFile)
                && resource.getName().toLowerCase().startsWith(AutorConstants.SQLMAP_PREFIX)
                && resource.getName().endsWith(".xml")) {
			
			IFile file = (IFile) resource;
			this.deleteMarkers(file);
			// 
			if (iFileMap.containsKey(file.getName())) {
				return;
			}else {
				iFileMap.put(file.getName(), file);
			}
		}
	}
	
	/**
	 * 删除标记
	 * 
	 * @param file
	 */
	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(AutorConstants.MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}
	
	/**
	 * 如果一直改变项目名，可导致内存溢出o(╯□╰)o
	 * 
	 * @param template
	 * @return true 如果发生变化
	 */
	private synchronized boolean compare(JavaTemplate template){
		String oldValue = last_cache.get(getProject().getName());
		
		if (StringUtils.isEmpty(oldValue)) {
			last_cache.put(getProject().getName(), JsonUtil.toJson(template));
			return false;
		}
		
        return TemplateCompare.compareTemplate(JsonUtil.fromJson(oldValue, JavaTemplate.class),
                template);
	}
	
	public synchronized static void removeCache(String projectName){
		last_cache.remove(projectName);
	}
	
	// 资源监听
	class MyDeltaVisitor implements IResourceDeltaVisitor {
		private Map<String, IFile> ifileMap;
		public MyDeltaVisitor(Map<String, IFile> ifileMap){
			this.ifileMap=ifileMap;
		}
		/**
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
				case IResourceDelta.ADDED:
					checkXML(resource,ifileMap); // handle added resource
					break;
				case IResourceDelta.REMOVED:
				    //do nothing.
					break;
				case IResourceDelta.CHANGED:
					checkXML(resource,ifileMap);; // handle changed resource
					break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class MyResourceVisitor implements IResourceVisitor {
		private Map<String,IFile> ifileMap;
		public MyResourceVisitor(Map<String,IFile> ifileMap){
			this.ifileMap=ifileMap;
		}
		
		public boolean visit(IResource resource) {
			// IResource.PROJECT,IResource.FILE,IResource.FOLDER;
		    int type = resource.getType();
		    switch (type) {
                case IResource.PROJECT:
                    checkXML(resource,ifileMap);
                    break;
                case IResource.FILE:
                    checkXML(resource,ifileMap);
                    break;
                case IResource.FOLDER:
                    //do nothing
                    break;
            }

		    //return true to continue visiting children.
			return true;
		}
	}
}
