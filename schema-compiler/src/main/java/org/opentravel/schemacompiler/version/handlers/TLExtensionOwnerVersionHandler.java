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
package org.opentravel.schemacompiler.version.handlers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.opentravel.schemacompiler.model.NamedEntity;
import org.opentravel.schemacompiler.model.TLContextualFacet;
import org.opentravel.schemacompiler.model.TLExtension;
import org.opentravel.schemacompiler.model.TLExtensionOwner;
import org.opentravel.schemacompiler.model.TLFacet;
import org.opentravel.schemacompiler.model.TLFacetType;
import org.opentravel.schemacompiler.model.TLVersionedExtensionOwner;
import org.opentravel.schemacompiler.version.VersionSchemeException;

/**
 * Abstract base class for all handlers of entities that implement the <code>TLVersionedExtensionOwner</code>
 * interface.
 * 
 * @author S. Livezey
 */
public abstract class TLExtensionOwnerVersionHandler<V extends TLVersionedExtensionOwner> extends VersionHandler<V> {

	/**
	 * @see org.opentravel.schemacompiler.version.handlers.VersionHandler#getExtendedEntity(org.opentravel.schemacompiler.version.Versioned)
	 */
	@Override
	protected NamedEntity getExtendedEntity(V entity) {
		NamedEntity extendedEntity = null;
		
		if (entity != null) {
	        TLExtension extension = ((TLExtensionOwner) entity).getExtension();
	        extendedEntity = (extension == null) ? null : extension.getExtendsEntity();
		}
		return extendedEntity;
	}

	/**
	 * @see org.opentravel.schemacompiler.version.handlers.VersionHandler#setExtension(org.opentravel.schemacompiler.version.Versioned, org.opentravel.schemacompiler.version.Versioned)
	 */
	@Override
	public void setExtension(V laterVersion, V earlierVersion) {
		if (laterVersion != null) {
			TLExtension extension = laterVersion.getExtension();
			
	        if (extension == null) {
	            extension = new TLExtension();
	            laterVersion.setExtension(extension);
	        }
	        extension.setExtendsEntity( earlierVersion );
		}
	}

	/**
	 * @see org.opentravel.schemacompiler.version.handlers.VersionHandler#getBaseExtension(org.opentravel.schemacompiler.version.Versioned)
	 */
	@Override
	protected NamedEntity getBaseExtension(V entity) throws VersionSchemeException {
		NamedEntity baseExtension = null;
		
		if (entity != null) {
	    	List<V> priorMinorVersions = getAllVersionExtensions( entity );
	    	V earliestMinorVersion = priorMinorVersions.isEmpty() ?
	    			null : priorMinorVersions.get(priorMinorVersions.size() - 1);
	    	
	    	if (earliestMinorVersion != null) {
				TLExtension extension = (earliestMinorVersion == null) ? null : earliestMinorVersion.getExtension();
	    		
		    	if (extension != null) {
		    		baseExtension = getExtendedEntity( earliestMinorVersion );
		    	}
		    	
	    	} else if (entity.getExtension() != null) {
				baseExtension = entity.getExtension().getExtendsEntity();
	    	}
		}
		return baseExtension;
	}
	
	/**
	 * Assigns the minor version's base extension to the major version.  If the minor
	 * version does not have a base extension, the major version's extension will be
	 * set to null.
	 * 
	 * @param majorVersion  the major version whose extension will be assigned
	 * @param minorVersion  the minor version from which to derive the base extension
     * @throws VersionSchemeException  thrown if the entity's version scheme is not recognized
	 */
	protected void assignBaseExtension(V majorVersion, V minorVersion) throws VersionSchemeException {
    	NamedEntity baseExtension = getBaseExtension( minorVersion );
    	
        majorVersion = getCloner( minorVersion ).clone( minorVersion );
        
    	if (baseExtension != null) {
    		TLExtension extension = new TLExtension();
    		
    		extension.setExtendsEntity( baseExtension );
    		majorVersion.setExtension( extension );
    		
    	} else {
    		majorVersion.setExtension( null );
    	}
	}
	
	/**
	 * Recursively performs a rollup of all nested contextual facets that have not already been
	 * processed.
	 * 
	 * @param sourceOwningFacet  the source contextual facet whose children are to be rolled-up
	 * @param targetOwningFacet  the target contextual facet that will receive the rolled-up children
	 * @param mergeUtils  the version handler merge utilities instance
	 * @param sourceFacets  the identity facet map for all source facets
	 * @param targetFacets  the identity facet map for all target facets
	 * @param visitedSourceFacets  the set of source owning facets that have already been visited (in case of circular references)
	 */
	protected void rollupNestedLocalContextualFacets(TLContextualFacet sourceOwningFacet, TLContextualFacet targetOwningFacet,
			VersionHandlerMergeUtils mergeUtils, Map<String, TLFacet> sourceFacets, Map<String, TLFacet> targetFacets,
			Set<TLContextualFacet> visitedSourceFacets) {
		if (!visitedSourceFacets.contains( sourceOwningFacet )) {
			visitedSourceFacets.add( sourceOwningFacet );
			
	        for (TLContextualFacet sourceFacet : sourceOwningFacet.getChildFacets()) {
	            TLContextualFacet targetFacet = targetOwningFacet.getChildFacet( sourceFacet.getName() );
	            
	            if (sourceFacet.isLocalFacet()) {
	                if (targetFacet == null) {
	                    targetFacet = new TLContextualFacet();
	                    targetFacet.setName( sourceFacet.getName() );
	                    targetFacet.setFacetType( TLFacetType.CUSTOM );
	                    targetFacet.setOwningLibrary( targetOwningFacet.getOwningLibrary() );
	                    targetOwningFacet.addChildFacet( targetFacet );
	                    rollupNestedLocalContextualFacets( sourceFacet, targetFacet, mergeUtils,
	                    		sourceFacets, targetFacets, visitedSourceFacets );
	                }
	                mergeUtils.addToIdentityFacetMap( targetFacet, targetFacets );
	                mergeUtils.addToIdentityFacetMap( sourceFacet, sourceFacets );
	            }
	        }
		}
	}
	
}
