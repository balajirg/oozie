/**
 * Copyright (c) 2010 Yahoo! Inc. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. See accompanying LICENSE file.
 */
package org.apache.oozie.command.bundle;

import org.apache.oozie.BundleJobBean;
import org.apache.oozie.client.Job;
import org.apache.oozie.command.CommandException;
import org.apache.oozie.executor.jpa.BundleJobGetJPAExecutor;
import org.apache.oozie.service.JPAService;
import org.apache.oozie.service.Services;
import org.apache.oozie.test.XDataTestCase;

public class TestBundleJobXCommand extends XDataTestCase {

    private Services services;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        services = new Services();
        services.init();
        cleanUpDBTables();
    }

    @Override
    protected void tearDown() throws Exception {
        services.destroy();
        super.tearDown();
    }

    /**
     * Test: submit bundle job, then check job info
     * 
     * @throws Exception
     */
    public void testBundleJobInfo1() throws Exception {
        BundleJobBean job = this.addRecordToBundleJobTable(Job.Status.PREP);

        JPAService jpaService = Services.get().get(JPAService.class);
        assertNotNull(jpaService);
        BundleJobGetJPAExecutor bundleJobGetjpa = new BundleJobGetJPAExecutor(job.getId());
        job = jpaService.execute(bundleJobGetjpa);
        assertEquals(job.getStatus(), Job.Status.PREP);

        BundleJobBean bundleJob = (new BundleJobXCommand(job.getId())).call();

        assertEquals(0, bundleJob.getCoordinators().size());
        assertEquals(bundleJob.getStatus(), Job.Status.PREP);
        assertEquals(bundleJob.getId(), job.getId());
    }

    /**
     * Test: jobId is wrong
     * 
     * @throws Exception
     */
    public void testBundleJobInfoFailed() throws Exception {
        this.addRecordToBundleJobTable(Job.Status.PREP);

        try {
            new BundleJobXCommand("bundle-id").call();
            fail("Job doesn't exist. Should fail.");
        }
        catch (CommandException ce) {
            // Job doesn't exist. Exception is expected.
        }
    }
}