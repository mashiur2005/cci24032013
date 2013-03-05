package com.cefalo.cci.service;

import com.cefalo.cci.model.Issue;
import com.cefalo.cci.utils.Utils;
import com.google.inject.Inject;
import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

@javax.inject.Singleton
@org.nnsoft.guice.guartz.Scheduled(jobName = "test", cronExpression = "0/25 * * * * ?")
public class PurgeFileService implements org.quartz.Job{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String baseDirPath = Utils.HOME_DIR;
    private String seprator = Utils.FILE_SEPARATOR;
    private int daysBack = Utils.FILE_REMOVING_DAY;

    @Inject
    private IssueService issueService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("cron job working for removing old files ......." + System.currentTimeMillis());
        DateTime prevDate = new DateTime();
        long purgeTime = prevDate.minusDays(daysBack).getMillis();
        Date purgeDate = new Date(purgeTime);
        List<Issue> issueList = issueService.getOldIssueList(purgeDate);

        File file = null;

        for (Issue anIssueList : issueList) {
            file = new File(baseDirPath + seprator + anIssueList.getPublication().getOrganization().getId() + seprator + anIssueList.getPublication().getId() + seprator + anIssueList.getName());
            logger.info("Epubs to be deleted: " + file.getAbsolutePath());
            if (file.exists()) {
                if (!file.delete()) {
                    logger.info("can't delete file");
                } else {
                    logger.info("file deleted..........." + file.getAbsolutePath());
                }
            } else {
                logger.info("file does not exists..........." + file.getAbsolutePath());
            }
        }
    }
}
