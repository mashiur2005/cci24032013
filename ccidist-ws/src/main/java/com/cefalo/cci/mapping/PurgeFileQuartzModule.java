package com.cefalo.cci.mapping;

import com.cefalo.cci.service.IssueService;
import com.cefalo.cci.service.IssueServiceImpl;
import com.cefalo.cci.service.PurgeFileService;
import org.nnsoft.guice.guartz.QuartzModule;

public class PurgeFileQuartzModule extends QuartzModule{
    @Override
    protected void schedule() {
        scheduleJob(PurgeFileService.class);
        bind(IssueService.class).to(IssueServiceImpl.class);
    }
}
