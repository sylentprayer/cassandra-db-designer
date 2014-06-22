function queryVis(data){
	var graph = new joint.dia.Graph;

	var paper = new joint.dia.Paper({
		el: $('#myholder'),
		width: 1500,
		height: 1000,
		model: graph,
		gridSize: 1
	});
			
	var tables = {};
	$.each(data.COLUMN_REF, function(index, value){
		/*"tableName": "b",
            "columnName": "c4",
            "columnAlias": "m",
            "function": "MAX"*/
		if(!tables[value.tableName]){
			tables[value.tableName]=[];
		}
		tables[value.tableName].push(value);
		
	});
	var i=0, lastTableCols=0, shapes = [];
	$.each(tables, function(key,value){
		var xOffset,yOffset;
		if(i%2==0){
			xOffset=i*150;
			yOffset=0;
		}else{
			xOffset=i*150;
			yOffset=(lastTableCols+3)*30;
		}
		alert(xOffset+" "+key+yOffset);
		var table = new joint.shapes.basic.Rect({
			position: { x: 100+xOffset, y: 30+yOffset },
			size: { width: 200, height: 30 },
			attrs: { text: { text: key} }
		});
		shapes.push(table);
		$.each(value, function(idx,columns){
			var column = new joint.shapes.basic.Rect({
				id:key+'-'+columns.columnName,
				position: { x: 100+xOffset, y: 30+yOffset+(idx+1)*30 },
				size: { width: 200, height: 30 },
				attrs: { text: { text: columns.columnName} }
			});
			table.embed(column);
			shapes.push(column);
		});
		lastTableCols=value.length;
		i++;
		
	});
	
	$.each(data.JOIN_LINK,function(index,link){
		/*"leftTable": "a",
            "rightTable": "b",
            "leftTableColumn": "q",
            "rightTableColumn": "w"*/
		var join = new joint.dia.Link({
			source: { id: link.leftTable+'-'+link.leftTableColumn },
			target: { id: link.rightTable+'-'+link.rightTableColumn }
		});
		shapes.push(join);
	});
	graph.addCells(shapes);
}