package com.jdrx;

import javax.swing.JOptionPane;

import com.jdrx.phone.util.AiHuiShouUtil;
import com.jdrx.phone.util.HuiShouBaoUtil;
import com.jdrx.phone.util.TaolvUtil;
import com.jdrx.phone.util.YijiJsoupUtil;

public class JsoupStart {

	public static void main(String[] args) {
		JOptionPane.showMessageDialog(new JOptionPane(),
				"抓取程序已启动，请稍后在当前目录查看结果（xls的文件）!"
				+"\r\n查询（淘绿网、爱回收、回收宝、易机网）",
				"提示信息", JOptionPane.INFORMATION_MESSAGE);
		
		Thread t1 = new Thread(new Runnable(){//爱回收
	      	public void run(){
	      		AiHuiShouUtil util = new AiHuiShouUtil();
	      		util.queryAiHuiShou();
	      	}
		});
		t1.setName("aihuishou");
		t1.start();
		
		Thread t2 = new Thread(new Runnable(){//回收宝
	      	public void run(){
	      		HuiShouBaoUtil util = new HuiShouBaoUtil();
	      		util.queryHuiShouBao();
	      	}
		}); 
		t2.setName("huishoubao");
		t2.start();
		
		Thread t3 = new Thread(new Runnable(){//淘绿网
	      	public void run(){
	      		TaolvUtil util = new TaolvUtil();
	      		util.queryTaoLvPhone();
	      	}
		});
		t3.setName("taolv");
		t3.start();

		Thread t4 = new Thread(new Runnable(){ //易机网
	      	public void run(){
	      		YijiJsoupUtil util = new YijiJsoupUtil();
	      		util.queryYiJiPhone();
	      	}
		});
		t4.setName("yiji");
		t4.start();
		
	}

}
