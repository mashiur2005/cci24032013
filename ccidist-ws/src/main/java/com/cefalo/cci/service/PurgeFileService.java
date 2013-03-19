package com.cefalo.cci.service;

import com.cefalo.cci.model.Issue;
import com.cefalo.cci.storage.Storage;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

@javax.inject.Singleton
@org.nnsoft.guice.guartz.Scheduled(jobName = "test", cronExpression = "0 0 0 * * ?")
public class PurgeFileService implements org.quartz.Job{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private IssueService issueService;

    @Inject
    @Named("cacheDirFullPath")
    private String cacheDirFullPath;

    @Inject
    @Named("cacheEpubDirFullPath")
    private String cacheEpubDirFullPath;

    @Inject
    private Storage cachedStorage;

    @Inject
    @Named("interval")
    private int interval;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("cron job working for removing old files ......." + System.currentTimeMillis());
        DateTime prevDate = new DateTime();
        long purgeTime = prevDate.minusDays(interval).getMillis();
        Date purgeDate = new Date(purgeTime);
        List<Issue> issueList = issueService.getOldIssueList(purgeDate);

        File file = null;

        for (Issue anIssueList : issueList) {
            file = new File(cacheDirFullPath + "/" +  anIssueList.getEpubFile().getId());
            logger.info("Files to be deleted: " + file.getAbsolutePath());
            if (file.exists()) {
                deleteRecursive(file);
            }
            File epubFile = new File(cacheEpubDirFullPath + anIssueList.getEpubFile().getId());
            if (epubFile.delete()) {
                logger.info(String.format("epub files deleting from cron service %s", epubFile.getAbsolutePath()));
            }
            cachedStorage.invalidateExtractedFileCache(String.valueOf(anIssueList.getEpubFile().getId()));
        }
    }

    public void deleteRecursive(File path){
        File[] c = path.listFiles();
        logger.info("Cleaning out folder:" + path.toString());
        for (File file : c){
            if (file.isDirectory()){
                logger.info("Deleting file:" + file.toString());
                deleteRecursive(file);
                file.delete();
            } else {
                file.delete();
            }
        }

        path.delete();
    }
}
