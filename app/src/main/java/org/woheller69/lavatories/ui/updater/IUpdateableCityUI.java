package org.woheller69.lavatories.ui.updater;

import org.woheller69.lavatories.database.Station;

import java.util.List;

/**
 * Created by chris on 24.01.2017.
 */
public interface IUpdateableCityUI {

    void processUpdateStations(List<Station> stations, int cityID);

}
