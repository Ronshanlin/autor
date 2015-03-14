/**
 * 
 */
package autor.builder.utils;

import java.util.Collection;

/**
 * @author shanlin
 *
 */
public class CollectionUtils {
	
	public static <T> boolean isEmpty(Collection<T> collection){
		if (collection == null || collection.isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	public static <T> boolean isNotEmpty(Collection<T> collection){
		return !isEmpty(collection);
	}
}
