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
package org.opentravel.schemacompiler.codegen.json;

import java.util.List;

import org.opentravel.schemacompiler.codegen.impl.CodeGenerationTransformerContext;
import org.opentravel.schemacompiler.codegen.impl.CodegenArtifacts;
import org.opentravel.schemacompiler.codegen.json.facet.FacetJsonSchemaDelegate;
import org.opentravel.schemacompiler.codegen.json.facet.TLFacetJsonSchemaDelegate;
import org.opentravel.schemacompiler.codegen.json.model.JsonSchema;
import org.opentravel.schemacompiler.codegen.json.model.JsonSchemaNamedReference;
import org.opentravel.schemacompiler.codegen.json.model.JsonSchemaReference;
import org.opentravel.schemacompiler.codegen.util.FacetCodegenUtils;
import org.opentravel.schemacompiler.codegen.util.JsonSchemaNamingUtils;
import org.opentravel.schemacompiler.codegen.util.ResourceCodegenUtils;
import org.opentravel.schemacompiler.codegen.util.XsdCodegenUtils;
import org.opentravel.schemacompiler.model.NamedEntity;
import org.opentravel.schemacompiler.model.TLActionFacet;
import org.opentravel.schemacompiler.model.TLAlias;
import org.opentravel.schemacompiler.model.TLChoiceObject;
import org.opentravel.schemacompiler.model.TLCoreObject;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetType;
import org.opentravel.schemacompiler.model.TLMemberField;
import org.opentravel.schemacompiler.model.TLProperty;
import org.opentravel.schemacompiler.transform.ObjectTransformer;

/**
 * Performs the translation from <code>TLActionFacet</code> objects to the JSON schema elements
 * used to produce the output.
 */
public class TLActionFacetJsonCodegenTransformer extends AbstractJsonSchemaTransformer<TLActionFacet, CodegenArtifacts> {
	
	/**
	 * @see org.opentravel.schemacompiler.transform.ObjectTransformer#transform(java.lang.Object)
	 */
	@Override
	public CodegenArtifacts transform(TLActionFacet source) {
		CodegenArtifacts artifacts = new CodegenArtifacts();
		NamedEntity payloadType = ResourceCodegenUtils.getPayloadType( source );
		
		if (payloadType instanceof TLActionFacet) {
			NamedEntity basePayload = source.getBasePayload();
			
			if (basePayload instanceof TLCoreObject) {
				artifacts.addAllArtifacts( generateCoreObjectWrapper(
						source, (TLCoreObject) basePayload ) );
				
			} else if (basePayload instanceof TLChoiceObject) {
				artifacts.addAllArtifacts( generateChoiceObjectWrapper(
						source, (TLChoiceObject) basePayload ) );
				
			} else { // must be a wrapper for repeating business objects
				artifacts.addArtifact( generateEmptyWrapper( source ) );
			}
		}
		return artifacts;
	}
	
	/**
	 * Generates artifacts for an action facet wrapper class that is based on the
	 * specified core object.
	 * 
	 * @param source  the source action facet for which artifacts are being generated
	 * @param wrapper  the core object that will provide the basis for the wrapper artifacts
	 * @return CodegenArtifacts
	 */
	private CodegenArtifacts generateCoreObjectWrapper(TLActionFacet source, TLCoreObject wrapper) {
		CodegenArtifacts artifacts = new CodegenArtifacts();
		
        artifacts.addAllArtifacts( getDelegate(source, wrapper.getSummaryFacet())
        		.generateArtifacts().getConsolidatedArtifacts() );
        artifacts.addAllArtifacts( getDelegate(source, wrapper.getDetailFacet())
        		.generateArtifacts().getConsolidatedArtifacts() );
        return artifacts;
	}
	
	/**
	 * Generates artifacts for an action facet wrapper class that is based on the
	 * specified core object.
	 * 
	 * @param source  the source action facet for which artifacts are being generated
	 * @param wrapper  the core object that will provide the basis for the wrapper artifacts
	 * @return CodegenArtifacts
	 */
	private CodegenArtifacts generateChoiceObjectWrapper(TLActionFacet source, TLChoiceObject wrapper) {
        CodegenArtifacts artifacts = new CodegenArtifacts();
		
        artifacts.addAllArtifacts( getDelegate(source, wrapper.getSharedFacet())
        		.generateArtifacts().getConsolidatedArtifacts() );
        
        for (TLFacet choiceFacet : wrapper.getChoiceFacets()) {
        	artifacts.addAllArtifacts( getDelegate(source, choiceFacet)
        			.generateArtifacts().getConsolidatedArtifacts() );
        }
        for (TLFacet ghostFacet : FacetCodegenUtils.findGhostFacets(wrapper, TLFacetType.CHOICE)) {
        	artifacts.addAllArtifacts( getDelegate(source, ghostFacet)
        			.generateArtifacts().getConsolidatedArtifacts() );
        }
        return artifacts;
	}
	
	/**
	 * Generates artifacts for an action facet wrapper class that that only includes a
	 * repeating business object element.
	 * 
	 * @param source  the source action facet for which artifacts are being generated
	 * @return JsonSchemaNamedReference
	 */
	private JsonSchemaNamedReference generateEmptyWrapper(TLActionFacet source) {
        ObjectTransformer<TLProperty, JsonSchemaNamedReference, CodeGenerationTransformerContext> elementTransformer =
        		getTransformerFactory().getTransformer(TLProperty.class, JsonSchemaNamedReference.class);
        TLProperty boElement = ResourceCodegenUtils.createBusinessObjectElement( source, null );
        JsonSchemaNamedReference definition = new JsonSchemaNamedReference();
        JsonSchema schema = new JsonSchema();
        
        definition.setName( XsdCodegenUtils.getGlobalElementName( source ).getLocalPart() );
		transformDocumentation( source, schema );
        schema.setEntityInfo( jsonUtils.getEntityInfo( source ) );
        definition.setSchema( new JsonSchemaReference( schema ) );
        schema.getProperties().add( elementTransformer.transform( boElement ) );
        
        return definition;
	}
	
	/**
	 * Returns a <code>FacetJsonSchemaDelegate</code> to generate schema artifacts for
	 * the given facet.
	 * 
	 * @param source  the source action facet for which artifacts are being generated
	 * @param facet  the facet for which schema artifacts are to be generated
	 * @return FacetJsonSchemaDelegate<TLFacet>
	 */
	private FacetJsonSchemaDelegate<TLFacet> getDelegate(TLActionFacet source, TLFacet facet) {
		FacetJsonSchemaDelegate<TLFacet> delegate = new WrapperFacetJsonDelegate( source,  facet );
		
		delegate.setTransformerContext( context );
		return delegate;
	}
	
	/**
	 * Delegate used to generate wrapper class elements for core object summary facets.
	 */
	/**
	 *
	 */
	/**
	 *
	 */
	private class WrapperFacetJsonDelegate extends TLFacetJsonSchemaDelegate {
		
		private TLActionFacet actionFacet;
		
		/**
		 * Constructor that specifies the source facet for the delegate.
		 * 
		 * @param source  the source action facet for which artifacts are being generated
		 * @param sourceFacet  the source facet for which schema artifacts will be generated
		 */
		public WrapperFacetJsonDelegate(TLActionFacet actionFacet, TLFacet sourceFacet) {
			super(sourceFacet);
			this.actionFacet = actionFacet;
		}
		
		/**
		 * @see org.opentravel.schemacompiler.codegen.json.facet.TLFacetJsonSchemaDelegate#getMemberFields()
		 */
		@Override
		protected List<TLMemberField<?>> getMemberFields() {
			TLFacetType facetType = getSourceFacet().getFacetType();
			List<TLMemberField<?>> fieldList = super.getMemberFields();
			
			if ((facetType == TLFacetType.SUMMARY) || (facetType == TLFacetType.SHARED)) {
				fieldList.add( 0, ResourceCodegenUtils.createBusinessObjectElement( actionFacet, getSourceFacet() ) );
			}
			return fieldList;
		}

		/**
		 * @see org.opentravel.schemacompiler.codegen.json.facet.TLFacetJsonSchemaDelegate#createDefinition()
		 */
		@Override
		protected JsonSchemaNamedReference createDefinition() {
			JsonSchemaNamedReference definition = super.createDefinition();
			
			switch (getSourceFacet().getFacetType()) {
				case SUMMARY:
				case SHARED:
					definition.setName(
							JsonSchemaNamingUtils.getGlobalDefinitionName( actionFacet ) );
					break;
				case DETAIL:
					definition.setName(
							JsonSchemaNamingUtils.getGlobalDefinitionName( actionFacet ) + "_Detail" );
					break;
				case CHOICE:
					definition.setName(
							JsonSchemaNamingUtils.getGlobalDefinitionName( actionFacet ) + "_" +
									getSourceFacet().getLabel() );
					break;
				default:
					break;
			}
			return definition;
		}

		/**
		 * @see org.opentravel.schemacompiler.codegen.json.facet.TLFacetJsonSchemaDelegate#createDefinition(org.opentravel.schemacompiler.model.TLAlias)
		 */
		@Override
		protected JsonSchemaNamedReference createDefinition(TLAlias alias) {
			return null; // Suppress generation of alias definitions
		}
		
	}
	
}