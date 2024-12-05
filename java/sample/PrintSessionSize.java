package sample.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@slf4j
public class PrintSessionSize {

    public int getSessionSize(HttpSession session) {
        log.info("=====");
        Enumeration en = session.getAttributeNames();
        String name = null;
        Object obj = null;
        ByteArrayOutputStream bastream = null;
        ObjectOutputStream objOut = null;
        int objSize;
        int totalSize = 0;
        while (en.hasMoreElements()) {
            name = (String) en.nextElement();
            obj = session.getAttribute(name);

            try {
                bastream = new ByteArrayOutputStream();
                objOut = new ObjectOutputStream(bastream);
                objOut.writeObject(obj);

                //objSize
                objSize = bastream.size();
            } catch (Exception ex) {
                //TODO 오류처리
                objSize = 0;
            }
            log.info("sessionName : {} \t sessionSize : {}", name, objSize);
            totalSize += objSize;
        }
        log.info("===== [TOTAL HTTP_SESSION_SIZE] : {}", totalSize);
        return totalSize;
    } // End getSessionSize().

}