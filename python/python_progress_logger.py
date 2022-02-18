# python_progress_logger.py
import sys
from abc import ABC, abstractmethod
from xml.dom import minidom

import time

class ParallelLogger(ABC):
    @abstractmethod
    def add_task(self, description):
        pass

    @abstractmethod
    def report_tasks(self):
        pass

    @abstractmethod
    def report_progress(self, task_number, progress):
        pass

class CsvParallelLogger(ParallelLogger):
    def __init__(self, rank, size):
        self.tasks = []
        self.progress = []
        self.tasks_number = 0
        self.file_name = "progress_"+str(rank)+".plog"
        self.tasks_reported = False
        self.size = size
        return

    def __write_to_file(self, text):
        self.file = open(self.file_name, "w")
        self.file.write(text);
        self.file.close();
        return

    def __append_to_file(self, text):
        self.file = open(self.file_name, "a")
        self.file.write(text);
        self.file.close();
        return

    def __replace_line(self, text):
        lines = open(self.file_name, 'r').readlines()
        lines_number = len(lines)
        if lines_number < 2:
            self.__append_to_file(text)
        else:
            lines[1] = text
            out = open(self.file_name, 'w')
            out.writelines(lines)
            out.close()
        return

    def __update_timestap(self):
        unix_epoch_time = int(time.time()* 1000)
        self.__replace_line(str(unix_epoch_time)+"\n"); # UNIX Epoch Time

    def add_task(self, description):
        id = -1
        if not self.tasks_reported:
            self.tasks.append(description)
            self.progress.append(-1)
            self.tasks_number = self.tasks_number + 1
            id = self.tasks_number-1
        return id

    def report_tasks(self):
        if not self.tasks_reported:
            self.__write_to_file(str(self.size)+"\n")
            self.__update_timestap()
            for task_number in range(self.tasks_number):
                self.__append_to_file(str(task_number)+","+self.tasks[task_number]+"\n")
            self.tasks_reported = True
        return

    def report_progress(self, task_number, progress):
        if self.tasks_reported:
            if task_number <= self.tasks_number and task_number >= 0:
                if progress > self.progress[task_number] and progress <= 100:
                    self.progress[task_number] = progress
                    self.__append_to_file(str(task_number)+","+str(progress)+"\n")
                    self.__update_timestap()
        return

class XmlParallelLogger(ParallelLogger):
    def __init__(self, rank, size):
        self.root = minidom.Document()
        self.tasks = []
        self.progress = []
        self.tasks_number = 0
        self.tasks_repoted = False
        self.file_name = "progress_"+str(rank)+".plog"
        self.size = size
        return

    def __set_text_node(self, node, text):
        textnode = self.root.createTextNode(text)

        if not node.hasChildNodes():
            node.appendChild(textnode)
        else:
            oldnode = node.firstChild
            node.replaceChild(textnode, oldnode)
        return

    def __update_timestap(self):
        unix_epoch_time = int(time.time()* 1000)
        job = self.root.getElementsByTagName('job')[0]
        nodesList = self.root.getElementsByTagName('lastUpdated')
        timestamp = self.root.createElement('lastUpdated')
        if len(nodesList) == 0:
            job.appendChild(timestamp)
        else:
            old_timestamp = nodesList[0]
            job.replaceChild(timestamp, old_timestamp)
        self.__set_text_node(timestamp, str(unix_epoch_time))
        return

    def __write_to_file(self):
        xml_str = self.root.toprettyxml(indent ="")
        with open(self.file_name, "w") as f:
            f.write(xml_str.replace('\n', ''))
        return

    def add_task(self, description):
        id = -1
        if not self.tasks_repoted:
            self.tasks.append(description)
            self.progress.append(-1)
            self.tasks_number = self.tasks_number + 1
            id = self.tasks_number-1
        return id

    def report_tasks(self):
        if not self.tasks_repoted:
            job = self.root.createElement('job')
            self.root.appendChild(job)

            nodes = self.root.createElement('nodes')
            self.__set_text_node(nodes, str(self.size))
            job.appendChild(nodes)

            # Add tasks:
            for task_number in range(self.tasks_number):
                task = self.root.createElement('task')
                job.appendChild(task)
                task.setAttribute("id", str(task_number))
                description = self.root.createElement('description')
                progress = self.root.createElement('progress')
                task.appendChild(description)
                task.appendChild(progress)
                self.__set_text_node(description, self.tasks[task_number])
                self.__set_text_node(progress, str(self.progress[task_number]))
            self.__update_timestap()
            self.__write_to_file()
            self.tasks_repoted = True
        return

    def report_progress(self, task_number, progress):
        if self.tasks_repoted:
            if task_number <= self.tasks_number and task_number >= 0:
                if progress > self.progress[task_number] and progress <= 100:
                    self.progress[task_number] = progress
                    progress_element = self.root.getElementsByTagName('progress')[task_number]
                    self.__set_text_node(progress_element, str(progress))
                    self.__update_timestap()
                    self.__write_to_file()
        return

def select_progress_logger(typeOfLogger):
    if str. lower(typeOfLogger) == 'xml':
        return XmlParallelLogger(0,1)
    elif str. lower(typeOfLogger) == 'csv':
        return CsvParallelLogger(0,1)
    else:
        raise ValueError(format)

def sample_csv_use():
    pl = select_progress_logger('csv')
    pl.add_task("My first task")
    pl.add_task("My second task")
    pl.report_tasks()
    pl.report_progress(0, 50)
    time.sleep(10)
    pl.report_progress(0, 100)
    pl.report_progress(1, 100)    

def sample_xml_use():
    pl = select_progress_logger('xml')
    pl.add_task("My first task")
    pl.add_task("My second task")
    pl.report_tasks()
    pl.report_progress(0, 10)
    pl.report_progress(0, 20)
    pl.report_progress(0, 100)

if __name__ == "__main__":
    print(f"Arguments count: {len(sys.argv)}")
    for i, arg in enumerate(sys.argv):
        print(f"Argument {i:>6}: {arg}")
    #sample_csv_use()
    #sample_xml_use()