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
package org.opentravel.schemacompiler.codegen.html.builders;

import org.opentravel.schemacompiler.codegen.util.FacetCodegenUtils;
import org.opentravel.schemacompiler.model.TLBusinessObject;
import org.opentravel.schemacompiler.model.TLExtension;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetType;

import org.opentravel.schemacompiler.codegen.html.Content;
import org.opentravel.schemacompiler.codegen.html.writers.BusinessObjectWriter;

/**
 * @author Eric.Bronson
 *
 */
public class BusinessObjectDocumentationBuilder extends
		ComplexTypeDocumentationBuilder<TLBusinessObject> {

	public BusinessObjectDocumentationBuilder(TLBusinessObject t) {
		super(t);
		TLExtension tle = t.getExtension();
		if (tle != null) {
			superType = new BusinessObjectDocumentationBuilder(
					(TLBusinessObject) tle.getExtendsEntity());
		}
	}

	@Override
	protected void initializeFacets(TLBusinessObject t) {
		for (TLFacet facet : FacetCodegenUtils.getAllFacetsOfType(element,
				TLFacetType.ID)) {
			addFacet(facet);
		}

		for (TLFacet facet : FacetCodegenUtils.getAllFacetsOfType(element,
				TLFacetType.SUMMARY)) {
			if (shouldAddFacet(facet)) {
				addFacet(facet);
			}
		}

		for (TLFacet facet : FacetCodegenUtils.getAllFacetsOfType(element,
				TLFacetType.DETAIL)) {
			if (shouldAddFacet(facet)) {
				addFacet(facet);
			}
		}

		for (TLFacet facet : FacetCodegenUtils.getAllFacetsOfType(element,
				TLFacetType.CUSTOM)) {
			addFacet(facet);
		}

		for (TLFacet facet : FacetCodegenUtils.getAllFacetsOfType(element,
				TLFacetType.QUERY)) {
			addFacet(facet);
		}
	}

	@Override
	public DocumentationBuilderType getDocType() {
		return DocumentationBuilderType.BUSINESS_OBJECT;
	}
	
	@Override
	public void build() throws Exception {
		BusinessObjectWriter writer = new BusinessObjectWriter(this, prev, next);
		Content contentTree = writer.getHeader();
		writer.addMemberInheritanceTree(contentTree);
		Content classContentTree = writer.getContentHeader();
		Content tree = writer.getMemberTree(classContentTree);

		Content classInfoTree = writer.getMemberInfoItemTree();
		writer.addDocumentationInfo(classInfoTree);
		tree.addContent(classInfoTree);

		classInfoTree = writer.getMemberInfoItemTree();
		writer.addFacetInfo(classInfoTree);
		tree.addContent(classInfoTree);

		classInfoTree = writer.getMemberInfoItemTree();
		writer.addAliasInfo(classInfoTree);
		tree.addContent(classInfoTree);

		Content desc = writer.getMemberInfoTree(tree);
		classContentTree.addContent(desc);
		contentTree.addContent(classContentTree);
		writer.addFooter(contentTree);
		writer.printDocument(contentTree);
		writer.close();
		super.build();
	}

}