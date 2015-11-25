package ch.heig_vd.pebble.communications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ch.heig_vd.pebble.interfaces.OnCommunicationRequest;
import ch.heig_vd.pebble.utils.Utils;

/***************************************************************************************************
 * HEIG-VD // heig-vd.ch
 * Haute Ecole d'Ingénierie et de Gestion du Canton de Vaud
 * School of Business and Engineering in Canton de Vaud
 * *************************************************************************************************
 * Author               : Jonathan Bischof
 * Email                : jonathan.bischof@heig-vd.ch
 * Date                 : 28 october 2015
 * Project              : Pebble IHM
 * *************************************************************************************************
 * Modifications :
 * Ver      Date          Engineer                                Comments
 * 1.0      28.10.2015    Jonathan Bischof                        Creation
 * *************************************************************************************************
 * ...
 **************************************************************************************************/
public class RequestGET extends Communication {

    private String mRequest = "";

    public RequestGET(OnCommunicationRequest<String, Exception> callBack, String request) {
        setCallBack(callBack);
        mRequest = request;
    }

    @Override
    protected String communication() {
        int status;
        String body = "";
        InputStream is = null;
        try {
            URL url = new URL(mRequest);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                body += line + "\n";
            }
            br.close();
            if (status != HttpURLConnection.HTTP_OK) {
                setException(new Exception(body));
            }
        } catch (IOException e) {
            setException(e);
            Utils.log(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Utils.log(e.getMessage());
                }
            }
        }
        return body;
    }
}