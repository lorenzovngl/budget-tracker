package it.unibo.studio.vainigli.lorenzo.budgettracker.utilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import it.unibo.studio.vainigli.lorenzo.budgettracker.database.Const;
import it.unibo.studio.vainigli.lorenzo.budgettracker.models.Movement;

public class SortUtils {

    public static List<Movement> sortListByDate(List<Movement> list, final String order){
        ListUtils.printListItems(list);
        Collections.sort(list, new Comparator<Movement>(){
            @Override
            public int compare(Movement lhs, Movement rhs) {
                Date lhsDate = lhs.getDate();
                Date rhsDate = rhs.getDate();
                if (order.equals(Const.ORDER_ASC)){
                    return lhsDate.compareTo(rhsDate);
                } else if (order.equals(Const.ORDER_DESC)){
                    return -lhsDate.compareTo(rhsDate);
                } else {
                    return 0;
                }
            }
        });
        ListUtils.printListItems(list);
        return list;
    }
}
