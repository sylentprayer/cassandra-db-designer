package modeler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.foundationdb.sql.StandardException;
import com.foundationdb.sql.parser.BinaryOperatorNode;
import com.foundationdb.sql.parser.FromSubquery;
import com.foundationdb.sql.parser.GroupByColumn;
import com.foundationdb.sql.parser.JoinNode;
import com.foundationdb.sql.parser.OrderByColumn;
import com.foundationdb.sql.parser.ResultColumn;
import com.foundationdb.sql.parser.SubqueryNode;
import com.foundationdb.sql.parser.Visitable;
import com.foundationdb.sql.parser.Visitor;

public class StatementVisitor implements Visitor{

	private VisitableParser parser = new VisitableParser();
	
	//track inner query depth
	private transient StatementVisitor parent = null;
	private transient static Set<String> addedSubquries = new HashSet<String>();
	private List<StatementVisitor> children = new ArrayList<StatementVisitor>();
	private String subqueryType;
	private String correlationName;
	
	public VisitableParser getParser() {
		return parser;
	}
	
	@Override
	public boolean skipChildren(Visitable arg0) throws StandardException {
		return false;
	}

	@Override
	public boolean stopTraversal() {
		return false;
	}

	@Override
	public Visitable visit(Visitable visitable) throws StandardException {
		if(visitable instanceof ResultColumn){
			parser.parseResultColumn((ResultColumn) visitable);
		}else if(visitable instanceof GroupByColumn){
			parser.parseGroupByColumn((GroupByColumn) visitable);
		}else if(visitable instanceof OrderByColumn){
			parser.parseOrderByColumn((OrderByColumn) visitable);
		}else if(visitable instanceof BinaryOperatorNode){
			parser.parseBinaryOperatorNode((BinaryOperatorNode) visitable);
		}else if(visitable instanceof SubqueryNode){
			StatementVisitor subQueryVisitor = new StatementVisitor();
			SubqueryNode node = (SubqueryNode) visitable;
			subQueryVisitor.parent=this;
			node.accept(subQueryVisitor);
			subQueryVisitor.correlationName=node.getTableName();
			subQueryVisitor.subqueryType=node.getClass().getSimpleName();
			if(addedSubquries.add(subQueryVisitor.correlationName)){//Quick hack
				subQueryVisitor.parent.children.add(subQueryVisitor);
				addedSubquries.add(subQueryVisitor.correlationName);
			}
		}else if(visitable instanceof FromSubquery){
			StatementVisitor subQueryVisitor = new StatementVisitor();
			FromSubquery node = (FromSubquery) visitable;
			subQueryVisitor.parent=this;
			subQueryVisitor.correlationName=node.getCorrelationName();
			VisitableParser.CN=subQueryVisitor.correlationName;
			node.getSubquery().accept(subQueryVisitor);
			subQueryVisitor.subqueryType=node.getClass().getSimpleName();
			if(addedSubquries.add(subQueryVisitor.correlationName)){
				subQueryVisitor.parent.children.add(subQueryVisitor);
				addedSubquries.add(subQueryVisitor.correlationName);
			}
		}else if(visitable instanceof JoinNode){
			parser.parse((JoinNode) visitable);
		}
		return visitable;
	}

	@Override
	public boolean visitChildrenFirst(Visitable arg0) {
		return false;
	}

}
