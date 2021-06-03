package com.cwl.demo;

import com.cwl.demo.config.MailBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.security.NoSuchProviderException;
import java.util.Properties;
import javax.mail.Part;

@RestController
public class MailReceiver {
    final static int START_NUM=100;

    @Autowired
    private MailBox mailbox;

    @GetMapping("/hello")
    public void MailReceive() {
        try {
            // 1. 设置连接信息, 生成一个 Session
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", "smtp.genomics.cn");
            props.setProperty("mail.smtp.auth", "true");
            Session session = Session.getDefaultInstance(props);
            // 2. 获取 Store 并连接到服务器
            // 敏感信息给封装到properties文件里
            URLName urlname = new URLName(mailbox.getProtocol(),mailbox.getHost(),Integer.parseInt(mailbox.getPort()),null,mailbox.getUsername(),mailbox.getPassword());
            Store store = session.getStore(urlname);
            store.connect();
            Folder folder = store.getDefaultFolder();// 默认父目录
            if (folder == null) {
                System.out.println("服务器不可用");
                return ;
            }
            System.out.println("默认信箱名:" + folder.getName());
            Folder[] folders = folder.list();// 默认目录列表
            for(int i = 0; i < folders.length; i++) {
                System.out.println(folders[0].getName());
            }
            System.out.println("默认目录下的子目录数: " + folders.length);
            Folder popFolder = folder.getFolder("INBOX");// 获取收件箱
            popFolder.open(Folder.READ_ONLY);// 可读邮件,不可以删邮件的模式打开目录
            // 4. 列出来收件箱 下所有邮件
            // 取出来邮件数
            int msgCount = popFolder.getMessageCount();
            System.out.println("共有邮件: " + msgCount + "封");
            int count=0;
            // 10封10封一取，保证之前的Message[]被JVM回收
            for (int i = 0; i < msgCount-START_NUM ; i=i+10) {
                //邮件按时间顺序排布，所以实验二数据集中在100之后
                Message[] messages = popFolder.getMessages(START_NUM+i,Math.min(msgCount,START_NUM+i+10) );
                // 5. 循环处理每个邮件并实现邮件转为新闻的功能
                for (int j = 0; j < messages.length; j++) {
                    // 单个邮件
                    System.out.println("第" + (START_NUM+ count) +"邮件开始");
                    mailReceiver(messages[j],"D:\\");
                    System.out.println();
                    //邮件读取用来校验
//                    messages[j].writeTo(new FileOutputStream("D:/pop3MailReceiver"+ count +".eml"));
                    count++;
                }
            }
            // 7. 关闭 Folder 会真正删除邮件, false 不删除
            popFolder.close(true);
//             8. 关闭 store, 断开网络连接
            store.close();
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 解析邮件
     *
     * @return
     * @throws IOException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private void mailReceiver(Message msg,String downloadPath)throws Exception{
        // 发件人信息
        Address[] froms = msg.getFrom();
        if(froms != null) {
            InternetAddress addr = (InternetAddress)froms[0];
            System.out.println("邮件大小:"+ msg.getSize());
            System.out.println("发件人地址:" + addr.getAddress());
            System.out.println("发件人显示名:" + addr.getPersonal());
            System.out.println("发送时间："+msg.getSentDate());
        }
        String subject = msg.getSubject();
        System.out.println("邮件主题:" + subject);

        if( subject.matches(".*(实验二|实验2|第二次实验).*")) {

            //附件下载地址address
            String address=downloadPath+fileEncode(subject);
            File file = new File(address);
            //如果这个文件夹不存在，才创建
            if (!file.exists()) {
                file.mkdirs();
            }

            System.out.println("找到目标邮件，下载相应附件！");
            System.out.println("---------------------------");
            Object o = msg.getContent();
            if(o instanceof  Multipart){
                Multipart multipart=(Multipart) o;
                reMultipart(multipart,address);
            }else if(o instanceof Part){
                Part part = (Part) o;
                rePart(part,address);
            }
        }
    }

    /**
     * @param part 解析内容
     * @throws Exception
     */
    private void rePart(Part part,String address) throws MessagingException,
            UnsupportedEncodingException, IOException, FileNotFoundException {
        if (part.getDisposition() != null) {
            String strFileName = MimeUtility.decodeText(part.getFileName()); //MimeUtility.decodeText解决附件名乱码问题
            System.out.println("发现附件: " +  MimeUtility.decodeText(part.getFileName()));
            System.out.println("内容类型: " + MimeUtility.decodeText(part.getContentType()));
            System.out.println("附件内容:" + part.getContent());
            System.out.println("开始下载附件---------");
            InputStream in = part.getInputStream();// 打开附件的输入流
            // 读取附件字节并存储到文件中
            FileOutputStream out = new FileOutputStream(address+"\\"+strFileName);
            int data;
            while((data = in.read()) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
            System.out.println("附件 "+address+strFileName+"成功下载");
        }
    }

    /**
     * @param str 文件夹地址address
     * 这里是专为文件写的转义方法，涉及文件操作
     */
    public static String fileEncode(String str) {
        if (str != null) {
            return str
                    .replaceAll("\\\\", "＼")
                    .replaceAll("/", "／")
                    .replaceAll(":", "：")
                    .replaceAll("[*]", "＊")
                    .replaceAll("[?]", "？")
                    .replaceAll("\"", "”")
                    .replaceAll(":", "：")
                    .replaceAll("<", "＜")
                    .replaceAll(">", "＞")
                    .replaceAll("[|]", "｜");
        } else {
            //防止空，搞成空格
            return " ";
        }
    }

    private void reMultipart(Multipart multipart,String address) throws Exception {
        //System.out.println("邮件共有" + multipart.getCount() + "部分组成");
        // 依次处理各个部分
        for (int j = 0, n = multipart.getCount(); j < n; j++) {
            //System.out.println("处理第" + j + "部分");
            Part part = multipart.getBodyPart(j);//解包, 取出 MultiPart的各个部分, 每部分可能是邮件内容,
            // 也可能是另一个小包裹(MultipPart)
            // 判断此包裹内容是不是一个小包裹, 一般这一部分是 正文 Content-Type: multipart/alternative
            if (part.getContent() instanceof Multipart) {
                Multipart p = (Multipart) part.getContent();// 转成小包裹
                //递归迭代
                reMultipart(p,address);
            } else {
                rePart(part,address);
            }
        }
    }

}
