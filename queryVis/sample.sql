select
    t1.col2,
    t2.col2,
    t3.col3,
    t4.col2 
from
    table1 t1,
    table2 t2,
    table3 t3,
    table4 t4  
where
    t1.col1=t2.col1 
	and t2.col2=t1.col2 
    and t2.col2=t4.col2 
    and t2.col1=t4.col1  
group by
    t3.col1,
    t3.col2 
order by
    t4.col2;