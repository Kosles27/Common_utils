package dbUtils;

import java.util.List;

public class GeneralQueries {


    /**
     * Convert UN location code to IQShip Port code
     * @param unLocationCode UN location code
     * @param connectionSetting the DB connection String
     * @param user username to connect to the DB
     * @param password password to connect to the DB
     * @return IQShip Port code
     * @author genosar.dafna
     * @since 14.03.2022
     */
    public String convertUnLocationCodeToIQshipPortCode(String unLocationCode, String connectionSetting, String user, String password) {

        String query = "select prt_code\n" +
                "from aplcnt.tab_prt\n" +
                "where prt_locode = '" + unLocationCode + "'";

        List<String> results = new OracleDatabaseUtil().getResultsFromQuery(query, connectionSetting, user, password);
        if(results.size()>0)
            return results.get(0);
        else
            throw new Error(String.format("Cannot convert UNLocation code '%s' to IQship Port Code", unLocationCode));
    }

    /**
     * Convert IQShip Port code to UN location code
     * @param iqShipPortCode IQShip Port code
     * @param connectionSetting the DB connection String
     * @param user username to connect to the DB
     * @param password password to connect to the DB
     * @return UN location code
     * @author genosar.dafna
     * @since 24.03.2022
     */
    public String convertIQshipPortCodeToUnLocationCode(String iqShipPortCode, String connectionSetting, String user, String password) {

        String query = "select prt_locode\n" +
                "from aplcnt.tab_prt\n" +
                "where prt_code = '" + iqShipPortCode + "'";

        List<String> results = new OracleDatabaseUtil().getResultsFromQuery(query, connectionSetting, user, password);
        if(results.size()>0)
            return results.get(0);
        else
            throw new Error(String.format("Cannot convert IQship Port Code '%s' to UNLocation code", iqShipPortCode));
    }
}
