import os
import sqlite3
import imp
import atexit
import sys

if os.path.isfile('world.db'):
    #connect to the database
    _conn = sqlite3.connect('world.db')
    def getNextTask():
        task = _conn.execute("""
            SELECT MIN(tasks.id), task_name , worker_id , time_to_make, 
                   resource_name, resource_amount       , workers.name
            FROM tasks 
            JOIN workers 
            ON workers.id= worker_id AND workers.status='idle'
            """).fetchone()
        # print(task)
        if None in task:
            return {}
        else:
            return {
                'taskID': task[0],
                'task_name': task[1], 
                'workerID': task[2], 
                'time_to_make':task[3], 
                'resource_name': task[4], 
                'resource_amount':task[5],
                'worker_name': task[6]
            }

    def getNumOfTasks():
        return _conn.execute("SELECT Count(*) FROM tasks").fetchone()[0];

    def getWorker(workerID):
        worker = _conn.execute("SELECT * FROM workers WHERE id='{}'".format(workerID)).fetchone()
        return {
            'id': worker[0],
            'name': worker[1], 
            'status': worker[2] 
        }

    def updateResource(name, amountUsed):
        _conn.execute("UPDATE resources SET amount= amount - {} WHERE name='{}'".format(amountUsed, name))

    def updateWorkerStatus(workerID, status):
        _conn.execute("UPDATE workers SET status='{}' WHERE id={}".format(status, workerID))

    def removeTask(taskID):
        _conn.execute("DELETE FROM tasks WHERE id={}".format(taskID))

    def printTable(tableName):
        cursor=_conn.cursor()
        for entry in cursor.execute("SELECT * FROM {}".format(tableName)).fetchall():
            print(entry)
    
    def printAll():
        printTable('workers')
        print ("-------")
        printTable('resources')
        print ("-------")
        printTable('tasks')
        print ("************")

    def getWorkers():
        return _conn.execute("SELECT * FROM workers").fetchall()

    # main simulation loop
    newTasks=[]
    activeTasks=[]
    counter=1
    while getNumOfTasks()> 0 or activeTasks:
        # print (" ")
        # print ("iteration {}:".format(counter))
        # counter+=1

        # assign tasks to idle workers
        nextTask= getNextTask()
        while nextTask:
            updateResource(nextTask['resource_name'], nextTask['resource_amount'])
            updateWorkerStatus(nextTask['workerID'], 'busy')
            print ("{} says: work work".format(nextTask['worker_name']))
            removeTask(nextTask['taskID'])
            newTasks.append(nextTask)
            nextTask= getNextTask()

        #loop through active tasks and make changes
        for task in activeTasks:
            print ("{} is busy {}...".format(task['worker_name'], task['task_name']))
            task['time_to_make']-=1

        # handle completed tasks
        for task in activeTasks:
            if task['time_to_make'] == 0:
                print ("{} says: All done!".format(task['worker_name']))
                updateWorkerStatus(task['workerID'], 'idle')
                activeTasks.remove(task)

        activeTasks.extend(newTasks)
        newTasks=[]
        # printAll()


    