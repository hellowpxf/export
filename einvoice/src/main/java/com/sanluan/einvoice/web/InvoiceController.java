package com.sanluan.einvoice.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.sanluan.einvoice.service.excelHandlerService.InternMangerFareService;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sanluan.einvoice.service.Invoice;
import com.sanluan.einvoice.service.OfdInvoiceExtractor;
import com.sanluan.einvoice.service.PdfInvoiceExtractor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/invoice")
public class InvoiceController {

    @Value("${backupPath}")
    private String backupPath;

    private static ThreadLocal<Map<String, DateFormat>> threadLocal = new ThreadLocal<>();
    private static final String FILE_NAME_FORMAT_STRING = "yyyy/MM-dd/HH-mm-ssSSSS";
    public static final RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000).build();

    /**
     * @param pattern
     * @return date format
     */
    public static DateFormat getDateFormat(String pattern) {
        Map<String, DateFormat> map = threadLocal.get();
        DateFormat format = null;
        if (null == map) {
            map = new HashMap<>();
            format = new SimpleDateFormat(pattern);
            map.put(pattern, format);
            threadLocal.set(map);
        } else {
            format = map.computeIfAbsent(pattern, k -> new SimpleDateFormat(k));
        }
        return format;
    }

    @RequestMapping(value = "/extrat")
    public Invoice extrat(@RequestParam(value = "file", required = false) MultipartFile file, String url) {
        String fileName = getDateFormat(FILE_NAME_FORMAT_STRING).format(new Date());
        File dest = null;
        boolean ofd = false;
        if (null != file && !file.isEmpty()) {
            if (file.getOriginalFilename().toLowerCase().endsWith(".ofd")) {
                ofd = true;
                dest = new File(backupPath, fileName + ".ofd");
            } else {
                dest = new File(backupPath, fileName + ".pdf");
            }
            dest.getParentFile().mkdirs();
            try {
                FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
            } catch (IOException e) {
            }
        } else if (null != url) {
            if (url.toLowerCase().endsWith(".ofd")) {
                ofd = true;
                dest = new File(backupPath, fileName + ".ofd");
            } else {
                dest = new File(backupPath, fileName + ".pdf");
            }
            dest.getParentFile().mkdirs();
            try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();) {
                HttpUriRequest request = new HttpGet(url);
                try (CloseableHttpResponse response = httpclient.execute(request)) {
                    HttpEntity entity = response.getEntity();
                    if (null != entity) {
                        BufferedInputStream inputStream = new BufferedInputStream(entity.getContent());
                        FileUtils.copyInputStreamToFile(inputStream, dest);
                        EntityUtils.consume(entity);
                    }
                }
            } catch (Exception e) {
            }
        }
        Invoice result = null;
        try {
            if (null != dest) {
                if (ofd) {
                    result = OfdInvoiceExtractor.extract(dest);
                } else {
                    result = PdfInvoiceExtractor.extract(dest);
                }
                if (null != result.getAmount()) {
                    dest.delete();
                }
            } else {
                result = new Invoice();
                result.setTitle("error");
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            result = new Invoice();
            result.setTitle("error");
        }
        return result;
    }

    @RequestMapping(value = "/extrats")
    public List<Invoice> extrats(@RequestParam(value = "file", required = false) List<MultipartFile> files, String url) {
        String fileName = getDateFormat(FILE_NAME_FORMAT_STRING).format(new Date());
        File dest = null;
        boolean ofd = false;
        List<Invoice> invoices = new ArrayList<Invoice>();
        for (int i = 0; i <files.size() ; i++) {
            Invoice result = null;
            MultipartFile file = files.get(i);
            if (null != file && !file.isEmpty()) {
                if (file.getOriginalFilename().toLowerCase().endsWith(".ofd")) {
                    ofd = true;
                    dest = new File(backupPath, fileName + ".ofd");
                } else {
                    dest = new File(backupPath, fileName + ".pdf");
                }
                dest.getParentFile().mkdirs();
                try {
                    FileUtils.copyInputStreamToFile(file.getInputStream(), dest);
                } catch (IOException e) {
                }
            } else if (null != url) {
                if (url.toLowerCase().endsWith(".ofd")) {
                    ofd = true;
                    dest = new File(backupPath, fileName + ".ofd");
                } else {
                    dest = new File(backupPath, fileName + ".pdf");
                }
                dest.getParentFile().mkdirs();
                try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();) {
                    HttpUriRequest request = new HttpGet(url);
                    try (CloseableHttpResponse response = httpclient.execute(request)) {
                        HttpEntity entity = response.getEntity();
                        if (null != entity) {
                            BufferedInputStream inputStream = new BufferedInputStream(entity.getContent());
                            FileUtils.copyInputStreamToFile(inputStream, dest);
                            EntityUtils.consume(entity);
                        }
                    }
                } catch (Exception e) {
                }
            }
            try {
                if (null != dest) {
                    if (ofd) {
                        result = OfdInvoiceExtractor.extract(dest);
                    } else {
                        result = PdfInvoiceExtractor.extract(dest);
                    }
                    if (null != result.getAmount()) {
                        dest.delete();
                    }
                } else {
                    result = new Invoice();
                    result.setTitle("error");
                }
            } catch (IOException | DocumentException e) {
                e.printStackTrace();
                result = new Invoice();
                result.setTitle("error");
            }
            invoices.add(result);
        }
        return invoices;
    }

    @Resource
    InternMangerFareService internMangerFareService;
    @RequestMapping(value = "/intern/fare/export")
    @ResponseBody
    public Object internExcelExport(@RequestParam(value = "file", required = false) List<MultipartFile> files, HttpServletResponse response) throws IOException, InvalidFormatException {
        internMangerFareService.internExcelHandler(files,response);
        return  null;
    }
}
