package com.ww;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @description:Itinerary
 * @author:pxf
 * @data:2023/10/09
 **/
public class Itinerary {
    public static void main(String[] args) throws IOException {
        String pdfFilePath = "C:/Users/Administrator/Desktop/发票/xcd2.pdf";
        // 创建PDF阅读器
        PdfReader reader = new PdfReader(pdfFilePath);

        // 创建Word文档对象
        XWPFDocument document = new XWPFDocument();

        // 遍历PDF页码
        for (int page = 1; page <= reader.getNumberOfPages(); page++) {
            // 获取PDF页面内容
            String text = PdfTextExtractor.getTextFromPage(reader, page);

            // 将页面内容写入Word文档中
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(text);
        }

        // 将Word文档保存到指定文件中
        FileOutputStream out = new FileOutputStream("C:\\example.doc");
        document.write(out);
        out.close();
    }
}
