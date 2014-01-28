
package org.opentravel.schemacompiler.codegen.impl;

import java.util.List;

import javax.xml.namespace.QName;

import org.opentravel.schemacompiler.codegen.CodeGenerationFilter;
import org.opentravel.schemacompiler.codegen.util.PropertyCodegenUtils;
import org.opentravel.schemacompiler.ioc.SchemaDependency;
import org.opentravel.schemacompiler.model.AbstractLibrary;
import org.opentravel.schemacompiler.model.BuiltInLibrary;
import org.opentravel.schemacompiler.model.LibraryElement;
import org.opentravel.schemacompiler.model.LibraryMember;
import org.opentravel.schemacompiler.model.TLAlias;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLBusinessObject;
import org.opentravel.schemacompiler.model.TLClosedEnumeration;
import org.opentravel.schemacompiler.model.TLCoreObject;
import org.opentravel.schemacompiler.model.TLExtensionPointFacet;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLLibrary;
import org.opentravel.schemacompiler.model.TLListFacet;
import org.opentravel.schemacompiler.model.TLModel;
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
import org.opentravel.schemacompiler.visitor.ModelElementVisitor;
import org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter;
import org.opentravel.schemacompiler.visitor.ModelNavigator;

/**
 * Builder used to construct a <code>CodeGenerationFilter</code> that can be used to identify imports
 * and includes for a single OTM library instance.
 * 
 * @author S. Livezey
 */
public class LibraryFilterBuilder {
	
	private CodeGenerationFilter globalFilter;
	private AbstractLibrary library;
	
	/**
	 * Default constructor.
	 */
	public LibraryFilterBuilder() {}
	
	/**
	 * Constructor that specifies the library instance for which a filter will be created.
	 * 
	 * @param library  the library for which imports and includes are to be defined
	 */
	public LibraryFilterBuilder(AbstractLibrary library) {
		setLibrary(library);
	}
	
	/**
	 * Assigns the library instance for which a filter will be created.
	 * 
	 * @param library  the library for which imports and includes are to be defined
	 * @return LibraryFilterBuilder
	 */
	public LibraryFilterBuilder setLibrary(AbstractLibrary library) {
		this.library = library;
		return this;
	}
	
	/**
	 * Assigns a glober filter for this builder instance.  If assigned, the filter that is produced
	 * by the 'build()' method is guaranteed to allow only a subset of the global filter.  No entities
	 * that are disallowed by the global filter will be allowed by the resulting filter.
	 * 
	 * @param globalFilter  the global filter to apply when generating the library-specific filter
	 * @return LibraryFilterBuilder
	 */
	public LibraryFilterBuilder setGlobalFilter(CodeGenerationFilter globalFilter) {
		this.globalFilter = globalFilter;
		return this;
	}
	
	/**
	 * Constructs the filter that can be used to identify the imports and includes of the target
	 * library.
	 * 
	 * @return CodeGenerationFilter
	 */
	public CodeGenerationFilter buildFilter() {
		DependencyVisitor visitor = new DependencyVisitor();
		DependencyModelNavigator navigator = new DependencyModelNavigator(visitor);
		
		if (library instanceof BuiltInLibrary) {
			navigator.navigateBuiltInLibrary((BuiltInLibrary) library);
			
		} else if (library instanceof XSDLibrary) {
			navigator.navigateLegacySchemaLibrary((XSDLibrary) library);
			
		} else if (library instanceof TLLibrary) {
			navigator.navigateUserDefinedLibrary((TLLibrary) library);
		}
		return visitor.getFilter();
	}
	
	/**
	 * Extends the navigation logic of the <code>ModelNavigator</code> to include the
	 * navigation of inherited attributes and elements.
	 *
	 * @author S. Livezey
	 */
	private class DependencyModelNavigator extends ModelNavigator {

		/**
		 * Constructor that initializes the visitor to be notified when model elements are encountered
	 	 * during navigation.
	 	 * 
	 	 * @param visitor  the visitor to be notified when model elements are encountered
		 */
		private DependencyModelNavigator(ModelElementVisitor visitor) {
			super(visitor);
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelNavigator#navigateFacet(org.opentravel.schemacompiler.model.TLFacet)
		 */
		@Override
		public void navigateFacet(TLFacet facet) {
			if (canVisit(facet) && visitor.visitFacet(facet)) {
				List<TLAttribute> attributeList = PropertyCodegenUtils.getInheritedAttributes(facet);
				List<TLProperty> propertyList = PropertyCodegenUtils.getInheritedProperties(facet);
				
				for (TLAlias alias : facet.getAliases()) {
					navigateAlias(alias);
				}
				for (TLAttribute attribute : attributeList) {
					navigateAttribute(attribute);
				}
				for (TLProperty element : propertyList) {
					navigateElement(element);
				}
			}
			addVisitedNode(facet);
		}
		
		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelNavigator#navigateValueWithAttributes(org.opentravel.schemacompiler.model.TLValueWithAttributes)
		 */
		@Override
		public void navigateValueWithAttributes(TLValueWithAttributes valueWithAttributes) {
			if (canVisit(valueWithAttributes) && visitor.visitValueWithAttributes(valueWithAttributes)) {
				List<TLAttribute> attributeList = PropertyCodegenUtils.getInheritedAttributes(valueWithAttributes);
				
				for (TLAttribute attribute : attributeList) {
					navigateAttribute(attribute);
				}
			}
			addVisitedNode(valueWithAttributes);
		}

	}
	
	/**
	 * Model element visitor that captures all named entities that are required by the
	 * currently-assigned service.
	 *
	 * @author S. Livezey
	 */
	private class DependencyVisitor extends ModelElementVisitorAdapter {
		
		private DefaultCodeGenerationFilter filter = new DefaultCodeGenerationFilter();
		
		/**
		 * Returns the filter that was populated during service dependency navigation.
		 * 
		 * @return CodeGenerationFilter
		 */
		public CodeGenerationFilter getFilter() {
			return filter;
		}
		
		/**
		 * Internal visitor method that adds the given library element and it's owning library to
		 * the filter that is being populated.
		 * 
		 * @param entity  the library element being visited
		 */
		private void visitLibraryElement(LibraryElement entity) {
			if ((entity != null) && isAllowedByGlobalFilter(entity)) {
				filter.addProcessedLibrary(entity.getOwningLibrary());
				filter.addProcessedElement(entity);
			}
		}
		
		/**
		 * Returns true if the global filter will allow the inclusion of the given entity.
		 * 
		 * @param entity  the library element being visited
		 * @return boolean
		 */
		private boolean isAllowedByGlobalFilter(LibraryElement entity) {
			return (globalFilter == null) || globalFilter.processEntity(entity);
		}

		/**
		 * When the compiled XSD includes an implied dependency on a built-in type, this method ensures
		 * it will be imported, even though it is not directly referenced by the source library.
		 * 
		 * @param model  the model that contains the built-in libraries to be searched
		 */
		private void visitSchemaDependency(SchemaDependency dependency, TLModel model) {
			QName dependencyQName = dependency.toQName();
			
			for (BuiltInLibrary library : model.getBuiltInLibraries()) {
				if (library.getNamespace().equals(dependencyQName.getNamespaceURI())) {
					LibraryMember builtInType = library.getNamedMember(dependencyQName.getLocalPart());
					
					if (builtInType != null) {
						visitLibraryElement(builtInType);
					}
				}
			}
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitSimple(org.opentravel.schemacompiler.model.TLSimple)
		 */
		@Override
		public boolean visitSimple(TLSimple simple) {
			visitLibraryElement( simple.getParentType() );
			return isAllowedByGlobalFilter( simple );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitValueWithAttributes(org.opentravel.schemacompiler.model.TLValueWithAttributes)
		 */
		@Override
		public boolean visitValueWithAttributes(TLValueWithAttributes valueWithAttributes) {
			if (valueWithAttributes.getParentType() == null) {
				visitSchemaDependency(SchemaDependency.getEmptyElement(), valueWithAttributes.getOwningModel());
			}
			visitLibraryElement( valueWithAttributes.getParentType() );
			return isAllowedByGlobalFilter( valueWithAttributes );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitSimpleFacet(org.opentravel.schemacompiler.model.TLSimpleFacet)
		 */
		@Override
		public boolean visitSimpleFacet(TLSimpleFacet simpleFacet) {
			visitLibraryElement( simpleFacet.getSimpleType() );
			return isAllowedByGlobalFilter( simpleFacet );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitAttribute(org.opentravel.schemacompiler.model.TLAttribute)
		 */
		@Override
		public boolean visitAttribute(TLAttribute attribute) {
			visitLibraryElement( attribute.getType() );
			return isAllowedByGlobalFilter( attribute.getType() );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitElement(org.opentravel.schemacompiler.model.TLProperty)
		 */
		@Override
		public boolean visitElement(TLProperty element) {
			if (element.getType() instanceof XSDComplexType) {
				XSDComplexType propertyType = (XSDComplexType) element.getType();
				
				if (propertyType.getIdentityAlias() != null) {
					visitLibraryElement(propertyType.getIdentityAlias());
					
				} else {
					AbstractLibrary owningLibrary = propertyType.getOwningLibrary();
					
					if (owningLibrary instanceof XSDLibrary) {
						// If the identity alias of the complex type is null, the code generator will have to
						// generate an extension schema for the type.  Therefore, we should create a dependency
						// on the extension schema instead of the legacy schema itself.
						filter.addExtensionLibrary((XSDLibrary) propertyType.getOwningLibrary());
						filter.addProcessedElement(propertyType);
					}
				}
			}
			visitLibraryElement( element.getType() );
			return isAllowedByGlobalFilter( element.getType() );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitOpenEnumeration(org.opentravel.schemacompiler.model.TLOpenEnumeration)
		 */
		@Override
		public boolean visitOpenEnumeration(TLOpenEnumeration enumeration) {
			visitSchemaDependency(SchemaDependency.getEnumExtension(), enumeration.getOwningModel());
			return isAllowedByGlobalFilter( enumeration );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitRole(org.opentravel.schemacompiler.model.TLRole)
		 */
		@Override
		public boolean visitRole(TLRole role) {
			visitSchemaDependency(SchemaDependency.getEnumExtension(), role.getOwningModel());
			return isAllowedByGlobalFilter( role.getRoleEnumeration().getOwningEntity() );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitClosedEnumeration(org.opentravel.schemacompiler.model.TLClosedEnumeration)
		 */
		@Override
		public boolean visitClosedEnumeration(TLClosedEnumeration enumeration) {
			return isAllowedByGlobalFilter( enumeration );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitCoreObject(org.opentravel.schemacompiler.model.TLCoreObject)
		 */
		@Override
		public boolean visitCoreObject(TLCoreObject coreObject) {
			return isAllowedByGlobalFilter( coreObject );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitBusinessObject(org.opentravel.schemacompiler.model.TLBusinessObject)
		 */
		@Override
		public boolean visitBusinessObject(TLBusinessObject businessObject) {
			return isAllowedByGlobalFilter( businessObject );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitFacet(org.opentravel.schemacompiler.model.TLFacet)
		 */
		@Override
		public boolean visitFacet(TLFacet facet) {
			return isAllowedByGlobalFilter( facet );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitListFacet(org.opentravel.schemacompiler.model.TLListFacet)
		 */
		@Override
		public boolean visitListFacet(TLListFacet listFacet) {
			return isAllowedByGlobalFilter( listFacet );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitAlias(org.opentravel.schemacompiler.model.TLAlias)
		 */
		@Override
		public boolean visitAlias(TLAlias alias) {
			return isAllowedByGlobalFilter( alias );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitService(org.opentravel.schemacompiler.model.TLService)
		 */
		@Override
		public boolean visitService(TLService service) {
			return isAllowedByGlobalFilter( service );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitOperation(org.opentravel.schemacompiler.model.TLOperation)
		 */
		@Override
		public boolean visitOperation(TLOperation operation) {
			return isAllowedByGlobalFilter( operation );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitExtensionPointFacet(org.opentravel.schemacompiler.model.TLExtensionPointFacet)
		 */
		@Override
		public boolean visitExtensionPointFacet(TLExtensionPointFacet extensionPointFacet) {
			return isAllowedByGlobalFilter( extensionPointFacet );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitXSDSimpleType(org.opentravel.schemacompiler.model.XSDSimpleType)
		 */
		@Override
		public boolean visitXSDSimpleType(XSDSimpleType xsdSimple) {
			return isAllowedByGlobalFilter( xsdSimple );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitXSDComplexType(org.opentravel.schemacompiler.model.XSDComplexType)
		 */
		@Override
		public boolean visitXSDComplexType(XSDComplexType xsdComplex) {
			return isAllowedByGlobalFilter( xsdComplex );
		}

		/**
		 * @see org.opentravel.schemacompiler.visitor.ModelElementVisitorAdapter#visitXSDElement(org.opentravel.schemacompiler.model.XSDElement)
		 */
		@Override
		public boolean visitXSDElement(XSDElement xsdElement) {
			return isAllowedByGlobalFilter( xsdElement );
		}

	}
	
}