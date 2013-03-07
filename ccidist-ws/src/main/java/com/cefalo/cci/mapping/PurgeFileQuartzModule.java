package com.cefalo.cci.mapping;

import com.cefalo.cci.service.IssueService;
import com.cefalo.cci.service.IssueServiceImpl;
import com.cefalo.cci.service.PurgeFileService;
import com.cefalo.cci.utils.Utils;
import com.google.inject.name.Names;
import org.nnsoft.guice.guartz.QuartzModule;

public class PurgeFileQuartzModule extends QuartzModule{
    @Override
    protected void schedule() {
        scheduleJob(PurgeFileService.class);
        bind(IssueService.class).to(IssueServiceImpl.class);
        bindConstant().annotatedWith(Names.named("cacheDirFullPath")).to(Utils.CACHE_DIR_FULLPATH);
        bindConstant().annotatedWith(Names.named("fileSystemSeperator")).to(Utils.FILE_SEPARATOR);
        bindConstant().annotatedWith(Names.named("interval")).to(Utils.CLEANING_INTERVAL);
    }
}
