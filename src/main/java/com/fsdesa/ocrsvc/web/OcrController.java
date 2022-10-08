package com.fsdesa.ocrsvc.web;

import com.fsdesa.ocrsvc.dto.ResultDto;
import com.fsdesa.ocrsvc.service.PdfOcrService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class OcrController {
    Logger logger = LoggerFactory.getLogger(OcrController.class);
    @Autowired
    PdfOcrService service;

    @GetMapping(value = "/status", produces = "text/json")
    public ResponseEntity<String> get() {
        logger.info("Devolviendo status");
        return new ResponseEntity<String>("OK", HttpStatus.OK);
    }

    @PostMapping("/extract/pages")
    public @ResponseBody
    ResponseEntity<?> extractTextPages(@RequestParam("file") MultipartFile file, @RequestParam(value = "pages", required = false) int pages) {
        try {
            logger.info("Extracting {}", file.getOriginalFilename());
            PDDocument document = PDDocument.load(file.getBytes());
            List<ResultDto> resultDtos = service.extractTextFromPages(document, pages);
            Map obj = new HashMap();
            obj.put("fileName", file.getOriginalFilename());
            obj.put("results", resultDtos);

            return new ResponseEntity<Map>(obj, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

