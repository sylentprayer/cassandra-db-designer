package modeler;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.AggregateNode;
import com.foundationdb.sql.parser.BinaryOperatorNode;
import com.foundationdb.sql.parser.ColumnReference;
import com.foundationdb.sql.parser.ConstantNode;
import com.foundationdb.sql.parser.GroupByColumn;
import com.foundationdb.sql.parser.JoinNode;
import com.foundationdb.sql.parser.OrderByColumn;
import com.foundationdb.sql.parser.ResultColumn;

public class VisitableParser {
	
	private Set<ColumnRef> COLUMN_REF = new LinkedHashSet<ColumnRef>();
	private List<Ordering> GROUP_BY = new ArrayList<Ordering>();
	private List<JoinLink> JOIN_LINK = new ArrayList<JoinLink>();
	private List<Ordering> ORDER_BY = new ArrayList<Ordering>();
	private List<Filter> FILTERS = new ArrayList<Filter>();
	static String CN;
	private static class JoinLink{
		String leftTable;
		String rightTable;
		String joinType;
		String leftTableColumn;
		String rightTableColumn;
		String correlationName;
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}		
	}
	private static class Filter{
		String table;
		String column;
		String operator;
		String operand;
		String correlationName;
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}	
	}
	private static class Ordering{
		String table;
		String column;
		boolean order;
		String correlationName;
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}	
	}
	private static class ColumnRef{
		String tableName;
		String tableAlias;
		String columnName;
		String columnAlias;
		String function;
		boolean isDistinct;
		String correlationName;
		
		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(obj, this, "function");
		}
		
		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
		}	
		
	}
	
	public void parseResultColumn(ResultColumn node) {
		ColumnRef ref = new ColumnRef();
		ref.columnAlias=node.getName();
		ref.columnName=node.getColumnName();
		ref.tableName=node.getTableName();
		if(node.getExpression() instanceof AggregateNode){
			AggregateNode aggregateNode = (AggregateNode) node.getExpression();
			ref.isDistinct=aggregateNode.isDistinct();
			ref.function=aggregateNode.getAggregateName();
			//TODO HANDLE * all column
			if(aggregateNode.getOperand() != null){
				ref.columnName=aggregateNode.getOperand().getColumnName();
				ref.tableName=aggregateNode.getOperand().getTableName();
			}
		}
		if(ref.columnName==null)ref.columnName=node.getExpression().getColumnName();
		if(ref.tableName==null)ref.tableName=node.getExpression().getTableName();
		ref.correlationName=CN;
		COLUMN_REF.add(ref);
	}
	
	public void parseBinaryOperatorNode(BinaryOperatorNode node) throws StandardException {
		ColumnRef columnRef = new ColumnRef();
		if(node.getRightOperand() instanceof ConstantNode){
			Filter filter = new Filter();
			filter.column=node.getLeftOperand().getColumnName();
			filter.operand=node.getRightOperand().getTypeId().getSQLTypeName();
			filter.table=node.getLeftOperand().getTableName();
			filter.operator=node.getOperator();
			filter.correlationName=CN;
			FILTERS.add(filter);
			columnRef.columnAlias=filter.column;
			columnRef.columnName=filter.column;
			columnRef.tableAlias=filter.table;
			columnRef.tableName=filter.table;
			columnRef.correlationName=CN;
			COLUMN_REF.add(columnRef);
		}else if(node.getRightOperand() instanceof ColumnReference){
			JoinLink joinLink = new JoinLink();
			joinLink.leftTable=node.getLeftOperand().getTableName();
			joinLink.rightTable=node.getRightOperand().getTableName();
			joinLink.leftTableColumn=node.getLeftOperand().getColumnName();
			joinLink.rightTableColumn=node.getRightOperand().getColumnName();
			joinLink.correlationName=CN;
			JOIN_LINK.add(joinLink);
			columnRef.columnAlias=joinLink.leftTableColumn;
			columnRef.columnName=joinLink.leftTableColumn;
			columnRef.tableAlias=joinLink.leftTable;
			columnRef.tableName=joinLink.leftTable;
			columnRef.correlationName=CN;
			COLUMN_REF.add(columnRef);
			columnRef=new ColumnRef();
			columnRef.columnAlias=joinLink.rightTableColumn;
			columnRef.columnName=joinLink.rightTableColumn;
			columnRef.tableAlias=joinLink.rightTable;
			columnRef.tableName=joinLink.rightTable;
			columnRef.correlationName=CN;
			COLUMN_REF.add(columnRef);
		}
	}
	
	public void parseGroupByColumn(GroupByColumn node) {
		Ordering ordering = new Ordering();
		ordering.column=node.getColumnName();
		ordering.table=node.getColumnExpression().getTableName();
		ordering.correlationName=CN;
		GROUP_BY.add(ordering);
		ColumnRef columnRef = new ColumnRef();
		columnRef.columnAlias=ordering.column;
		columnRef.columnName=ordering.column;
		columnRef.tableAlias=ordering.table;
		columnRef.tableName=ordering.table;
		columnRef.correlationName=CN;
		COLUMN_REF.add(columnRef);
	}
	
	public void parseOrderByColumn(OrderByColumn node) {
		Ordering ordering = new Ordering();
		ordering.column=node.getExpression().getColumnName();
		ordering.table=node.getExpression().getTableName();
		ordering.order=node.isAscending();
		ordering.correlationName=CN;
		ORDER_BY.add(ordering);
		ColumnRef columnRef = new ColumnRef();
		columnRef.columnAlias=ordering.column;
		columnRef.columnName=ordering.column;
		columnRef.tableAlias=ordering.table;
		columnRef.tableName=ordering.table;
		columnRef.correlationName=CN;
		COLUMN_REF.add(columnRef);
	}
	
	public void parse(JoinNode node) {
		node.getCorrelationName();
	}
}
