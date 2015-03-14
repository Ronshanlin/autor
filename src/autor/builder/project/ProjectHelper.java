/**
 * 
 */
package autor.builder.project;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import autor.builder.message.ConsoleHelper;
import autor.builder.message.Messages;
import autor.builder.utils.AutorConstants;
import autor.builder.utils.StringUtils;

/**
 * @author shanlin
 * @since 2015-03-08
 */
public class ProjectHelper {
	
	public static void toJavaFile(IProject project, String javaCode){
	    ConsoleHelper.printInfo("create java code start");
		ProjectHelper attr = new ProjectHelper();
		try {
			attr.createSrc(project, javaCode);
		} catch (CoreException e) {
			ConsoleHelper.printError("--AuotR plugin error: creat src or package error"+e.getMessage());
		}
	}
	
	public static void removeSrc(IProject project) throws CoreException{
	    IJavaProject javaProject = JavaCore.create(project);
        IClasspathEntry[] classpathEntries = javaProject.readRawClasspath();
        
        for (int i = 0; i < classpathEntries.length; i++) {
            IClasspathEntry iClasspathEntry = classpathEntries[i];
            
            if (IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind()
                && iClasspathEntry.getPath().toString().contains(AutorConstants.SRC_FOLDER_GEN)) {
                
                IClasspathEntry[] newClassPathEntries = new IClasspathEntry[classpathEntries.length - 1];
                System.arraycopy(classpathEntries, 0, newClassPathEntries, 0, i);
                System.arraycopy(classpathEntries, i + 1, newClassPathEntries, i, classpathEntries.length - i - 1);
                javaProject.setRawClasspath(newClassPathEntries, null);
                
                String srcPath = iClasspathEntry.getPath().toString();
                String path = project.getLocation().toString()
                        .concat(srcPath.substring(srcPath.lastIndexOf(AutorConstants.SEPARATOR_BACKSLASH)));
                
                ConsoleHelper.printInfo("remove path:"+path);
                
                File file = new File(path);
                if (file.exists()) {
                    deleteDir(file);
                }
                
                break;
            }
        }
	}
	
	/**
	 * 获取包名
	 *
	 * @param projectName
	 * @return
	 */
	public static String getPackagePrefix(String projectName){
	    String name = StringUtils.pkgNameFilter(projectName);
	    
	    String prefix = Messages.Package_Name_Prefix;
	    if (prefix.endsWith(AutorConstants.SEPARATOR_DOT)) {
            name = prefix.concat(name);
        }else {
            name = prefix.concat(AutorConstants.SEPARATOR_DOT).concat(name);
        }
	    
	    return name.toLowerCase();
	}
	
    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            // 递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
	
	private void createSrc(IProject project, String javaCode) throws CoreException{
	    ConsoleHelper.printInfo("create src..");
		boolean hasGen = false;
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] classpathEntries = javaProject.readRawClasspath();
		for (int i = 0; i < classpathEntries.length; i++) {
			IClasspathEntry iClasspathEntry = classpathEntries[i];
			if (IClasspathEntry.CPE_SOURCE == iClasspathEntry.getEntryKind()
				&& iClasspathEntry.getPath().toString().contains(AutorConstants.SRC_FOLDER_GEN)) {
				hasGen = true;
				break;
			}
		}
		
		if (!hasGen) {
			IFolder genFolder = project.getFolder(AutorConstants.SRC_FOLDER_GEN);
			if (!genFolder.exists()) {
			    ConsoleHelper.printInfo("gen src doesn't exist,create it");
				genFolder.create(true, true, null);
			}
			IClasspathEntry newClasspathEntry  = JavaCore.newSourceEntry(genFolder.getFullPath());
			
			List<IClasspathEntry> list = new ArrayList<IClasspathEntry>();
			list.addAll(Arrays.asList(classpathEntries));
			list.add(newClasspathEntry);
			
			javaProject.setRawClasspath(list.toArray(new IClasspathEntry[list.size()]), null);
		}
		
		// 创建包和类
		this.createPackage(javaProject, project, javaCode);
	}
	
	private void createPackage(IJavaProject javaProject,IProject project, String javaCode) throws JavaModelException{
	    ConsoleHelper.printInfo("create package");
	    
        // 先找指定的源文件夹所在的IPackageFragmentRoot
		IPackageFragmentRoot packageFragmentRoot = javaProject
				.findPackageFragmentRoot(new Path("/"+project.getName().concat("/"+
						AutorConstants.SRC_FOLDER_GEN)));
		
		String packageName = getPackagePrefix(project.getName());
        // 根据IPackageFragmentRoot创建IPackageFragment,IPackageFragment就是包了
		IPackageFragment packageFragment = packageFragmentRoot.getPackageFragment(packageName);
		if (!packageFragment.exists()) {
			packageFragment = packageFragmentRoot.createPackageFragment(packageName, true, null);
		}
		
		ConsoleHelper.printInfo("create R.java");
		// 创建java文件
		packageFragment.createCompilationUnit(AutorConstants.FILE_R_NAME, javaCode, true, new NullProgressMonitor());
	}
}