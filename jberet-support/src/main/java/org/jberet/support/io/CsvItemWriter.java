/*
 * Copyright (c) 2014 Red Hat, Inc. and/or its affiliates.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Cheng Fang - Initial API and implementation
 */

package org.jberet.support.io;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemWriter;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jberet.support._private.SupportLogger;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.io.ICsvListWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.io.ICsvWriter;

import static org.jberet.support.io.CsvProperties.BEAN_TYPE_KEY;
import static org.jberet.support.io.CsvProperties.RESOURCE_KEY;

/**
 * An implementation of {@code javax.batch.api.chunk.ItemWriter} that writes data to CSV file or resource.
 */
@Named
public class CsvItemWriter extends CsvItemReaderWriterBase implements ItemWriter {
    @Inject
    protected StepContext stepContext;

    @Inject
    @BatchProperty
    protected String[] header;

    @Inject
    @BatchProperty
    protected String writeComments;

    @Inject
    @BatchProperty
    protected boolean overwrite;

    private StringWriter stringWriter;
    protected ICsvWriter delegateWriter;

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        SupportLogger.LOGGER.openCsvItemWriter(checkpoint);
        if (beanType == null) {
            throw SupportLogger.LOGGER.invalidCsvPreference(null, BEAN_TYPE_KEY);
        } else if (java.util.List.class.isAssignableFrom(beanType)) {
            delegateWriter = new CsvListWriter(getOutputWriter(), getCsvPreference());
        } else if (java.util.Map.class.isAssignableFrom(beanType)) {
            delegateWriter = new CsvMapWriter(getOutputWriter(), getCsvPreference());
        } else {
            delegateWriter = new CsvBeanWriter(getOutputWriter(), getCsvPreference());
        }
        if (header == null) {
            throw SupportLogger.LOGGER.invalidCsvPreference(null, CsvProperties.HEADER_KEY);
        }
        if (this.nameMapping == null) {
            this.nameMapping = header;
        }
        this.cellProcessorInstances = getCellProcessors(header);
        if (writeComments != null) {
            delegateWriter.writeComment(writeComments);
        }
        delegateWriter.writeHeader(header);
    }

    @Override
    public void close() throws Exception {
        if (resource.equalsIgnoreCase(CsvProperties.RESOURCE_STEP_CONTEXT)) {
            final Object transientUserData = stepContext.getTransientUserData();
            if (transientUserData != null) {
                SupportLogger.LOGGER.existingTransientUserData(transientUserData);
            }
            stepContext.setTransientUserData(stringWriter.toString());
        }
        delegateWriter.close();
    }

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        SupportLogger.LOGGER.aboutToWriteItems(items.size(), items.get(0).getClass());
        if (delegateWriter instanceof ICsvBeanWriter) {
            final ICsvBeanWriter writer = (ICsvBeanWriter) delegateWriter;
            if (cellProcessorInstances.length == 0) {
                for (final Object e : items) {
                    writer.write(e, nameMapping);
                }
            } else {
                for (final Object e : items) {
                    writer.write(e, nameMapping, cellProcessorInstances);
                }
            }
        } else if (delegateWriter instanceof ICsvMapWriter) {
            final ICsvMapWriter writer = (ICsvMapWriter) delegateWriter;
            if (cellProcessorInstances.length == 0) {
                for (final Object e : items) {
                    writer.write((Map<String, ?>) e, nameMapping);
                }
            } else {
                for (final Object e : items) {
                    writer.write((Map<String, ?>) e, nameMapping, cellProcessorInstances);
                }
            }
        } else if (delegateWriter instanceof ICsvListWriter) {
            final ICsvListWriter writer = (ICsvListWriter) delegateWriter;
            if (cellProcessorInstances.length == 0) {
                for (final Object e : items) {
                    writer.write((List<?>) e);
                }
            } else {
                for (final Object e : items) {
                    writer.write((List<?>) e, cellProcessorInstances);
                }
            }
        }
        delegateWriter.flush();
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return delegateWriter.getRowNumber();
    }

    protected Writer getOutputWriter() {
        if (resource == null) {
            throw SupportLogger.LOGGER.invalidCsvPreference(resource, RESOURCE_KEY);
        }
        if (resource.equalsIgnoreCase(CsvProperties.RESOURCE_STEP_CONTEXT)) {
            return stringWriter = new StringWriter();
        }
        try {
            return new FileWriter(resource, !overwrite);
        } catch (final IOException e) {
            throw SupportLogger.LOGGER.invalidCsvPreference(resource, CsvProperties.RESOURCE_KEY);
        }
    }
}
