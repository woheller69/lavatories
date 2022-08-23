package org.woheller69.lavatories.ui.updater;

import org.woheller69.lavatories.database.Lavatory;

import java.util.List;

/**
 * Created by chris on 24.01.2017.
 */
public interface IUpdateableCityUI {

    void processUpdateLavatories(List<Lavatory> lavatories, int cityID);

}
