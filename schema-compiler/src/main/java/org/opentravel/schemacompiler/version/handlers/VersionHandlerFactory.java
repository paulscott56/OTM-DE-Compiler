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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.opentravel.schemacompiler.model.TLBusinessObject;
import org.opentravel.schemacompiler.model.TLChoiceObject;
import org.opentravel.schemacompiler.model.TLClosedEnumeration;
import org.opentravel.schemacompiler.model.TLCoreObject;
import org.opentravel.schemacompiler.model.TLModel;
import org.opentravel.schemacompiler.model.TLOpenEnumeration;
import org.opentravel.schemacompiler.model.TLOperation;
import org.opentravel.schemacompiler.model.TLResource;
import org.opentravel.schemacompiler.model.TLSimple;
import org.opentravel.schemacompiler.model.TLValueWithAttributes;
import org.opentravel.schemacompiler.util.ModelElementCloner;
import org.opentravel.schemacompiler.version.Versioned;

/**
 * Factory used to access type-specific <code>VersionHandler</code> implementations.
 * 
 * @author S. Livezey
 */
public class VersionHandlerFactory {
	
	private static Map<Class<?>,Class<?>> handlerMappings;
	
	private Map<TLModel,ModelElementCloner> clonerRegistry = new HashMap<>();
	
	/**
	 * Returns a <code>VersionHandler</code> for the given entity.
	 * 
	 * @param <V>  the type of the versioned entity
	 * @param versionedEntity  the entity for which to return a handler instance
	 * @return VersionHandler<V>
	 */
	@SuppressWarnings("unchecked")
	public <V extends Versioned> VersionHandler<V> getHandler(V versionedEntity) {
		return (VersionHandler<V>) getHandler( (versionedEntity == null) ? null : versionedEntity.getClass() );
	}
	
	/**
	 * Returns a <code>VersionHandler</code> for the given entity type.
	 * 
	 * @param <V>  the type of the versioned entity
	 * @param versionedType  the entity class for which to return a handler instance
	 * @return VersionHandler<V>
	 */
	@SuppressWarnings("unchecked")
	public <V extends Versioned> VersionHandler<V> getHandler(Class<V> versionedType) {
		Class<VersionHandler<?>> handlerClass = (Class<VersionHandler<?>>) handlerMappings.get( versionedType );
		
		if (versionedType == null) {
			throw new NullPointerException("The versioned entity type cannot be null.");
		}
		if (handlerClass == null) {
			throw new IllegalArgumentException(
					"No version handler registered for entity type: " + versionedType.getName());
		}
		try {
			VersionHandler<V> handler = (VersionHandler<V>) handlerClass.newInstance();
			
			handler.setFactory( this );
			return handler;
			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Error creating version handler instance.", e);
		}
	}
	
    /**
     * Returns a <code>ModelElementCloner</code> that can be used to create a deep clone
     * of a model entity.
     * 
     * @param model  the model instance for which to return a cloner 
     * @return ModelElementCloner
     */
    public ModelElementCloner getCloner(TLModel model) {
    	ModelElementCloner cloner = null;
    	
    	if (model != null) {
        	cloner = clonerRegistry.get( model );
    		
        	if (cloner == null) {
        		cloner = new ModelElementCloner( model );
        		clonerRegistry.put( model, cloner );
        	}
    	}
    	return cloner;
    }
    
	/**
	 * Initializes the mappings of <code>Versioned</code> objects to
	 * <code>VersionHandler</code> implementation classes.
	 */
	static {
		Map<Class<?>,Class<?>> _handlerMappings = new HashMap<>();
		
		_handlerMappings.put( TLBusinessObject.class, TLBusinessObjectVersionHandler.class );
		_handlerMappings.put( TLCoreObject.class, TLCoreObjectVersionHandler.class );
		_handlerMappings.put( TLChoiceObject.class, TLChoiceObjectVersionHandler.class );
		_handlerMappings.put( TLOperation.class, TLOperationVersionHandler.class );
		_handlerMappings.put( TLResource.class, TLResourceVersionHandler.class );
		_handlerMappings.put( TLValueWithAttributes.class, TLValueWithAttributesVersionHandler.class );
		_handlerMappings.put( TLOpenEnumeration.class, TLOpenEnumerationVersionHandler.class );
		_handlerMappings.put( TLClosedEnumeration.class, TLClosedEnumerationVersionHandler.class );
		_handlerMappings.put( TLSimple.class, TLSimpleVersionHandler.class );
		handlerMappings = Collections.unmodifiableMap( _handlerMappings );
	}
	
}
