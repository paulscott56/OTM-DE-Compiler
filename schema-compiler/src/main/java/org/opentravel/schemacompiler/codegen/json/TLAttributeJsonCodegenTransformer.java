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

import org.opentravel.schemacompiler.codegen.impl.CodegenArtifacts;
import org.opentravel.schemacompiler.codegen.json.model.JsonSchema;
import org.opentravel.schemacompiler.codegen.json.model.JsonSchemaNamedReference;
import org.opentravel.schemacompiler.codegen.json.model.JsonSchemaReference;
import org.opentravel.schemacompiler.codegen.json.model.JsonType;
import org.opentravel.schemacompiler.codegen.util.PropertyCodegenUtils;
import org.opentravel.schemacompiler.ioc.SchemaDependency;
import org.opentravel.schemacompiler.model.TLAttribute;
import org.opentravel.schemacompiler.model.TLAttributeType;
import org.opentravel.schemacompiler.model.TLCoreObject;
import org.opentravel.schemacompiler.model.TLOpenEnumeration;
import org.opentravel.schemacompiler.model.TLRole;
import org.opentravel.schemacompiler.model.TLRoleEnumeration;
import org.opentravel.schemacompiler.model.TLSimpleFacet;

/**
 * Performs the translation from <code>TLAttribute</code> objects to the JSON schema elements
 * used to produce the output.
 */
public class TLAttributeJsonCodegenTransformer extends AbstractJsonSchemaTransformer<TLAttribute, CodegenArtifacts> {

	/**
	 * @see org.opentravel.schemacompiler.transform.ObjectTransformer#transform(java.lang.Object)
	 */
	@Override
	public CodegenArtifacts transform(TLAttribute source) {
        TLAttributeType attributeType = PropertyCodegenUtils.getAttributeType(source);
		JsonSchemaNamedReference attr = new JsonSchemaNamedReference();
		JsonSchemaReference attrSchemaRef = new JsonSchemaReference();
		CodegenArtifacts artifacts = new CodegenArtifacts();
		
        // If the attribute's name has not been specified, use the name of its assigned type
        if ((source.getName() == null) || (source.getName().length() == 0)) {
            attr.setName(attributeType.getLocalName());
        } else {
            attr.setName(source.getName());
        }
		attr.setSchema( attrSchemaRef );
		artifacts.addArtifact( attr );
		
        if (attributeType instanceof TLCoreObject) {
            // Special Case: For core objects, use the simple facet as the attribute type
            TLCoreObject coreObject = (TLCoreObject) attributeType;
            TLSimpleFacet coreSimple = coreObject.getSimpleFacet();
            
        	setAttributeType( attrSchemaRef, coreSimple, source );

        } else if (attributeType instanceof TLRole) {
            // Special Case: For role assignments, use the core object's simple facet as the
            // attribute type
            TLCoreObject coreObject = ((TLRole) attributeType).getRoleEnumeration().getOwningEntity();
            TLSimpleFacet coreSimple = coreObject.getSimpleFacet();
            
        	setAttributeType( attrSchemaRef, coreSimple, source );

        } else if (attributeType instanceof TLOpenEnumeration) {
    		JsonSchemaNamedReference extensionAttr = new JsonSchemaNamedReference();
    		JsonSchemaReference extAttrSchemaRef = new JsonSchemaReference();
    		
    		extensionAttr.setName( attr.getName() + "Extension" );
    		extensionAttr.setSchema( extAttrSchemaRef );
    		extAttrSchemaRef.setSchemaPath( getSchemaReferencePath(
    				SchemaDependency.getEnumExtension(), source.getOwner() ) );
            artifacts.addArtifact(extensionAttr);
        	attrSchemaRef.setSchemaPath( getSchemaReferencePath( attributeType, source.getOwner() ) + "_Base" );

        } else if (attributeType instanceof TLRoleEnumeration) {
        	attrSchemaRef.setSchemaPath( getSchemaReferencePath( attributeType, source.getOwner() ) + "_Base" );
        	
        } else { // normal case
        	setAttributeType( attrSchemaRef, attributeType, source );
        }
		return artifacts;
	}
	
	/**
	 * Assigns the attribute type of the JSON schema reference provided.
	 * 
	 * @param attrSchemaRef  the JSON schema reference to which the attribute type will be assigned
	 * @param attributeType  the attribute type to assign
	 * @param source  the source attribute from the OTM model
	 */
	private void setAttributeType(JsonSchemaReference attrSchemaRef, TLAttributeType attributeType, TLAttribute source) {
        JsonType jsonType = JsonType.valueOf( attributeType );
        
        if (jsonType != null) {
        	JsonSchema attrSchema = new JsonSchema();
        	
        	attrSchema.setType( jsonType );
    		transformDocumentation( source, attrSchema );
    		attrSchema.getExampleItems().addAll( getExampleInfo( source ) );
    		attrSchema.getEquivalentItems().addAll( getEquivalentInfo( source ) );
    		attrSchemaRef.setSchema( attrSchema );
    		
        } else {
    		transformDocumentation( source, attrSchemaRef );
    		attrSchemaRef.getSchemaPathExampleItems().addAll( getExampleInfo( source ) );
    		attrSchemaRef.getSchemaPathEquivalentItems().addAll( getEquivalentInfo( source ) );
        	attrSchemaRef.setSchemaPath( getSchemaReferencePath( attributeType, source.getOwner() ) );
        }
	}
	
}
