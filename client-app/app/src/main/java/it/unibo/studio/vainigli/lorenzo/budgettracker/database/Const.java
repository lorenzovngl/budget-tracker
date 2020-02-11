package it.unibo.studio.vainigli.lorenzo.budgettracker.database;

/**
 * Created by Lorenzo on 03/11/2016.
 */

public class Const {

    public static final String DATABASES_INFO = "info.db";
    public static final String DATABASE_NAME = "budget.db";
    public static final String DB1_NAME = "budget1.db";
    public static final String DB2_NAME = "budget2.db";
    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_REGISTERS = "registers";
    public static final String TABLE_SHARED_USERS = "shared_users";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_MOVEMENTS = "movements";
    public static final String TABLE_CALENDAR = "calendar";
    public static final String TABLE_LOCATIONS = "locations";

    // TODO da eliminare
    public static class Accounts {
        public static final String COL_ID = "id";
        public static final String TCOL_ID = TABLE_ACCOUNTS + "." + COL_ID;
        public static final String COL_DESC = "description";
        public static final String TCOL_DESC = TABLE_ACCOUNTS + "." + COL_DESC;
        public static final String COL_ICON = "icon";
        public static final String TCOL_ICON = TABLE_ACCOUNTS + "." + COL_ICON;
    }

    public static class Users {
        public static final String COL_FULLNAME = "fullname";
        public static final String TCOL_FULLNAME = TABLE_USERS + "." + COL_FULLNAME;
        public static final String COL_USERNAME = "username";
        public static final String TCOL_USERNAME = TABLE_USERS + "." + COL_USERNAME;
        public static final String COL_MD5_PASSWORD = "md5_password";
        public static final String TCOL_MD5_PASSWORD = TABLE_USERS + "." + COL_MD5_PASSWORD;
    }

    public static class Registers {
        public static final String COL_ID = "id";
        public static final String TCOL_ID = TABLE_REGISTERS + "." + COL_ID;
        public static final String COL_REMOTE_ID = "remote_id";
        public static final String TCOL_REMOTE_ID = TABLE_REGISTERS + "." + COL_REMOTE_ID;
        public static final String COL_NAME = "name";
        public static final String TCOL_NAME = TABLE_REGISTERS + "." + COL_NAME;
        public static final String COL_MD5_PASSWORD = "md5_password";
        public static final String TCOL_MD5_PASSWORD = TABLE_REGISTERS + "." + COL_MD5_PASSWORD;
        public static final String COL_LAST_SYNC = "last_sync";
        public static final String TCOL_LAST_SYNC = TABLE_REGISTERS + "." + COL_LAST_SYNC;
        public static final String COL_OWNER = "owner";
        public static final String TCOL_OWNER = TABLE_REGISTERS + "." + COL_OWNER;
        public enum Action {
            ADD_LOCAL, ADD_REMOTE
        }
    }

    public static class SharedUsers {
        public static final String COL_ID = "id_db";
        public static final String TCOL_ID = TABLE_SHARED_USERS + "." + COL_ID;
        public static final String COL_NAME = "username";
        public static final String TCOL_NAME = TABLE_SHARED_USERS + "." + COL_NAME;
    }

    public static class Categories {
        public static final String COL_ID = "id";
        public static final String TCOL_ID = TABLE_CATEGORIES + "." + COL_ID;
        public static final String COL_DESC = "description";
        public static final String TCOL_DESC = TABLE_CATEGORIES + "." + COL_DESC;
        public static final String COL_PARENT = "parent";
        public static final String TCOL_PARENT = TABLE_CATEGORIES + "." + COL_PARENT;
        public static final String COL_TYPE = "type";
        public static final String TCOL_TYPE = TABLE_CATEGORIES + "." + COL_TYPE;
        public static final String COL_ICON = "icon";
        public static final String TCOL_ICON = TABLE_CATEGORIES + "." + COL_ICON;
        public static final String ACCOUNT = "0";
        public static final String INCOME = "1";
        public static final String EXPENSE = "-1";
        public enum Type {
            ALL, EXPENSES, INCOMES, ACCOUNTS
        }
        public enum Hierarchy{
            ALL, PRIMARY, SECONDARY
        }
        public enum Role{
            SOURCES, DESTINATIONS
        }
    }

    public static class Movements {
        public static final String COL_ID = "id";
        public static final String TCOL_ID = TABLE_MOVEMENTS + "." + COL_ID;
        public static final String COL_DATE = "date";
        public static final String TCOL_DATE = TABLE_MOVEMENTS + "." + COL_DATE;
        public static final String COL_DESC = "description";
        public static final String TCOL_DESC = TABLE_MOVEMENTS + "." + COL_DESC;
        //public static final String COL_ACCOUNT = "account";
        //public static final String TCOL_ACCOUNT = TABLE_MOVEMENTS + "." + COL_ACCOUNT;
        public static final String COL_SRCCAT = "src_category";
        public static final String TCOL_SRCCAT = TABLE_MOVEMENTS + "." + COL_SRCCAT;
        public static final String COL_DSTCAT = "dst_category";
        public static final String TCOL_DSTCAT = TABLE_MOVEMENTS + "." + COL_DSTCAT;
        public static final String COL_AMOUNT = "amount";
        public static final String TCOL_AMOUNT = TABLE_MOVEMENTS + "." + COL_AMOUNT;
        public static final String COL_START_DATE = "start_date";
        public static final String TCOL_START_DATE = TABLE_MOVEMENTS + "." + COL_START_DATE;
        public static final String COL_END_DATE = "end_date";
        public static final String TCOL_END_DATE = TABLE_MOVEMENTS + "." + COL_END_DATE;
        public static final String COL_ADDR = "address";
        public static final String TCOL_ADDR = TABLE_MOVEMENTS + "." + COL_ADDR;
    }

    public static class Calendar {
        public static final String COL_DATE = "date";
        public static final String TCOL_DATE = TABLE_CALENDAR + "." + COL_DATE;
        public static final String COL_ID_MOV = "id_movement";
        public static final String TCOL_ID_MOV = TABLE_CALENDAR + "." + COL_ID_MOV;
        public static final String COL_AMOUNT = "amount";
        public static final String TCOL_AMOUNT = TABLE_CALENDAR + "." + COL_AMOUNT;
    }

    public static class Locations {
        public static final String COL_ADDR = "address";
        public static final String TCOL_ADDR = TABLE_LOCATIONS + "." + COL_ADDR;
        public static final String COL_LAT = "latitude";
        public static final String TCOL_LAT = TABLE_LOCATIONS + "." + COL_LAT;
        public static final String COL_LNG = "longitude";
        public static final String TCOL_LNG = TABLE_LOCATIONS + "." + COL_LNG;
    }

    public static class Icons {
        public static final String BANK = "res/drawable/ic_bank.xml";
        public static final String BEACH = "res/drawable/ic_beach.xml";
        public static final String BUS = "res/drawable/ic_bus.xml";
        public static final String CAKE = "res/drawable/ic_cake.xml";
        public static final String CAMERA = "res/drawable/ic_camera.xml";
        public static final String CAR = "res/drawable/ic_car.xml";
        public static final String EMAIL = "res/drawable/ic_email.xml";
        public static final String FITNESS = "res/drawable/ic_fitness.xml";
        public static final String GAS = "res/drawable/ic_gas.xml";
        public static final String GAS_STATION = "res/drawable/ic_gas_station.xml";
        public static final String GIFTCARD = "res/drawable/ic_giftcard.xml";
        public static final String GROCERY_STORE = "res/drawable/ic_grocery_store.xml";
        public static final String HOME = "res/drawable/ic_home.xml";
        public static final String HOTEL = "res/drawable/ic_hotel.xml";
        public static final String LIGHTBULB = "res/drawable/ic_lightbulb.xml";
        public static final String MAP = "res/drawable/ic_map.xml";
        public static final String MOVIE = "res/drawable/ic_movie.xml";
        public static final String PARKING = "res/drawable/ic_parking.xml";
        public static final String PHONE = "res/drawable/ic_phone.xml";
        public static final String PHONE_ANDROID = "res/drawable/ic_phone_android.xml";
        public static final String REPORT = "res/drawable/ic_report.xml";
        public static final String RESTAURANT = "res/drawable/ic_restaurant.xml";
        public static final String SCHOOL = "res/drawable/ic_school.xml";
        public static final String STORE = "res/drawable/ic_store.xml";
        public static final String SWIMMING_POOL = "res/drawable/ic_swimming_pool.xml";
        public static final String TAXI = "res/drawable/ic_taxi.xml";
        public static final String TRAIN = "res/drawable/ic_train.xml";
        public static final String VIDEOGAME = "res/drawable/ic_videogame.xml";
        public static final String WALLET = "res/drawable/ic_wallet.xml";
        public static final String WATER = "res/drawable/ic_water.xml";
        public static final String WORK = "res/drawable/ic_work.xml";
    }

    public enum MovementType {
        ALL, EXPENSE, INCOME
    }

    public enum Period {
        ALLTIME, CURRENT, DAILY, WEEKLY,
        MONTHLY, TWO_MONTHLY, THREE_MONTHLY, FOUR_MONTHLY,
        HALF_YEARLY, YEARLY, TWO_YEARLY
    }

    public enum Action {
        ADD, EDIT
    }

    public enum DBMode {
        READ, WRITE
    }

    public enum Depreciation {
        EXPIRED, CURRENT, FUTURE
    }

    public enum GroupingMode {
        YEAR, MONTH, WEEK, DAY
    }

    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String ORDER_ASC = "ASC";
    public static final String ORDER_DESC = "DESC";
}
