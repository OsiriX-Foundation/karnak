package org.karnak.dicom.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.karnak.dicom.model.ConfigNode;
import org.karnak.dicom.thread.DicomEchoThread;

public class DicomEchoService {
	
	public String dicomEcho(List<ConfigNode> nodes) throws InterruptedException, ExecutionException {
		StringBuilder result = new StringBuilder();

		List<Future<String>> threadsResult = createThreadsResult(nodes);
		for (Future<String> threadResult : threadsResult) {
			result.append(threadResult.get());
		}
		
		return result.toString();
	}
	
	private List<Future<String>> createThreadsResult(List<ConfigNode> nodes) throws InterruptedException {
		List<Future<String>> threadResult = null;
		try {
			ExecutorService executorService = Executors.newFixedThreadPool(nodes.size());
			
			List<DicomEchoThread> threads = new ArrayList<>();

			for (ConfigNode node : nodes) {
				DicomEchoThread dicomEchoThread = new DicomEchoThread(node);
				threads.add(dicomEchoThread);
			}
			
			threadResult = executorService.invokeAll(threads);
			
			return threadResult;
		} catch (InterruptedException e) {
			throw e;
		}
	}

}