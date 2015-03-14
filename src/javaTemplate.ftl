package ${packageName};

public class ${className} {
	<#list subClasses as subClass>
	public static class ${subClass.subClassName} {
		<@sub subClass=subClass />
	}
	</#list>
}

<#macro sub subClass>
	<#list subClass.properties as property>
		public static final String ${property.propName} = "${property.propValue}";
	</#list>
</#macro>