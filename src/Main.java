import javax.xml.xquery.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import net.xqj.basex.BaseXXQDataSource;
import org.basex.api.client.ClientSession;
import org.basex.core.cmd.*;

public class Main {
    static String serverName = "localhost";
    static String port = "1984";
    static String databaseName = "books";
    static String userName = "admin";
    static String password = "admin";
    static String xmlFilePath = "C:\\DB\\books.xml";
    static String jsonFilePath = "C:\\DB\\books.json";
    static String csvFilePath = "C:\\DB\\books.csv";
    public static void main(String[] args) {
        try{
            ClientSession clientSession = new ClientSession(serverName, Integer.parseInt(port), userName, password);
            clientSession.create(databaseName, new FileInputStream(new File(xmlFilePath)));
            clientSession.execute(new Set("PARSER", "json"));
            clientSession.execute(new Add(databaseName, jsonFilePath));;
            clientSession.execute(new Set("PARSER", "csv"));
            clientSession.execute(new Add(databaseName, csvFilePath));
            clientSession.close();

            XQDataSource xqDataSource = new BaseXXQDataSource();
            xqDataSource.setProperty("serverName", serverName);
            xqDataSource.setProperty("port", port);
            xqDataSource.setProperty("databaseName", databaseName);
            XQConnection xqConnection = xqDataSource.getConnection(userName, password);
            XQPreparedExpression xqPreparedExpression;
            XQResultSequence xqResultSequence;

            System.out.println("XML:");
            xqPreparedExpression = xqConnection.prepareExpression("for $x in catalog/book return $x");
            xqResultSequence = xqPreparedExpression.executeQuery();
            while (xqResultSequence.next()){
                System.out.println(xqResultSequence.getItemAsString(null));
            }

            System.out.println("JSON:");
            xqPreparedExpression = xqConnection.prepareExpression("for $x in //json//catalog/book return $x");
            xqResultSequence = xqPreparedExpression.executeQuery();
            while (xqResultSequence.next()){
                System.out.println(xqResultSequence.getItemAsString(null));
            }

            System.out.println("CSV:");
            xqPreparedExpression = xqConnection.prepareExpression("for $x in //record return $x");
            xqResultSequence = xqPreparedExpression.executeQuery();
            while (xqResultSequence.next()){
                System.out.println(xqResultSequence.getItemAsString(null));
            }
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (XQException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
