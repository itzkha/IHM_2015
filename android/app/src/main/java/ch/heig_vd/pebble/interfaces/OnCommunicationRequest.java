package ch.heig_vd.pebble.interfaces;

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
public interface OnCommunicationRequest<S, F> {

    void success(S s);

    void failure(F f);
}