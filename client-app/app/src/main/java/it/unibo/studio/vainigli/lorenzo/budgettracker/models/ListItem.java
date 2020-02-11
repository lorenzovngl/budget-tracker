package it.unibo.studio.vainigli.lorenzo.budgettracker.models;

/**
 * Created by Lorenzo on 25/06/2016.
 *
 * Questa classe è utilizzata per dividere in sezioni gli elementi della lista delle spese.
 * Anziché solo movimenti, gli elementi possono essere item (cioè movimenti) o section (intestazioni di sezione).
 * Se ListItem è un movimento (spasa, entrata), contiene un puntatore ad un oggetto Movement, se invece è una sezione,
 * contiene una stringa di intestazione.
 */
public class ListItem {

    public static final int SECTION = 0;
    public static final int ITEM = 1;
    public int type;
    public String section;
    public Movement movement;

    public ListItem(int type, String section, Movement movement) {
        this.type = type;
        if (type == ITEM) {
            this.movement = movement;
        } else if (type == SECTION) {
            this.section = section;
        }
    }
}
