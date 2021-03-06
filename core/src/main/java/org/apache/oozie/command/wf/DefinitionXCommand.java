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
package org.apache.oozie.command.wf;

import org.apache.oozie.ErrorCode;
import org.apache.oozie.WorkflowJobBean;
import org.apache.oozie.XException;
import org.apache.oozie.command.CommandException;
import org.apache.oozie.command.PreconditionException;
import org.apache.oozie.executor.jpa.WorkflowJobGetJPAExecutor;
import org.apache.oozie.service.JPAService;
import org.apache.oozie.service.Services;
import org.apache.oozie.util.LogUtils;
import org.apache.oozie.util.ParamChecker;
import org.apache.oozie.util.XLog;

public class DefinitionXCommand extends WorkflowXCommand<String> {
    private String jobId;
    private WorkflowJobBean wfJob;
    private final XLog LOG = XLog.getLog(DefinitionXCommand.class);

    public DefinitionXCommand(String jobId) {
        super("definition", "definition", 1);
        this.jobId = ParamChecker.notEmpty(jobId, "jobId");
    }

    @Override
    protected boolean isLockRequired() {
        return false;
    }

    @Override
    protected String getEntityKey() {
        return this.jobId;
    }

    @Override
    protected void loadState() throws CommandException {
        try {
            JPAService jpaService = Services.get().get(JPAService.class);

            if (jpaService != null) {
                this.wfJob = jpaService.execute(new WorkflowJobGetJPAExecutor(jobId));
                LogUtils.setLogInfo(wfJob, logInfo);
            }
            else {
                LOG.error(ErrorCode.E0610);
            }
        }
        catch (XException ex) {
            throw new CommandException(ex);
        }
    }

    @Override
    protected void verifyPrecondition() throws CommandException, PreconditionException {
    }

    @Override
    protected String execute() throws CommandException {
        if (wfJob != null) {
            return wfJob.getWorkflowInstance().getApp().getDefinition();
        }
        else {
            throw new CommandException(ErrorCode.E0604);
        }
    }

}
