package pa.iscde.javadoc.generator;

import java.io.File;
import java.util.Map;

import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WildcardType;

public class FieldDeclarationWrapper {
    
    private String modifier;
    private ObjectMap varMap;

    public FieldDeclarationWrapper(FieldDeclaration field, Map<String, File> workSpaceFiles) {
	this.modifier = extractModifier(field);
	this.varMap = extractType(field, workSpaceFiles);
    }

    public String getModifier() {
	return modifier;
    }

    public ObjectMap getVarMap() {
	return varMap;
    }

    private String extractModifier(FieldDeclaration field) {
	return field.modifiers().get(0).toString();
    }

    private ObjectMap extractType(FieldDeclaration field, Map<String, File> workSpaceFiles) {
	VariableDeclarationFragment var = (VariableDeclarationFragment) field.fragments().get(0);
	String varType = field.getType().toString();
	String varName = var.getName().toString();
	String clazz = "";
	
	if (field.getType() instanceof ArrayType) {
	    ArrayType x = ((ArrayType) field.getType());
	    clazz = x.getElementType().toString();
	} else if (field.getType() instanceof SimpleType) {
	    SimpleType x = ((SimpleType) field.getType());
	    clazz = x.toString();
	} else if (field.getType() instanceof QualifiedType) {
	    System.out.println(3);
	} else if (field.getType() instanceof NameQualifiedType) {
	    System.out.println(4);
	} else if (field.getType() instanceof WildcardType) {
	    System.out.println(5);
	} else if (field.getType() instanceof ParameterizedType) {
	    ParameterizedType x = ((ParameterizedType) field.getType());
	    clazz = x.typeArguments().get(0).toString();
	} else if (field.getType() instanceof UnionType) {
	    System.out.println(7);
	} else if (field.getType() instanceof IntersectionType) {
	    System.out.println(8);
	}
	
	String path = workSpaceFiles.get(clazz + ".java") != null ? workSpaceFiles.get(clazz + ".java").getAbsolutePath() : null;
	
	//Substituição por caracteres identicos, umas vez que os caracteres < e >, são delimitdores de String na Template
	varType = varType.replace('<', '≤');
	varType = varType.replace('>', '≥');
	ObjectMap varMap = new ObjectMap(varType, varName, path);

	return varMap;
    }
}