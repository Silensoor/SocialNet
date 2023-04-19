package socialnet.logging;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WhatSom {

    public void what() throws IOException {

        pushLogs(getHref());

    }

    public String getHref () throws IOException {

        URL url = new URL("https://cloud-api.yandex.net/v1/disk/resources/upload?path=logs.log&overwrite=true");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "OAuth y0_AgAAAABpyRv7AADLWwAAAADgTODc9qxs-Et7T1GdZE2muWAFM0eiubA");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        String s = String.valueOf(content);
        String[] split = s.split("\"");
        String href = split[7];

        return href;
    }

    public void pushLogs(String href) throws IOException {

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {

            File file = new File("logs.log");
            HttpPut httpPut = new HttpPut(href);
            httpPut.setEntity(new FileEntity(file));
            HttpResponse response = httpclient.execute(httpPut);
        }
    }

}
