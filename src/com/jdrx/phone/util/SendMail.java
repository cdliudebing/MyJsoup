package com.jdrx.phone.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * @ClassName: SendMail
 * @Description: 发送带附件的邮件 （需要jar包：mail.jar）
 * @author liudebing@evercreative.com.cn
 * @date 2016年5月27日 上午10:13:20
 *
 * @version
 */
public class SendMail {

	private static String host = "smtp.163.com"; // smtp服务器
	private static String from = "cdliudb@163.com"; // 发件人地址

	/** 要发送的邮件集合 */
	private List<MailUtil> mailUtil;

	private static String user = "cdliudb@163.com"; // 用户名
	private static String pwd = "qazwsx520"; // 密码
	private String subject = "Java 邮件发送 (带附近)，HTML格式"; // 邮件标题

	/**
	 * @Title: getMailAddress
	 * @Description: 转换接收人，多人用英文“,”分隔
	 * @param addressArray
	 * @return
	 */
	public String getMailAddress(String[] addressArray) {
		if (addressArray != null && addressArray.length == 1) {
			return addressArray[0];
		}
		int length = addressArray.length;
		StringBuffer toList = new StringBuffer();
		for (int i = 0; i < length; i++) {
			toList.append(addressArray[i]);
			if (i != (length - 1)) {
				toList.append(",");
			}
		}
		return toList.toString();
	}

	/**
	 * @Title: send
	 * @Description: 发送邮件
	 * 
	 * @param addressArray
	 *            需要接收的邮箱地址
	 * @param listFile 
	 *            附件list
	 */
	public void sendMailMessage(String[] addressArray, List<MailUtil> listFile) {
		Properties props = new Properties();

		// 设置发送邮件的邮件服务器的属性
		props.put("mail.smtp.host", host);
		// 需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
		props.put("mail.smtp.auth", "true");

		// 用刚刚设置好的props对象构建一个session
		Session session = Session.getDefaultInstance(props);

		// 有了这句便可以在发送邮件的过程中在console处显示过程信息，供调试使
		// 用（你可以在控制台（console)上看到发送邮件的过程）
		session.setDebug(true);

		// 用session为参数定义消息对象
		MimeMessage message = new MimeMessage(session);
		try {
			// 加载发件人地址
			message.setFrom(new InternetAddress(from));
			// 转换多邮件接收人，多人接收时 用逗号分隔
			String addressString = getMailAddress(addressArray);
			InternetAddress[] address = InternetAddress.parse(addressString);

			// 加载收件人地址
			message.addRecipients(Message.RecipientType.TO, address);
			// 加载标题
			message.setSubject(subject);

			// 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
			Multipart multipart = new MimeMultipart();

			// 设置邮件的文本内容
			BodyPart contentPart = new MimeBodyPart();
			contentPart.setContent("多邮件发送测试   <a href='http://www.baidu.com' target='_blank'>点击打开新窗口</a>",
					"text/html;charset=UTF-8");
			multipart.addBodyPart(contentPart);

			// 判断是否有附件发送
			if (listFile != null && listFile.size() > 0) {
				for (MailUtil file : listFile) {
					// 添加附件
					BodyPart messageBodyPart = new MimeBodyPart();
					DataSource source = new FileDataSource(file.getAffix());
					// 添加附件的内容
					messageBodyPart.setDataHandler(new DataHandler(source));
					// 添加附件的标题
					// 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
					sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
					messageBodyPart.setFileName("=?GBK?B?" + enc.encode(file.getAffixName().getBytes()) + "?=");
					multipart.addBodyPart(messageBodyPart);
				}
			}
			// 将multipart对象放到message中
			message.setContent(multipart);
			// 保存邮件
			message.saveChanges();
			// 发送邮件
			Transport transport = session.getTransport("smtp");
			// 连接服务器的邮箱
			transport.connect(host, user, pwd);
			// 把邮件发送出去
			transport.sendMessage(message, message.getAllRecipients());
			// 关闭
			transport.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SendMail sm = new SendMail();
		// 添加附件
		List<MailUtil> listFile = new ArrayList<MailUtil>();
		MailUtil file1 = new MailUtil();
		file1.setAffix("F:\\download\\mail\\activation.jar");
		file1.setAffixName("activation.jar");
		MailUtil file2 = new MailUtil();
		file2.setAffix("F:\\download\\mail\\mail.jar");
		file2.setAffixName("mail.jar");
		listFile.add(file1);
		listFile.add(file2);
		sm.setMailUtil(listFile);
		// "729580808@qq.com","497834304@qq.com",
		// 添加多人邮件接收人
		//String[] addressArray = { "935911153@qq.com", "497834304@qq.com", "408206505@qq.com", "605051929@qq.com",
		//		"522123428@qq.com", "449734946@qq.com", "56221867@qq.com" };
		String[] addressArray = {"729580808@qq.com"};
		// 设置smtp服务器以及邮箱的帐号和密码
		sm.sendMailMessage(addressArray, listFile);

	}

	public List<MailUtil> getMailUtil() {
		return mailUtil;
	}

	public void setMailUtil(List<MailUtil> mailUtil) {
		this.mailUtil = mailUtil;
	}

}