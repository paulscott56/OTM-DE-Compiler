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
package org.opentravel.schemacompiler.transform.jaxb16_2tl;

import org.opentravel.ns.ota2.librarymodel_v01_06.ActionResponse;
import org.opentravel.ns.ota2.librarymodel_v01_06.Documentation;
import org.opentravel.schemacompiler.model.TLActionResponse;
import org.opentravel.schemacompiler.model.TLDocumentation;
import org.opentravel.schemacompiler.transform.ObjectTransformer;
import org.opentravel.schemacompiler.transform.symbols.DefaultTransformerContext;

/**
 * Handles the transformation of objects from the <code>ActionResponse</code> type to the
 * <code>TLActionResponse</code> type.
 *
 * @author S. Livezey
 */
public class ActionResponseTransformer extends ComplexTypeTransformer<ActionResponse,TLActionResponse> {
	
	/**
	 * @see org.opentravel.schemacompiler.transform.ObjectTransformer#transform(java.lang.Object)
	 */
	@Override
	public TLActionResponse transform(ActionResponse source) {
		TLActionResponse response = new TLActionResponse();
		
		response.setStatusCodes(source.getStatusCodes());
		response.setPayloadTypeName(trimString(source.getPayloadType()));
		response.setMimeTypes(ActionTransformer.transformMimeTypes(source.getMimeTypes()));
		
        if (source.getDocumentation() != null) {
            ObjectTransformer<Documentation, TLDocumentation, DefaultTransformerContext> docTransformer = getTransformerFactory()
                    .getTransformer(Documentation.class, TLDocumentation.class);

            response.setDocumentation(docTransformer.transform(source.getDocumentation()));
        }
        
		return response;
	}
	
}
