package com.github.tonivade.resp.CrossDaySystem;

import java.io.File;



public class CrossDayFileOps {
    private CrossDayCheck crossDayCheck = new CrossDayCheck();

    public static void createRuntimeDirectory(String primaryRocksPath, String runtimeDateDirectory){
        File directory = new File(primaryRocksPath + runtimeDateDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}
