package com.ernest.pocs.awsmigrator.writer;

import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;


@Service
public class FileWriter {

    public void writeIntoFile(String filePath, String content) {

        BufferedWriter bw = null;
        java.io.FileWriter fw = null;

        try {
            fw = new java.io.FileWriter(filePath);
            bw = new BufferedWriter(fw);
            bw.write(content);

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                if (bw != null)
                    bw.close();

                if (fw != null)
                    fw.close();

            } catch (IOException ex) {

                ex.printStackTrace();

            }

        }
    }
}
