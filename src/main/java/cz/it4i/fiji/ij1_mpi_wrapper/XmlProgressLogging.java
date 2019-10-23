
package cz.it4i.fiji.ij1_mpi_wrapper;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class XmlProgressLogging extends ProgressLoggingRestrictions implements
	ProgressLogging
{

	private Logger logger = Logger.getLogger(ParallelMacro.class.getName());

	private Map<Integer, String> tasks = new HashMap<>();

	private Integer numberOfTasks = 0;

	private boolean tasksWereReported = false;

	private Map<Integer, Integer> lastWrittenTaskPercentage = new HashMap<>();

	private Document openXmlFile(int rank) {
		String progressFilePath = LOG_FILE_PROGRESS_PREFIX + String.valueOf(rank) +
			LOG_FILE_PROGRESS_POSTFIX;

		Document document = null;

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.parse(progressFilePath);
		}
		catch (Exception exc) {
			logger.warning(exc.getMessage());
		}
		return document;
	}

	private Document createXmlFile() {
		Document document = null;

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			document = dBuilder.newDocument();

			Element rootElement = document.createElement("job");
			document.appendChild(rootElement);
		}
		catch (Exception exc) {
			logger.warning(exc.getMessage());
		}

		return document;
	}

	private void saveXmlFile(int rank, Document document) {
		String progressFilePath = LOG_FILE_PROGRESS_PREFIX + String.valueOf(rank) +
			LOG_FILE_PROGRESS_POSTFIX;
		try {
			// Write the content into xml file:
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
			
			Transformer transformer;

			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(progressFilePath));
			transformer.transform(source, result);
		}
		catch (Exception exc) {
			logger.warning(exc.getMessage());
		}
	}

	@Override
	public int addTask(String description) {
		if (!super.followsAddTaskRestrictions(tasksWereReported)) {
			return -1;
		}

		tasks.put(numberOfTasks, description);
		return numberOfTasks++;
	}

	@Override
	public void reportTasks(int rank, int size) {
		if (!super.followsReportTasksRestrictions(tasks, tasksWereReported)) {
			return;
		}

		// Create the XML document:
		Document document = createXmlFile();

		if (document == null) {
			logger.warning("Xml document does not exist!");
			return;
		}

		// Find the root node of the document:
		Node rootNode = document.getChildNodes().item(0);

		// Add an element that indicates the total number of nodes of the job:
		Element nodesElement = document.createElement("nodes");
		nodesElement.setTextContent(String.valueOf(size));
		rootNode.appendChild(nodesElement);

		// Add the tasks as elements in the XML document:
		for (int counter = 0; counter < numberOfTasks; counter++) {
			Element taskElement = document.createElement("task");
			taskElement.setAttribute("id", String.valueOf(counter));

			Element descriptionElement = document.createElement("description");
			descriptionElement.setTextContent(tasks.get(counter));
			taskElement.appendChild(descriptionElement);

			rootNode.appendChild(taskElement);
		}

		// Save the XML document with the reported tasks:
		saveXmlFile(rank, document);

		// The tasks should not be reported twice:
		tasksWereReported = true;
	}

	@Override
	public int reportProgress(int taskId, int progress, int rank) {
		if (!super.followsReportProgressRestrictions(tasks, taskId, progress,
			lastWrittenTaskPercentage))
		{
			return -1;
		}

		// Ignore impossible new progress percentages:
		if (progress > 100 || progress < 0) {
			return lastWrittenTaskPercentage.get(taskId);
		}

		lastWrittenTaskPercentage.put(taskId, progress);
		Document document = openXmlFile(rank);

		if (document == null) {
			logger.warning("Xml document does not exist!");
			return -1;
		}

		// Create or update progress element for specified task:
		Node progressNode = findNode(document, "//task[@id='" + taskId +
			"']//progress");
		if (progressNode == null) {
			progressNode = document.createElement("progress");
			progressNode.setTextContent(String.valueOf(progress));
			Node taskNode = findNode(document, "//task[@id='" + taskId + "']");
			if (taskNode == null) {
				logger.warning("Task with id " + taskId + " could not be found!");
				return -1;
			}
			taskNode.appendChild(progressNode);
		}
		else {
			progressNode.setTextContent(String.valueOf(progress));
		}

		// Save the document with the new progress:
		saveXmlFile(rank, document);

		// success
		return 0;
	}

	private Node findNode(Document document, String xpathExpression) {
		// Create XPathFactory object
		XPathFactory xpathFactory = XPathFactory.newInstance();

		// Create XPath object
		XPath xpath = xpathFactory.newXPath();

		Node node = null;
		try {
			// Create XPathExpression object:
			XPathExpression expr = xpath.compile(xpathExpression);

			// Evaluate expression result on XML document:
			node = (Node) expr.evaluate(document, XPathConstants.NODE);
		}
		catch (Exception exc) {
			logger.warning(exc.getMessage());
		}
		return node;
	}
}
