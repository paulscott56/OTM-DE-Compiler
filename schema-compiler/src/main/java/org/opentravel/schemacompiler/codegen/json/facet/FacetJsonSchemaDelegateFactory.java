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

import org.opentravel.schemacompiler.codegen.impl.CodeGenerationTransformerContext;
import org.opentravel.schemacompiler.model.TLAbstractFacet;
import org.opentravel.schemacompiler.model.TLActionFacet;
import org.opentravel.schemacompiler.model.TLBusinessObject;
import org.opentravel.schemacompiler.model.TLChoiceObject;
import org.opentravel.schemacompiler.model.TLCoreObject;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetOwner;
import org.opentravel.schemacompiler.model.TLListFacet;
import org.opentravel.schemacompiler.model.TLOperation;
import org.opentravel.schemacompiler.model.TLResource;
import org.opentravel.schemacompiler.model.TLSimpleFacet;

/**
 * Factory used to determine which <code>FacetJsonSchemaDelegate</code> should be used to generate
 * artifacts for a particular facet instance.
 */
public class FacetJsonSchemaDelegateFactory {
	
    protected CodeGenerationTransformerContext transformerContext;

    /**
     * Constructor that supplies the transformer factory and symbol resolve to be used by the code
     * generation delegates produced by this factory instance.
     * 
     * @param transformerFactory
     *            the transformer factory used to generate code artifacts for facet sub-elements
     */
    public FacetJsonSchemaDelegateFactory(CodeGenerationTransformerContext transformerContext) {
        this.transformerContext = transformerContext;
    }

    /**
     * Returns a <code>FacetJsonSchemaDelegate</code> to use for the generation of code artifacts from
     * the given facet instance.
     * 
     * @param <F>  the type of facet for which the delegate will generate artifacts
     * @param facetInstance  the facet instance for which to generate code artifacts
     * @return FacetJsonSchemaDelegate<F>
     */
    @SuppressWarnings("unchecked")
    public <F extends TLAbstractFacet> FacetJsonSchemaDelegate<F> getDelegate(F facetInstance) {
        TLFacetOwner facetOwner = facetInstance.getOwningEntity();
        FacetJsonSchemaDelegate<F> delegate = null;

        if (facetOwner instanceof TLBusinessObject) {
            if (facetInstance instanceof TLFacet) {
                TLFacet facet = (TLFacet) facetInstance;

                switch (facetInstance.getFacetType()) {
                    case ID:
                        delegate = (FacetJsonSchemaDelegate<F>) new BusinessObjectIDFacetJsonSchemaDelegate(facet);
                        break;
                    case SUMMARY:
                        delegate = (FacetJsonSchemaDelegate<F>) new BusinessObjectSummaryFacetJsonSchemaDelegate(facet);
                        break;
                    case DETAIL:
                        delegate = (FacetJsonSchemaDelegate<F>) new BusinessObjectDetailFacetJsonSchemaDelegate(facet);
                        break;
                    case CUSTOM:
                        delegate = (FacetJsonSchemaDelegate<F>) new BusinessObjectCustomFacetJsonSchemaDelegate(facet);
                        break;
                    case QUERY:
                        delegate = (FacetJsonSchemaDelegate<F>) new BusinessObjectQueryFacetJsonSchemaDelegate(facet);
                        break;
					default:
						break;
                }
            }
        } else if (facetOwner instanceof TLCoreObject) {
            if (facetInstance instanceof TLFacet) {
                TLFacet facet = (TLFacet) facetInstance;

                switch (facetInstance.getFacetType()) {
                    case SUMMARY:
                        delegate = (FacetJsonSchemaDelegate<F>) new CoreObjectSummaryFacetJsonSchemaDelegate(facet);
                        break;
                    case DETAIL:
                        delegate = (FacetJsonSchemaDelegate<F>) new CoreObjectDetailFacetJsonSchemaDelegate(facet);
                        break;
					default:
						break;
                }
            } else if (facetInstance instanceof TLListFacet) {
                TLListFacet facet = (TLListFacet) facetInstance;

                switch (facetInstance.getFacetType()) {
                    case SIMPLE:
                        delegate = (FacetJsonSchemaDelegate<F>) new CoreObjectListSimpleFacetJsonSchemaDelegate(facet);
                        break;
                    case SUMMARY:
                    case DETAIL:
                        delegate = (FacetJsonSchemaDelegate<F>) new CoreObjectListFacetJsonSchemaDelegate(facet);
                        break;
					default:
						break;
                }
            } else if (facetInstance instanceof TLSimpleFacet) {
                TLSimpleFacet facet = (TLSimpleFacet) facetInstance;

                switch (facetInstance.getFacetType()) {
                    case SIMPLE:
                        delegate = (FacetJsonSchemaDelegate<F>) new TLSimpleFacetJsonSchemaDelegate(facet);
                        break;
					default:
						break;
                }
            }
        } else if (facetOwner instanceof TLChoiceObject) {
            if (facetInstance instanceof TLFacet) {
                TLFacet facet = (TLFacet) facetInstance;

                switch (facetInstance.getFacetType()) {
                    case SHARED:
                        delegate = (FacetJsonSchemaDelegate<F>) new ChoiceObjectSharedFacetJsonSchemaDelegate(facet);
                        break;
                    case CHOICE:
                        delegate = (FacetJsonSchemaDelegate<F>) new ChoiceObjectChoiceFacetJsonSchemaDelegate(facet);
                        break;
					default:
						break;
                }
            }
        } else if (facetOwner instanceof TLResource) {
            if (facetInstance instanceof TLActionFacet) {
            	delegate = (FacetJsonSchemaDelegate<F>) new ResourceActionFacetJsonSchemaDelegate((TLActionFacet) facetInstance);
            }
        } else if (facetOwner instanceof TLOperation) {
            if (facetInstance instanceof TLFacet) {
                TLFacet facet = (TLFacet) facetInstance;

                delegate = (FacetJsonSchemaDelegate<F>) new OperationFacetJsonSchemaDelegate(facet);
            }
        }

        if (delegate != null) {
            delegate.setTransformerContext(transformerContext);
        }
        return delegate;
    }

}
