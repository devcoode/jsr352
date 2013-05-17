/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jberet.testapps.common;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.batch.api.BatchProperty;
import javax.batch.api.Batchlet;
import javax.inject.Inject;

public class BatchletNoNamed extends PostConstructPreDestroyBase implements Batchlet {
    @Inject
    @BatchProperty(name = "batchlet-prop")
    protected String batchletProp;

    @Inject
    @BatchProperty(name = "reference-job-prop")
    protected String referencingJobProp;

    @Inject
    @BatchProperty(name = "reference-system-prop")
    protected String referencingSystemProp;

    @Inject
    @BatchProperty(name = "reference-job-param")
    protected String referencingJobParam;

    @Override
    public String process() throws Exception {
        System.out.printf("%nIn %s, running step %s, job batch/exit status: %s/%s, step batch/exit status: %s/%s%n, job properties: %s, step properties: %s%n%n",
                this, stepContext.getStepName(),
                jobContext.getBatchStatus(), jobContext.getExitStatus(),
                stepContext.getBatchStatus(), stepContext.getExitStatus(),
                jobContext.getProperties(), stepContext.getProperties()
        );
        return "Processed";
    }

    @Override
    public void stop() throws Exception {
    }

    //overridden in org.jberet.testapps.common.Batchlet0, so this PostConstruct should not be invoked.
    @PostConstruct
    void ps() {
        System.out.printf("BatchletNoNamed PostConstruct of %s%n", this);
        addToJobExitStatus("BatchletNoNamed.ps");
    }

    //overridden in org.jberet.testapps.common.Batchlet0, so this PostConstruct should not be invoked.
    @PreDestroy
    void pd() {
        System.out.printf("BatchletNoNamed PreDestroy of %s%n", this);
        addToJobExitStatus("BatchletNoNamed.pd");
    }

}
