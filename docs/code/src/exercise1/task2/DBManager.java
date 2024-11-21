package exercise1.task2;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBManager {
    private static Logger LOGGER = Logger.getLogger(DBManager.class.getName());
    private static Dao<Letter, Integer> letterDao;
    private static ConnectionSource connectionSource;

    public static void main(String[] args) {
        int[] arrayIndexes = {
                20, 44, 50, 13, 17, 33, 41,
                68, 77, 44, 29, 72, 48, 71,
                37, 48, 11, 69, 5, 65, 65
        };

        String[] letters ={
                "V","b","t"
        };

        boolean connected = connectToDB(
                "jdbc:mariadb://bilbao.informatik.uni-stuttgart.de/pe2-db-a1",
                "pe2-nutzer",
                "esJLtFm6ksCT4mCyOS");

        if (connected) {
            //2.2a)
            String solutionWord = buildWordFromIndexes(arrayIndexes);
            System.out.println("Das gesuchte Wort lautet: " + solutionWord);

            //2.2b)
            searchFor(letters);

            //2.2c) sum divided by the count of entries
            System.out.println("sum: " + getSum());
            System.out.println("count: " + getEntryCount());
            System.out.println("average: " + (float)getSum()/ getEntryCount());

            closeConnectionToDB();
        }
    }

    /**
     * connects to the given database
     * @param connectionString
     * @param user
     * @param password
     * @return true if the connection was successful
     */
    private static boolean connectToDB(String connectionString, String user, String password) {
        try {
            connectionSource = new JdbcConnectionSource(connectionString, user, password);
            letterDao = DaoManager.createDao(connectionSource, Letter.class);
            LOGGER.log(Level.INFO, "Connected to the database");
            return true;
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error code: " + exception.getErrorCode());
            LOGGER.log(Level.SEVERE, "Error message: " + exception.getMessage());
        }
        return false;
    }

    /**
     * Exercise 2.2 a)
     * build a searched word by the indeces given in the array
     * @param indexes
     * @return the searched word
     */
    private static String buildWordFromIndexes(int[] indexes) {
        String word = "";
        try {
            for (int index: indexes) {
                Letter letter = letterDao.queryForId(index);
                if (letter != null) {
                    word +=letter.getLetter();
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error getting the letters: " + exception.getMessage());
        }
        return word;
    }


    /**
     * Exercise 2.2 b)
     * search for id's for the given letters in the array and print them
     * @param letters
     */
    private static void searchFor(String[] letters) {
        try {
            for (String letter: letters) {
                 List<Letter> letterObjs = letterDao.queryForEq("letter",letter);
                 System.out.print(letter+ ": |");
                 letterObjs.forEach(l-> System.out.print(l.getId() +"|"));
                 System.out.println();
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error getting the letters: " + exception.getMessage());
        }
    }

    /**
     * Exercise 2.2c)
     * to calculate the sum of the IDs
     * @return the sum of all IDs
     */
    private static int getSum() {
        int sum = 0;
        try {
            List<Letter> letterObjs= letterDao.queryForAll();
            for (Letter letter: letterObjs) {
                sum += letter.getId();
            }
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error getting the letters: " + exception.getMessage());
        }
        return sum;
    }

    /**
     * Exercise 2.2c)
     * to get the count of all entries
     * @return the number of entries
     */
    private static int getEntryCount(){
        int sum = 0;
        try {
            List<Letter> letterObjs= letterDao.queryForAll();
            sum=letterObjs.size();
        } catch (SQLException exception) {
            LOGGER.log(Level.SEVERE, "Error getting the letters: " + exception.getMessage());
        }
        return sum;
    }

    /**
     * closes the connection to the current database
     */
    private static void closeConnectionToDB() {
        if(connectionSource != null) {
            try {
                connectionSource.close();
                LOGGER.log(Level.INFO, "Closed Connection to the database");
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "Error message: " + exception.getMessage());
            }
        }
    }

}
