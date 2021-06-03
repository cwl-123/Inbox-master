package com.cwl.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class testExample {

    public static void main(String[] args) throws IOException {
        String url1 ="https://mail.qq.com/cgi-bin/download?sid=nug6uYig_J4skVo6&mailid=ZL2626-otR6uIlyJkDtikEhIrVLUb5";
        System.out.println(url1);
        String url="https://ppt.sotary.com/web/wxapp/index.html";
        Document document = Jsoup.parse(new URL(url), 10000);
        Elements elements = document.getElementsByClass("com-shadow");
        int id=0;
        String pattern = "http.*";
        for (Element element:elements) {
            Element img = element.getElementsByTag("img").first();
            String src = img.attr("src");
            System.out.println(src);
            if(src.matches(pattern)){
            id++;
            saveFile(src,"D:\\test\\",id+".jpg");}
            else{
                System.out.println(src+"是非正常链接，不下载！");
            }
        }
        saveFile(url1,"D:\\test\\","hhh.zip");
    }


    public static void saveFile(String url,String filePath,String fileName) throws IOException {
        File file = new File(filePath);
        if(!file.exists()){file.mkdirs();}
        URL src = new URL(url);
        URLConnection urlConnection = src.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        System.out.println(file.getPath());
        FileOutputStream fileOutputStream = new FileOutputStream(file.getPath()+"\\"+fileName);
        int len;
        byte[] bs = new byte[1024];
        while((len=inputStream.read(bs))>0){
            fileOutputStream.write(bs,0,len);
        }
        System.out.println(fileName+"下载完毕！");
        inputStream.close();
        fileOutputStream.close();
    }

    private void savePic(InputStream inputStream, String fileName) {

        OutputStream os = null;
        try {
            String path = "D:\\testFile\\";
            // 2、保存到临时文件
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流保存到本地文件

            File tempFile = new File(path);
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath() + File.separator + fileName);
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                os.write(bs, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 完毕，关闭所有链接
            try {
                os.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
