import unittest

from python_progress_logger import ParallelLogger, XmlParallelLogger, CsvParallelLogger


class Test(unittest.TestCase):
    def test_Xml_progress_should_only_increase(self):
        # Test, progress should only increase.
        pl = XmlParallelLogger()
        pl.add_task("Task 1")
        pl.report_tasks()
        pl.report_progress(0, 50)
        pl.report_progress(0, 100)
        pl.report_progress(0, 30)
        self.assertEqual(pl.progress[0], 100)

    def test_Xml_progress_should_be_max_100(self):
        # Test, progress should be have a maximum value of 100.
        pl = XmlParallelLogger()
        pl.add_task("Task 1")
        pl.report_tasks()
        pl.report_progress(0, 100)
        pl.report_progress(0, 110)
        self.assertEqual(pl.progress[0], 100)
        
    def test_Xml_task_number_should_match_added_tasks(self):
        # Test, number of tasks should match the number of added tasks.
        pl = XmlParallelLogger()
        pl.add_task("Task 1")
        pl.add_task("Task 2")
        pl.add_task("Task 3")
        pl.report_tasks()
        self.assertEqual(pl.tasks_number, 3)
    
    def test_Xml_task_should_not_be_added_after_reported(self):
        # Test, task should not be added after reported.
        pl = XmlParallelLogger()
        pl.add_task("Task 1")
        pl.report_tasks()
        pl.add_task("Task 2")
        self.assertEqual(pl.tasks_number, 1)

    def test_Xml_progress_should_be_reported_after_tasks_are_reported(self):
        # Test, progress should be updated after the tasks have been reported.
        pl = XmlParallelLogger()
        pl.add_task("Task 1")
        pl.report_progress(0, 100)
        self.assertEqual(pl.progress[0], -1)
    
    def test_Csv_progress_should_only_increase(self):
        # Test, progress should only increase.
        pl = CsvParallelLogger()
        pl.add_task("Task 1")
        pl.report_tasks()
        pl.report_progress(0, 50)
        pl.report_progress(0, 100)
        pl.report_progress(0, 30)
        self.assertEqual(pl.progress[0], 100)

    def test_Csv_progress_should_be_max_100(self):
        # Test, progress should be have a maximum value of 100.
        pl = CsvParallelLogger()
        pl.add_task("Task 1")
        pl.report_tasks()
        pl.report_progress(0, 100)
        pl.report_progress(0, 110)
        self.assertEqual(pl.progress[0], 100)
        
    def test_Csv_task_number_should_match_added_tasks(self):
        # Test, number of tasks should match the number of added tasks.
        pl = CsvParallelLogger()
        pl.add_task("Task 1")
        pl.add_task("Task 2")
        pl.add_task("Task 3")
        pl.report_tasks()
        self.assertEqual(pl.tasks_number, 3)
    
    def test_Csv_task_should_not_be_added_after_reported(self):
        # Test, task should not be added after reported.
        pl = CsvParallelLogger()
        pl.add_task("Task 1")
        pl.report_tasks()
        pl.add_task("Task 2")
        self.assertEqual(pl.tasks_number, 1)

    def test_Csv_progress_should_be_reported_after_tasks_are_reported(self):
        # Test, progress should be updated after the tasks have been reported.
        pl = CsvParallelLogger()
        pl.add_task("Task 1")
        pl.report_progress(0, 100)
        self.assertEqual(pl.progress[0], -1)
        
if __name__ == '__main__':
    unittest.main()