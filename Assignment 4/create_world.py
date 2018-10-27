#TODO delete from table when accompished. if size==0 exit 
import os
import sqlite3
import imp
import atexit
import sys

# if the DB doesn't exist- create it and parse the config file
# else: exit.
if not os.path.isfile('world.db'):
    #connect to the database
    _conn = sqlite3.connect('world.db')

    #register a function to be called immediately when the interpreter terminates
    def _close_db():
        _conn.commit()
        _conn.close()

    def closeWorld():
        os.remove('world.db')
        
    # atexit.register(closeWorld)
    atexit.register(_close_db)

     
    def create_tables():
        _conn.executescript("""
            CREATE TABLE tasks (
                id  INTEGER PRIMARY KEY,
                task_name TEXT NOT NULL,
                worker_id INTEGER REFERENCES workers(id),
                time_to_make INTEGER NOT NULL,
                resource_name TEXT NOT NULL,
                resource_amount INTEGER NOT NULL
            );

            CREATE TABLE workers (
                id INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                status TEXT NOT NULL
            );

            CREATE TABLE resources (
                name TEXT PRIMARY KEY,
                amount INTEGER NOT NULL
            );
            """)

    def insert_task(id ,task_name, worker_id ,resource_name, resource_amount, time_to_make):
        _conn.execute("""
            INSERT INTO tasks (id ,task_name , worker_id , 
                        time_to_make, resource_name, resource_amount) 
            VALUES (?, ? ,?, ? ,?, ?)
        """, [int(id) ,task_name ,worker_id ,int(time_to_make), resource_name, int(resource_amount)])
     
     
    def insert_worker(id, name, status):
        _conn.execute("""
            INSERT INTO workers (id, name, status) 
            VALUES (?, ?, ?)
        """, [int(id), name, status])
     
     
    def insert_resource(name, amount):
        _conn.execute("""
            INSERT INTO resources (name, amount) 
            VALUES (?, ?)
        """, [name, int(amount)])

    def parse_config():
        taskID=1
        for line in open(sys.argv[1], 'r'):
            parameters= line.strip().split(',')
            numOfArgs=len(parameters)

            if(numOfArgs==2):
                # resource
                insert_resource(*parameters)
            elif(numOfArgs==3):
                # worker
                parameters.append('idle')
                insert_worker(*parameters[1:])
            else:
                # task (5 arguments)
                parameters.insert(0, taskID)
                taskID+=1
                insert_task(*parameters)    

    create_tables()
    parse_config()