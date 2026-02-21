package Commands;

import managers.BandAsker;
import managers.Context;
import models.MusicBand;
import sup.Console;
import sup.IdValidator;
import sup.Pair;
import sup.Status;

public abstract class AskingCommand extends Command{
    protected final Context context;


    public AskingCommand(String name, String description, Console console, Context context){
        super(name, description, console);
        this.context = context;
    }

    @Override
    public Status execute(String arg){
        Long id;
        IdValidator validator = new IdValidator(context);
        if (arg.equals(" ")){
            id = Long.parseLong(arg);
            Pair<Status, MusicBand> validationStatusPair = parseBand(console, id);
            if (!validationStatusPair.getFirst().isSuccess()) {
                return validationStatusPair.getFirst();
            } else {
                return execute(validationStatusPair.getSecond());
            }

        } else if (validator.validate(arg).isSuccess()) {
            id = Long.parseLong(arg);
            Pair<Status, MusicBand> validationStatusPair = parseBand(console, id);
            if (!validationStatusPair.getFirst().isSuccess()) {
                return validationStatusPair.getFirst();
            } else {
                return execute(validationStatusPair.getSecond());
            }
        } else {
            return new Status(false, "Введен неверный аргумент");
        }
    }

    public abstract Status execute(MusicBand band);


    public Pair<Status, MusicBand> parseBand(Console console, Long id){
        try {
            MusicBand band = BandAsker.askBand(console, id);
            if (band != null && band.validate()) {
                return new Pair<>(new Status(true, "Элемент введён корректно!"), band);
            }
            return new Pair<>(new Status(false, "Введены некорректные данные!"), null);
        } catch (BandAsker.Breaker e) {
            return new Pair<>(new Status(false, "Ввод был прерван пользователем!"), null);
        } catch (BandAsker.IllegalInputException e) {
            return new Pair<>(new Status(false, e.getMessage()), null);
        }
    }

}
