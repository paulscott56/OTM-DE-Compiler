
package org.opentravel.schemacompiler.visitor;

import org.opentravel.schemacompiler.model.BuiltInLibrary;
import org.opentravel.schemacompiler.model.TLAlias;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLBusinessObject;
import org.opentravel.schemacompiler.model.TLClosedEnumeration;
import org.opentravel.schemacompiler.model.TLContext;
import org.opentravel.schemacompiler.model.TLCoreObject;
import org.opentravel.schemacompiler.model.TLDocumentation;
import org.opentravel.schemacompiler.model.TLEnumValue;
import org.opentravel.schemacompiler.model.TLEquivalent;
import org.opentravel.schemacompiler.model.TLExample;
import org.opentravel.schemacompiler.model.TLExtension;
import org.opentravel.schemacompiler.model.TLExtensionPointFacet;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLInclude;
import org.opentravel.schemacompiler.model.TLIndicator;
import org.opentravel.schemacompiler.model.TLLibrary;
import org.opentravel.schemacompiler.model.TLListFacet;
import org.opentravel.schemacompiler.model.TLNamespaceImport;
import org.opentravel.schemacompiler.model.TLOpenEnumeration;
import org.opentravel.schemacompiler.model.TLOperation;
import org.opentravel.schemacompiler.model.TLProperty;
import org.opentravel.schemacompiler.model.TLRole;
import org.opentravel.schemacompiler.model.TLService;
import org.opentravel.schemacompiler.model.TLSimple;
import org.opentravel.schemacompiler.model.TLSimpleFacet;
import org.opentravel.schemacompiler.model.TLValueWithAttributes;
import org.opentravel.schemacompiler.model.XSDComplexType;
import org.opentravel.schemacompiler.model.XSDElement;
import org.opentravel.schemacompiler.model.XSDLibrary;
import org.opentravel.schemacompiler.model.XSDSimpleType;

/**
 * Adapter class that provides empty implementations for all methods of the
 * <code>ModelElementVisitor</code> interface.
 * 
 * @author S. Livezey
 */
public class ModelElementVisitorAdapter implements ModelElementVisitor {
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitBuiltInLibrary(org.opentravel.schemacompiler.model.BuiltInLibrary)
	 */
	@Override
	public boolean visitBuiltInLibrary(BuiltInLibrary library) {
		return true;
	}

	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitLegacySchemaLibrary(org.opentravel.schemacompiler.model.XSDLibrary)
	 */
	@Override
	public boolean visitLegacySchemaLibrary(XSDLibrary library) {
		return true;
	}

	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitUserDefinedLibrary(org.opentravel.schemacompiler.model.TLLibrary)
	 */
	@Override
	public boolean visitUserDefinedLibrary(TLLibrary library) {
		return true;
	}

	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitContext(org.opentravel.schemacompiler.model.TLContext)
	 */
	@Override
	public boolean visitContext(TLContext context) {
		return false;
	}

	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitSimple(org.opentravel.schemacompiler.model.TLSimple)
	 */
	@Override
	public boolean visitSimple(TLSimple simple) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitValueWithAttributes(org.opentravel.schemacompiler.model.TLValueWithAttributes)
	 */
	@Override
	public boolean visitValueWithAttributes(TLValueWithAttributes valueWithAttributes) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitClosedEnumeration(org.opentravel.schemacompiler.model.TLClosedEnumeration)
	 */
	@Override
	public boolean visitClosedEnumeration(TLClosedEnumeration enumeration) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitOpenEnumeration(org.opentravel.schemacompiler.model.TLOpenEnumeration)
	 */
	@Override
	public boolean visitOpenEnumeration(TLOpenEnumeration enumeration) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitEnumValue(org.opentravel.schemacompiler.model.TLEnumValue)
	 */
	@Override
	public boolean visitEnumValue(TLEnumValue enumValue) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitCoreObject(org.opentravel.schemacompiler.model.TLCoreObject)
	 */
	@Override
	public boolean visitCoreObject(TLCoreObject coreObject) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitRole(org.opentravel.schemacompiler.model.TLRole)
	 */
	@Override
	public boolean visitRole(TLRole role) {
		return true;
	}

	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitBusinessObject(org.opentravel.schemacompiler.model.TLBusinessObject)
	 */
	@Override
	public boolean visitBusinessObject(TLBusinessObject businessObject) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitService(org.opentravel.schemacompiler.model.TLService)
	 */
	@Override
	public boolean visitService(TLService service) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitOperation(org.opentravel.schemacompiler.model.TLOperation)
	 */
	@Override
	public boolean visitOperation(TLOperation operation) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitExtensionPointFacet(org.opentravel.schemacompiler.model.TLExtensionPointFacet)
	 */
	@Override
	public boolean visitExtensionPointFacet(TLExtensionPointFacet extensionPointFacet) {
		return true;
	}

	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitXSDSimpleType(org.opentravel.schemacompiler.model.XSDSimpleType)
	 */
	@Override
	public boolean visitXSDSimpleType(XSDSimpleType xsdSimple) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitXSDComplexType(org.opentravel.schemacompiler.model.XSDComplexType)
	 */
	@Override
	public boolean visitXSDComplexType(XSDComplexType xsdComplex) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitXSDElement(org.opentravel.schemacompiler.model.XSDElement)
	 */
	@Override
	public boolean visitXSDElement(XSDElement xsdElement) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitFacet(org.opentravel.schemacompiler.model.TLFacet)
	 */
	@Override
	public boolean visitFacet(TLFacet facet) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitSimpleFacet(org.opentravel.schemacompiler.model.TLSimpleFacet)
	 */
	@Override
	public boolean visitSimpleFacet(TLSimpleFacet simpleFacet) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitListFacet(org.opentravel.schemacompiler.model.TLListFacet)
	 */
	@Override
	public boolean visitListFacet(TLListFacet listFacet) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitAlias(org.opentravel.schemacompiler.model.TLAlias)
	 */
	@Override
	public boolean visitAlias(TLAlias alias) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitAttribute(org.opentravel.schemacompiler.model.TLAttribute)
	 */
	@Override
	public boolean visitAttribute(TLAttribute attribute) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitElement(org.opentravel.schemacompiler.model.TLProperty)
	 */
	@Override
	public boolean visitElement(TLProperty element) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitIndicator(org.opentravel.schemacompiler.model.TLIndicator)
	 */
	@Override
	public boolean visitIndicator(TLIndicator indicator) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitExtension(org.opentravel.schemacompiler.model.TLExtension)
	 */
	@Override
	public boolean visitExtension(TLExtension extension) {
		return true;
	}

	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitNamespaceImport(org.opentravel.schemacompiler.model.TLNamespaceImport)
	 */
	@Override
	public boolean visitNamespaceImport(TLNamespaceImport nsImport) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitInclude(org.opentravel.schemacompiler.model.TLInclude)
	 */
	@Override
	public boolean visitInclude(TLInclude include) {
		return true;
	}

	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitEquivalent(org.opentravel.schemacompiler.model.TLEquivalent)
	 */
	@Override
	public boolean visitEquivalent(TLEquivalent equivalent) {
		return true;
	}
	
	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitExample(org.opentravel.schemacompiler.model.TLExample)
	 */
	@Override
	public boolean visitExample(TLExample example) {
		return false;
	}

	/**
	 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitor#visitDocumentation(org.opentravel.schemacompiler.model.TLDocumentation)
	 */
	@Override
	public boolean visitDocumentation(TLDocumentation documentation) {
		return true;
	}

}