package com.fsdesa.ocrsvc.service;

import com.fsdesa.ocrsvc.dto.ResultDto;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static com.fsdesa.ocrsvc.utils.Strings.concat;
import static com.fsdesa.ocrsvc.utils.Strings.splitLines;

@Service
public class PdfOcrService {

    Logger logger = LoggerFactory.getLogger(PdfOcrService.class);
    @Value("${tmp.location:/tmp}")
    String tmpPath;

    private String getBase64(File image) {
        try {
            ConvertCmd cmd = new ConvertCmd(true);
            IMOperation resize600 = new IMOperation();
            resize600.size(600);
            resize600.addImage();
            resize600.resize(600);
            resize600.quality(70d);
            resize600.addImage();
            Path thumbPath = Paths.get(tmpPath, concat(FilenameUtils.getBaseName(image.getName()), "_thumb.", FilenameUtils.getExtension(image.getPath())));
            logger.info("Ejecutando {} {}", image.toString(), thumbPath.toString());
            cmd.run(resize600, image.toString(), thumbPath.toString());
            byte[] fileContent = FileUtils.readFileToByteArray(thumbPath.toFile());
            String encodedString = Base64.encodeBase64String(fileContent);
            logger.info("Devolviendo el thumbnail");
            Files.delete(thumbPath);

            return encodedString;
        } catch (Exception e) {
            throw new RuntimeException("Exception execute getBase64", e);
        }
    }

    public List<ResultDto> extractTextFromPages(PDDocument document, int maxpages) throws IOException {
        List<ResultDto> results = new ArrayList<>();
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        for (int page = 0; page < document.getNumberOfPages() && page < maxpages; page++) {
            PDFTextStripper reader = new PDFTextStripper();
            reader.setStartPage(page + 1);
            reader.setEndPage(page + 1);
            String pageText = reader.getText(document);

            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
            File temp = File.createTempFile("tempfile_" + page, ".png");
            ImageIO.write(bim, "png", temp);
            String base64 = getBase64(temp);
            // logger.info("Resultado ocr {}", pageText);
            results.add(new ResultDto(base64, splitLines(pageText)));
            temp.delete();
        }
        return results;
    }
}
