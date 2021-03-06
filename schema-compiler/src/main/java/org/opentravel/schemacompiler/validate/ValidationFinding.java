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
package org.opentravel.schemacompiler.validate;

import java.text.MessageFormat;
import java.util.Locale;

import org.opentravel.schemacompiler.ioc.SchemaCompilerApplicationContext;
import org.springframework.context.NoSuchMessageException;

/**
 * Encapsulates information for an individual finding discovered during the validation process.
 * 
 * @author S. Livezey
 */
public class ValidationFinding implements Comparable<ValidationFinding> {

    private Validatable source;
    private FindingType type;
    private String messageKey;
    private Object[] messageParams;
    private long findingTimestamp;

    /**
     * Constructs a new finding message using the current timestamp (using
     * <code>System.nanoTime()</code>).
     * 
     * @param source
     *            the source object of the finding
     * @param type
     *            the type of the finding
     * @param messageKey
     *            the message key for the finding
     * @param messageParams
     *            the message parameters for the finding
     */
    public ValidationFinding(Validatable source, FindingType type, String messageKey,
            Object[] messageParams) {
        this(source, type, messageKey, messageParams, System.nanoTime());
    }

    /**
     * Constructs a new finding message using the specified timestamp.
     * 
     * @param source
     *            the source object of the finding
     * @param type
     *            the type of the finding
     * @param messageKey
     *            the message key for the finding
     * @param messageParams
     *            the message parameters for the finding
     * @param findingTimestamp
     *            the system timestamp (should be obtained using <code>System.nanoTime()</code>)
     */
    public ValidationFinding(Validatable source, FindingType type, String messageKey,
            Object[] messageParams, long findingTimestamp) {
        this.source = source;
        this.type = type;
        this.messageKey = messageKey;
        this.messageParams = messageParams;
        this.findingTimestamp = findingTimestamp;
    }

    /**
     * Returns a formatted version of this validation finding using the resource bundle specified by
     * the schema compiler's application context file.
     * 
     * @param format
     *            the message format to use when rendering text for this finding
     * @return String
     */
    public String getFormattedMessage(FindingMessageFormat format) {
        String sourceIdentity = (source == null) ? "<NULL>" : source.getValidationIdentity();
        String typeDisplayName = getDisplayText(type);
        String messageText;

        try {
            messageText = SchemaCompilerApplicationContext.getContext().getMessage(messageKey,
                    messageParams, Locale.getDefault());

        } catch (NoSuchMessageException e) {
            messageText = messageKey; // No error - just use the raw message key
        }
        return MessageFormat.format(format.getFormat(), sourceIdentity, typeDisplayName,
                messageText);
    }

    /**
     * Returns the display text that should be used to represent the given <code>FindingType</code>
     * value.
     * 
     * @param type
     *            the finding type to display
     * @return String
     */
    private String getDisplayText(FindingType type) {
        String displayText;

        if (type == null) {
            displayText = "NULL";
        } else {
            try {
                displayText = SchemaCompilerApplicationContext.getContext().getMessage(
                        type.getResourceKey(), null, Locale.getDefault());

            } catch (NoSuchMessageException e) {
                displayText = type.getDisplayName();
            }
        }
        return displayText;
    }

    /**
     * Returns the source object of the finding.
     * 
     * @return Validatable
     */
    public Validatable getSource() {
        return source;
    }

    /**
     * Returns the type of the finding.
     * 
     * @return FindingType
     */
    public FindingType getType() {
        return type;
    }

    /**
     * Returns the message key for the finding.
     * 
     * @return String
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Returns the message parameters for the finding (may be null).
     * 
     * @return Object[]
     */
    public Object[] getMessageParams() {
        return messageParams;
    }

    /**
     * Returns the timestamp of the finding (in nanos).
     * 
     * @return long
     */
    public long getFindingTimestamp() {
        return findingTimestamp;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        boolean result = false;

        if (other instanceof ValidationFinding) {
            result = (this.compareTo((ValidationFinding) other) == 0);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (findingTimestamp ^ (findingTimestamp >>> 32));
        result = prime * result
                + ((source == null) ? 0 : source.getValidationIdentity().hashCode());
        return result;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(ValidationFinding other) {
        if (this.findingTimestamp == other.findingTimestamp) {
            return this.source.getValidationIdentity().compareTo(
                    other.source.getValidationIdentity());
        } else {
            return (this.findingTimestamp < other.findingTimestamp) ? -1 : 1;
        }
    }

}
