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
package org.opentravel.schemacompiler.codegen.json.facet;

import org.opentravel.schemacompiler.ioc.SchemaDependency;
import org.opentravel.schemacompiler.model.TLFacet;

/**
 * Code generation delegate for <code>TLFacet</code> instances with a facet type of
 * <code>REQUEST</code>, <code>RESPONSE</code>, or <code>NOTIFICATION</code> and a facet owner of
 * type <code>TLOperation</code>.
 */
public class OperationFacetJsonSchemaDelegate extends TLFacetJsonSchemaDelegate {
	
    /**
     * Constructor that specifies the source facet for which code artifacts are being generated.
     * 
     * @param sourceFacet  the source facet
     */
    public OperationFacetJsonSchemaDelegate(TLFacet sourceFacet) {
        super(sourceFacet);
    }

	/**
	 * @see org.opentravel.schemacompiler.codegen.json.facet.FacetJsonSchemaDelegate#getLocalBaseFacetDependency()
	 */
	@Override
	protected SchemaDependency getLocalBaseFacetDependency() {
        SchemaDependency messagePayload = null;

        switch (getSourceFacet().getFacetType()) {
            case REQUEST:
                messagePayload = SchemaDependency.getRequestPayload();
                break;
            case RESPONSE:
                messagePayload = SchemaDependency.getResponsePayload();
                break;
            case NOTIFICATION:
                messagePayload = SchemaDependency.getNotifPayload();
                break;
			default:
				break;
        }
        return messagePayload;
	}
    
}
