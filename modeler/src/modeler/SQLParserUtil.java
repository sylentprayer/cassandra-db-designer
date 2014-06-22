package modeler;

import java.lang.reflect.Modifier;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.SQLParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SQLParserUtil {

	private static final Gson GSON = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
	
	public static void main(String[] args) throws StandardException {
		SQLParser parser = new SQLParser();
		StatementVisitor visitor = new StatementVisitor();
		parser.parseStatement("select t1.col2,t2.col2,t3.col3,t4.col2 from table1 t1, table2 t2, table3 t3, table4 t4 where t1.col1=t2.col1 and t1.col2=t2.col2 and t2.col2=t4.col2 and  t2.col1=t4.col1 group by t3.col1,t3.col2 order by t4.col2").accept(visitor);

		System.out.println(GSON.toJson(visitor));
	}
}
