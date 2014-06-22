Cassandra Data Modeling Tool
============================
[Don't model data, model queries]
----------------------------------

This is to create data modeling tool to analyze existing SQL queries (from RDBMS) and generate Cassandra data model and tables. Also, attempt to generate equivalent CQL queries for read/write and migration of existing data to cassandra. This will be primarily used for migrating existing application to Cassandra.

Users can provide SQLs from there existing appliation, as generally every application has query builders separate classes/code.

This can be used in migrating existing application from RDBMS to Cassandra. Cassandra mainly differs from RDBMS in following ways:
* It is denormalized, no joins.
* No Group By or aggregation.
* Ad-hoc query restricted to have condition on primary key(partition+segmentation) only and in order.
* No ACID transaction.

Tables in Cassandra store results of queries, so we can analyze existing SQL queries to figure out what data model can answer these queries. Then we can express tables in this data model in CQL (DDLs to create tables incassandra). Also, we can generate code/scripts for migrating existing data from RDBMS to Cassandra.

Initial design and technology stack:

* First of all need to analyze queries to get following info:
  *  Data types of columns.
  *  Tables joined together.
  *  Join conditions and where conditions.
  *  Auto-increment columns.
  *  Group By, Order By and distinct (distinct can be handled same as Group By).
  *  Analyse sub-queries to figure out final query result.

For this we can use https://github.com/FoundationDB/sql-parser

Demo for analyzing and visualizing SQL based using this http://sylentprayer.github.io

* Based on the information collected we can figure out what data model (minimal and optimal set of tables) can answer all the queries. 
  This is the core of application. Other functionalities like migration code can be done based on this.

* Look for learning algorithms (machine learning) to provide better recomendations. Generally machine learning is used when input and output does not have direct relation. Here, there is direct relation but still scope of usage for better recomendation.

*  Technology stack:
  * Play as web framework.
  * Spring for dependency injection.
  * Cassandra, Mongo or any other data store.
  * AngularJS as UI MVC framework.
  * Bootstrap/AngularUI as layout.
  * Datastax Java Driver for Cassandra data access.
  
User will upload a file with set of SQLs to analyze and other file containing DDLs of database (to get metadata). A zip file containing CQL create table scripts, CQL query rewrites (DML), and simple application with launch script to migrate data from RDBMS to Cassandra will be provided to user as output.

Will add code/release/downloads as they become available.
