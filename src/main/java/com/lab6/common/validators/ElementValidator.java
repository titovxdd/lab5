package com.lab6.common.validators;

import com.lab6.client.managers.BandAsker;
import com.lab6.common.models.MusicBand;
import com.lab6.client.sup.Console;
import com.lab6.common.Sup.ExecutionStatus;
import com.lab6.common.Sup.Pair;

public class ElementValidator {

    public Pair<ExecutionStatus, MusicBand> validateAsking(Console console, Long id) {
        try {
            MusicBand band = BandAsker.askBand(console, id);
            return validating(band);
        } catch (BandAsker.Breaker e) {
            return new Pair<>(new ExecutionStatus(false, "Ввод был прерван пользователем!"), null);
        } catch (BandAsker.IllegalInputException e) {
            return new Pair<>(new ExecutionStatus(false, e.getMessage()), null);
        }
    }

    public Pair<ExecutionStatus, MusicBand> validating(MusicBand band) {
        if (band != null && band.validate()) {
            return new Pair<>(new ExecutionStatus(true, "Элемент введён корректно!"), band);
        }
        return new Pair<>(new ExecutionStatus(false, "Введены некорректные данные!"), null);
    }
}