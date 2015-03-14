package autor.builder;

import java.util.Iterator;
import org.eclipse.core.commands.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import autor.builder.message.ConsoleHelper;
import autor.builder.project.ProjectHelper;
import autor.builder.template.TemplateSerialization;

public class AddRemoveAutorNatureHandler extends AbstractHandler {

	private ISelection selection;

	public Object execute(ExecutionEvent event) throws ExecutionException {
	    ConsoleHelper.printInfo("execute AddRemoveAutorNatureHandler nature:");
	    
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		//
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				IProject project = null;
				if (element instanceof IProject) {
					project = (IProject) element;
				} else if (element instanceof IAdaptable) {
					project = (IProject) ((IAdaptable) element)
							.getAdapter(IProject.class);
				}
				if (project != null) {
					try {
						toggleNature(project);
					} catch (CoreException e) {
						ConsoleHelper.printError("Failed to toggle nature:"+e.getMessage());
                        throw new ExecutionException("Failed to toggle nature", e);
					}
				}
			}
		}

		return null;
	}

	/**
	 * Toggles sample nature on a project
	 *
	 * @param project
	 *            to have sample nature added or removed
	 */
	private void toggleNature(IProject project) throws CoreException {
	    ConsoleHelper.printInfo("execute toggleNature");
	    
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i) {
			if (AutorNature.NATURE_ID.equals(natures[i])) {
				// Remove the nature
				String[] newNatures = new String[natures.length - 1];
				System.arraycopy(natures, 0, newNatures, 0, i);
				System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);
				description.setNatureIds(newNatures);
				project.setDescription(description, null);
				
				// 删除序列化文件
				TemplateSerialization.removeSerilFile(project.getName());
				// 删除gen资源包
				ProjectHelper.removeSrc(project);
				// 删除缓存
				AutorBuilder.removeCache(project.getName());
				
				return;
			}
		}
		
		ConsoleHelper.printInfo("execute Add the nature");
		// Add the nature
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = AutorNature.NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

}