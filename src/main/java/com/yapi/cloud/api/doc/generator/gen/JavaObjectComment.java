package com.yapi.cloud.api.doc.generator.gen;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version 8.0.0
 * @program: api-doc-generator
 * @author: "清歌"
 * @create: 2020-06-05 14:38

 */
public class JavaObjectComment {



    public static Map<String, String> getFieldComment(String typeName) throws IOException {
        HashMap<String, String> map = new HashMap<>();
        String filePath = InterfaceConfig.PATH + typeName.replace(".", "\\") + ".java";

        FileReader fileReader = new FileReader(filePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String s = null;
        StringBuilder sb = new StringBuilder();
        while ((s = bufferedReader.readLine()) != null) {
            //System.out.println(s);
            sb.append(s);
        }

        List<String> annoList = getRegexAnnotationExp("\\/\\*([^\\*^\\/]*|[\\*^\\/*]*|[^\\**\\/]*)*\\*\\/", sb);
        List<String> fieldList = getRegexFieldExp("private\\s+([a-zA-Z]*)*\\s+([a-zA-Z]*)*", sb);

        for (int i = 0; i < fieldList.size(); i++) {
            if (fieldList.size() > i) {
                if (fieldList.get(i).equalsIgnoreCase("final")) {
                    continue;
                }
//                String item = "{'title':'" + annoList.get(i) + ",'column':'" + fieldList.get(i) + "'},";
                map.put(fieldList.get(i), annoList.get(i));
            }
        }
        return map;
    }


    /**
     * 读取注释文字
     */
    private static List<String> getRegexAnnotationExp(String regexExp, StringBuilder sb) {
        List<String> resultList = new ArrayList<String>();

        Pattern pattern = Pattern.compile(regexExp);
        String searchPlainText = sb.toString();
        Matcher matcher = pattern.matcher(searchPlainText);
        int size = 0;
        while (matcher.find()) {
            String groupExp = matcher.group(0).replaceAll("/\\*+\\s+\\*", "").replaceAll("\\s+\\*/", "");
            if (size == 0) {
                size++;
                continue;
            }
            resultList.add(groupExp);
//            System.out.println("匹配到："+groupExp+"  位置：(" + matcher.start()+","+matcher.end()+")");
        }
        return resultList;
    }

    /**
     * 读取属性field
     */
    private static List<String> getRegexFieldExp(String regexExp, StringBuilder sb) {
        List<String> resultList = new ArrayList<String>();

        Pattern pattern = Pattern.compile(regexExp);
        String searchPlainText = sb.toString();
        Matcher matcher = pattern.matcher(searchPlainText);
        while (matcher.find()) {
            String groupExp = matcher.group(0).replaceAll("private\\s+([a-zA-Z]*)*\\s+", "");
            resultList.add(groupExp);
//            System.out.println("匹配到："+groupExp+"  位置：(" + matcher.start()+","+matcher.end()+")");
        }
        return resultList;
    }




}
