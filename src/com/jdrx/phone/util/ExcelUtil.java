package com.jdrx.phone.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jdrx.phone.entity.PhoneEntity;

public class ExcelUtil {

	/**
	 * @Title: outExcel
	 * @Description: 生成 excel
	 * @param list
	 * @param fileAddress 文件存放地址（包含文件名称）
	 */
	public void outExcel(List<PhoneEntity> list,String fileAddress) {
		System.out.println("生成excel...list"+list.size());
		HSSFWorkbook wb = new HSSFWorkbook(); // 创建了一个excel文件
		HSSFSheet sheet = wb.createSheet("手机回收价格"); // 创建了一个工作簿
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(0);

		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
		cell.setCellValue("品牌");
		cell.setCellStyle(style);
		cell = row.createCell(1);
		cell.setCellValue("渠道");
		cell.setCellStyle(style);
		cell = row.createCell(2);
		cell.setCellValue("型号");
		cell.setCellStyle(style);
		cell = row.createCell(3);
		cell.setCellValue("价格");
		cell.setCellStyle(style);
		cell = row.createCell(4);
		cell.setCellValue("查询路径");
		cell.setCellStyle(style);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			row = sheet.createRow(i + 1);
			PhoneEntity phone = (PhoneEntity) list.get(i);
			if (phone == null) {
				break;
			}
			// 第四步，创建单元格，并设置值
			row.createCell(0).setCellValue(phone.getName());
			row.createCell(1).setCellValue(phone.getChannel());
			row.createCell(2).setCellValue(phone.getModel());
			row.createCell(3).setCellValue(phone.getPrice());
			System.out.println("生成excel..+"+phone.getName()+"            url"+phone.getUrlDetail());
			if (phone.getUrlDetail() == null) {
				row.createCell(4).setCellValue("");
			} else {
				row.createCell(4).setCellValue(phone.getUrlDetail());
			}
		}
		// 第六步，将文件存到指定位置
		try {
			File directory = new File("");//参数为空
			//获取当前路径
			String courseFile = directory.getCanonicalPath();
			FileOutputStream fout = new FileOutputStream(courseFile+"/"+fileAddress);
			wb.write(fout);
			fout.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
