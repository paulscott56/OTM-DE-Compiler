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
package org.opentravel.schemacompiler.codegen.json.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Provides documentation properties for a JSON schema.
 */
public class JsonDocumentation {
	
	private String[] descriptions = new String[0];
	private List<String> deprecations = new ArrayList<>();
	private List<String> references = new ArrayList<>();
	private List<String> implementers = new ArrayList<>();
	private List<String> moreInfos = new ArrayList<>();
	private List<String> otherDocContexts = new ArrayList<>();
	private Map<String,String> otherDocTexts = new HashMap<>();
	
	/**
	 * Default constructor.
	 */
	public JsonDocumentation() {}
	
	/**
	 * Constructor that assigns the description for this documentation item.
	 * 
	 * @param descriptions  the list of descriptions for this documentation item
	 */
	public JsonDocumentation(String... descriptions) {
		this.descriptions = descriptions;
	}

	/**
	 * Returns the descriptions list for this documentation.
	 *
	 * @return String
	 */
	public String[] getDescriptions() {
		return descriptions;
	}
	
	/**
	 * Assigns the descriptions list for this documentation.
	 *
	 * @param descriptions  the list of descriptions to assign
	 */
	public void setDescriptions(String... descriptions) {
		this.descriptions = descriptions;
	}
	
	/**
	 * Returns true if this documentation element has one or more non-blank/null
	 * descriptions assigned.
	 * 
	 * @return boolean
	 */
	public boolean hasDescription() {
		boolean hasDesc = false;
		
		if ((descriptions != null) && (descriptions.length > 0)) {
			for (String description : descriptions) {
				String trimmedDesc = (description == null) ? null : description.trim();
				
				if ((trimmedDesc != null) && (trimmedDesc.length() > 0)) {
					hasDesc = true;
					break;
				}
			}
		}
		return hasDesc;
	}

	/**
	 * Returns the list of deprecation notices for this documentation item.
	 *
	 * @return List<String>
	 */
	public List<String> getDeprecations() {
		return Collections.unmodifiableList( deprecations );
	}
	
	/**
	 * Adds the given deprecation notice to this documentation item.
	 * 
	 * @param deprecation  the deprecation notice to add
	 */
	public void addDeprecation(String deprecation) {
		deprecations.add( deprecation );
	}
	
	/**
	 * Returns the list of references for this documentation item.
	 *
	 * @return List<String>
	 */
	public List<String> getReferences() {
		return Collections.unmodifiableList( references );
	}
	
	/**
	 * Adds the given reference to this documentation item.
	 * 
	 * @param reference  the reference to add
	 */
	public void addReference(String reference) {
		references.add( reference );
	}
	
	/**
	 * Returns the list of implementers for this documentation item.
	 *
	 * @return List<String>
	 */
	public List<String> getImplementers() {
		return Collections.unmodifiableList( implementers );
	}
	
	/**
	 * Adds the given implementers to this documentation item.
	 * 
	 * @param deprecation  the implementers to add
	 */
	public void addImplementer(String implementer) {
		implementers.add( implementer );
	}
	
	/**
	 * Returns the list of more-info notices for this documentation item.
	 *
	 * @return List<String>
	 */
	public List<String> getMoreInfos() {
		return Collections.unmodifiableList( moreInfos );
	}
	
	/**
	 * Adds the given more-info notice to this documentation item.
	 * 
	 * @param moreInfo  the more-info notice to add
	 */
	public void addMoreInfo(String moreInfo) {
		moreInfos.add( moreInfo );
	}
	
	/**
	 * Returns the list of other documentation context values that have been
	 * added to this documentation item.
	 * 
	 * @return List<String>
	 */
	public List<String> getOtherDocumentationContexts() {
		return Collections.unmodifiableList( otherDocContexts );
	}
	
	/**
	 * Returns the other documentation text for the specified context value.
	 * 
	 * @param context  the context for which to return the other documentation text
	 * @return String
	 */
	public String getOtherDocumentation(String context) {
		return otherDocTexts.get( context );
	}
	
	public void addOtherDocumentation(String context, String text) {
		if ((context != null) && (text != null)) {
			otherDocContexts.add( context );
			otherDocTexts.put( context, text );
		}
	}
	
	/**
	 * Returns a <code>JsonObject</code> representation of this documentation item.  If all
	 * documentation entries are empty, this method will return null.
	 * 
	 * @return JsonObject
	 */
	public JsonObject toJson() {
		JsonObject docItem = new JsonObject();
		boolean hasDocumentation = false;
		
		if ((descriptions != null) && (descriptions.length > 0)) {
			List<String> nonEmptyDescriptions = new ArrayList<>();
			
			for (String description : descriptions) {
				String trimmedDesc = (description == null) ? null : description.trim();
				
				if ((trimmedDesc != null) && (trimmedDesc.length() > 0)) {
					nonEmptyDescriptions.add( StringEscapeUtils.escapeJson( trimmedDesc ) );
				}
			}
			
			if (!nonEmptyDescriptions.isEmpty()) {
				if (nonEmptyDescriptions.size() == 1) {
					docItem.addProperty( "description", nonEmptyDescriptions.get( 0 ) );
					
				} else {
					JsonArray jsonDescriptions = new JsonArray();
					
					for (String desc : nonEmptyDescriptions) {
						jsonDescriptions.add( desc );
					}
					docItem.add( "description", jsonDescriptions );
				}
				hasDocumentation = true;
			}
		}
		hasDocumentation |= addOtmDocumentationItems( docItem, deprecations, "deprecations" );
		hasDocumentation |= addOtmDocumentationItems( docItem, references, "references" );
		hasDocumentation |= addOtmDocumentationItems( docItem, implementers, "implementers" );
		hasDocumentation |= addOtmDocumentationItems( docItem, moreInfos, "more-info" );
		
		if (!otherDocContexts.isEmpty()) {
			JsonArray jsonOtherDocs = new JsonArray();
			
			for (String context : otherDocContexts) {
				JsonObject jsonOtherDoc = new JsonObject();
				
				jsonOtherDoc.addProperty( "context", context );
				jsonOtherDoc.addProperty( "text", otherDocTexts.get( context ) );
				jsonOtherDocs.add( jsonOtherDoc );
			}
			docItem.add( "other-docs", jsonOtherDocs );
			hasDocumentation = true;
		}
		return hasDocumentation ? docItem : null;
	}
	
	/**
	 * Adds a list of OTM documentation items to the given JSON object.  If a property
	 * was added to the OTM documentation, this method will return true (false otherwise).
	 * 
	 * @param doc  the owning JSON object for all OTM documentation items
	 * @param docItems  the list of documentation items to add
	 * @param propertyName  the name of the property for the array of documentation items
	 * @return boolean
	 */
	private boolean addOtmDocumentationItems(JsonObject doc, List<String> docItems, String propertyName) {
		boolean itemsAdded = false;
		
		if (!docItems.isEmpty()) {
			JsonArray jsonDocItems = new JsonArray();
			
			for (String docItem : docItems) {
				if ((docItem != null) && (docItem.length() > 0)) {
					jsonDocItems.add( StringEscapeUtils.escapeJson( docItem ) );
					itemsAdded = true;
				}
			}
			doc.add( propertyName, jsonDocItems );
		}
		return itemsAdded;
	}
	
}
