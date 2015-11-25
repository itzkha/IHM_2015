package ch.heig_vd.pebble.communications;

import android.os.AsyncTask;
import ch.heig_vd.pebble.interfaces.OnCommunicationRequest;

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
public abstract class Communication extends AsyncTask<Void, Void, String> {
    private OnCommunicationRequest<String, Exception> mCallBack;
    private Exception mException;

    @Override
    protected String doInBackground(Void... params) {
        mException = null;
        return communication();
    }

    @Override
    protected void onPostExecute(String ret) {
        if (mException == null) {
            mCallBack.success(ret);
        } else {
            mCallBack.failure(mException);
        }
    }

    protected abstract String communication();

    protected void setCallBack(OnCommunicationRequest<String, Exception> callBack) {
        mCallBack = callBack;
    }

    protected void setException(Exception exception) {
        mException = exception;
    }
}