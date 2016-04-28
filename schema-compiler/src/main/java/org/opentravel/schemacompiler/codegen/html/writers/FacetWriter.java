/**
 * 
 */
package org.opentravel.schemacompiler.codegen.html.writers;

import java.io.IOException;

import org.opentravel.schemacompiler.codegen.html.builders.DocumentationBuilder;
import org.opentravel.schemacompiler.codegen.html.builders.FacetDocumentationBuilder;
import org.opentravel.schemacompiler.codegen.html.Content;
import org.opentravel.schemacompiler.codegen.html.writers.info.AliasInfoWriter;
import org.opentravel.schemacompiler.codegen.html.writers.info.ExampleInfoWriter;
import org.opentravel.schemacompiler.codegen.html.writers.info.FacetAttributeInfoWriter;
import org.opentravel.schemacompiler.codegen.html.writers.info.FacetIndicatorInfoWriter;
import org.opentravel.schemacompiler.codegen.html.writers.info.InfoWriter;
import org.opentravel.schemacompiler.codegen.html.writers.info.PropertyInfoWriter;

/**
 * @author Eric.Bronson
 *
 */
public class FacetWriter extends
		NamedEntityWriter<FacetDocumentationBuilder> implements
		FieldOwnerWriter, AliasOwnerWriter {

	/**
	 * @param configuration
	 * @param filename
	 * @throws IOException
	 */
	public FacetWriter(FacetDocumentationBuilder classDoc,
			DocumentationBuilder prev,
			DocumentationBuilder next) throws Exception {
		super(classDoc, prev, next);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opentravel.schemacompiler.codegen.html.writers.AliasOwnerWriter
	 * #addAliasInfo(org.opentravel.schemacompiler.codegen.html.Content)
	 */
	@Override
	public void addAliasInfo(Content aliasTree) {
		if (member.getAliases().size() > 0) {
			InfoWriter aliasWriter = new AliasInfoWriter(this,
					member);
			aliasWriter.addInfo(aliasTree);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opentravel.schemacompiler.codegen.html.writers.FieldOwnerWriter
	 * #addPropertyInfo(org.opentravel.schemacompiler.codegen.html.Content)
	 */
	@Override
	public void addPropertyInfo(Content memberTree) {
		if (member.getProperties().size() > 0) {
			InfoWriter propWriter = new PropertyInfoWriter(this,
					member);
			propWriter.addInfo(memberTree);
		}
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opentravel.schemacompiler.codegen.html.writers.FieldOwnerWriter
	 * #getAttributeTree
	 * (org.opentravel.schemacompiler.codegen.html.Content)
	 */
	@Override
	public void addAttributeInfo(Content memberTree) {
		if (member.getAttributes().size() > 0) {
			InfoWriter attWriter = new FacetAttributeInfoWriter(this,
					member);
			attWriter.addInfo(memberTree);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opentravel.schemacompiler.codegen.html.writers.FieldOwnerWriter
	 * #getIndicatorTree
	 * (org.opentravel.schemacompiler.codegen.html.Content)
	 */
	@Override
	public void addIndicatorInfo(Content memberTree) {
		if (member.getIndicators().size() > 0) {
			InfoWriter attWriter = new FacetIndicatorInfoWriter(this,
					member);
			attWriter.addInfo(memberTree);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opentravel.schemacompiler.codegen.html.writers.FieldOwnerWriter
	 * #getExampleTree(org.opentravel.schemacompiler.codegen.html.Content)
	 */
	@Override
	public void addExampleInfo(Content memberTree) {
		InfoWriter exampleWriter = new ExampleInfoWriter(this, member);
		exampleWriter.addInfo(memberTree);
	}
}
