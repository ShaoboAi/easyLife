package com.shaobo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     email:pd-shaobo@qq.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.*;
import java.util.*;
import java.util.concurrent.SynchronousQueue;

/**
 * auto config
 *
 * @goal gogo
 * 
 * @phase process-sources
 */
public class MyMojo   extends AbstractMojo{
    /**
     * @parameter expression="${project.basedir}"
     * @required
     * @readonly
     */
    private File basedir;

    /**
     * Location of the file.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;
    /**
     * @parameter expression="${project.build.sourceDirectory}"
     * @required
     * @readonly
     */
    private File sourcedir;

    /**
     * @parameter
     */
    private String[] includes;
    private List<String> allFiles;
    private List<List<String>> fileLines;
    private Properties properties;

    private static final String ANTX = "antx.properties";

    public void execute()  throws MojoExecutionException {
        try {
            init();
            config();
        } catch (Exception e){
            getLog().error(e);
        }

    }
    private List<String> replace(Properties properties, List<String> src){
        List<String> result = new ArrayList<String>(src.size());
        Map<String,String> mp = new HashMap<String, String>((Map)properties);
        String placeHolder = null;
        for (String line : src){
            for (Map.Entry<String,String> item : mp.entrySet()){
                placeHolder = "\\$\\{" + item.getKey() + "\\}" ;
                line = line.replaceAll(placeHolder, item.getValue());
            }
            result.add(line);
        }
        return result;
    }

    private static  List<String> populate(String[] includes){
        List<String> res = new ArrayList<String>();
        for (String f : includes){
            if(f.indexOf("**") > 0){
                int s1 = f.lastIndexOf(47);//slash
                int s2 = f.lastIndexOf(92);//back slash
                String dir = f.substring(0, s1 > s2 ? s1:s2);
                File directory = new File(dir);
                File[] fs = directory.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith("xml");
                    }
                });
                for(File path: fs){
                    res.add(path.getAbsolutePath());
                }
            } else {
                res.add(f);
            }
        }
        return res;
    }
    public void config() throws IOException {
        FileOutputStream fos = null;
        List<String> lines = null;
        List<String> res = null;
        int cnt = 0;
        for(String afile : allFiles){
            fos  = new FileOutputStream(afile);
            lines = fileLines.get(cnt); cnt += 1;
            res =  replace(properties, lines);
            for (String line : res){
                IOUtils.write(line + "\n", fos);
            }
            IOUtils.closeQuietly(fos);
        }
    }
    public  void init() throws Exception{
        properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(basedir + System.getProperty("file.separator") + ANTX);
            properties.load(fis);
        } finally {
            if(fis!=null){
                fis.close();
            }
        }
        List<String> lines = null;
        fileLines = new ArrayList<List<String>>();
        allFiles = populate(includes);
        for(String include : allFiles){
            fis = new FileInputStream(include);
            lines = IOUtils.readLines(fis);
            fileLines.add(lines);
            IOUtils.closeQuietly(fis);
        }
    }
}
