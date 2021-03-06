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

import org.opentravel.schemacompiler.model.TLDocumentationOwner;

/**
 * @author Eric.Bronson
 *
 */
public abstract class FieldDocumentationBuilder<T extends TLDocumentationOwner>
		extends AbstractDocumentationBuilder<T> {

	protected DocumentationBuilder type;
	
	protected NamedEntityDocumentationBuilder<?> owner;

	protected String typeName;
	
	protected String sinceVersion;
	
	protected boolean isRequired;
	
	protected int maxOcurrences;

	protected String pattern;

	protected String exampleValue;
	
	
//	private int minLength = -1;
//  private int maxLength = -1;
//  private int fractionDigits = -1;
//  private int totalDigits = -1;
//  private String minInclusive;
//  private String maxInclusive;
//  private String minExclusive;
//  private String maxExclusive;
	

	/**
	 * @param 
	 */
	public FieldDocumentationBuilder(T t) {
		super(t);
	}

	/**
	 * @return the isRequired
	 */
	public boolean isRequired() {
		return isRequired;
	}

	/**
	 * @return the maxOcurrences
	 */
	public int getMaxOcurrences() {
		return maxOcurrences;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @return the exampleValue
	 */
	public String getExampleValue() {
		return exampleValue;
	}
	
	
	public DocumentationBuilder getType() {
		return type;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getSinceVersion() {
		return sinceVersion;
	}

	/**
	 * @return the owner
	 */
	public NamedEntityDocumentationBuilder<?> getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(NamedEntityDocumentationBuilder<?> owner) {
		this.owner = owner;
	}
	

}
