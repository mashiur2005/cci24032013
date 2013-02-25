package com.cefalo.cci.unitTest;

import com.cefalo.cci.service.CciService;
import com.google.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules({ServicesTestModule.class })
public class CciServiceUnitTest {
    @Inject
    private CciService cciService;

    @Test
    public void getAllFileNamesInDirectoryTest() {
        File file = FileUtils.getFile("src", "test", "resources", "epubs");
        String fileDir = file.getAbsolutePath();
        List<String> listFileNames = cciService.getAllFileNamesInDirectory(fileDir);
        assertEquals(2, listFileNames.size());
        assertTrue(listFileNames.contains("widget-quiz-20121022.epub"));
    }
}
