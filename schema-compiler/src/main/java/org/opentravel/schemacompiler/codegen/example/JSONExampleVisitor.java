/**
 * Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opentravel.schemacompiler.codegen.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.opentravel.ns.ota2.librarymodel_v01_05.EnumXsdSimpleType;
import org.opentravel.schemacompiler.codegen.util.AliasCodegenUtils;
import org.opentravel.schemacompiler.codegen.util.FacetCodegenUtils;
import org.opentravel.schemacompiler.codegen.util.JsonSchemaNamingUtils;
import org.opentravel.schemacompiler.codegen.util.PropertyCodegenUtils;
import org.opentravel.schemacompiler.codegen.util.XsdCodegenUtils;
import org.opentravel.schemacompiler.model.AbstractLibrary;
import org.opentravel.schemacompiler.model.NamedEntity;
import org.opentravel.schemacompiler.model.TLAbstractEnumeration;
import org.opentravel.schemacompiler.model.TLActionFacet;
import org.opentravel.schemacompiler.model.TLAlias;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLAttributeOwner;
import org.opentravel.schemacompiler.model.TLAttributeType;
import org.opentravel.schemacompiler.model.TLBusinessObject;
import org.opentravel.schemacompiler.model.TLComplexTypeBase;
import org.opentravel.schemacompiler.model.TLCoreObject;
import org.opentravel.schemacompiler.model.TLExtensionPointFacet;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetType;
import org.opentravel.schemacompiler.model.TLIndicator;
import org.opentravel.schemacompiler.model.TLListFacet;
import org.opentravel.schemacompiler.model.TLOpenEnumeration;
import org.opentravel.schemacompiler.model.TLOperation;
import org.opentravel.schemacompiler.model.TLPatchableFacet;
import org.opentravel.schemacompiler.model.TLProperty;
import org.opentravel.schemacompiler.model.TLPropertyOwner;
import org.opentravel.schemacompiler.model.TLPropertyType;
import org.opentravel.schemacompiler.model.TLRole;
import org.opentravel.schemacompiler.model.TLRoleEnumeration;
import org.opentravel.schemacompiler.model.TLSimple;
import org.opentravel.schemacompiler.model.TLSimpleFacet;
import org.opentravel.schemacompiler.model.TLValueWithAttributes;
import org.opentravel.schemacompiler.model.XSDComplexType;
import org.opentravel.schemacompiler.model.XSDElement;
import org.opentravel.schemacompiler.model.XSDSimpleType;
import org.w3._2001.xmlschema.TopLevelSimpleType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * <code>ExampleVisitor</code> component used to construct a JSON tree
 * containing the example data.
 * 
 * @author E. Bronson
 */
public class JSONExampleVisitor extends AbstractExampleVisitor<JsonNode> {

	private JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
	private ObjectNode node;
	private List<JsonIdReferenceAssignment> referenceAssignments = new ArrayList<JsonIdReferenceAssignment>();

	/**
	 * Default constructor.
	 */
	public JSONExampleVisitor() {
		this(null);
	}

	public JSONExampleVisitor(String preferredContext) {
		super(preferredContext);
		node = nodeFactory.objectNode();
	}

	@Override
	public Collection<String> getBoundNamespaces() {
		return null;
	}

	/**
	 * Returns the JsonNode instance that was constructed during the navigation
	 * process.
	 * 
	 * @return JsonNode
	 */
	public JsonNode getNode() {
		synchronized (referenceAssignments) {
			for (JsonIdReferenceAssignment refAssignment : referenceAssignments) {
				refAssignment.assignReferenceValue();
			}
			referenceAssignments.clear(); // clear the list so we don't do this
											// more than once
			idRegistry.clear();
		}
		return node;
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#visitSimpleType(org.opentravel.schemacompiler.model.TLAttributeType)
	 */
	@Override
	public void visitSimpleType(TLAttributeType simpleType) {
		super.visitSimpleType(simpleType);
		createSimpleElement(simpleType);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#startFacet(org.opentravel.schemacompiler.model.TLFacet)
	 */
	@Override
	public void startFacet(TLFacet facet) {
		super.startFacet(facet);
		facetStack.push(facet);
		createObjectNode(facet);
		
		// check if this facet is in a substitution group
		TLAlias elementAlias = context.getModelAlias();
		
		addJsonFacetType( facet, elementAlias );
//		NamedEntity elementType = facet;
		
		// If this element is part of a polymorphic array, use the array values for
		// the @type property
//		if (!contextStack.isEmpty()) {
//			ExampleContext ctx = contextStack.peek();
//			
//			if (ctx.getNode().isArray() && (ctx.getModelElement() != null)) {
//				elementType = ctx.getModelElement().getType();
//				elementAlias = ctx.getModelAlias();
//			}
//		}
//		
//		if (elementType instanceof TLAlias) {
//			elementAlias = (TLAlias) elementType;
//			elementType = elementAlias.getOwningEntity();
//		}
		/*
		if ((elementType instanceof TLFacet) || (elementType instanceof TLFacetOwner)) {
			String typeName;
			
			if (elementAlias != null) {
				typeName = JsonSchemaNamingUtils.getGlobalDefinitionName( elementAlias );
			} else {
				typeName = JsonSchemaNamingUtils.getGlobalDefinitionName( elementType );
			}
			((ObjectNode) context.getNode()).put("@type", typeName);
			if ("SampleCore_Detail".equals(typeName)) {
				System.out.println("tag-1");
			}
		}
		((ObjectNode) context.getNode()).put("@type", "DUMMYTYPE-FACET");
		*/
		
		// Add additional properties for specialized entity types
		if (facet.getOwningEntity() instanceof TLCoreObject) {
			addRoleAttributes((TLCoreObject) facet.getOwningEntity());

		} else if (facet.getOwningEntity() instanceof TLOperation) {
			addOperationPayloadContent(facet);
		}
	}
	
	/**
	 * @see org.opentravel.schemacompiler.codegen.example.AbstractExampleVisitor#endFacet(org.opentravel.schemacompiler.model.TLFacet)
	 */
	@Override
	public void endFacet(TLFacet facet) {
		super.endFacet(facet);

		if (facetStack.peek() == facet) {
			facetStack.pop();
		}
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#startListFacet(org.opentravel.schemacompiler.model.TLListFacet,
	 *      org.opentravel.schemacompiler.model.TLRole)
	 */
	@Override
	public void startListFacet(TLListFacet listFacet, TLRole role) {
		super.startListFacet(listFacet, role);
		// If this is a repeat visit of the list facet for a different role, we
		// must complete the
		// old element and start a new one.
		JsonNode parent = null;

		if (context.getNode() != null) {
			TLAlias contextAlias = context.getModelAlias();
			context = new ExampleContext(context.getModelElement());
			parent = contextStack.peek().getNode();
			context.setModelAlias(contextAlias);
		}

		// If this is a list facet for a normal TLFacet, push the underlying
		// item facet onto the
		// stack
		if (listFacet.getItemFacet() instanceof TLFacet) {
			facetStack.push((TLFacet) listFacet.getItemFacet());
		}
		// been here more than once, we only make it an array now because a list
		// facet with only 1 role is not repeatable in the schema.
		if (parent != null) {
			String nodeName = getElementName(listFacet);
			ArrayNode arrayNode;
			// second time through
			if (parent.isObject()) {
				JsonNode currentNode = parent.findValue(nodeName);
				if (!currentNode.isArray()) {
					arrayNode = nodeFactory.arrayNode();
					((ObjectNode) parent).replace(nodeName, arrayNode);
					arrayNode.add(currentNode);
				} else {
					arrayNode = (ArrayNode) currentNode;
				}
			} else { // more than twice
				arrayNode = (ArrayNode) parent;
			}
			context.setNode(arrayNode);
			contextStack.push(context);
			TLAlias contextAlias = context.getModelAlias();
			context = new ExampleContext(context.getModelElement());
			context.setModelAlias(contextAlias);
		} 
		createObjectNode(listFacet);
		
		if (listFacet.getItemFacet() instanceof TLFacet) {
			TLFacet itemFacet = (TLFacet) listFacet.getItemFacet();
			TLAlias contextAlias = context.getModelAlias();
			TLAlias ownerAlias = (contextAlias == null) ? null : AliasCodegenUtils.getOwnerAlias( contextAlias );
			TLAlias itemAlias = (ownerAlias == null) ? null : AliasCodegenUtils.getFacetAlias(
					ownerAlias, itemFacet.getFacetType(), itemFacet.getContext(), itemFacet.getLabel() );
			
			addJsonFacetType( itemFacet, itemAlias );
			
			if (listFacet.getOwningEntity() instanceof TLCoreObject) {
				addRoleAttributes((TLCoreObject) listFacet.getOwningEntity());
			}
		}

//		((ObjectNode) context.getNode()).put("@type", "DUMMYTYPE-LISTFACET");

//		if ((listFacet.getOwningEntity() instanceof TLCoreObject)
//				&& !(listFacet.getItemFacet() instanceof TLSimpleFacet)) {
//			addRoleAttributes((TLCoreObject) listFacet.getOwningEntity());
//		}
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.AbstractExampleVisitor#endListFacet(org.opentravel.schemacompiler.model.TLListFacet,
	 *      org.opentravel.schemacompiler.model.TLRole)
	 */
	@Override
	public void endListFacet(TLListFacet listFacet, TLRole role) {
		super.endListFacet(listFacet, role);

		if (facetStack.peek() == listFacet.getItemFacet()) {
			facetStack.pop();
		}

		if (contextStack.peek().getNode().isArray()) {
			context = contextStack.pop();
		}

	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#startAlias(org.opentravel.schemacompiler.model.TLAlias)
	 */
	@Override
	public void startAlias(TLAlias alias) {
		super.startAlias(alias);

		if (context == null) {
			throw new IllegalStateException(
					"Alias encountered without an available element context.");
		}
		context.setModelAlias(alias);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#endAlias(org.opentravel.schemacompiler.model.TLAlias)
	 */
	@Override
	public void endAlias(TLAlias alias) {
		super.endAlias(alias);

		if (context == null) {
			throw new IllegalStateException(
					"Alias encountered without an available element context.");
		}
		context.setModelAlias(null);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.AbstractExampleVisitor#startActionFacet(org.opentravel.schemacompiler.model.TLActionFacet, org.opentravel.schemacompiler.model.TLFacet)
	 */
	@Override
	public void startActionFacet(TLActionFacet actionFacet, TLFacet payloadFacet) {
		super.startActionFacet(actionFacet, payloadFacet);
		context.setModelActionFacet(actionFacet);
		facetStack.push(payloadFacet);
		createObjectNode(actionFacet);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.AbstractExampleVisitor#endActionFacet(org.opentravel.schemacompiler.model.TLActionFacet, org.opentravel.schemacompiler.model.TLFacet)
	 */
	@Override
	public void endActionFacet(TLActionFacet actionFacet, TLFacet payloadFacet) {
		super.endActionFacet(actionFacet, payloadFacet);

		if (facetStack.peek() == payloadFacet) {
			facetStack.pop();
		}
		context.setModelActionFacet(null);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#startAttribute(org.opentravel.schemacompiler.model.TLAttribute)
	 */
	@Override
	public void startAttribute(TLAttribute attribute) {
		super.startAttribute(attribute);

		if (context == null) {
			throw new IllegalStateException(
					"Attribute encountered without an available element context.");
		}
		context.setModelAttribute(attribute);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#endAttribute(org.opentravel.schemacompiler.model.TLAttribute)
	 */
	@Override
	public void endAttribute(TLAttribute attribute) {
		super.endAttribute(attribute);

		if (context == null) {
			throw new IllegalStateException(
					"Attribute encountered without an available element context.");
		}

		// Capture ID values in the registry for use during post-processing
		if (XsdCodegenUtils.isIdType(attribute.getType())) {
			TLAttributeOwner owner = attribute.getOwner();
			NamedEntity contextFacet = getContextFacet();

			if (contextFacet != null) {
				registerIdValue(contextFacet, lastExampleValue);
			} else {
				registerIdValue(owner, lastExampleValue);
			}
		}

		// Queue up IDREF(S) attributes for assignment during post-processing
		if (XsdCodegenUtils.isIdRefType(attribute.getType())) {
			referenceAssignments.add(new JsonIdReferenceAssignment(null, 1,
					attribute.getName(), false));
		}
		if (XsdCodegenUtils.isIdRefsType(attribute.getType())) {
			referenceAssignments.add(new JsonIdReferenceAssignment(null, 3,
					attribute.getName(), false));
		}

		// If the attribute was an open enumeration type, we have to add an
		// additional attribute
		// for the 'Extension' value.
		TLAttributeType attributeType = attribute.getType();

		while (attributeType instanceof TLValueWithAttributes) {
			attributeType = ((TLValueWithAttributes) attributeType)
					.getParentType();
		}
		if (attributeType instanceof TLOpenEnumeration) {
			((ObjectNode) context.getNode()).put(context.getModelAttribute()
					.getName() + "Extension", context.getModelAttribute()
					.getName() + "_Other_Value");
		}
		context.setModelAttribute(null);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#startElement(org.opentravel.schemacompiler.model.TLProperty)
	 */
	@Override
	public void startElement(TLProperty element) {
		super.startElement(element);
		int repeat = element.getRepeat();
		TLPropertyType type = element.getType();
		// list facets and repeatable refs are handled separately
		if ((repeat > 1 || repeat < 0) && !(type instanceof TLListFacet)
				&& !element.isReference()) {
			String nodeName = getPropertyElementName(element, type);
			JsonNode node = context.getNode();
			JsonNode jn = node.findValue(nodeName);
			ArrayNode arrayNode;
			if (jn instanceof ArrayNode) {
				// must be an array
				arrayNode = (ArrayNode) jn;
			} else {
				arrayNode = nodeFactory.arrayNode();
				((ObjectNode) node).set(nodeName, arrayNode);
			}

			contextStack.add(context);
			context = new ExampleContext(element);
			context.setNode(arrayNode);
		}

		contextStack.push(context);
		context = new ExampleContext(element);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#endElement(org.opentravel.schemacompiler.model.TLProperty)
	 */
	@Override
	public void endElement(TLProperty element) {
		super.endElement(element);

		// Capture ID values in the registry for use during post-processing
		if (XsdCodegenUtils.isIdType(element.getType())) {
			TLPropertyOwner owner = element.getOwner();
			NamedEntity contextFacet = getContextFacet();

			if (contextFacet != null) {
				registerIdValue(contextFacet, lastExampleValue);
			} else {
				registerIdValue(owner, lastExampleValue);
			}
		}

		// Queue up IDREF(S) attributes for assignment during post-processing
		if (XsdCodegenUtils.isIdRefType(element.getType())) {
			referenceAssignments.add(new JsonIdReferenceAssignment(null, 1, element.getName(), true));
		}
		if (XsdCodegenUtils.isIdRefsType(element.getType())) {
			referenceAssignments.add(new JsonIdReferenceAssignment(null, 3, element.getName(), true));
		}
		if (element.isReference()) {
			int referenceCount = (element.getRepeat() <= 1) ? 1 : element
					.getRepeat();

			referenceAssignments.add(new JsonIdReferenceAssignment(element
					.getType(), referenceCount, element.getName(), true));
		}
		context = contextStack.pop();
		int repeat = element.getRepeat();
		// list facets and repeatable refs are handled separately
		if ((repeat > 1 || repeat < 0)
				&& !(element.getType() instanceof TLListFacet)
				&& !element.isReference()) {
			// we must be in an array so pop again
			context = contextStack.pop();
		}
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.AbstractExampleVisitor#startIndicatorAttribute(org.opentravel.schemacompiler.model.TLIndicator)
	 */
	@Override
	public void startIndicatorAttribute(TLIndicator indicator) {
		super.startIndicatorAttribute(indicator);

		if (context.getNode() == null) {
			throw new IllegalStateException(
					"Indicator encountered without an available element context.");
		}
		String attributeName = indicator.getName();

		if (!attributeName.endsWith("Ind")) {
			attributeName += "Ind";
		}
		((ObjectNode) context.getNode()).put(attributeName, Boolean.TRUE);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.AbstractExampleVisitor#startIndicatorElement(org.opentravel.schemacompiler.model.TLIndicator)
	 */
	@Override
	public void startIndicatorElement(TLIndicator indicator) {
		super.startIndicatorElement(indicator);
		String elementName = indicator.getName();

		if (!elementName.endsWith("Ind")) {
			elementName += "Ind";
		}
		((ObjectNode) context.getNode()).put(elementName, Boolean.TRUE);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#startOpenEnumeration(org.opentravel.schemacompiler.model.TLOpenEnumeration)
	 */
	@Override
	public void startOpenEnumeration(TLOpenEnumeration openEnum) {
		super.startOpenEnumeration(openEnum);
		createSimpleElement(openEnum);
		((ObjectNode) context.getNode()).put("extension", "Other_Value");
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.AbstractExampleVisitor#startRoleEnumeration(org.opentravel.schemacompiler.model.TLRoleEnumeration)
	 */
	@Override
	public void startRoleEnumeration(TLRoleEnumeration roleEnum) {
		super.startRoleEnumeration(roleEnum);
		createSimpleElement(roleEnum);
		((ObjectNode) context.getNode()).put("extension", "Other_Value");
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#startValueWithAttributes(org.opentravel.schemacompiler.model.TLValueWithAttributes)
	 */
	@Override
	public void startValueWithAttributes(
			TLValueWithAttributes valueWithAttributes) {
		NamedEntity parentType = valueWithAttributes.getParentType();

		// Find the root parent type for the VWA
		while (parentType instanceof TLValueWithAttributes) {
			parentType = ((TLValueWithAttributes) parentType).getParentType();
		}

		// Construct the example content for the VWA
		super.startValueWithAttributes(valueWithAttributes);
		createObjectNode(valueWithAttributes);

		// Queue up IDREF(S) attributes for assignment during post-processing
		if (XsdCodegenUtils.isIdRefType(valueWithAttributes.getParentType())) {
			referenceAssignments.add(new JsonIdReferenceAssignment(null, 1, "value", false));
		}
		if (XsdCodegenUtils.isIdRefsType(valueWithAttributes.getParentType())) {
			referenceAssignments.add(new JsonIdReferenceAssignment(null, 3, "value", false));
		}
		
		if ((parentType instanceof TLOpenEnumeration)
				|| (parentType instanceof TLRoleEnumeration)) {
			((ObjectNode) context.getNode()).put("extension", "Other_Value");
		}
		if (XsdCodegenUtils.isIdRefsType(valueWithAttributes.getParentType())) {
			((ObjectNode) context.getNode()).set("value",
					generateExampleValueArrayNode(valueWithAttributes));
		} else {
			((ObjectNode) context.getNode()).set("value",
					generateExampleValueNode(valueWithAttributes));
		}
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.AbstractExampleVisitor#startExtensionPoint(org.opentravel.schemacompiler.model.TLPatchableFacet)
	 */
	@Override
    public void startExtensionPoint(TLPatchableFacet facet) {
		super.startExtensionPoint(facet);
        QName extensionElementName = getExtensionPoint( facet );
		ObjectNode owningNode = (ObjectNode) context.getNode();

		if ((extensionElementName != null) && (owningNode != null)) {
			contextStack.push(context);
			context = new ExampleContext(null);
			ObjectNode objectNode = nodeFactory.objectNode();
			context.setNode(objectNode);
			owningNode.set(extensionElementName.getLocalPart(), objectNode);
		}
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.AbstractExampleVisitor#endExtensionPoint(org.opentravel.schemacompiler.model.TLPatchableFacet)
	 */
	@Override
	public void endExtensionPoint(TLPatchableFacet facet) {
		super.endExtensionPoint(facet);
        QName extensionElementName = getExtensionPoint( facet );
		ObjectNode domElement = (ObjectNode) context.getNode();

		if (extensionElementName != null && domElement != null) {
			JsonNode parent = contextStack.peek().getNode();
			if (parent.findValue(extensionElementName.getLocalPart()) != null) {
				context = contextStack.pop();
			}
		}
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#startExtensionPointFacet(org.opentravel.schemacompiler.model.TLExtensionPointFacet)
	 */
	@Override
	public void startExtensionPointFacet(TLExtensionPointFacet facet) {
		super.startExtensionPointFacet(facet);

		contextStack.push(context);
		context = new ExampleContext(null);
		facetStack.push(facet);
		createObjectNode(facet);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.AbstractExampleVisitor#endExtensionPointFacet(org.opentravel.schemacompiler.model.TLExtensionPointFacet)
	 */
	@Override
	public void endExtensionPointFacet(TLExtensionPointFacet facet) {
		super.endExtensionPointFacet(facet);

		if (facetStack.peek() == facet) {
			facetStack.pop();
		}
		context = contextStack.pop();
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#startXsdComplexType(org.opentravel.schemacompiler.model.XSDComplexType)
	 */
	@Override
	public void startXsdComplexType(XSDComplexType xsdComplexType) {
		super.startXsdComplexType(xsdComplexType);
		createObjectNode(xsdComplexType);
	}

	/**
	 * @see org.opentravel.schemacompiler.codegen.example.ExampleVisitor#startXsdElement(org.opentravel.schemacompiler.model.XSDElement)
	 */
	@Override
	public void startXsdElement(XSDElement xsdElement) {
		super.startXsdElement(xsdElement);
		createObjectNode(xsdElement);
	}

	/**
	 * Constructs a complex XML element using the current context information.
	 * The new element is assigned as a child of the 'currentElement' for this
	 * visitor, and replaces that 'currentElement' as the DOM element that will
	 * receive attributes and child elements within the current context.
	 * 
	 * @param elementType
	 *            specifies the type of element to use when the current context
	 *            is null (i.e. when the new element will be the root of the XML
	 *            document)
	 */
	private ObjectNode createObjectNode(NamedEntity elementType) {
		ObjectNode newElement = nodeFactory.objectNode();

		// Check the context state to make sure nothing illegal is happening
		if (context.getModelAttribute() != null) {
			throw new IllegalStateException(
					"Complex elements cannot be created as DOM attribute values.");
		}
		if (context.getNode() != null) {
			throw new IllegalStateException(
					"A complex element has already been defined for the current context.");
		}
		String nodeName = getElementName(elementType);

		context.setNode(newElement);

		// Assign the new DOM element as a child of the previous context
		if (contextStack.isEmpty() || (contextStack.peek().getNode() == null)) {
			node.set(nodeName, newElement);
		} else {
			JsonNode node = contextStack.peek().getNode();
			if (node.isArray()) {
				((ArrayNode) node).add(newElement);
			} else {
				((ObjectNode) node).set(nodeName, newElement);
			}

		}
		return newElement;
	}

	/**
	 * Adds the <code>@type</code> property to the current JSON node.  If an
	 * <code>@type</code> property has already been defined, this method returns
	 * without action.
	 * 
	 * @param facet  the facet that is represented by the current context node
	 * @param alias  the alias for the given facet (may be null)
	 */
	private void addJsonFacetType(TLFacet facet, TLAlias alias) {
		ObjectNode jsonNode = (ObjectNode) context.getNode();
		
		if ((jsonNode != null) && (jsonNode.get("@type") == null)) {
			String typeName;
			
			if (alias != null) {
				typeName = JsonSchemaNamingUtils.getGlobalDefinitionName( alias );
			} else {
				typeName = JsonSchemaNamingUtils.getGlobalDefinitionName( facet );
			}
			jsonNode.put("@type", typeName);
		}
	}

	/**
	 * Constructs a complex XML element using the current context information.
	 * The new element is assigned as a child of the 'currentElement' for this
	 * visitor, and replaces that 'currentElement' as the DOM element that will
	 * receive attributes and child elements within the current context.
	 * 
	 * @param elementType
	 *            specifies the type of element to use when the current context
	 *            is null (i.e. when the new element will be the root of the XML
	 *            document)
	 */
	private String getElementName(NamedEntity elementType) {
		String nodeName = null;
		if (elementType instanceof TLFacet) {
			TLAlias alias = context.getModelAlias();
			if (alias != null) {
				elementType = alias;
			}
			nodeName = JsonSchemaNamingUtils.getGlobalPropertyName(elementType);
		} else if ((context.getModelElement() != null)
				&& ((elementType instanceof TLAttributeType) || (elementType
						.equals(context.getModelElement().getType())))) {
			nodeName = getPropertyElementName(context.getModelElement(),
					(TLPropertyType) elementType);
		} else {
			nodeName = getContextElementName(elementType, false);
		}
		return nodeName;
	}

	/**
	 * Constructs a simple XML element or an XML attribute depending upon the
	 * content of the current context information. New elements are assigned as
	 * a child of the 'currentElement' for this visitor, and then replace that
	 * 'currentElement' as the DOM element for the context.
	 * 
	 * @param elementType
	 *            specifies the type of element to use when the current context
	 *            is null (i.e. when the new element will be the root of the XML
	 *            document)
	 */
	private void createSimpleElement(NamedEntity elementType) {
		if (context.getModelAttribute() != null) {
			if (context.getNode() == null) {
				throw new IllegalStateException(
						"No element available for new attribute creation.");
			}
			JsonNode node;
			
			if (elementType instanceof TLListFacet || XsdCodegenUtils.isIdRefsType((TLPropertyType) elementType)) {
				node = generateExampleValueArrayNode(context.getModelAttribute());
			} else {
				node = generateExampleValueNode(context.getModelAttribute());
			}
			((ObjectNode) context.getNode()).set(context.getModelAttribute()
					.getName(), node);

		} else {
			String nodeName = getElementName(elementType);
			if (contextStack.isEmpty() && (context.getNode() == null)) {
				JsonNode jn = getSimpleTypeNode(elementType);
				context.setNode(node);
				node.set(nodeName, jn);
			} else {
				// If the element has not already been created, do it now
				if (context.getNode() == null) {
					JsonNode newNode;
					TLProperty prop = context.getModelElement();
					if (prop.isReference()) {
						String value = generateExampleValue(elementType);
						if (prop.getRepeat() > 1) {
							// should be an array

							newNode = getArrayNode(value);
						} else {
							newNode = getTextNode(value);
						}
					} else {
						newNode = getSimpleTypeNode(elementType);

					}
					context.setNode(newNode);

					// Assign the new DOM element as a child of the previous
					// context
					JsonNode currentNode = contextStack.peek().getNode();
					if (contextStack.isEmpty() || (currentNode == null)) {
						node.set(nodeName, newNode);
					} else {
						if (currentNode.isArray()) {
							((ArrayNode) currentNode).add(newNode);
						} else {
							((ObjectNode) currentNode).set(nodeName, newNode);
						}
					}
					// constructs a new DOM
					// element with no
					// content
				}

			}
		}
	}

    /**
     * Generates an example value node for the given model entity (if possible).
     * 
     * @param entity  the entity for which to generate an example
     * @return ValueNode
     */
    private ValueNode generateExampleValueNode(Object entity) {
    	return getExampleValueNode( entity, generateExampleValue( entity ) );
    }
    
    /**
     * Generates an example value node for the given model entity (if possible).
     * 
     * @param entity  the entity for which to generate an example
     * @return ArrayNode
     */
    private ArrayNode generateExampleValueArrayNode(Object entity) {
    	String values = generateExampleValue( entity );
    	ArrayNode arrayNode = nodeFactory.arrayNode();
    	
    	if (values != null) {
    		for (String value : values.split(" ")) {
    			arrayNode.add( getExampleValueNode( entity, value) );
    		}
    	}
    	return arrayNode;
    }
    
    /**
     * Returns an example value node for the given model entity (if possible).
     * 
     * @param entity  the entity for which to generate an example
     * @param exampleStr  string representation of the exaple value to return
     * @return ValueNode
     */
    private ValueNode getExampleValueNode(Object entity, String exampleStr) {
    	Class<?> literalType = getLiteralType( entity );
    	ValueNode exampleNode = null;
    	
    	if (literalType == null) {
    		exampleNode = nodeFactory.textNode( exampleStr ); // unknown types as text nodes
    		
    	} else if (Number.class.isAssignableFrom( literalType )) {
    		try {
    	    	if (literalType.equals( Byte.class )) {
    	    		exampleNode = nodeFactory.numberNode( Byte.parseByte( exampleStr ) );
    	    		
    	    	} else if (literalType.equals( Short.class )) {
    	    		exampleNode = nodeFactory.numberNode( Short.parseShort( exampleStr ) );
    	    		
    	    	} else if (literalType.equals( Integer.class )) {
    	    		exampleNode = nodeFactory.numberNode( Integer.parseInt( exampleStr ) );
    	    		
    	    	} else if (literalType.equals( Long.class )) {
    	    		exampleNode = nodeFactory.numberNode( Long.parseLong( exampleStr ) );
    	    		
    	    	} else if (literalType.equals( Float.class )) {
    	    		exampleNode = nodeFactory.numberNode( Float.parseFloat( exampleStr ) );
    	    		
    	    	} else if (literalType.equals( Double.class )) {
    	    		exampleNode = nodeFactory.numberNode( Double.parseDouble( exampleStr ) );
    	    	}
    		} catch (NumberFormatException e) {
    			exampleNode = nodeFactory.numberNode( 0 ); // assign zero on error
    		}
    				
    	} else if (literalType.equals( Boolean.class )) {
    		exampleNode = nodeFactory.booleanNode( Boolean.parseBoolean( exampleStr ) );
    		
    	} else { // Render strings or anything else as text nodes
    		exampleNode = nodeFactory.textNode( exampleStr );
    	}
    	return exampleNode;
    }
    
    /**
     * Returns the type of the given entity as either a <code>java.lang.String</code>,
     * <code>java.lang.Number</code> (or a Number sub-class), or <code>java.lang.Boolean</code>.
     * 
     * @param entity  the entity type to analyze
     * @return Class<?>
     */
    private Class<?> getLiteralType(Object entity) {
    	Class<?> literalType;
    	
    	if (entity instanceof TLAttribute) {
    		literalType = getLiteralType( ((TLAttribute) entity).getType() );
    		
    	} else if (entity instanceof TLProperty) {
    		literalType = getLiteralType( ((TLProperty) entity).getType() );
    		
    	} else if (entity instanceof TLIndicator) {
    		literalType = Boolean.class;
    		
    	} else if (entity instanceof TLSimple) {
    		literalType = getLiteralType( ((TLSimple) entity).getParentType() );
    		
    	} else if (entity instanceof TLSimpleFacet) {
    		literalType = getLiteralType( ((TLSimpleFacet) entity).getSimpleType() );
    		
    	} else if (entity instanceof TLAbstractEnumeration) {
    		literalType = String.class;
    		
    	} else if (entity instanceof TLRoleEnumeration) {
    		literalType = String.class;
    		
    	} else if (entity instanceof TLValueWithAttributes) {
    		literalType = getLiteralType( ((TLValueWithAttributes) entity).getParentType() );
    		
    	} else if (entity instanceof TLCoreObject) {
    		literalType = getLiteralType( ((TLCoreObject) entity).getSimpleFacet() );
    		
    	} else if (entity instanceof XSDSimpleType) {
    		XSDSimpleType xsdSimple = (XSDSimpleType) entity;
    		
    		if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals( xsdSimple.getNamespace() )) {
    			try {
        			switch (EnumXsdSimpleType.fromValue( xsdSimple.getLocalName() )) {
						case BOOLEAN:
		    				literalType = Boolean.class;
							break;
						case FLOAT:
		    				literalType = Float.class;
							break;
						case DECIMAL:
						case DOUBLE:
		    				literalType = Double.class;
							break;
						case INT:
						case INTEGER:
						case POSITIVE_INTEGER:
		    				literalType = Integer.class;
							break;
						case LONG:
		    				literalType = Long.class;
							break;
						default:
		    				literalType = String.class;
							break;
        			}
    				
    			} catch (IllegalArgumentException e) {
    				literalType = String.class;
    			}
    			
    		} else {
    			XSDSimpleType xsdBaseType = findParentType( xsdSimple );
    			
    			if (xsdBaseType == null) {
    				literalType = String.class;
    			} else {
    				literalType = getLiteralType( xsdBaseType );
    			}
    		}
    	} else {
    		literalType = null;
    	}
    	return literalType;
    }
    
    /**
     * Attempts to locate the parent (base) type of the given <code>XSDSimpleType</code>
     * entity.  If the parent type cannot be identified, this method will return null.
     * 
     * @param xsdSimple  the simple type for which to return the parent or base type
     * @return XSDSimpleType
     */
    private XSDSimpleType findParentType(XSDSimpleType xsdSimple) {
    	TopLevelSimpleType jaxbSimple = xsdSimple.getJaxbType();
    	XSDSimpleType parentType = null;
    	
    	if ((jaxbSimple != null) && (jaxbSimple.getRestriction() != null)) {
    		QName parentTypeName = jaxbSimple.getRestriction().getBase();
    		
    		if (parentTypeName != null) {
    			List<AbstractLibrary> libs = xsdSimple.getOwningModel().getLibrariesForNamespace(
    					parentTypeName.getNamespaceURI() );
    			
    			for (AbstractLibrary lib : libs) {
    				NamedEntity entity = lib.getNamedMember( parentTypeName.getLocalPart() );
    				
    				if (entity instanceof XSDSimpleType) {
    					parentType = (XSDSimpleType) entity;
    				}
    				if (entity != null) break;
    			}
    		}
    	}
    	return parentType;
    }
    
	private JsonNode getTextNode(String value) {
		return nodeFactory.textNode(value);
	}

	private ArrayNode getArrayNode(String value) {
		ArrayNode arrayNode = nodeFactory.arrayNode();
		for (String str : value.split(" ")) {
			arrayNode.add(str);
		}
		return arrayNode;
	}

	private JsonNode getSimpleTypeNode(NamedEntity elementType) {
		JsonNode node;
		
		// we are a simple type so we should be a TLPopertyType
		if (elementType instanceof TLListFacet || XsdCodegenUtils.isIdType((TLPropertyType) elementType)) {
			node = generateExampleValueNode(context.getModelElement());
		} else {
			if (XsdCodegenUtils.isIdRefsType((TLPropertyType) elementType)) {
				node = generateExampleValueArrayNode(elementType);
			} else {
				node = generateExampleValueNode(elementType);
			}
		}
		
		if (elementType instanceof TLOpenEnumeration) {
			ObjectNode objNode = createObjectNode(elementType);
			objNode.set("value", node);
			node = objNode;
			
		} else if ((elementType instanceof TLSimple && ((TLSimple) elementType)
				.isListTypeInd()) || elementType instanceof TLListFacet) {
			node = generateExampleValueArrayNode( elementType );
		}
		return node;
	}

	/**
	 * Creates a DOM element using the property information provided.
	 * 
	 * @param property
	 *            the model property for which to construct a DOM element
	 * @param propertyType
	 *            the type of the property being navigated (may be different
	 *            than the property's assigned type)
	 * @return Element
	 */
	private String getPropertyElementName(TLProperty property,
			TLPropertyType propertyType) {
		String elementName = null;

		if (!PropertyCodegenUtils.hasGlobalElement(propertyType)) {
			if (context.getModelAlias() != null) {
				elementName = JsonSchemaNamingUtils.getGlobalPropertyName(context.getModelAlias());

			} else if (propertyType instanceof TLListFacet) {
				TLListFacet listFacetType = (TLListFacet) propertyType;

				if (listFacetType.getFacetType() != TLFacetType.SIMPLE) {
					elementName = JsonSchemaNamingUtils.getGlobalPropertyName(listFacetType.getItemFacet());
				}
			}

			if (elementName == null) {
				if ((property.getName() == null)
						|| (property.getName().length() == 0)) {
					elementName = propertyType.getLocalName();
				} else {
					elementName = property.getName();
				}

				// The element may be inherited, so to obtain the proper
				// namespace we need to use
				// the
				// most recently encountered facet (i.e. the top of the facet
				// stack)

				if (facetStack.isEmpty()) {
				} else {
					if (facetStack.peek() == propertyType) {
						// If the top of the facet stack is our property type,
						// we need to go up one
						// more level
						// to find the facet that declared (or inherited) this
						// property.
						if (facetStack.size() > 1) {
						} else {
						}
					} else {
					}
				}
			}

			if (property.isReference() && !elementName.endsWith("Ref")) {
				// probably a VWA reference, so we need to make sure the "Ref"
				// suffix is appended
				elementName += "Ref";
			}

		} else if (propertyType instanceof TLAlias) {
			elementName = JsonSchemaNamingUtils.getGlobalPropertyName(propertyType);
		} else if (propertyType instanceof TLComplexTypeBase) {
			elementName = propertyType.getLocalName();
		} else {
			elementName = getContextElementName(propertyType,
					property.isReference());
		}
		return elementName;
	}

	/**
	 * Returns the element name for the current context model element.
	 * 
	 * @param elementType
	 *            specifies the type of element to use when the current context
	 *            is null or the given entity does not have a pre-defined global
	 *            element
	 * @param isReferenceProperty
	 *            indicates whether the element type is assigned by value or
	 *            reference
	 * @return String
	 */
	private String getContextElementName(NamedEntity elementType,
			boolean isReferenceProperty) {
		boolean useSubstitutableElementName = false;
		String elementName;

		// Determine whether we should be using the substitutable or
		// non-substitutable name for the
		// element
		if (!XsdCodegenUtils.isSimpleCoreObject(elementType)) {
			if (context.getModelElement() != null) {
				TLPropertyType modelPropertyType = context.getModelElement()
						.getType();

				if (modelPropertyType instanceof TLAlias) {
					modelPropertyType = (TLPropertyType) ((TLAlias) modelPropertyType)
							.getOwningEntity();
				}
				if ((modelPropertyType instanceof TLBusinessObject)
						|| (modelPropertyType instanceof TLCoreObject)) {
					useSubstitutableElementName = true;
				}
			} else { // no property - this is the root element of the document
				if (elementType instanceof TLAlias) {
					elementType = ((TLAlias) elementType).getOwningEntity();
				}
				
				if ((elementType instanceof TLBusinessObject)
						|| (elementType instanceof TLCoreObject)) {
					useSubstitutableElementName = true;
				}
			}
		}

		// Lookup the correct name for the element depending upon its specific
		// characteristics
		if (context.getModelAlias() != null) {
			if (!isReferenceProperty && useSubstitutableElementName) {
				elementName = JsonSchemaNamingUtils.getGlobalPropertyName((TLAlias) context
								.getModelAlias());

			} else {
				elementName = JsonSchemaNamingUtils.getGlobalPropertyName(
						context.getModelAlias(), isReferenceProperty);
			}
		} else {
			if (!isReferenceProperty && useSubstitutableElementName
					&& (elementType instanceof TLFacet)) {
				elementName = JsonSchemaNamingUtils.getGlobalPropertyName((TLFacet) elementType);

			} else {
				elementName = JsonSchemaNamingUtils.getGlobalPropertyName(elementType, isReferenceProperty);
			}
		}
		return (elementName != null) ? elementName : elementType.getLocalName();
	}

	/**
	 * Adds an example role value for the given core object and each of the
	 * extended objects that it inherits role attributes from.
	 * 
	 * @param coreObject
	 *            the core object for which to generate role attributes
	 */
	protected void addRoleAttributes(TLCoreObject coreObject) {
		while (coreObject != null) {
			if (coreObject.getRoleEnumeration().getRoles().size() > 0) {
				TLCoreObject extendedCore = (TLCoreObject) FacetCodegenUtils
						.getFacetOwnerExtension(coreObject);
				String attrName = (extendedCore == null) ? "role" : coreObject
						.getLocalName() + "Role";

				((ObjectNode) context.getNode()).put(attrName,
						exampleValueGenerator.getExampleRoleValue(coreObject));
			}
			coreObject = (TLCoreObject) FacetCodegenUtils
					.getFacetOwnerExtension(coreObject);
		}
	}

	/**
	 * Adds any json attributes and/or child elements that are required by the
	 * base payload type of the operation facet to the current json node.
	 *
	 * @param operationFacet
	 *            the operation facet for which to add example web service
	 *            payload content
	 */
	protected void addOperationPayloadContent(TLFacet operationFacet) {
		ObjectNode node = (ObjectNode) context.getNode();

		if ((node != null) && (wsdlBindings != null)) {
			wsdlBindings.addPayloadExampleContent(node, operationFacet);
		}

	}

	/**
	 * Handles the deferred assignment of 'IDREF' and 'IDREFS' values as a
	 * post-processing step of the example generation process.
	 */
	private class JsonIdReferenceAssignment extends IdReferenceAssignment{

		private JsonNode parentNode;

		/**
		 * Constructor used for assigning an IDREF(S) value to an XML attribute.
		 * 
		 * @param referencedEntity
		 *            the named entity that was referenced (may be null for
		 *            legacy IDREF(S) values)
		 * @param referenceCount
		 *            indicates the number of reference values that should be
		 *            applied
		 * @param attributeName
		 *            the name of the IDREF(S) attribute to which the value
		 *            should be assigned
		 */
		public JsonIdReferenceAssignment(NamedEntity referencedEntity,
				int referenceCount, String nodeName, boolean isElement) {
			super(referencedEntity, referenceCount, nodeName);
			
			if (!isElement) { // attribute or VWA value
				parentNode = context.getNode();
				
			} else { // element value
				JsonNode jn = contextStack.peek().getNode();
				
				if(jn.isArray()){
					jn = context.getNode();
				}
				parentNode = jn ;
			}
		}

		/**
		 * Assigns the IDREF value(s) to the appropriate attribute or element
		 * based on information collected in the message ID registry during
		 * document generation.
		 */
		public void assignReferenceValue() {
			String referenceValue = getIdValues();

			if (referenceValue != null) {
				JsonNode jn = parentNode.get(nodeName);
				
				if (jn != null) {
					if (jn.isArray()) {
						ArrayNode an = (ArrayNode) jn;
						an.removeAll();
						for (String str : referenceValue.split(" ")) {
							an.add(nodeFactory.textNode(str));
						}
					} else if (jn.isTextual()) {
						((ObjectNode) parentNode).set(nodeName,
								nodeFactory.textNode(referenceValue));

					}
				}
			}
		}

	}

}
