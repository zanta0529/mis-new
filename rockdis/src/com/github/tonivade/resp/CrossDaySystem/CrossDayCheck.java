package com.github.tonivade.resp.CrossDaySystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;



public class CrossDayCheck {

    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    public static String currentRuntimeDate = null;

    public static String getCurrentRuntimeDate(){
        return currentRuntimeDate;
    }

    public static void setCurrentRuntimeDate(String crossDayDate){
        CrossDayCheck.currentRuntimeDate = crossDayDate;
    }

    public String getCurrentFileLastModifiedDate(String primaryRocksPath){
        FileTime time = getCurrentFileDate(primaryRocksPath);
        Date lastModifiedDate = new Date(time.toMillis());
        return formatter.format(lastModifiedDate);
    }

    public String getRuntimeDate(){
        Date runtimeDate = new Date();
        return formatter.format(runtimeDate);
    }

    public FileTime getCurrentFileDate(String primaryRocksPath){
        String filenameOfCurrentFile = "CURRENT";
        BasicFileAttributes attr = null;
        try{
            Path file = Paths.get(primaryRocksPath + filenameOfCurrentFile);
            attr = Files.readAttributes(file, BasicFileAttributes.class);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return attr.lastModifiedTime();
    }

    public static void main(String[] args) {
        CrossDayCheck crossDayCheck = new CrossDayCheck();
        String time = crossDayCheck.getCurrentFileLastModifiedDate("/tmp/hgetall/");

        System.out.println("CURRENT 檔案的最後異動時間：" + time);
    }
}
