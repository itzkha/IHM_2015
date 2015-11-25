package ch.heig_vd.pebble.utils;

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
public enum MessageType {
    CURRENT_LOCATION,
    FIX_LOCATION,
    START_FIXED_LOCATION,
    STOP_FIXED_LOCATION,
    ELEVATION,
    WEATHER,
    TEMPERATURE,
    PRESSURE,
    HUMIDITY,
    WIND,
    SUNRISE,
    SUNSET,
    TRANSPORT;

    public static MessageType getById(int id) {
        for(MessageType e : values()) {
            if(e.ordinal() == id) return e;
        }
        return FIX_LOCATION;
    }
}
